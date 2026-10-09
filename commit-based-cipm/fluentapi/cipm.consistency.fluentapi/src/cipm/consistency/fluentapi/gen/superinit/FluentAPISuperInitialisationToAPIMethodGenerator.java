package cipm.consistency.fluentapi.gen.superinit;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPISuperInitialisationToAPIMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String toAPIMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC("return " + ModelConstants.SuperInitialisation.RootAPI.NAME.thisGetterCall());

	public EOperation generateToAPIMethod(FluentAPIGenerationContext context) {
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.SuperInitialisation.ToAPI.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, toAPIMethodBodyTemplate);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.SuperInitialisation.ToAPI.DOC.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.ToAPI.NAME.get(),
				ModelConstants.SuperInitialisation.ToAPI.SUMMARY.get());
	}
}
