package cipm.consistency.fluentapi.postprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;

/**
 * Introduces overloading methods for certain original methods that consider
 * singular parameters: Given an EOperation op, where param is its only
 * EParameter of type T, overloads op regarding param with 2 overloading
 * methods: opArr with paramArr of type {@code T[]} and opCol with paramCol of
 * type {@code Collection<? extends T>}.
 * <p>
 * <p>
 * For more details on the method bodies of overloading methods, refer to the
 * concrete implementor's documentation.
 * <p>
 * <p>
 * Due to type erasure related limitations of Java, results of applying this
 * post-processor might cause compilation errors, if there are multiple variants
 * of a method with a Collection parameter. In such cases, the methods in this
 * post-processor can be overridden to fix those cases.
 * 
 * @author Alp Torac Genc
 */
public abstract class FluentAPIGenerationMultipleValueParameterPostProcessor
		implements FluentAPIGenerationPostProcessor {
	/**
	 * {@link #getContext()}
	 */
	private FluentAPIGenerationContext context;
	/**
	 * {@link #getEClsScope()}
	 */
	private List<EClass> eClsScope;

	/**
	 * @param context   {@link #getContext()}
	 * @param eClsScope {@link #getEClsScope()}
	 */
	public FluentAPIGenerationMultipleValueParameterPostProcessor(FluentAPIGenerationContext context,
			List<EClass> eClsScope) {
		this.context = context;
		this.eClsScope = eClsScope;
	}

	/**
	 * @return The list of {@link EClass}es that this post-processor should apply to
	 */
	public List<EClass> getEClsScope() {
		return this.eClsScope;
	}

	/**
	 * @return The generation context of the model
	 */
	public FluentAPIGenerationContext getContext() {
		return this.context;
	}

	/**
	 * @param oldParam A given EParameter (with type T)
	 * @return An EParameter with an array-typed version of oldParam (T[])
	 */
	private EParameter getArrayVersion(EParameter oldParam) {
		var param = FluentAPIGenerationUtil.generateArrayValuedEParameter(this.getContext(), oldParam.getName(),
				oldParam.getEType());
		FluentAPIGenerationUtil.useDocumentationOf(param, oldParam);
		return param;
	}

	/**
	 * @param oldParam A given EParameter (with type T)
	 * @return An EParameter with an collection-typed version of oldParam
	 *         (Collection<? extends T>)
	 */
	private EParameter getColVersion(EParameter oldParam) {
		var param = FluentAPIGenerationUtil.generateSingleValuedEParameter(oldParam.getName(),
				FluentAPIGenerationUtil.generateCollectionTypeParameter(this.getContext(), oldParam.getEType()));
		FluentAPIGenerationUtil.useDocumentationOf(param, oldParam);
		return param;
	}

	/**
	 * Contains the method overloading logic.Prepares overloadingOp as an
	 * overloading EOperation of an original EOperation.
	 * 
	 * @param overloadingOp The EOperation, which will overload the original
	 *                      EOperation
	 * @param newParam      The EParameter that the overloadingOp will use
	 * @return overloadingOp
	 */
	protected abstract EOperation overloadMethodBody(EOperation overloadingOp, EParameter newParam);

	private EOperation createOverloadingMultipleValueMethodFor(EOperation opToOverload, EParameter paramToOverload,
			EParameter newParam) {
		var copier = new EcoreUtil.Copier();
		var overloadingOp = (EOperation) copier.copy(opToOverload);
		copier.copyReferences();

		// Adjust parameter
		var oldParamIdx = opToOverload.getEParameters().indexOf(paramToOverload);
		var oldParam = overloadingOp.getEParameters().get(oldParamIdx);

		overloadingOp.getEParameters().add(oldParamIdx, newParam);
		overloadingOp.getEParameters().remove(oldParam);

		// Adjust method body
		overloadMethodBody(overloadingOp, newParam);
		FluentAPIGenerationUtil.useDocumentationOf(overloadingOp, opToOverload);

		return overloadingOp;
	}

	/**
	 * @return Whether op already has an overloading array-typed method with respect
	 *         to EParameter p, or another method with a clashing signature
	 */
	private boolean hasArrayOverload(EOperation op, EParameter p) {
		var pIdx = op.getEParameters().indexOf(p);
		return op.getEContainingClass().getEOperations().stream().anyMatch((opTwo) -> op != opTwo
				&& op.getName().equals(opTwo.getName()) && op.getEType().equals(opTwo.getEType())
				&& opTwo.getEParameters().get(pIdx).getEGenericType().getEClassifier().getInstanceClass().isArray()
				&& p.getEType().getInstanceClass().equals(opTwo.getEParameters().get(pIdx).getEGenericType()
						.getEClassifier().getInstanceClass().getComponentType()));
	}

	/**
	 * @return Whether op already has an overloading collection-typed method with
	 *         respect to EParameter p, or another method with a clashing signature
	 */
	private boolean hasColOverload(EOperation op, EParameter p) {
		var pIdx = op.getEParameters().indexOf(p);
		return op.getEContainingClass().getEOperations().stream()
				.anyMatch((opTwo) -> op != opTwo && op.getName().equals(opTwo.getName())
						&& op.getEType().equals(opTwo.getEType()) && opTwo.getEParameters().get(pIdx).getEGenericType()
								.getEClassifier().getInstanceClass().equals(Collection.class));
	}

	/**
	 * Given an EOperation op, where param is one of its parameters, decides whether
	 * op should be overloaded with respect to param. For more details about how it
	 * is overloaded, refer to the documentation of the concrete implementor.
	 * 
	 * @param param The EParameter of an EOperation op, for which op will
	 *              potentially be overloaded
	 * @return Whether op should be overloaded for param
	 */
	protected abstract boolean shouldOverloadParameter(EParameter param);

	private List<EOperation> createOverloadingMethodsFor(EOperation opToOverload) {
		var ops = new ArrayList<EOperation>();
		for (var p : opToOverload.getEParameters().stream().filter(this::shouldOverloadParameter)
				.collect(Collectors.toList())) {
			if (!hasArrayOverload(opToOverload, p)) {
				ops.add(createOverloadingMultipleValueMethodFor(opToOverload, p, getArrayVersion(p)));
			}
			if (!hasColOverload(opToOverload, p)) {
				ops.add(createOverloadingMultipleValueMethodFor(opToOverload, p, getColVersion(p)));
			}
		}
		return ops;
	}

	/**
	 * Given an EOperation op, decides whether op should be overloaded. For more
	 * details about how it is overloaded, refer to the documentation of the
	 * concrete implementor.
	 * 
	 * @param op The EOperation to potentially overload
	 * @return Whether the given EOperation should be overloaded
	 */
	protected abstract boolean shouldOverloadMethod(EOperation op);

	@Override
	public void apply() {
		for (var eCls : this.getEClsScope()) {
			// Only consider EOperations, which have a single parameter and whose return
			// type is their containing EClass
			var opsToOverload = eCls.getEOperations().stream().filter((op) -> shouldOverloadMethod(op))
					.collect(Collectors.toList());
			for (var op : opsToOverload) {
				op.getEContainingClass().getEOperations().addAll(createOverloadingMethodsFor(op));
			}
		}
	}
}
