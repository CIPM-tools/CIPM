package cipm.consistency.fluentapi.gen.rootapi;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.extensions.FluentAPIWaitForMarkExtension;
import cipm.consistency.fluentapi.gen.FluentAPIGeneralParameterGenerator;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIWaitForMarkMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String waitForMarkMethodBody = FluentAPIMethodsUtil
			.joinLOC(
					FluentAPIWaitForMarkExtension.class.getName() + ".addTask("
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ", "
							+ ModelConstants.GeneralParameters.WAIT_FOR_MARK_TASK_PARAMETER_NAME.get() + ")",
					"return this");

	public EOperation generateAllWaitForMarkMethods(FluentAPIGenerationContext context) {
		var taskParam = FluentAPIGeneralParameterGenerator.getWaitForMarkTaskParam(context);
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.WaitForMark.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, waitForMarkMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.WaitForMark.DOC.get());
		FluentAPIGenerationUtil.addEParameters(op, FluentAPIGeneralParameterGenerator.getMarkKeyParam(), taskParam);
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.WaitForMark.NAME.get(),
				ModelConstants.FluentAPI.WaitForMark.SUMMARY.get());
	}
}
