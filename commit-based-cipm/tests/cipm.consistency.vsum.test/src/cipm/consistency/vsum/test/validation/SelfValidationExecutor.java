package cipm.consistency.vsum.test.validation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.ValidationFeedbackComponent;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;

public class SelfValidationExecutor {
	private static final String SERVICE_NAME = "confirmOrder";
	private List<PCMContextRecord> monitoringData;
	private List<SelfValidationStep> validationSteps = new ArrayList<>();
	private ValidationData lastValidationResult;
	private ValidationFeedbackComponent selfValidationComponent;

	public SelfValidationExecutor(List<PCMContextRecord> data) throws Exception {
		this.monitoringData = data;
		this.selfValidationComponent = new ValidationFeedbackComponent();
	}
	
	public void validate(InMemoryPCM pcm, int iteration) throws Exception {
		var stepResult = new SelfValidationStep();
		stepResult.setIteration(iteration);
		
		long time = System.currentTimeMillis();
		var validationResult = selfValidationComponent.process(pcm, this.monitoringData, "self-validation-" + iteration);
		time = System.currentTimeMillis() - time;
		stepResult.setExecutionTime(time);
		selfValidationComponent.clearSimulationData();
		
		var filteredValidationResult = new ValidationData();
		validationResult.getValidationPoints().forEach(vdPoint -> {
			if (!vdPoint.getMeasuringPoint().getStringRepresentation().contains(SERVICE_NAME)) {
				return;
			}
			stepResult.setMetrics(vdPoint.getMetricValues());
			filteredValidationResult.setValidationPoints(List.of(vdPoint));
		});
		
		if (this.lastValidationResult != null) {
			filteredValidationResult.calculateImprovmentScore(this.lastValidationResult);
			stepResult.setImprovementScore(
				filteredValidationResult.getValidationImprovementScore().orElse(Double.NaN)
			);
		}
		
		this.validationSteps.add(stepResult);
		this.lastValidationResult = filteredValidationResult;
	}
	
	public void saveResults(Path file) throws IOException {
		JsonMapper mapper = new JsonMapper();
		mapper.writeValue(file.toFile(), this.validationSteps);
	}
}
