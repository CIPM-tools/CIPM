package cipm.consistency.models;

import java.nio.file.Path;

public interface ModelDirLayout {

    void initialize(Path rootDirPath);

    Path getRootDirPath();

    void delete();

}
