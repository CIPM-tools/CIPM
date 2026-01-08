package cipm.consistency.runtime.pipeline.validation.data.metric.impl;

import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.springframework.stereotype.Service;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

@Service
public class ServiceCallAverageDistanceRelative extends ServiceCallMetric {

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 1
					&& point.getMonitoringDistribution().getYValues().size() >= 1) {
				double avg1 = avg(point.getAnalysisDistribution().yAxis());
				double avg2 = avg(point.getMonitoringDistribution().yAxis());

				double distance = Math.abs(avg1 - avg2);
				return new DoubleMetricValue(distance / Math.max(avg1, avg2), ValidationMetricType.AVG_DISTANCE_REL,
						false);
			}
		}
		return null;
	}

	private double avg(double[] all) {
		double sum = 0;
		for (double d : all) {
			sum += d;
		}
		return sum / all.length;
	}

}
