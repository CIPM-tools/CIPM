package cipm.consistency.models.code;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.models.ModelDirLayout;

public interface CodeModelDirLayout extends ModelDirLayout {
    Path getParsedCodePath();

    URI getParsedCodeURI();
}
