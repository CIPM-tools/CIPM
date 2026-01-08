package cipm.consistency.runtime.pipeline.validation.data.metric;

import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;

public interface IValidationMetric<T extends ValidationMetricValue> {

	public boolean isTarget(ValidationPoint point);

	public T calculate(ValidationPoint point);

}
