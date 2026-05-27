package cipm.consistency.fluentapi.gen.rootapi;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIGetOngoingInitsMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String getOngoingInitsMethodBody = FluentAPIMethodsUtil
			// %s: Fully qualified super initialisation class name
			.joinLOC("return " + FluentAPIInitialisationStorage.class.getName()
					+ ".getOngoingInitialisations().stream().map((i) -> (%s) i).collect("
					+ java.util.stream.Collectors.class.getName() + ".toList())");

	public EOperation generateGetOngoingInitsMethod(FluentAPIGenerationContext context) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.GetOngoingInitialisations.NAME.get(),
				FluentAPIGenerationUtil.generateEGenericTypeWithTypeArgument(context, java.util.List.class,
						FluentAPIGenerationUtil.generateEGenericTypeWithClassifier(context.getInitSuperECls())));
		FluentAPIGenerationUtil.addBody(op, String.format(getOngoingInitsMethodBody,
				FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls())));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetOngoingInitialisations.SUMMARY.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.GetOngoingInitialisations.NAME.get(),
				ModelConstants.FluentAPI.GetOngoingInitialisations.SUMMARY.get());
	}
}
