package cipm.consistency.vsum;

import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import cipm.consistency.models.ModelDirLayoutImpl;

/**
 * Internal layout for the directory structure of VSUM, PCM and IMM.
 * 
 * @author Martin Armbruster
 */

public class VsumDirLayout extends ModelDirLayoutImpl {
  private static final String vsumCorrespondenceModelName = "correspondence.correspondence";

  private Path vsumCorrespondenceModelPath;

  private URI vsumCorrespondenceModelUri;

  public void initialize(final Path rootDirPath) {
    super.initialize(rootDirPath);
    this.vsumCorrespondenceModelPath = rootDirPath.resolve(VsumDirLayout.vsumCorrespondenceModelName);
    this.vsumCorrespondenceModelUri = URI.createFileURI(this.vsumCorrespondenceModelPath.toString());
  }

  public Path getVsumCorrespondenceModelPath() {
    return this.vsumCorrespondenceModelPath;
  }

  public void setVsumCorrespondenceModelPath(final Path vsumCorrespondenceModelPath) {
    this.vsumCorrespondenceModelPath = vsumCorrespondenceModelPath;
  }

  public URI getVsumCorrespondenceModelUri() {
    return this.vsumCorrespondenceModelUri;
  }

  public void setVsumCorrespondenceModelUri(final URI vsumCorrespondenceModelUri) {
    this.vsumCorrespondenceModelUri = vsumCorrespondenceModelUri;
  }
}
