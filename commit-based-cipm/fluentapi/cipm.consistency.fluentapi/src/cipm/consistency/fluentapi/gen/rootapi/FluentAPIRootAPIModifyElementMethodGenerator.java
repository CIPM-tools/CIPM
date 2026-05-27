package cipm.consistency.fluentapi.gen.rootapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.gen.FluentAPIGeneralParameterGenerator;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIModifyElementMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String modifyElementMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			// %s: Initialisation class name
			"return (%s) " + ModelConstants.FluentAPI.GetInitialisationFor.NAME
					.thisCall(ModelConstants.GeneralParameters.USED_EOBJECT_PARAMETER_NAME.get()));

	private static final String modifyMarkedElementMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			// %s: Initialisation type class name
			// %s: GetMarked method name
			"return (%s) " + ModelConstants.FluentAPI.GetInitialisationFor.NAME
					.thisCall("this.%s(" + ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ")"));

	public List<EOperation> getAllRootAPIModifyElementOperations(FluentAPIGenerationContext context) {
		var eObjEClss = context.getAllEligibleTargetMetamodelConcreteEClasses();
		var ops = new ArrayList<EOperation>();
		ops.add(getRootAPITopLevelModifyElementOperation(context));
		ops.add(getRootAPITopLevelModifyMarkedElementOperation(context));

		for (int i = 0; i < eObjEClss.size(); i++) {
			var elemToInitECls = eObjEClss.get(i);
			var initEClass = context.getAllInitEClss().get(i);
			ops.add(getRootAPIModifyElementOperationForEClass(context, elemToInitECls, initEClass));
			ops.add(getRootAPIModifyMarkedElementOperationForEClass(context, elemToInitECls, initEClass));
		}

		return ops;
	}

	private EOperation getRootAPIModifyMarkedElementOperationForEClass(FluentAPIGenerationContext context,
			EClass elemToInitECls, EClass initECls) {
		var markKeyParam = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.ModifyMarked.NAME.getFor(StringUtils.capitalize(elemToInitECls.getName())),
				initECls);
		FluentAPIGenerationUtil.addBody(op, String.format(modifyMarkedElementMethodBodyTemplate,
				FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, initECls),
				ModelConstants.FluentAPI.GetMarked.NAME.getFor(StringUtils.capitalize(elemToInitECls.getName()))));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.ModifyMarked.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, markKeyParam);
		return op;
	}

	private EOperation getRootAPIModifyElementOperationForEClass(FluentAPIGenerationContext context,
			EClass elemToInitECls, EClass initECls) {
		var param = FluentAPIGeneralParameterGenerator.getEObjectParamOfType(elemToInitECls);
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.Modify.NAME.getFor(StringUtils.capitalize(elemToInitECls.getName())),
				initECls);
		FluentAPIGenerationUtil.addBody(op, String.format(modifyElementMethodBodyTemplate,
				FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, initECls)));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Modify.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	private EOperation getRootAPITopLevelModifyElementOperation(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.Modify.TOP_NAME.get(),
				context.getInitSuperECls());
		FluentAPIGenerationUtil.addBody(op, String.format(modifyElementMethodBodyTemplate,
				FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls())));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Modify.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	private EOperation getRootAPITopLevelModifyMarkedElementOperation(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.ModifyMarked.TOP_NAME.get(),
				context.getInitSuperECls());
		FluentAPIGenerationUtil.addBody(op,
				String.format(modifyMarkedElementMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						ModelConstants.FluentAPI.GetMarked.TOP_NAME.get()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.ModifyMarked.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.Modify.NAME.getEmpty(), ModelConstants.FluentAPI.Modify.SUMMARY.get(),
				ModelConstants.FluentAPI.ModifyMarked.NAME.getEmpty(),
				ModelConstants.FluentAPI.ModifyMarked.SUMMARY.get());
	}
}
