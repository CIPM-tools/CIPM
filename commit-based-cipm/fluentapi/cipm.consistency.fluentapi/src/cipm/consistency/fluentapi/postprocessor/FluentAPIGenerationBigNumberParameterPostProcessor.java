package cipm.consistency.fluentapi.postprocessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;

/**
 * A post-processor that adds overloading variants of methods, which take
 * {@link BigInteger} or {@link BigDecimal} as parameters, for convenience.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationBigNumberParameterPostProcessor implements FluentAPIGenerationPostProcessor {
	/**
	 * Replaces the original parameter in the method call string
	 * <p>
	 * %s: Name of the original parameter
	 */
	private static final String adaptedBigDecimalParameterTemplate = java.math.BigDecimal.class.getName()
			+ ".valueOf(%s)";
	/**
	 * Replaces the original parameter in the method call string
	 * <p>
	 * %s: Name of the original parameter
	 */
	private static final String adaptedBigIntegerParameterTemplate = java.math.BigInteger.class.getName()
			+ ".valueOf(%s)";

	/**
	 * Original parameter type to overload -> List of types to overload for
	 */
	private static final Map<EClassifier, List<EClassifier>> paramTypeOverrides = Map.of(
			EcorePackage.Literals.EBIG_INTEGER, List.of(EcorePackage.Literals.EINT, EcorePackage.Literals.ELONG),
			EcorePackage.Literals.EBIG_DECIMAL, List.of(EcorePackage.Literals.EDOUBLE, EcorePackage.Literals.EFLOAT));

	/**
	 * {@link #getEClsScope()}
	 */
	private List<EClass> eClsScope;

	/**
	 * @param eClsScope {@link #getEClsScope()}
	 */
	public FluentAPIGenerationBigNumberParameterPostProcessor(List<EClass> eClsScope) {
		this.eClsScope = eClsScope;
	}

	/**
	 * @return The list of {@link EClass}es that this post-processor should apply to
	 */
	public List<EClass> getEClsScope() {
		return this.eClsScope;
	}

	/**
	 * @return The appropriate replacement template for the original parameter for
	 *         the given EClassifier
	 */
	private String getParameterAdaptationTemplate(EClassifier paramTypeToReplace) {
		return paramTypeToReplace.equals(EcorePackage.Literals.EBIG_INTEGER) ? adaptedBigIntegerParameterTemplate
				: adaptedBigDecimalParameterTemplate;
	}

	private EOperation createOverloadingMethodFor(EOperation opToOverload, EClassifier paramTypeToReplace,
			EClassifier newParamType) {
		var copier = new EcoreUtil.Copier();
		var overloadingOp = (EOperation) copier.copy(opToOverload);
		copier.copyReferences();

		// Adjust parameters
		var params = overloadingOp.getEParameters();
		var oldTypes = new LinkedHashMap<EParameter, EClassifier>();

		for (var param : params) {
			if (param.getEType().equals(paramTypeToReplace)) {
				oldTypes.put(param, param.getEType());
				param.setEType(newParamType);
			}
		}

		// Adjust method body
		var serialisedParameters = String.join(",",
				params.stream()
						.map((p) -> oldTypes.containsKey(p)
								? String.format(getParameterAdaptationTemplate(oldTypes.get(p)), p.getName())
								: p.getName())
						.toArray(String[]::new));
		FluentAPIGenerationUtil.addBody(overloadingOp, FluentAPIMethodsUtil
				.joinLOC("return this." + overloadingOp.getName() + "(" + serialisedParameters + ")"));
		FluentAPIGenerationUtil.useDocumentationOf(overloadingOp, opToOverload);

		return overloadingOp;
	}

	private List<EOperation> createOverloadingMethodsFor(EOperation opToOverload) {
		var overloadingOpList = new ArrayList<EOperation>();

		for (var paramTypeOverride : paramTypeOverrides.entrySet()) {
			var paramTypeToReplace = paramTypeOverride.getKey();
			var newParamTypes = paramTypeOverride.getValue();
			if (opToOverload.getEParameters().stream()
					.anyMatch((p) -> p.getEType() != null && p.getEType().equals(paramTypeToReplace))) {
				for (var newParamType : newParamTypes) {
					overloadingOpList.add(createOverloadingMethodFor(opToOverload, paramTypeToReplace, newParamType));
				}
			}
		}

		return overloadingOpList;
	}

	@Override
	public void apply() {
		for (var eCls : this.getEClsScope()) {
			for (var op : new ArrayList<>(eCls.getEOperations())) {
				eCls.getEOperations().addAll(createOverloadingMethodsFor(op));
			}
		}
	}
}
