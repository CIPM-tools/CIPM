package cipm.consistency.fluentapi.gen.rootapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.extensions.FluentEObjectAPIMethods;
import cipm.consistency.fluentapi.gen.FluentAPIGeneralParameterGenerator;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIContinueMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String continueMethodBodyTemplate =
			// %s: Full Initialisation class name
			// %s: Initialised element class (statically, i.e. either via method parameter
			// or via .class)
			FluentAPIMethodsUtil
					.joinLOC("return (%s) " + FluentEObjectAPIMethods.class.getName() + ".continueElement(%s)");

	private static final String continueMarkedMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			"var markedElem = " + ModelConstants.FluentAPI.GetMarked.TOP_NAME
					.thisCall(ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()),
			// %s: Init class name
			"return markedElem == null ? null : (%s) " + FluentAPIInitialisationStorage.class.getName()
					+ ".getOngoingInitialisations().stream().filter((i) -> (("
					// %s: Target metamodel package name (lower case)
					+ ModelConstants.FULL_ROOT_PACKAGE_NAME.getFor("%s") + "."
					+ ModelConstants.SuperInitialisation.CLASS_NAME.get() + ") i)"
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.getterCall()
					+ " == markedElem).findFirst().get()");

	public List<EOperation> generateAllContinueMethods(FluentAPIGenerationContext context) {
		var eObjEClss = context.getAllEligibleTargetMetamodelConcreteEClasses();
		var ops = new ArrayList<EOperation>();

		ops.add(generateTopLevelContinueMethod(context));
		ops.add(generateTopLevelContinueMarkedMethod(context));

		for (int i = 0; i < eObjEClss.size(); i++) {
			var eObjEClass = eObjEClss.get(i);
			var initEClass = context.getAllInitEClss().get(i);
			if (context.getTargetMetamodelFilter().hasModifiableFeatures(eObjEClass)) {
				ops.add(generateContinueMethod(context, eObjEClass, initEClass));
				ops.add(generateContinueMarkedMethod(eObjEClass, initEClass, context));
			}
		}

		return ops;
	}

	private EOperation generateTopLevelContinueMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getArbitraryClassParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.Continue.TOP_NAME.get(),
				context.getInitSuperECls());

		FluentAPIGenerationUtil.addEParameters(op, param);
		FluentAPIGenerationUtil.addBody(op,
				String.format(continueMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						param.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Continue.SUMMARY.get());
		return op;
	}

	private EOperation generateContinueMethod(FluentAPIGenerationContext context, EClass elemToInit, EClass initECls) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.Continue.NAME.getFor(StringUtils.capitalize(elemToInit.getName())), initECls);

		FluentAPIGenerationUtil.addBody(op,
				String.format(continueMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, initECls),
						elemToInit.getInstanceClass().getName() + ".class"));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Continue.SUMMARY.get());
		return op;
	}

	private EOperation generateContinueMarkedMethod(EClass elemToInit, EClass initECls,
			FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.ContinueMarked.NAME.getFor(StringUtils.capitalize(elemToInit.getName())),
				initECls);

		FluentAPIGenerationUtil.addBody(op,
				String.format(continueMarkedMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, initECls),
						context.getTargetMetamodelPackageProvider().getTargetMetamodelName()));
		FluentAPIGenerationUtil.addEParameters(op, param);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.ContinueMarked.SUMMARY.get());
		return op;
	}

	private EOperation generateTopLevelContinueMarkedMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.ContinueMarked.TOP_NAME.get(),
				context.getInitSuperECls());

		FluentAPIGenerationUtil.addBody(op,
				String.format(continueMarkedMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						context.getTargetMetamodelPackageProvider().getTargetMetamodelName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.ContinueMarked.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.Continue.NAME.getEmpty(),
				ModelConstants.FluentAPI.Continue.SUMMARY.get(),
				ModelConstants.FluentAPI.ContinueMarked.NAME.getEmpty(),
				ModelConstants.FluentAPI.ContinueMarked.SUMMARY.get());
	}
}
