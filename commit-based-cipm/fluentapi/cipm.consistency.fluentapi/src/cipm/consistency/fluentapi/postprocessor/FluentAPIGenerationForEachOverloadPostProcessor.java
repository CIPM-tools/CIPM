package cipm.consistency.fluentapi.postprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * An implementation of
 * {@link FluentAPIGenerationMultipleValueParameterPostProcessor} that uses a
 * for-each loop in overloading EOperations' method bodies, effectively calling
 * the original EOperation for each given element in its parameter.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationForEachOverloadPostProcessor
		extends FluentAPIGenerationMultipleValueParameterPostProcessor {

	private static final Pattern methodNamePatternToOverload = Pattern.compile(String.join("|",
			ModelConstants.FluentAPI.WithAddedFeat.NAME.get(), ModelConstants.FluentAPI.WithRemovedFeat.NAME.get(),
			ModelConstants.Initialiation.WithAdded.NAME.getFor(".*"),
			ModelConstants.Initialiation.WithRemoved.NAME.getFor(".*")));

	private static final Pattern paramNamePatternToOverload = Pattern
			.compile(String.join("|", ModelConstants.GeneralParameters.FEATURE_VALUE_PARAMETER_NAME.get(),
					ModelConstants.Initialiation.With.PARAMETER_NAME.get(),
					ModelConstants.Initialiation.WithAdded.PARAMETER_NAME.get(),
					ModelConstants.Initialiation.WithRemoved.PARAMETER_NAME.get()));

	private static final String iterationParamName = "e";
	/**
	 * The method body template for method overloads with collections / lists /
	 * arrays of feature values as parameters.
	 */
	private static final String multipleValueMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			"for (var " + iterationParamName + " : %s) this.%s(%s)",
			// %s: Overloaded parameter name
			// %s: Original method name
			// %s: Serialised arguments
			"return this");

	public FluentAPIGenerationForEachOverloadPostProcessor(FluentAPIGenerationContext context, List<EClass> eClsScope) {
		super(context, eClsScope);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Uses a for-each loop in the method body of overloadingOp and calls the
	 * original EOperation for each element in its parameter.
	 */
	@Override
	protected EOperation overloadMethodBody(EOperation overloadingOp, EParameter newParam) {
		var serialisedArguments = new ArrayList<String>();
		for (var p : overloadingOp.getEParameters()) {
			if (p != newParam) {
				serialisedArguments.add(p.getName());
			} else {
				serialisedArguments.add(iterationParamName);
			}
		}
		FluentAPIGenerationUtil.addBody(overloadingOp,
				String.format(multipleValueMethodBodyTemplate, newParam.getName(), overloadingOp.getName(),
						String.join(",", serialisedArguments.toArray(String[]::new))));
		return overloadingOp;
	}

	@Override
	protected boolean shouldOverloadParameter(EParameter param) {
		return paramNamePatternToOverload.matcher(param.getName()).matches();
	}

	@Override
	protected boolean shouldOverloadMethod(EOperation op) {
		return methodNamePatternToOverload.matcher(op.getName()).matches()
				&& op.getEParameters().stream().anyMatch(this::shouldOverloadParameter)
				&& op.getEType().equals(op.getEContainingClass());
	}
}
