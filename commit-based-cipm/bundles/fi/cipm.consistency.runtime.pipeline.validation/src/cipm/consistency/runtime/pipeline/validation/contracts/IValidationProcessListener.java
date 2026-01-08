package cipm.consistency.runtime.pipeline.validation.contracts;

import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationState;

public interface IValidationProcessListener {

	public void stateChanged(ValidationState state);

	public void validationFinished(ValidationData data);

}
