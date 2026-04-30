package cipm.consistency.vsum;

import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Internal layout for the directory structure of VSUM, PCM and IMM.
 * 
 * @author Martin Armbruster
 */

public class VsumDirLayout /* implements ModelDirLayoutImpl  */{
  private static final String vsumCorrespondenceModelName = "correspondence.correspondence";

  private Path vsumCorrespondenceModelPath;

  private URI vsumCorrespondenceModelUri;

  public URI initialize(final Path rootDirPath) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field super is undefined"
      + "\ninitialize cannot be resolved");
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
