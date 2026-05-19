package cipm.consistency.commitintegration.lang.lua.appspace.detection;

import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.BuildFileBasedComponentDetectionStrategy;
import java.nio.file.Path;

public class AppSpaceComponentDetectionStrategy extends BuildFileBasedComponentDetectionStrategy {
    private static final String APP_MANIFEST_FILE_NAME = "project.mf.xml";

    @Override
    protected ComponentState checkDirectoryForComponent(Path parent) {
        if (checkSiblingExistence(parent, APP_MANIFEST_FILE_NAME)) {
            return ComponentState.REGULAR_COMPONENT;
        }
        return null;
    }
}
