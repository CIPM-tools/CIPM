package cipm.consistency.fluentapi.gen.rootapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.extensions.FluentEObjectAPIMethods;
import cipm.consistency.fluentapi.gen.FluentAPIGeneralParameterGenerator;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIWithOperationGenerator implements IFluentAPIMethodGenerator {
	private static final String featureValModificationMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			// %s: Corresponding method's name in FluentEObjectAPIMethods
			FluentEObjectAPIMethods.class.getName() + ".%s(this, "
					+ ModelConstants.GeneralParameters.USED_EOBJECT_PARAMETER_NAME.get() + ", "
					+ ModelConstants.GeneralParameters.MODIFIED_FEATURE_PARAMETER_NAME.get() + ", "
					+ ModelConstants.GeneralParameters.FEATURE_VALUE_PARAMETER_NAME.get() + ")",
			"return this");

	private static final String featureValCleaningMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			// %s: Corresponding method's name in FluentEObjectAPIMethods
			FluentEObjectAPIMethods.class.getName() + ".%s(this, "
					+ ModelConstants.GeneralParameters.USED_EOBJECT_PARAMETER_NAME.get() + ", "
					+ ModelConstants.GeneralParameters.MODIFIED_FEATURE_PARAMETER_NAME.get() + ")",
			"return this");

	public List<EOperation> getAllAPITopLevelWithOperations(FluentAPIGenerationContext context) {
		var ops = new ArrayList<EOperation>();

		ops.add(getWithFeatOp(context));
		ops.add(getWithoutFeatOp(context));
		ops.add(getWithAddedFeatOp(context));
		ops.add(getWithRemovedFeatOp(context));
		ops.add(getCleanFeatOp(context));

		return ops;
	}

	private EOperation getWithFeatOp(FluentAPIGenerationContext context) {
		var eobjParam = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var featParam = FluentAPIGeneralParameterGenerator.getFeatParam();
		var featValParam = FluentAPIGeneralParameterGenerator.getFeatValParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.WithFeat.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, String.format(featureValModificationMethodBodyTemplate, "xWithFeat"));
		FluentAPIGenerationUtil.addEParameters(op, eobjParam, featParam, featValParam);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.WithFeat.SUMMARY.get());
		return op;
	}

	private EOperation getWithoutFeatOp(FluentAPIGenerationContext context) {
		var eobjParam = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var featParam = FluentAPIGeneralParameterGenerator.getFeatParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.WithoutFeat.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, String.format(featureValCleaningMethodBodyTemplate, "xWithoutFeat"));
		FluentAPIGenerationUtil.addEParameters(op, eobjParam, featParam);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.WithoutFeat.SUMMARY.get());
		return op;
	}

	private EOperation getWithAddedFeatOp(FluentAPIGenerationContext context) {
		var eobjParam = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var featParam = FluentAPIGeneralParameterGenerator.getFeatParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.WithAddedFeat.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, String.format(featureValModificationMethodBodyTemplate, "xWithAddedFeat"));
		FluentAPIGenerationUtil.addEParameters(op, eobjParam, featParam,
				FluentAPIGeneralParameterGenerator.getFeatValParam());
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.WithAddedFeat.SUMMARY.get());
		return op;
	}

	private EOperation getWithRemovedFeatOp(FluentAPIGenerationContext context) {
		var eobjParam = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var featParam = FluentAPIGeneralParameterGenerator.getFeatParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.WithRemovedFeat.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op,
				String.format(featureValModificationMethodBodyTemplate, "xWithRemovedFeat"));
		FluentAPIGenerationUtil.addEParameters(op, eobjParam, featParam,
				FluentAPIGeneralParameterGenerator.getFeatValParam());
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.WithRemovedFeat.SUMMARY.get());
		return op;
	}

	private EOperation getCleanFeatOp(FluentAPIGenerationContext context) {
		var eobjParam = FluentAPIGeneralParameterGenerator.getEObjectParam();
		var featParam = FluentAPIGeneralParameterGenerator.getFeatParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.CleanFeat.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, String.format(featureValCleaningMethodBodyTemplate, "xCleanFeat"));
		FluentAPIGenerationUtil.addEParameters(op, eobjParam, featParam);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.CleanFeat.SUMMARY.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.WithFeat.NAME.get(), ModelConstants.FluentAPI.WithFeat.SUMMARY.get(),

				ModelConstants.FluentAPI.WithoutFeat.NAME.get(), ModelConstants.FluentAPI.WithoutFeat.SUMMARY.get(),

				ModelConstants.FluentAPI.WithAddedFeat.NAME.get(), ModelConstants.FluentAPI.WithAddedFeat.SUMMARY.get(),

				ModelConstants.FluentAPI.WithRemovedFeat.NAME.get(),
				ModelConstants.FluentAPI.WithRemovedFeat.SUMMARY.get(),

				ModelConstants.FluentAPI.CleanFeat.NAME.get(), ModelConstants.FluentAPI.CleanFeat.SUMMARY.get());
	}
}
