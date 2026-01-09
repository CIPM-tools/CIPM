package cipm.consistency.runtime.pipeline.validation.data;

import java.util.List;

import org.pcm.headless.shared.data.results.PlainMeasuringPoint;
import org.pcm.headless.shared.data.results.PlainMetricDescription;

import com.google.common.collect.Lists;

// @Builder
public class ValidationPoint {

	private String id;

	private String serviceId;

	private PlainMeasuringPoint measuringPoint;
	private PlainMetricDescription metricDescription;

	private TimeValueDistribution analysisDistribution;
	private TimeValueDistribution monitoringDistribution;

	private List<ValidationMetricValue> metricValues = Lists.newArrayList();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public PlainMeasuringPoint getMeasuringPoint() {
		return measuringPoint;
	}

	public void setMeasuringPoint(PlainMeasuringPoint measuringPoint) {
		this.measuringPoint = measuringPoint;
	}

	public PlainMetricDescription getMetricDescription() {
		return metricDescription;
	}

	public void setMetricDescription(PlainMetricDescription metricDescription) {
		this.metricDescription = metricDescription;
	}

	public TimeValueDistribution getAnalysisDistribution() {
		return analysisDistribution;
	}

	public void setAnalysisDistribution(TimeValueDistribution analysisDistribution) {
		this.analysisDistribution = analysisDistribution;
	}

	public TimeValueDistribution getMonitoringDistribution() {
		return monitoringDistribution;
	}

	public void setMonitoringDistribution(TimeValueDistribution monitoringDistribution) {
		this.monitoringDistribution = monitoringDistribution;
	}

	public List<ValidationMetricValue> getMetricValues() {
		return metricValues;
	}

	public void setMetricValues(List<ValidationMetricValue> metricValues) {
		this.metricValues = metricValues;
	}
}
