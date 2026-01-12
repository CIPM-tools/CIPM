package cipm.consistency.runtime.pipeline.validation;

import java.util.List;

import org.pcm.headless.shared.data.results.InMemoryResultRepository;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationMetricValue;
import cipm.consistency.runtime.pipeline.validation.data.metric.IValidationMetric;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceRelative;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallKSTestMetric;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallMedianDistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallQ1DistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallQ3DistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallWassersteinDistanceMetric;
import cipm.consistency.runtime.pipeline.validation.eval.ValidationDataExtractor;
import cipm.consistency.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

public class ValidationFeedbackComponent implements IValidationProcessor {
	private HeadlessPCMSimulator simulator = new HeadlessPCMSimulator();

	private ValidationDataExtractor extractor = new ValidationDataExtractor();

	private List<IValidationMetric<?>> metrics = List.of(
			new ServiceCallAverageDistanceAbsolute(),
			new ServiceCallAverageDistanceRelative(),
			new ServiceCallKSTestMetric(),
			new ServiceCallMedianDistanceAbsolute(),
			new ServiceCallQ1DistanceAbsolute(),
			new ServiceCallQ3DistanceAbsolute(),
			new ServiceCallWassersteinDistanceMetric()
		);

	private boolean workingBefore = false;
	private boolean initial = true;
	
	public ValidationFeedbackComponent() throws Exception {
		simulator.initialize();
	}

	// @Scheduled(fixedRate = 10000L, initialDelay = 0)
//	public void checkAvailability() {
//		if (checkPreconditions()) {
//			boolean reachable = simulator.isReachable();
//			if (reachable && !workingBefore) {
//				super.removeAllProblems();
//				super.updateState();
//				workingBefore = true;
//				initial = false;
//			} else if (!reachable && (workingBefore || initial)) {
//				super.reportError(
//						"Headless PCM simulator is not reachable. Please check your configuration and/or the availability of the headless simulator.");
//				super.updateState();
//				workingBefore = false;
//				initial = false;
//			}
//		}
//	}

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
}