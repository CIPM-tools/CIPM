package cipm.consistency.fluentapi.gen.superinit;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.gen.FluentAPIDocumentationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * The generator class responsible for generating the EClass, which represents
 * the abstract (super) initialisation class. This generator class will only
 * generate the EClass of the super initialisation class and its EOperations,
 * nothing else. Therefore, this generator class alone is not enough to generate
 * the fluent api model.
 * <p>
 * <p>
 * Note that the methods {@link #generateSuperInitialisationEClass()} and
 * {@link #setupSuperInitialisationEClass(FluentAPIGenerationContext)} have to
 * be called, in order to generate the EClass of the super initialisation class.
 * 
 * @see {@link cipm.consistency.fluentapi.gen.FluentAPIGenerator} For more
 *      details on the flow of the fluent api generation.
 * 
 * @author Alp Torac Genc
 * 
 */
public class FluentAPISuperInitialisationEClassGenerator {
	private static final String delegatedDoc = "Delegates to its correspondent in "
			+ ModelConstants.SuperInitialisation.RootAPI.NAME.inThis();
	private static final Map<String, String> summaries = new LinkedHashMap<>();

	private EReference getCurrentElementReference() {
		var currentElementReference = EcoreFactory.eINSTANCE.createEReference();
		currentElementReference.setChangeable(true);
		currentElementReference.setContainment(false);
		currentElementReference.setEType(EcorePackage.Literals.EOBJECT);
		currentElementReference.setName(ModelConstants.SuperInitialisation.CurrentElement.NAME.get());
		currentElementReference.setLowerBound(1);
		currentElementReference.setUpperBound(1);
		FluentAPIGenerationUtil.addDocumentation(currentElementReference,
				ModelConstants.SuperInitialisation.CurrentElement.DOC.get());
		return currentElementReference;
	}

	private EReference getRootAPIReference(FluentAPIGenerationContext context) {
		var rootAPIRef = EcoreFactory.eINSTANCE.createEReference();
		rootAPIRef.setChangeable(true);
		rootAPIRef.setContainment(false);
		rootAPIRef.setEType(context.getFluentAPIECls());
		rootAPIRef.setName(ModelConstants.SuperInitialisation.RootAPI.NAME.get());
		rootAPIRef.setLowerBound(1);
		rootAPIRef.setUpperBound(1);
		FluentAPIGenerationUtil.addDocumentation(rootAPIRef, ModelConstants.SuperInitialisation.RootAPI.DOC.get());
		return rootAPIRef;
	}

	/**
	 * @return The EClass of the super initialisation class.
	 */
	public EClass generateSuperInitialisationEClass() {
		var superType = EcoreFactory.eINSTANCE.createEClass();
		superType.setAbstract(true);
		superType.setInterface(false);
		superType.setName(ModelConstants.SuperInitialisation.CLASS_NAME.get());
		return superType;
	}

	private void addEClassDoc(FluentAPIGenerationContext context) {
		var doc = ModelConstants.SuperInitialisation.CLASS_DOC
				.getFor(FluentAPIDocumentationUtil.serialiseSummaries(summaries));
		FluentAPIGenerationUtil.addDocumentation(context.getInitSuperECls(), doc);
	}

	private void addRefs(FluentAPIGenerationContext context) {
		context.setInitSuperEClsApiReference(getRootAPIReference(context));
		context.getInitSuperECls().getEStructuralFeatures().add(context.getInitSuperEClsApiReference());

		context.setInitSuperEClsCurrentElement(getCurrentElementReference());
		context.getInitSuperECls().getEStructuralFeatures().add(context.getInitSuperEClsCurrentElement());
	}

	private void addOperations(FluentAPIGenerationContext context) {
		var getInitEClsGen = new FluentAPISuperInitialisationGetInitialisedEClassMethodGenerator();
		context.getInitSuperECls().getEOperations().add(getInitEClsGen.generateGetInitialisedEClassMethod());
		summaries.putAll(getInitEClsGen.getMethodNamesToDescriptions());

		var createNowGen = new FluentAPISuperInitialisationCreateNowMethodGenerator();
		context.getInitSuperECls().getEOperations()
				.addAll(createNowGen.generateAllCreateNowMethods(EcorePackage.Literals.EOBJECT));
		summaries.putAll(createNowGen.getMethodNamesToDescriptions());

		var newElemGen = new FluentAPISuperInitialisationNewElementMethodGenerator();
		context.getInitSuperECls().getEOperations().add(newElemGen.generateNewElementMethod(context));
		summaries.putAll(newElemGen.getMethodNamesToDescriptions());

		var resetGen = new FluentAPISuperInitialisationResetOperationGenerator();
		context.getInitSuperECls().getEOperations()
				.add(resetGen.generateResetInitialisationMethod(context.getInitSuperECls()));
		summaries.putAll(resetGen.getMethodNamesToDescriptions());

		var toAPIGen = new FluentAPISuperInitialisationToAPIMethodGenerator();
		context.getInitSuperECls().getEOperations().add(toAPIGen.generateToAPIMethod(context));
		summaries.putAll(toAPIGen.getMethodNamesToDescriptions());

		var delegateOpGen = new FluentAPISuperInitialisationDelegateMethodGenerator();
		var delegateOps = delegateOpGen.generateAllDelegateMethods(context);
		context.getInitSuperECls().getEOperations().addAll(delegateOps);
		delegateOps.forEach((op) -> summaries.put(op.getName(), delegatedDoc));
	}

	/**
	 * Sets up the super initialisation class' EClass by generating and adding its
	 * EOperations, EReferences, as well as documenting them.
	 * 
	 * @param context The object encapsulating the context of fluent api generation.
	 */
	public void setupSuperInitialisationEClass(FluentAPIGenerationContext context) {
		addRefs(context);
		addOperations(context);
		addEClassDoc(context);
	}
}
