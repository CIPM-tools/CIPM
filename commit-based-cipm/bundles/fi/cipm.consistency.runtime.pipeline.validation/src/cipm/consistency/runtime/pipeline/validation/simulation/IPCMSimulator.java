package cipm.consistency.runtime.pipeline.validation.simulation;

import org.pcm.headless.api.client.ISimulationResultListener;
import org.pcm.headless.shared.data.results.InMemoryResultRepository;

import cipm.consistency.base.shared.pcm.InMemoryPCM;

public interface IPCMSimulator {

	public InMemoryResultRepository simulateBlocking(InMemoryPCM pcm, String name);

	public void simulate(InMemoryPCM pcm, String name, ISimulationResultListener listener);

}
