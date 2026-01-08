package cipm.consistency.runtime.pipeline.validation;

import java.util.List;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.health.AbstractHealthStateComponent;
import cipm.consistency.base.core.health.HealthState;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.metric.IValidationMetric;
import cipm.consistency.runtime.pipeline.validation.eval.ValidationDataExtractor;
import cipm.consistency.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

@Component
public class ValidationFeedbackComponent extends AbstractHealthStateComponent implements IValidationProcessor {
	@Autowired
	private HeadlessPCMSimulator simulator;

	@Autowired
	private ValidationDataExtractor extractor;

	@Autowired
	private List<IValidationMetric<?>> metrics;

	private boolean workingBefore = false;
	private boolean initial = true;

	public ValidationFeedbackComponent() {
		super(HealthStateObservedComponent.VALIDATION_CONTROLLER, HealthStateObservedComponent.CONFIGURATION);
	}

	@Scheduled(fixedRate = 10000L, initialDelay = 0)
	public void checkAvailability() {
		if (checkPreconditions()) {
			boolean reachable = simulator.isReachable();
			if (reachable && !workingBefore) {
				super.removeAllProblems();
				super.updateState();
				workingBefore = true;
				initial = false;
			} else if (!reachable && (workingBefore || initial)) {
				super.reportError(
						"Headless PCM simulator is not reachable. Please check your configuration and/or the availability of the headless simulator.");
				super.updateState();
				workingBefore = false;
				initial = false;
			}
		}
	}

	@Override
	public ValidationData process(InMemoryPCM instance, List<PCMContextRecord> monitoringData, String taskName) {
		// 1. simulate it
		InMemoryResultRepository analysisResults = simulator.simulateBlocking(instance, taskName);
		if (analysisResults == null) {
			return new ValidationData();
		}

		// 2. enrich with data
		ValidationData preparedData = extractor.extractValidationData(analysisResults, instance, monitoringData);

		// 3. derive metrics
		preparedData.getValidationPoints().stream().forEach(valPoint -> {
			metrics.forEach(metric -> {
				if (metric.isTarget(valPoint)) {
					ValidationMetricValue result = metric.calculate(valPoint);
					if (result != null)
						valPoint.getMetricValues().add(result);
				}
			});
		});

		return preparedData;
	}

	@Override
	public void clearSimulationData() {
		simulator.clearAllSimulationData();
	}

	@Override
	protected void onMessage(HealthStateObservedComponent source, HealthState state) {
		// nothing to do here because no deps
	}
}