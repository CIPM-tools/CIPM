package cipm.consistency.fluentapi.gen.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;

import cipm.consistency.fluentapi.gen.FluentAPIDocumentationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;
import cipm.consistency.fluentapi.gen.superinit.FluentAPISuperInitialisationGetInitialisedEClassMethodGenerator;
import cipm.consistency.fluentapi.gen.superinit.FluentAPISuperInitialisationResetOperationGenerator;
import cipm.consistency.fluentapi.gen.superinit.FluentAPISuperInitialisationToAPIMethodGenerator;

/**
 * The generator class responsible for generating the EClasses, which represent
 * the parts (Initialisation classes) of the fluent api that are responsible for
 * creating / modifying model elements of the metamodel, which the fluent api is
 * generated for. This generator class will only generate the Initialisation
 * classes' EClasses and their EOperations, nothing else. Therefore, this
 * generator class alone is not enough to generate the fluent api model.
 * <p>
 * <p>
 * Note that both methods
 * {@link #generateFluentAPIInitialisationClasses(FluentAPIGenerationContext)}
 * and
 * {@link #setupFluentAPIInitialisationFor(EClass, EClass, FluentAPIGenerationContext)}
 * have to be called (for each generated initialisation EClass), in order to
 * generate the EClasses of the Initialisation classes.
 * 
 * @author Alp Torac Genc
 * 
 * @see {@link cipm.consistency.fluentapi.gen.FluentAPIGenerator} For more
 *      details on the flow of the fluent api generation.
 */
public class FluentAPIInitialisationEClassGenerator {
	private static final Map<String, String> summaries = new LinkedHashMap<>();

	/**
	 * @param context The object encapsulating the context of fluent api generation.
	 * @return The list of initialisation classes' EClasses generated for the model
	 *         elements of the metamodel the fluent api is generated for.
	 */
	public List<EClass> generateFluentAPIInitialisationClasses(FluentAPIGenerationContext context) {
		var initSubClss = new ArrayList<EClass>();

		for (var initialisedEClass : context.getAllEligibleTargetMetamodelConcreteEClasses()) {
			var initSubCls = generateInitialisationEClass(initialisedEClass, context);
			context.addInitECls(initialisedEClass, initSubCls);
			initSubClss.add(initSubCls);
		}

		return initSubClss;
	}

	private void addXInitEClassDocumentation(EClass xInitEClass, EClass initialisedEClass,
			FluentAPIGenerationContext context) {

		var doc = ModelConstants.Initialiation.CLASS_DOC.getFor(initialisedEClass.getName(),
				context.getTargetMetamodelPackageProvider().getTargetMetamodelName(), initialisedEClass.getName(),
				FluentAPIDocumentationUtil.serialiseSummaries(summaries));
		FluentAPIGenerationUtil.addDocumentation(xInitEClass, doc);
	}

	private EClass generateInitialisationEClass(EClass initialisedEClass, FluentAPIGenerationContext context) {
		var xInitEClass = EcoreFactory.eINSTANCE.createEClass();
		xInitEClass.setName(
				ModelConstants.Initialiation.CLASS_NAME.getFor(StringUtils.capitalize(initialisedEClass.getName())));
		return xInitEClass;
	}

	/**
	 * Sets up xInitEClass as EClass of the initialisation class for the given
	 * initialisedEClass.
	 * 
	 * @param xInitEClass       An initialisation EClass
	 * @param initialisedEClass The EClass of the model element, which xInitEClass
	 *                          is meant for
	 * @param context           The object encapsulating the context of fluent api
	 *                          generation.
	 */
	public void setupFluentAPIInitialisationFor(EClass xInitEClass, EClass initialisedEClass,
			FluentAPIGenerationContext context) {
		xInitEClass.getEOperations().addAll(new FluentAPIInitialisationReturnTypeOverrideGenerator()
				.generateMethodsWithOverridingReturnType(context, xInitEClass, initialisedEClass));

		addNonOverriddenInheritedMethodSummaries();
		addOperations(xInitEClass, initialisedEClass, context);
		addXInitEClassDocumentation(xInitEClass, initialisedEClass, context);
	}

	private void addNonOverriddenInheritedMethodSummaries() {
		var getInitEClsGen = new FluentAPISuperInitialisationGetInitialisedEClassMethodGenerator();
		summaries.putAll(getInitEClsGen.getMethodNamesToDescriptions());

		var resetGen = new FluentAPISuperInitialisationResetOperationGenerator();
		summaries.putAll(resetGen.getMethodNamesToDescriptions());

		var toAPIGen = new FluentAPISuperInitialisationToAPIMethodGenerator();
		summaries.putAll(toAPIGen.getMethodNamesToDescriptions());
	}

	private void addOperations(EClass xInitEClass, EClass initialisedEClass, FluentAPIGenerationContext context) {
		var newElementGen = new FluentAPIInitialisationNewElementOperationGenerator();
		xInitEClass.getEOperations().add(newElementGen.getNewElementOperationFor(xInitEClass, initialisedEClass));
		summaries.putAll(newElementGen.getMethodNamesToDescriptions());

		var withGen = new FluentAPIInitialisationWithOperationGenerator();
		xInitEClass.getEOperations()
				.addAll(withGen.generateAllWithOperationsFor(context, xInitEClass, initialisedEClass));
		summaries.putAll(withGen.getMethodNamesToDescriptions());
	}
}
