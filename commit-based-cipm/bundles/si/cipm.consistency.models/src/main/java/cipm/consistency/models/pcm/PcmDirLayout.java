package cipm.consistency.models.pcm;

import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import cipm.consistency.models.ModelDirLayoutImpl;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

public class PcmDirLayout extends ModelDirLayoutImpl {
  private static final String pcmRepositoryFileName = "Repository.repository";

  private static final String pcmSystemFileName = "System.system";

  private static final String pcmAllocationFileName = "Allocation.allocation";

  private static final String pcmUsageModelFileName = "Usage.usagemodel";

  private static final String pcmResourceEnvironmentFileName = "ResourceEnvironment.resourceenvironment";

  private Path pcmRepositoryPath;

  private URI pcmRepositoryURI;

  private Path pcmSystemPath;

  private URI pcmSystemURI;

  private Path pcmAllocationPath;

  private URI pcmAllocationURI;

  private Path pcmUsageModelPath;

  private URI pcmUsageModelURI;

  private Path pcmResourceEnvironmentPath;

  private URI pcmResourceEnvironmentURI;

  @Override
  public void initialize(final Path rootDirPath) {
    super.initialize(rootDirPath);
    this.pcmRepositoryPath = rootDirPath.resolve(PcmDirLayout.pcmRepositoryFileName).toAbsolutePath();
    this.pcmRepositoryURI = URI.createFileURI(this.pcmRepositoryPath.toString());
    this.pcmSystemPath = rootDirPath.resolve(PcmDirLayout.pcmSystemFileName).toAbsolutePath();
    this.pcmSystemURI = URI.createFileURI(this.pcmSystemPath.toString());
    this.pcmAllocationPath = rootDirPath.resolve(PcmDirLayout.pcmAllocationFileName).toAbsolutePath();
    this.pcmAllocationURI = URI.createFileURI(this.pcmAllocationPath.toString());
    this.pcmUsageModelPath = rootDirPath.resolve(PcmDirLayout.pcmUsageModelFileName).toAbsolutePath();
    this.pcmUsageModelURI = URI.createFileURI(this.pcmUsageModelPath.toString());
    this.pcmResourceEnvironmentPath = rootDirPath.resolve(PcmDirLayout.pcmResourceEnvironmentFileName).toAbsolutePath();
    this.pcmResourceEnvironmentURI = URI.createFileURI(this.pcmResourceEnvironmentPath.toString());
  }

  public LocalFilesystemPCM getFilePCM() {
	var filePCM = new LocalFilesystemPCM();
	filePCM.setRepositoryFile(pcmRepositoryPath.toFile());
	filePCM.setAllocationModelFile(pcmAllocationPath.toFile());
	filePCM.setSystemFile(pcmSystemPath.toFile());
	filePCM.setResourceEnvironmentFile(pcmResourceEnvironmentPath.toFile());
	filePCM.setUsageModelFile(pcmUsageModelPath.toFile());
	return filePCM;
  }

  public Path getPcmRepositoryPath() {
    return this.pcmRepositoryPath;
  }

  public void setPcmRepositoryPath(final Path pcmRepositoryPath) {
    this.pcmRepositoryPath = pcmRepositoryPath;
  }

  public URI getPcmRepositoryURI() {
    return this.pcmRepositoryURI;
  }

  public void setPcmRepositoryURI(final URI pcmRepositoryURI) {
    this.pcmRepositoryURI = pcmRepositoryURI;
  }

  public Path getPcmSystemPath() {
    return this.pcmSystemPath;
  }

  public void setPcmSystemPath(final Path pcmSystemPath) {
    this.pcmSystemPath = pcmSystemPath;
  }

  public URI getPcmSystemURI() {
    return this.pcmSystemURI;
  }

  public void setPcmSystemURI(final URI pcmSystemURI) {
    this.pcmSystemURI = pcmSystemURI;
  }

  public Path getPcmAllocationPath() {
    return this.pcmAllocationPath;
  }

  public void setPcmAllocationPath(final Path pcmAllocationPath) {
    this.pcmAllocationPath = pcmAllocationPath;
  }

  public URI getPcmAllocationURI() {
    return this.pcmAllocationURI;
  }

  public void setPcmAllocationURI(final URI pcmAllocationURI) {
    this.pcmAllocationURI = pcmAllocationURI;
  }

  public Path getPcmUsageModelPath() {
    return this.pcmUsageModelPath;
  }

  public void setPcmUsageModelPath(final Path pcmUsageModelPath) {
    this.pcmUsageModelPath = pcmUsageModelPath;
  }

  public URI getPcmUsageModelURI() {
    return this.pcmUsageModelURI;
  }

  public void setPcmUsageModelURI(final URI pcmUsageModelURI) {
    this.pcmUsageModelURI = pcmUsageModelURI;
  }

  public Path getPcmResourceEnvironmentPath() {
    return this.pcmResourceEnvironmentPath;
  }

  public void setPcmResourceEnvironmentPath(final Path pcmResourceEnvironmentPath) {
    this.pcmResourceEnvironmentPath = pcmResourceEnvironmentPath;
  }

  public URI getPcmResourceEnvironmentURI() {
    return this.pcmResourceEnvironmentURI;
  }

  public void setPcmResourceEnvironmentURI(final URI pcmResourceEnvironmentURI) {
    this.pcmResourceEnvironmentURI = pcmResourceEnvironmentURI;
  }
}
