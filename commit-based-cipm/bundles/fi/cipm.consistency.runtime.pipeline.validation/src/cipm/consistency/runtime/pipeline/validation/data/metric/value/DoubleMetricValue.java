package cipm.consistency.runtime.pipeline.validation.data.metric.value;

import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.metric.ValidationMetricType;

public class DoubleMetricValue extends ValidationMetricValue {
	private double value;
	private boolean ascending;
	private ValidationMetricType type;

	public DoubleMetricValue(double value, ValidationMetricType type) {
		this(value, type, true);
	}

	public DoubleMetricValue(double value, ValidationMetricType type, boolean orderedAscending) {
		this.value = value;
		this.ascending = orderedAscending;
		this.type = type;
	}

	@Override
	public double compare(Object other) {
		if (other instanceof DoubleMetricValue) {
			return ascending ? this.value - ((DoubleMetricValue) other).value
					: ((DoubleMetricValue) other).value - this.value;
		}
		return 0;
	}

	@Override
	public ValidationMetricType type() {
		return type;
	}

	@Override
	public Object value() {
		return value;
	}

	public double getDoubleValue() {
		return value;
	}

}
