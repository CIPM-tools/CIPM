package cipm.consistency.runtime.pipeline.validation;

import java.util.List;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;

public interface IValidationProcessor {

	ValidationData process(InMemoryPCM instance, List<PCMContextRecord> monitoringData, String taskName);

	void clearSimulationData();

}
