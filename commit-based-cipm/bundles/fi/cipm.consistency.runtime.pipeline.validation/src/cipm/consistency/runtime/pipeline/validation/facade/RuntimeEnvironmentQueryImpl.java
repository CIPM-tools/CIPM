package cipm.consistency.runtime.pipeline.validation.facade;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import cipm.consistency.base.core.ISpecificModelProvider;
import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.models.runtimeenvironment.REModel.REModelFactory;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import cipm.consistency.base.vsum.facade.CentralVsumFacade;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.base.vsum.manager.VsumManager.VsumChangeSource;

@Component
public class RuntimeEnvironmentQueryImpl implements IRuntimeEnvironmentQueryFacade {
	@Autowired
	private CentralVsumFacade vsum;

	@Autowired
	private ISpecificModelProvider remProvider;

	@Autowired
	private VsumManager vsumManager;

	private Map<String, RuntimeResourceContainer> containerCache = Maps.newHashMap();
	private Map<Pair<String, String>, RuntimeResourceContainerConnection> linkCache = Maps.newHashMap();

	@Override
	public RuntimeResourceContainer getContainerById(String hostId) {
		// the cache should always have this (except unknown side effects)
		return containerCache.get(hostId);
	}

	@Override
	public RuntimeResourceContainerConnection getLinkByIds(String fromId, String toId) {
		return linkCache.get(Pair.of(fromId, toId));
	}

	@Override
	public boolean containsHostId(String hostId) {
		return containerCache.containsKey(hostId);
	}

	@Override
	public RuntimeResourceContainer createResourceContainer(String hostId, String hostName) {
		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostID(hostId);
		nContainer.setHostname(hostName);
		containerCache.put(hostId, nContainer);

		vsumManager.executeTransaction(() -> {
			remProvider.getRuntimeEnvironment().getContainers().add(nContainer);
			vsum.createdObject(nContainer, VsumChangeSource.RUNTIME_ENVIRONMENT);
			return null;
		});

		return nContainer;
	}

	@Override
	public boolean containsLink(String fromId, String toId) {
		return linkCache.containsKey(Pair.of(fromId, toId));
	}

	@Override
	public void createResourceContainerLink(String fromId, String toId) {
		RuntimeResourceContainerConnection nConnection = REModelFactory.eINSTANCE
				.createRuntimeResourceContainerConnection();

		RuntimeResourceContainer containerFrom = this.getContainerById(fromId);
		RuntimeResourceContainer containerTo = this.getContainerById(toId);

		nConnection.setContainerFrom(containerFrom);
		nConnection.setContainerTo(containerTo);

		linkCache.put(Pair.of(fromId, toId), nConnection);
		linkCache.put(Pair.of(toId, fromId), nConnection);

		vsumManager.executeTransaction(() -> {
			remProvider.getRuntimeEnvironment().getConnections().add(nConnection);
			vsum.createdObject(nConnection, VsumChangeSource.RUNTIME_ENVIRONMENT);
			return null;
		});
	}

	@Override
	public void reset(boolean hard) {
		containerCache.clear();
		linkCache.clear();

		// fill cache
		remProvider.getRuntimeEnvironment().getContainers().stream().forEach(c -> containerCache.put(c.getHostID(), c));
		remProvider.getRuntimeEnvironment().getConnections().stream().forEach(l -> {
			Pair<String, String> pairA = Pair.of(l.getContainerFrom().getHostID(), l.getContainerTo().getHostID());
			Pair<String, String> pairB = Pair.of(l.getContainerTo().getHostID(), l.getContainerFrom().getHostID());
			linkCache.put(pairA, l);
			linkCache.put(pairB, l);
		});
	}

	@Override
	public List<RuntimeResourceContainer> getContainers() {
		return remProvider.getRuntimeEnvironment().getContainers();
	}

	@Override
	public List<RuntimeResourceContainerConnection> getConnections() {
		return remProvider.getRuntimeEnvironment().getConnections();
	}

}
