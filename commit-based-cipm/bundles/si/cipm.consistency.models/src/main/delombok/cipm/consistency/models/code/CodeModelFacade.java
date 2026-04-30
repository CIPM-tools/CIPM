package cipm.consistency.models.code;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.models.ModelFacade;

public interface CodeModelFacade extends ModelFacade {

    public Resource parseSourceCodeDir(Path dir);
    
    public Path createNamedCopyOfParsedModel(String name) throws IOException;

    public CodeModelDirLayout getDirLayout();
}
