package cipm.consistency.runtime.pipeline.validation.data.metric.impl;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.springframework.stereotype.Service;

import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;
import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;

@Service
public class ServiceCallWassersteinDistanceMetric extends ServiceCallMetric {

	@Override
	public DoubleMetricValue calculate(ValidationPoint point) {
		if (point.getAnalysisDistribution() != null && point.getMonitoringDistribution() != null) {
			if (point.getAnalysisDistribution().getYValues().size() >= 2
					&& point.getMonitoringDistribution().getYValues().size() >= 2) {

				double wsMetric = wsDistance(point.getAnalysisDistribution().yAxis(),
						point.getMonitoringDistribution().yAxis(), false);

				return new DoubleMetricValue(wsMetric, ValidationMetricType.WASSERSTEIN, false);
			}
		}
		return null;
	}

	private double wsDistance(double[] a, double[] b, boolean normed) {
		Arrays.sort(a);
		Arrays.sort(b);

		double minA = DoubleStream.of(a).min().getAsDouble();
		double minB = DoubleStream.of(b).min().getAsDouble();
		double maxA = DoubleStream.of(a).max().getAsDouble();
		double maxB = DoubleStream.of(b).max().getAsDouble();

		int minAB = (int) Math.floor(Math.min(minA, minB));
		int maxAB = (int) Math.ceil(Math.min(maxA, maxB));
		double[] transA = new double[maxAB - minAB];
		double[] transB = new double[maxAB - minAB];
		int currentPos = 0;

		while (currentPos < transA.length) {
			int currentLower = minAB + currentPos;
			int currentUpper = minAB + currentPos + 1;

			transA[currentPos] = (double) DoubleStream.of(a).filter(d -> d >= currentLower && d < currentUpper).count()
					/ (double) a.length;
			transB[currentPos++] = (double) DoubleStream.of(b).filter(d -> d >= currentLower && d < currentUpper)
					.count() / (double) b.length;
		}

		return new EarthMoversDistance().compute(transA, transB) / (normed ? (transA.length - 1) : 1d);

		// for ks test
		// return new KolmogorovSmirnovTest().kolmogorovSmirnovStatistic(a, b);
	}

}
