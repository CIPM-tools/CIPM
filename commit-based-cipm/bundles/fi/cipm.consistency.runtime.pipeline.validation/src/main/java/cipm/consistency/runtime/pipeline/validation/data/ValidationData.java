package cipm.consistency.runtime.pipeline.validation.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import cipm.consistency.runtime.pipeline.validation.data.metric.value.DoubleMetricValue;
import lombok.Data;

@Data
public class ValidationData {

	private List<ValidationPoint> validationPoints;

	private Optional<Double> validationImprovementScore = Optional.empty();

	public boolean isEmpty() {
		return validationPoints == null || validationPoints.isEmpty();
	}

	public void calculateImprovmentScore(ValidationData reference) {
		if (reference.isEmpty() || this.isEmpty()) {
			return;
		}

		double sum = 0;
		int correspondences = 0;

		Map<String, ValidationPoint> referenceValidationPointMapping = Maps.newHashMap();
		for (ValidationPoint p : reference.getValidationPoints()) {
			referenceValidationPointMapping.put(p.getId(), p);
		}

		for (ValidationPoint pointA : validationPoints) {
			if (referenceValidationPointMapping.containsKey(pointA.getId())) {
				List<ValidationMetricValue> metricsA = pointA.getMetricValues();
				List<ValidationMetricValue> metricsB = referenceValidationPointMapping.get(pointA.getId())
						.getMetricValues();

				for (ValidationMetricValue metricA : metricsA) {
					Optional<ValidationMetricValue> metricB = metricsB.stream().filter(b -> b.type() == metricA.type())
							.findFirst();

					if (metricB.isPresent()) {
						if (metricA instanceof DoubleMetricValue && metricB.get() instanceof DoubleMetricValue) {
							DoubleMetricValue typedMetricA = (DoubleMetricValue) metricA;
							DoubleMetricValue typedMetricB = (DoubleMetricValue) metricB.get();

							double valA = typedMetricA.getDoubleValue();
							double valB = typedMetricB.getDoubleValue();

							if (valA != 0.0d && valB != 0.0d) {
								double improvement = 1d - ((1d / Math.max(valA, valB)) * Math.min(valA, valB));
								sum += improvement * (typedMetricA.compare(typedMetricB) > 0 ? 1d : -1d);
								correspondences++;
							}
						}
					}
				}
			}
		}

		if (correspondences == 0) {
			return;
		}

		// set improvement score
		validationImprovementScore = Optional.of(sum / (double) correspondences);
	}

}
