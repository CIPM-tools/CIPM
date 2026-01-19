package org.pcm.headless.shared.data.config;

import java.util.UUID;

import org.pcm.headless.shared.data.ESimulationType;

//import lombok.AllArgsConstructor;
//import lombok.Builder;

//@Builder
//@AllArgsConstructor
public class HeadlessSimulationConfig {

//	@Builder.Default
	private String experimentName = UUID.randomUUID().toString();

//	@Builder.Default
	private long simulationTime = 150000;

//	@Builder.Default
	private long maximumMeasurementCount = 10000;

//	@Builder.Default
	private boolean useFixedSeed = false;

//	@Builder.Default
	private boolean parallelizeRepetitions = false;

//	@Builder.Default
	private int repetitions = 1;

//	@Builder.Default
	private ESimulationType type = ESimulationType.SIMULIZAR;

//	@Builder.Default
	private String simuComStoragePath = null;
	
	public HeadlessSimulationConfig() {}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public long getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(long simulationTime) {
		this.simulationTime = simulationTime;
	}

	public long getMaximumMeasurementCount() {
		return maximumMeasurementCount;
	}

	public void setMaximumMeasurementCount(long maximumMeasurementCount) {
		this.maximumMeasurementCount = maximumMeasurementCount;
	}

	public boolean isUseFixedSeed() {
		return useFixedSeed;
	}

	public void setUseFixedSeed(boolean useFixedSeed) {
		this.useFixedSeed = useFixedSeed;
	}

	public boolean isParallelizeRepetitions() {
		return parallelizeRepetitions;
	}

	public void setParallelizeRepetitions(boolean parallelizeRepetitions) {
		this.parallelizeRepetitions = parallelizeRepetitions;
	}

	public int getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

	public ESimulationType getType() {
		return type;
	}

	public void setType(ESimulationType type) {
		this.type = type;
	}

	public String getSimuComStoragePath() {
		return simuComStoragePath;
	}

	public void setSimuComStoragePath(String simuComStoragePath) {
		this.simuComStoragePath = simuComStoragePath;
	}

}
