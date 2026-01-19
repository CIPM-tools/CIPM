package org.pcm.headless.shared.data.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class HeadlessModelConfig {

	private List<File> repositoryFiles = new ArrayList<>();
	private File systemFile;
	private File resourceEnvironmentFile;
	private File allocationFile;
	private File usageFile;

	private File monitorRepository;

	private List<File> additionals = new ArrayList<>();

	public List<File> getRepositoryFiles() {
		return repositoryFiles;
	}

	public void setRepositoryFiles(List<File> repositoryFiles) {
		this.repositoryFiles = repositoryFiles;
	}

	public File getSystemFile() {
		return systemFile;
	}

	public void setSystemFile(File systemFile) {
		this.systemFile = systemFile;
	}

	public File getResourceEnvironmentFile() {
		return resourceEnvironmentFile;
	}

	public void setResourceEnvironmentFile(File resourceEnvironmentFile) {
		this.resourceEnvironmentFile = resourceEnvironmentFile;
	}

	public File getAllocationFile() {
		return allocationFile;
	}

	public void setAllocationFile(File allocationFile) {
		this.allocationFile = allocationFile;
	}

	public File getUsageFile() {
		return usageFile;
	}

	public void setUsageFile(File usageFile) {
		this.usageFile = usageFile;
	}

	public File getMonitorRepository() {
		return monitorRepository;
	}

	public void setMonitorRepository(File monitorRepository) {
		this.monitorRepository = monitorRepository;
	}

	public List<File> getAdditionals() {
		return additionals;
	}

	public void setAdditionals(List<File> additionals) {
		this.additionals = additionals;
	}

	public List<File> getAllFiles() {
		List<File> ret = Lists.newArrayList(systemFile, resourceEnvironmentFile, allocationFile, usageFile,
				monitorRepository);
		ret.addAll(repositoryFiles);
		ret.addAll(additionals);
		return ret;
	}

}
