package cipm.consistency.fluentapi.gen.superinit;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPISuperInitialisationNewElementMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String newElementMethodBody = FluentAPIMethodsUtil.joinLOC("return this");

	public EOperation generateNewElementMethod(FluentAPIGenerationContext context) {
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.SuperInitialisation.NewElement.NAME.get(),
				context.getInitSuperECls());
		FluentAPIGenerationUtil.addBody(op, newElementMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.SuperInitialisation.NewElement.DOC.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.NewElement.NAME.get(),
				ModelConstants.SuperInitialisation.NewElement.SUMMARY.get());
	}
}
