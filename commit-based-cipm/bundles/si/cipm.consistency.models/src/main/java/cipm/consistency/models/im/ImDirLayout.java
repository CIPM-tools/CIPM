package cipm.consistency.models.im;

import cipm.consistency.models.ModelDirLayoutImpl;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

public class ImDirLayout extends ModelDirLayoutImpl {
  private static final String imFileName = "imm.imm";

  private Path imFilePath;

  private URI imFileUri;

  @Override
  public void initialize(final Path rootDirPath) {
    super.initialize(rootDirPath);
    this.imFilePath = rootDirPath.resolve(ImDirLayout.imFileName);
    this.imFileUri = URI.createFileURI(this.imFilePath.toAbsolutePath().toString());
  }

  public Path getImFilePath() {
    return this.imFilePath;
  }

  public void setImFilePath(final Path imFilePath) {
    this.imFilePath = imFilePath;
  }

  public URI getImFileUri() {
    return this.imFileUri;
  }

  public void setImFileUri(final URI imFileUri) {
    this.imFileUri = imFileUri;
  }
}
