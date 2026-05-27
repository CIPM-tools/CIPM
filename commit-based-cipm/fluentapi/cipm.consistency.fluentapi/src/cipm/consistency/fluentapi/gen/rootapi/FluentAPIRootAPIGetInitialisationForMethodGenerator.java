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

public class FluentAPIRootAPIGetInitialisationForMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String getInitialisationMethodBodyTemplate = FluentAPIMethodsUtil
			// %s: Initialisation class
			// %s: EClass / class / EObject parameter name
			.joinLOC("return (%s)" + FluentEObjectAPIMethods.class.getName() + ".getInitialisationForX(this, %s)");

	public List<EOperation> getAllInitialisationForMethods(FluentAPIGenerationContext context) {
		var ops = new ArrayList<EOperation>();
		ops.add(getInitialisationForEClassMethod(context));
		ops.add(getInitialisationForClassMethod(context));
		ops.add(getInitialisationForEObjectMethod(context));
		return ops;
	}

	private EOperation getInitialisationForEClassMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getArbitraryEClassParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.GetInitialisationFor.NAME.get(),
				context.getInitSuperECls());

		FluentAPIGenerationUtil.addBody(op,
				String.format(getInitialisationMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						param.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetInitialisationFor.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);

		return op;
	}

	private EOperation getInitialisationForClassMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getArbitraryClassParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.GetInitialisationFor.NAME.get(),
				context.getInitSuperECls());

		FluentAPIGenerationUtil.addBody(op,
				String.format(getInitialisationMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						param.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetInitialisationFor.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);

		return op;
	}

	private EOperation getInitialisationForEObjectMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getEObjectParam();

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.GetInitialisationFor.NAME.get(),
				context.getInitSuperECls());

		FluentAPIGenerationUtil.addBody(op,
				String.format(getInitialisationMethodBodyTemplate,
						FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, context.getInitSuperECls()),
						param.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetInitialisationFor.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);

		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		// Leave it empty, since this method is not intended to be used from outside
		return Map.of();
	}
}
