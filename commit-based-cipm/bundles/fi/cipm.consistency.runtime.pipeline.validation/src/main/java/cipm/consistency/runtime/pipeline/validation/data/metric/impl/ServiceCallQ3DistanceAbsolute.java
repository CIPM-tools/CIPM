package cipm.consistency.runtime.pipeline.validation.data.metric.impl;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.stereotype.Service;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

@Service
public class ServiceCallQ3DistanceAbsolute extends ServiceCallMetric {

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 1
					&& point.getMonitoringDistribution().getYValues().size() >= 1) {
				Percentile q1 = new Percentile(0.75);
				double distance = Math.abs(q1.evaluate(point.getAnalysisDistribution().yAxis())
						- q1.evaluate(point.getMonitoringDistribution().yAxis()));
				return new DoubleMetricValue(distance, ValidationMetricType.Q3_DISTANCE, false);
			}
		}
		return null;
	}

}
