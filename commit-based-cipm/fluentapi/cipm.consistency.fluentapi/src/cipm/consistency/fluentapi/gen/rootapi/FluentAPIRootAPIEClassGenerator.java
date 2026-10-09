package cipm.consistency.fluentapi.gen.rootapi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;

import cipm.consistency.fluentapi.gen.FluentAPIDocumentationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * The generator class responsible for generating the EClass, which represents
 * the facade of the fluent API. This generator class will only generate the
 * fluent api's EClass and its EOperations, nothing else. Therefore, this
 * generator class alone is not enough to generate the fluent api model.
 * <p>
 * <p>
 * Note that the methods {@link #generateRootAPIEClass()} and
 * {@link #setupRootAPIEClass(FluentAPIGenerationContext)} have to be called, in
 * order to generate the EClass of the fluent api.
 * 
 * @see {@link cipm.consistency.fluentapi.gen.FluentAPIGenerator} For more
 *      details on the flow of the fluent api generation.
 * 
 * @author Alp Torac Genc
 * 
 */
public class FluentAPIRootAPIEClassGenerator {
	private static final Map<String, String> summaries = new LinkedHashMap<>();

	/**
	 * 
	 * @param context The object encapsulating the context of fluent api generation.
	 * @return
	 */
	public EClass generateRootAPIEClass(FluentAPIGenerationContext context) {
		var fluentAPIECls = EcoreFactory.eINSTANCE.createEClass();
		fluentAPIECls.setAbstract(false);
		fluentAPIECls.setInterface(false);
		fluentAPIECls.setName(ModelConstants.FluentAPI.CLASS_NAME
				.getFor(StringUtils.capitalize(context.getTargetMetamodelPackageProvider().getTargetMetamodelName())));
		return fluentAPIECls;
	}

	private void addEClassDoc(FluentAPIGenerationContext context) {
		var doc = ModelConstants.FluentAPI.CLASS_DOC.getFor(FluentAPIDocumentationUtil.serialiseSummaries(summaries));
		FluentAPIGenerationUtil.addDocumentation(context.getFluentAPIECls(), doc);
	}

	private void addOperations(FluentAPIGenerationContext context) {
		var createNewMetGen = new FluentAPIRootAPICreateNewMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(createNewMetGen.generateAllCreateNewMethods(context));
		summaries.putAll(createNewMetGen.getMethodNamesToDescriptions());

		var newMetGen = new FluentAPIRootAPINewMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(newMetGen.getAllRootAPINewOperations(context));
		summaries.putAll(newMetGen.getMethodNamesToDescriptions());

		var modElemMetGen = new FluentAPIRootAPIModifyElementMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(modElemMetGen.getAllRootAPIModifyElementOperations(context));
		summaries.putAll(modElemMetGen.getMethodNamesToDescriptions());

		var conMetGen = new FluentAPIRootAPIContinueMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(conMetGen.generateAllContinueMethods(context));
		summaries.putAll(conMetGen.getMethodNamesToDescriptions());

		var dropInitMetGen = new FluentAPIRootAPIDropInitialisationMethodGenerator();
		context.getFluentAPIECls().getEOperations().add(dropInitMetGen.generateDropInitialisationMethod(context));
		summaries.putAll(dropInitMetGen.getMethodNamesToDescriptions());

		var markMetGen = new FluentAPIRootAPIMarkMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(markMetGen.generateAllMarkMethods(context));
		summaries.putAll(markMetGen.getMethodNamesToDescriptions());

		var waitForMarkGen = new FluentAPIRootAPIWaitForMarkMethodGenerator();
		context.getFluentAPIECls().getEOperations().add(waitForMarkGen.generateAllWaitForMarkMethods(context));
		summaries.putAll(waitForMarkGen.getMethodNamesToDescriptions());

		var getInitMetGen = new FluentAPIRootAPIGetInitialisationForMethodGenerator();
		context.getFluentAPIECls().getEOperations().addAll(
				new FluentAPIRootAPIGetInitialisationForMethodGenerator().getAllInitialisationForMethods(context));
		summaries.putAll(getInitMetGen.getMethodNamesToDescriptions());

		var withOpMetGen = new FluentAPIRootAPIWithOperationGenerator();
		context.getFluentAPIECls().getEOperations()
				.addAll(new FluentAPIRootAPIWithOperationGenerator().getAllAPITopLevelWithOperations(context));
		summaries.putAll(withOpMetGen.getMethodNamesToDescriptions());

		var getAllSupClsMetGen = new FluentAPIRootAPIGetAllSupportedClassesMethodGenerator();
		context.getFluentAPIECls().getEOperations().add(new FluentAPIRootAPIGetAllSupportedClassesMethodGenerator()
				.generateGetAllSupportedClassesMethodGenerator());
		summaries.putAll(getAllSupClsMetGen.getMethodNamesToDescriptions());

		var getOngInitMetGen = new FluentAPIRootAPIGetOngoingInitsMethodGenerator();
		context.getFluentAPIECls().getEOperations()
				.add(new FluentAPIRootAPIGetOngoingInitsMethodGenerator().generateGetOngoingInitsMethod(context));
		summaries.putAll(getOngInitMetGen.getMethodNamesToDescriptions());

		var clrOngInitMetGen = new FluentAPIRootAPIClearAllOngoingInitialisationsMethodGenerator();
		context.getFluentAPIECls().getEOperations()
				.add(new FluentAPIRootAPIClearAllOngoingInitialisationsMethodGenerator()
						.generateClearAllOngoingInitsMethod(context));
		summaries.putAll(clrOngInitMetGen.getMethodNamesToDescriptions());
	}

	/**
	 * Sets up the fluent api class' EClass by generating and adding its
	 * EOperations, as well as documenting them.
	 * 
	 * @param context The object encapsulating the context of fluent api generation.
	 */
	public void setupRootAPIEClass(FluentAPIGenerationContext context) {
		addOperations(context);
		addEClassDoc(context);
	}
}
