package cipm.consistency.commitintegration;

import cipm.consistency.models.ModelDirLayoutImpl;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class CommitIntegrationDirLayout extends ModelDirLayoutImpl {
  private static String vsumDirName = "vsum";

  private static String imDirName = "im";

  private static String pcmDirName = "pcm";

  private static String codeDirName = "code";

  private static String instrumentedCodeDirName = "instrumented-code";

  private static String vsumCodeModelName = "vsum.code.xmi";

  private static String commitsFileName = "commits";

  private static String settingsFileName = "settings.settings";

  private static String evaluationDataFileName = "evaluationData.json";

  private Path vsumDirPath;

  private Path pcmDirPath;

  private Path imDirPath;

  private Path codeDirPath;

  private Path instrumentedCodeDirPath;

  private Path vsumCodeModelPath;

  private URI vsumCodeModelURI;

  private Path commitsFilePath;

  private Path settingsFilePath;

  private Path evaluationDataFilePath;

  @Override
  public void initialize(final Path rootDirPath) {
    super.initialize(rootDirPath);
    this.vsumDirPath = rootDirPath.resolve(CommitIntegrationDirLayout.vsumDirName);
    this.pcmDirPath = rootDirPath.resolve(CommitIntegrationDirLayout.pcmDirName);
    this.imDirPath = rootDirPath.resolve(CommitIntegrationDirLayout.imDirName);
    this.codeDirPath = rootDirPath.resolve(CommitIntegrationDirLayout.codeDirName);
    this.instrumentedCodeDirPath = rootDirPath.resolve(CommitIntegrationDirLayout.instrumentedCodeDirName);
    this.vsumCodeModelPath = this.codeDirPath.resolve(CommitIntegrationDirLayout.vsumCodeModelName).toAbsolutePath();
    this.vsumCodeModelURI = URI.createFileURI(this.vsumCodeModelPath.toString());
    this.commitsFilePath = rootDirPath.resolve(CommitIntegrationDirLayout.commitsFileName);
    this.settingsFilePath = rootDirPath.resolve(CommitIntegrationDirLayout.settingsFileName);
    this.evaluationDataFilePath = rootDirPath.resolve(CommitIntegrationDirLayout.evaluationDataFileName).toAbsolutePath();
  }

  @Pure
  public Path getVsumDirPath() {
    return this.vsumDirPath;
  }

  public void setVsumDirPath(final Path vsumDirPath) {
    this.vsumDirPath = vsumDirPath;
  }

  @Pure
  public Path getPcmDirPath() {
    return this.pcmDirPath;
  }

  public void setPcmDirPath(final Path pcmDirPath) {
    this.pcmDirPath = pcmDirPath;
  }

  @Pure
  public Path getImDirPath() {
    return this.imDirPath;
  }

  public void setImDirPath(final Path imDirPath) {
    this.imDirPath = imDirPath;
  }

  @Pure
  public Path getCodeDirPath() {
    return this.codeDirPath;
  }

  public void setCodeDirPath(final Path codeDirPath) {
    this.codeDirPath = codeDirPath;
  }

  @Pure
  public Path getInstrumentedCodeDirPath() {
    return this.instrumentedCodeDirPath;
  }

  public void setInstrumentedCodeDirPath(final Path instrumentedCodeDirPath) {
    this.instrumentedCodeDirPath = instrumentedCodeDirPath;
  }

  @Pure
  public Path getVsumCodeModelPath() {
    return this.vsumCodeModelPath;
  }

  public void setVsumCodeModelPath(final Path vsumCodeModelPath) {
    this.vsumCodeModelPath = vsumCodeModelPath;
  }

  @Pure
  public URI getVsumCodeModelURI() {
    return this.vsumCodeModelURI;
  }

  public void setVsumCodeModelURI(final URI vsumCodeModelURI) {
    this.vsumCodeModelURI = vsumCodeModelURI;
  }

  @Pure
  public Path getCommitsFilePath() {
    return this.commitsFilePath;
  }

  public void setCommitsFilePath(final Path commitsFilePath) {
    this.commitsFilePath = commitsFilePath;
  }

  @Pure
  public Path getSettingsFilePath() {
    return this.settingsFilePath;
  }

  public void setSettingsFilePath(final Path settingsFilePath) {
    this.settingsFilePath = settingsFilePath;
  }

  @Pure
  public Path getEvaluationDataFilePath() {
    return this.evaluationDataFilePath;
  }

  public void setEvaluationDataFilePath(final Path evaluationDataFilePath) {
    this.evaluationDataFilePath = evaluationDataFilePath;
  }
}
