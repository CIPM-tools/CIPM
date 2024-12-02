package cipm.consistency.base.shared.pcm;

import java.io.File;

/**
 * Contains file system paths where a PCM model can be stored.
 * 
 * @author David Monschein
 *
 */
public class LocalFilesystemPCM {

    private File repositoryFile;
    private File systemFile;
    private File usageModelFile;
    private File allocationModelFile;
    private File resourceEnvironmentFile;

    /**
     * Determines whether the given paths for the models are valid. Does not check its content!
     * 
     * @return true if all paths are correct and not null, false otherwise
     */
    public boolean isValid() {
        return repositoryFile != null && systemFile != null && usageModelFile != null && allocationModelFile != null
                && resourceEnvironmentFile != null && repositoryFile.exists() && systemFile.exists()
                && usageModelFile.exists() && allocationModelFile.exists() && resourceEnvironmentFile.exists();
    }

	public File getRepositoryFile() {
		return repositoryFile;
	}

	public void setRepositoryFile(File repositoryFile) {
		this.repositoryFile = repositoryFile;
	}

	public File getSystemFile() {
		return systemFile;
	}

	public void setSystemFile(File systemFile) {
		this.systemFile = systemFile;
	}

	public File getUsageModelFile() {
		return usageModelFile;
	}

	public void setUsageModelFile(File usageModelFile) {
		this.usageModelFile = usageModelFile;
	}

	public File getAllocationModelFile() {
		return allocationModelFile;
	}

	public void setAllocationModelFile(File allocationModelFile) {
		this.allocationModelFile = allocationModelFile;
	}

	public File getResourceEnvironmentFile() {
		return resourceEnvironmentFile;
	}

	public void setResourceEnvironmentFile(File resourceEnvironmentFile) {
		this.resourceEnvironmentFile = resourceEnvironmentFile;
	}
}