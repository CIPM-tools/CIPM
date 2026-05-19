package cipm.consistency.commitintegration.lang.lua.appspace.detection;

import cipm.consistency.commitintegration.lang.detection.ComponentCandidates;
import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.xtext.lua.scoping.LuaLinkingService;

public class StdLibCrownComponentDetectionStrategy implements ComponentDetectionStrategy {

    @Override
    public void detectComponent(Resource res, Path projectRoot, ComponentCandidates candidates) {
        if (res.getURI()
            .equals(LuaLinkingService.MOCK_URI)) {
            candidates.addModuleClassifier(ComponentState.REGULAR_COMPONENT, LuaLinkingService.MOCK_URI.path(), res);
        }
    }

}
