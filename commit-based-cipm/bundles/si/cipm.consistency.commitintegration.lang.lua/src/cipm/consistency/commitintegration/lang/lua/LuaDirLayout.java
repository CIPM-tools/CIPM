package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.models.ModelDirLayoutImpl;
import cipm.consistency.models.code.CodeModelDirLayout;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

public class LuaDirLayout extends ModelDirLayoutImpl implements CodeModelDirLayout {
  static final String localRepoDirName = "local-repo-clone";

  static final String instrumentationDirName = "instrumented";

  static final String parsedFileName = "parsed.code.xmi";

  static final String moduleConfigurationFileName = "module-configuration.properties";

  static final String externalCallTargetPairsFileName = "external-call-target-pairs.json";

  Path localRepoDir;

  Path instrumentationDir;

  Path parsedFilePath;

  Path moduleConfigurationPath;

  Path externalCallTargetPairsFilePath;

  URI modelFileUri;

  URI parsedFileUri;

  public void initialize(final Path rootDirPath) {
    super.initialize(rootDirPath);
    this.localRepoDir = rootDirPath.resolve(LuaDirLayout.localRepoDirName);
    this.instrumentationDir = rootDirPath.resolve(LuaDirLayout.instrumentationDirName);
    this.parsedFilePath = rootDirPath.resolve(LuaDirLayout.parsedFileName).toAbsolutePath();
    this.moduleConfigurationPath = rootDirPath.resolve(LuaDirLayout.moduleConfigurationFileName);
    this.externalCallTargetPairsFilePath = rootDirPath.resolve(LuaDirLayout.externalCallTargetPairsFileName);
    this.parsedFileUri = URI.createFileURI(this.parsedFilePath.toString());
  }

  public Path getParsedCodePath() {
    return this.parsedFilePath;
  }

  public URI getParsedCodeURI() {
    return this.parsedFileUri;
  }

  public Path getLocalRepoDir() {
    return this.localRepoDir;
  }

  public void setLocalRepoDir(final Path localRepoDir) {
    this.localRepoDir = localRepoDir;
  }

  public Path getInstrumentationDir() {
    return this.instrumentationDir;
  }

  public void setInstrumentationDir(final Path instrumentationDir) {
    this.instrumentationDir = instrumentationDir;
  }

  public Path getParsedFilePath() {
    return this.parsedFilePath;
  }

  public void setParsedFilePath(final Path parsedFilePath) {
    this.parsedFilePath = parsedFilePath;
  }

  public Path getModuleConfigurationPath() {
    return this.moduleConfigurationPath;
  }

  public void setModuleConfigurationPath(final Path moduleConfigurationPath) {
    this.moduleConfigurationPath = moduleConfigurationPath;
  }

  @Pure
  public Path getExternalCallTargetPairsFilePath() {
    return this.externalCallTargetPairsFilePath;
  }

  public void setExternalCallTargetPairsFilePath(final Path externalCallTargetPairsFilePath) {
    this.externalCallTargetPairsFilePath = externalCallTargetPairsFilePath;
  }

  public URI getModelFileUri() {
    return this.modelFileUri;
  }

  public void setModelFileUri(final URI modelFileUri) {
    this.modelFileUri = modelFileUri;
  }

  public URI getParsedFileUri() {
    return this.parsedFileUri;
  }

  public void setParsedFileUri(final URI parsedFileUri) {
    this.parsedFileUri = parsedFileUri;
  }
}
