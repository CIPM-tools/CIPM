package cipm.consistency.vsum.test.validation;

import java.util.ArrayList;
import java.util.List;

import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;

public class SelfValidationStep {
	private int iteration;
	private long executionTime;
	private double improvementScore;
	private List<ValidationMetricValue> metrics = new ArrayList<>();
	
	public int getIteration() {
		return iteration;
	}
	
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	public double getImprovementScore() {
		return this.improvementScore;
	}
	
	public void setImprovementScore(double score) {
		this.improvementScore = score;
	}
	
	public List<ValidationMetricValue> getMetrics() {
		return metrics;
	}
	
	public void setMetrics(List<ValidationMetricValue> metrics) {
		this.metrics.clear();
		this.metrics.addAll(metrics);
	}
}
