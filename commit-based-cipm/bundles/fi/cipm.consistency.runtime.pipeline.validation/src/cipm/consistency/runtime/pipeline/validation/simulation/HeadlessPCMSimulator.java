package cipm.consistency.runtime.pipeline.validation.simulation;

import java.util.concurrent.CountDownLatch;

import org.pcm.headless.api.client.ISimulationResultListener;
import org.pcm.headless.api.client.PCMHeadlessClient;
import org.pcm.headless.api.client.SimulationClient;
import org.pcm.headless.shared.data.ESimulationType;
import org.pcm.headless.shared.data.config.HeadlessSimulationConfig;
import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cipm.consistency.base.core.config.ConfigurationContainer;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import lombok.extern.java.Log;

@Log
@Component
public class HeadlessPCMSimulator implements IPCMSimulator, InitializingBean {
	private static final long TIMEOUT_VFL = 120000;

	@Autowired
	private ConfigurationContainer config;

	private PCMHeadlessClient client;

	private boolean reachable;

	@Scheduled(fixedRate = 30000L)
	public void checkAvailability() {
		if (client != null) {
			this.reachable = client.isReachable(TIMEOUT_VFL);
		}
	}

	public boolean isReachable() {
		return reachable;
	}

	@Override
	public void simulate(InMemoryPCM pcm, String name, ISimulationResultListener listener) {
		try {
			// set properties
			SimulationClient simulationClient = client.prepareSimulation();
			if (simulationClient != null) {
				simulationClient.setSimulationConfig(HeadlessSimulationConfig.builder().type(ESimulationType.SIMUCOM)
						.experimentName(name).repetitions(1).maximumMeasurementCount(config.getVfl().getMeasurements())
						.simulationTime(config.getVfl().getSimulationTime()).build());
				simulationClient.setRepository(pcm.getRepository());
				simulationClient.setSystem(pcm.getSystem());
				simulationClient.setResourceEnvironment(pcm.getResourceEnvironmentModel());
				simulationClient.setAllocation(pcm.getAllocationModel());
				simulationClient.setUsageModel(pcm.getUsageModel());

				// transitive closure & sync
				simulationClient.createTransitiveClosure();
				simulationClient.sync();

				// start simulation
				simulationClient.executeSimulation(listener, TIMEOUT_VFL * 20);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public InMemoryResultRepository simulateBlocking(InMemoryPCM pcm, String name) {
		// start simulation
		CountDownLatch signal = new CountDownLatch(1);
		ResultValueWrapper wrapper = new ResultValueWrapper();

		this.simulate(pcm, name, res -> {
			wrapper.setValue(res);
			signal.countDown();
		});

		try {
			signal.await();
		} catch (InterruptedException e) {
			log.warning("Thread interrupted while waiting on the simulation restults.");
		}

		return wrapper.isSet() ? wrapper.res : null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		config.getVfl().registerChangeListener(v -> {
			updatePCMHeadlessClient();
		});
		updatePCMHeadlessClient();
	}

	private void updatePCMHeadlessClient() {
		client = new PCMHeadlessClient(buildBackendUrl(config.getVfl().getUrl(), config.getVfl().getPort()));

		// check if it is available
		if (client.isReachable(TIMEOUT_VFL)) {
			reachable = true;
			log.info("Backend for VFL is reachable.");
		} else {
			reachable = false;
			log.warning("Backend for VFL seems to be offline.");
		}
	}

	private String buildBackendUrl(String url, int port) {
		StringBuilder res = new StringBuilder();
		if (!url.startsWith("http://")) {
			res.append("http://");
		}
		res.append(url);
		res.append(":");
		res.append(port);
		res.append("/");

		return res.toString();
	}

	private class ResultValueWrapper {
		private InMemoryResultRepository res = null;

		void setValue(InMemoryResultRepository result) {
			res = result;
		}

		boolean isSet() {
			return res != null;
		}
	}

	public void clearAllSimulationData() {
		if (client.isReachable(TIMEOUT_VFL)) {
			client.clear();
		}
	}

}
