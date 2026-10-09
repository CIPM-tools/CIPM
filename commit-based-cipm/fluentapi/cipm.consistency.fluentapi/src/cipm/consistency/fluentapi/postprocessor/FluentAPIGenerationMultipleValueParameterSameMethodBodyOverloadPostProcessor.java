package cipm.consistency.fluentapi.postprocessor;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * An implementation of
 * {@link FluentAPIGenerationMultipleValueParameterPostProcessor} that uses a
 * the same method body as the original EOperation in the overloading
 * EOperations' method bodies.
 * <p>
 * <p>
 * Note: Even though the original and overloading methods have the same body,
 * their behaviour may differ, if changing the EParameter in overloading methods
 * results in calling different methods (despite having identical method
 * bodies).
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationMultipleValueParameterSameMethodBodyOverloadPostProcessor
		extends FluentAPIGenerationMultipleValueParameterPostProcessor {
	private static final Pattern newMethodPatternToSkip = Pattern.compile(String.join("|",
			ModelConstants.FluentAPI.New.TOP_NAME.get(), ModelConstants.SuperInitialisation.NewElement.NAME.get()));

	private static final Pattern newMethodPatternToOverload = Pattern
			.compile(ModelConstants.FluentAPI.New.NAME.getFor(".*"));

	private static final Pattern waitForMarkMethodPatternToOverload = Pattern
			.compile(ModelConstants.FluentAPI.WaitForMark.NAME.get());

	private static final Pattern paramNamePatternToOverload = Pattern
			.compile(String.join("|", ModelConstants.GeneralParameters.FEATURE_VALUE_PARAMETER_NAME.get(),
					ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()));

	public FluentAPIGenerationMultipleValueParameterSameMethodBodyOverloadPostProcessor(
			FluentAPIGenerationContext context, List<EClass> eClsScope) {
		super(context, eClsScope);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Does not modify overloadingOp, since its method body should remain the same.
	 */
	@Override
	protected EOperation overloadMethodBody(EOperation overloadingOp, EParameter newParam) {
		return overloadingOp;
	}

	@Override
	protected boolean shouldOverloadParameter(EParameter param) {
		return paramNamePatternToOverload.matcher(param.getName()).matches();
	}

	@Override
	protected boolean shouldOverloadMethod(EOperation op) {
		return ((!newMethodPatternToSkip.matcher(op.getName()).matches()
				&& newMethodPatternToOverload.matcher(op.getName()).matches()
				&& op.getEAnnotations().get(0).getDetails().get(ModelConstants.GEN_MODEL_BODY_KEY.get())
						.contains("." + ModelConstants.Initialiation.WithAdded.NAME.getEmpty()))
				|| (waitForMarkMethodPatternToOverload.matcher(op.getName()).matches()))
				&& op.getEParameters().stream().anyMatch(this::shouldOverloadParameter);
	}
}
