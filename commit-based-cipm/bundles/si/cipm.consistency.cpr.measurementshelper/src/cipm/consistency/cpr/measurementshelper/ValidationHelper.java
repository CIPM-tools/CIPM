package cipm.consistency.cpr.measurementshelper;

import java.util.List;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.runtime.pipeline.validation.ValidationFeedbackComponent;

public class ValidationHelper {
	private ValidationFeedbackComponent validator;
	
	public ValidationHelper() {
		try {
			this.validator = new ValidationFeedbackComponent();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public boolean validate(InMemoryPCM pcm) {
		var copy = pcm.copyDeep();
    	//copy.getRepository().getComponents__Repository();
    	var result = this.validator.process(copy, List.of(), "reactions-validation");
    	for (var vd : result.getValidationPoints()) {
    		
    	}
		return false;
	}
}
