package cipm.consistency.fluentapi.gen.superinit;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPISuperInitialisationResetOperationGenerator implements IFluentAPIMethodGenerator {
	private static final String resetMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			ModelConstants.SuperInitialisation.CurrentElement.NAME.thisSetterCall("null"),
			//
			"return this");

	public EOperation generateResetInitialisationMethod(EClass initType) {
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.SuperInitialisation.Reset.NAME.get(),
				initType);
		FluentAPIGenerationUtil.addBody(op, resetMethodBodyTemplate);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.SuperInitialisation.Reset.DOC.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.Reset.NAME.get(),
				ModelConstants.SuperInitialisation.Reset.SUMMARY.get());
	}
}
