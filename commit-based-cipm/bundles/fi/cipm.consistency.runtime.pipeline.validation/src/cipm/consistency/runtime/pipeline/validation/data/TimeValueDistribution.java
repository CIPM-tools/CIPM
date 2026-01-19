package cipm.consistency.runtime.pipeline.validation.data;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

// This class was annotated with lombok.Data.
public class TimeValueDistribution {
	private List<Double> xValues;
	private List<Double> yValues;

	public TimeValueDistribution() {
		xValues = Lists.newArrayList();
		yValues = Lists.newArrayList();
	}

	public void addValueX(double val) {
		xValues.add(val);
	}

	public void addValueY(double val) {
		yValues.add(val);
	}

	public void swapAxis() {
		List<Double> copy = xValues;
		xValues = yValues;
		yValues = copy;
	}

	public double[] xAxis() {
		return Doubles.toArray(xValues);
	}

	public double[] yAxis() {
		return Doubles.toArray(yValues);
	}

	public List<Double> getXValues() {
		return xValues;
	}

	public void setXValues(List<Double> xValues) {
		this.xValues = xValues;
	}

	public List<Double> getYValues() {
		return yValues;
	}

	public void setYValues(List<Double> yValues) {
		this.yValues = yValues;
	}
}
