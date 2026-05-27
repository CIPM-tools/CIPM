package cipm.consistency.fluentapi.java.postprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.TypesPackage;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;
import cipm.consistency.fluentapi.postprocessor.FluentAPIGenerationPostProcessor;

/**
 * A post-processor that adds overloads for methods that use parameters of type
 * {@link TypeReference} for convenience. Using the overloading methods spare
 * having to manually construct {@link ClassifierReference}s for
 * {@link Classifier}s, in order to create a TypeReference instance for them.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationJavaMetamodelPostProcessor implements FluentAPIGenerationPostProcessor {
	private static final Pattern methodNamePatternToOverload = Pattern
			.compile(String.join("|", ModelConstants.Initialiation.With.NAME.getFor(".*"),
					ModelConstants.Initialiation.WithAdded.NAME.getFor(".*")));

	private static final String typeReferenceParameterOverrideTemplate = ModelConstants.SuperInitialisation.ToAPI.NAME
			.thisCall()
			+ ModelConstants.FluentAPI.New.NAME.callFor(new String[] { ClassifierReference.class.getSimpleName() })
			+ ModelConstants.Initialiation.With.NAME.callFor(new String[] { "Target" }, "%s")
			+ ModelConstants.SuperInitialisation.CreateNow.NAME.call();
	private static final String typeReferenceParameterOverrideParameterDocumentation = "The classifier instance, which will be referenced";

	/**
	 * {@link #getInitEClss()}
	 */
	private List<EClass> initEClss;
	private FluentAPITargetMetamodelPackageProvider provider;

	/**
	 * @param initEClss {@link #getInitEClss()}
	 */
	public FluentAPIGenerationJavaMetamodelPostProcessor(List<EClass> initEClss,
			FluentAPITargetMetamodelPackageProvider provider) {
		this.initEClss = initEClss;
		this.provider = provider;
	}

	/**
	 * @return The list of {@link EClass}es that this post-processor should apply to
	 */
	public List<EClass> getInitEClss() {
		return this.initEClss;
	}

	/**
	 * @return The object that grants access to the JaMoPP metamodel.
	 */
	public FluentAPITargetMetamodelPackageProvider getTargetMetamodelPackageProvider() {
		return provider;
	}

	private List<EOperation> createOverloadingMethodsFor(EOperation opToOverload) {
		var copier = new EcoreUtil.Copier();
		var overloadingOp = (EOperation) copier.copy(opToOverload);
		copier.copyReferences();

		// Adjust parameters

		var paramExprs = new ArrayList<String>();

		for (int i = 0; i < overloadingOp.getEParameters().size(); i++) {
			var currentParam = overloadingOp.getEParameters().get(i);
			if (currentParam.getEType().equals(provider.getEClass(TypesPackage.Literals.TYPE_REFERENCE.getName()))) {
				paramExprs.add(String.format(typeReferenceParameterOverrideTemplate, currentParam.getName()));
				currentParam.setEType(provider.getEClass(ClassifiersPackage.Literals.CLASSIFIER.getName()));

				// Add parameter documentation to clarify intent
				FluentAPIGenerationUtil.addDocumentation(currentParam,
						typeReferenceParameterOverrideParameterDocumentation);
			} else {
				paramExprs.add(currentParam.getName());
			}
		}

		// Adjust method body
		var serialisedParameters = String.join(",", paramExprs.toArray(String[]::new));
		FluentAPIGenerationUtil.addBody(overloadingOp, FluentAPIMethodsUtil
				.joinLOC("return this." + overloadingOp.getName() + "(" + serialisedParameters + ")"));

		return List.of(overloadingOp);
	}

	@Override
	public void apply() {
		for (var initECls : this.getInitEClss()) {
			var opsToOverload = initECls.getEOperations().stream()
					.filter((op) -> methodNamePatternToOverload.matcher(op.getName()).matches())
					.collect(Collectors.toList());
			for (var op : opsToOverload) {
				// Skip parameters of type EList, since overloading them results in type erasure
				// related issues
				if (op.getEParameters().stream().anyMatch((p) -> p.getEType() != null && !p.isMany()
						&& p.getEType().equals(provider.getEClass(TypesPackage.Literals.TYPE_REFERENCE.getName())))) {
					initECls.getEOperations().addAll(createOverloadingMethodsFor(op));
				}
			}
		}
	}
}
