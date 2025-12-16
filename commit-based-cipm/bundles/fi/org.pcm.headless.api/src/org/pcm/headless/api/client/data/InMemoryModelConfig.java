package org.pcm.headless.api.client.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

public class InMemoryModelConfig {

	private List<EObject> repositorys = new ArrayList<>();
	private EObject system;
	private EObject allocation;
	private EObject usage;
	private EObject resourceEnvironment;
	private EObject monitorRepository;

	private List<EObject> additionals = new ArrayList<>();

	public List<EObject> getAllModels() {
		List<EObject> res = Lists.newArrayList(system, allocation, usage, resourceEnvironment, monitorRepository);
		res.addAll(repositorys);
		res.addAll(additionals);
		return res;
	}

	public void clear() {
		this.repositorys.clear();
		system = null;
		allocation = null;
		usage = null;
		resourceEnvironment = null;
		monitorRepository = null;
		additionals.clear();
	}

	public List<EObject> getRepositorys() {
		return repositorys;
	}

	public void setRepositorys(List<EObject> repositorys) {
		this.repositorys = repositorys;
	}

	public EObject getSystem() {
		return system;
	}

	public void setSystem(EObject system) {
		this.system = system;
	}

	public EObject getAllocation() {
		return allocation;
	}

	public void setAllocation(EObject allocation) {
		this.allocation = allocation;
	}

	public EObject getUsage() {
		return usage;
	}

	public void setUsage(EObject usage) {
		this.usage = usage;
	}

	public EObject getResourceEnvironment() {
		return resourceEnvironment;
	}

	public void setResourceEnvironment(EObject resourceEnvironment) {
		this.resourceEnvironment = resourceEnvironment;
	}

	public EObject getMonitorRepository() {
		return monitorRepository;
	}

	public void setMonitorRepository(EObject monitorRepository) {
		this.monitorRepository = monitorRepository;
	}

	public List<EObject> getAdditionals() {
		return additionals;
	}

	public void setAdditionals(List<EObject> additionals) {
		this.additionals = additionals;
	}
}
