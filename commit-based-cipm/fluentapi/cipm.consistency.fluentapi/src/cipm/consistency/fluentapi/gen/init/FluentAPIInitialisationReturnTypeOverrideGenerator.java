package cipm.consistency.fluentapi.gen.init;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.FluentAPIParameterUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * Generates overridden methods with more specific return types.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIInitialisationReturnTypeOverrideGenerator {
	private static final Pattern initReturnTypeOverridePattern = Pattern.compile(String.join("|",
			new String[] { ModelConstants.SuperInitialisation.Reset.NAME.get(),
					ModelConstants.FluentAPI.DropInitialisation.NAME.get(),
					ModelConstants.FluentAPI.WaitForMark.NAME.get(), ModelConstants.SuperInitialisation.Mark.NAME.get(),
					ModelConstants.SuperInitialisation.Unmark.NAME.get(), }));

	private static final Pattern initialisedElementReturnTypeOverridePattern = Pattern
			.compile(ModelConstants.SuperInitialisation.CreateNow.NAME.get());

	private static final String methodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			// %s: Returned class
			// %s: Method call string (with parameters in brackets)
			"return (%s) %s");

	private EOperation generateInitReturnTypeOverridingMethod(List<EOperation> overridingOps, EOperation opToOverride,
			EClass initType, FluentAPIGenerationContext context) {
		var serialisedOriginalMethodCall = "super." + opToOverride.getName() + "("
				+ FluentAPIParameterUtil.getSerialisedParametersFor(opToOverride) + ")";
		var copier = new EcoreUtil.Copier();
		var overridingOp = (EOperation) copier.copy(opToOverride);
		copier.copyReferences();

		if (FluentAPIParameterUtil.hasClashingMethods(overridingOps, overridingOp))
			return null;

		// Adjust return type
		overridingOp.setEType(initType);

		// Adjust body
		FluentAPIGenerationUtil.addBody(overridingOp, String.format(methodBodyTemplate,
				FluentAPIGenerationUtil.getFullyQualifiedEClassName(context, initType), serialisedOriginalMethodCall));

		return overridingOp;
	}

	private EOperation generateInitialisedElementReturnTypeOverridingMethod(List<EOperation> overridingOps,
			EOperation opToOverride, EClass initialisedElemType) {
		var serialisedOriginalMethodCall = "super." + opToOverride.getName() + "("
				+ FluentAPIParameterUtil.getSerialisedParametersFor(opToOverride) + ")";
		var copier = new EcoreUtil.Copier();
		var overridingOp = (EOperation) copier.copy(opToOverride);
		copier.copyReferences();

		if (FluentAPIParameterUtil.hasClashingMethods(overridingOps, overridingOp))
			return null;

		// Adjust return type
		overridingOp.setEType(initialisedElemType);

		// Adjust body
		FluentAPIGenerationUtil.addBody(overridingOp, String.format(methodBodyTemplate,
				initialisedElemType.getInstanceTypeName(), serialisedOriginalMethodCall));

		return overridingOp;
	}

	public List<EOperation> generateMethodsWithOverridingReturnType(FluentAPIGenerationContext context, EClass initType,
			EClass initialisedElemType) {
		var overridingOps = new ArrayList<EOperation>();
		for (var opToOverride : context.getInitSuperECls().getEOperations()) {
			// Skip non-concrete return types (such as type parameters)
			if (opToOverride.getEGenericType() != null && opToOverride.getEGenericType().getETypeParameter() != null)
				continue;
			if (initReturnTypeOverridePattern.matcher(opToOverride.getName()).matches()) {
				overridingOps
						.add(generateInitReturnTypeOverridingMethod(overridingOps, opToOverride, initType, context));
			} else if (initialisedElementReturnTypeOverridePattern.matcher(opToOverride.getName()).matches()) {
				overridingOps.add(generateInitialisedElementReturnTypeOverridingMethod(overridingOps, opToOverride,
						initialisedElemType));
			}
		}
		return overridingOps;
	}
}
