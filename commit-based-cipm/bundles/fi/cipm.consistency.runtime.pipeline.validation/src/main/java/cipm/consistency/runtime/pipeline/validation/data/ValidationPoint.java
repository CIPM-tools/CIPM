package cipm.consistency.runtime.pipeline.validation.data;

import java.util.List;

import org.pcm.headless.shared.data.results.PlainMeasuringPoint;
import org.pcm.headless.shared.data.results.PlainMetricDescription;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationPoint {

	private String id;

	private String serviceId;

	private PlainMeasuringPoint measuringPoint;
	private PlainMetricDescription metricDescription;

	private TimeValueDistribution analysisDistribution;
	private TimeValueDistribution monitoringDistribution;

	@Builder.Default
	private List<ValidationMetricValue> metricValues = Lists.newArrayList();

}
