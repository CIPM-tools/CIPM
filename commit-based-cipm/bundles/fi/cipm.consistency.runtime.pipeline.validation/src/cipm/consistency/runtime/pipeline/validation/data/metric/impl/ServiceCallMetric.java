package cipm.consistency.runtime.pipeline.validation.data.metric.impl;

import org.pcm.headless.shared.data.results.MeasuringPointType;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.IValidationMetric;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

public abstract class ServiceCallMetric implements IValidationMetric<DoubleMetricValue> {

	@Override
	public boolean isTarget(ValidationPoint point) {
		if (point.getMeasuringPoint().getType() == MeasuringPointType.ASSEMBLY_OPERATION
				|| point.getMeasuringPoint().getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
			return true;
		}
		return false;
	}

}
