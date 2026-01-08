package cipm.consistency.runtime.pipeline.validation.data.metric.impl;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.springframework.stereotype.Service;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

@Service
public class ServiceCallKSTestMetric extends ServiceCallMetric {

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 2
					&& point.getMonitoringDistribution().getYValues().size() >= 2) {
				return new DoubleMetricValue(
						new KolmogorovSmirnovTest().kolmogorovSmirnovStatistic(point.getAnalysisDistribution().yAxis(),
								point.getMonitoringDistribution().yAxis()),
						ValidationMetricType.KS_TEST, false);
			}
		}
		return null;
	}

}
