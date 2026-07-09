package cipm.consistency.fluentapi.gen.rootapi;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIClearAllOngoingInitialisationsMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String clearAllOngoingInitsMethodBody = FluentAPIMethodsUtil.joinLOC(
			FluentAPIInitialisationStorage.class.getName() + ".clearAllOngoingInitialisations()", "return this");

	public EOperation generateClearAllOngoingInitsMethod(FluentAPIGenerationContext context) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.ClearAllOngoingInitialisations.NAME.get(), context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, clearAllOngoingInitsMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.FluentAPI.ClearAllOngoingInitialisations.SUMMARY.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.ClearAllOngoingInitialisations.NAME.get(),
				ModelConstants.FluentAPI.ClearAllOngoingInitialisations.SUMMARY.get());
	}
}
