package cipm.consistency.vsum.test.pcm.cprunittests;

import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.PackageBasedComponentDetectionStrategy;

public class UnnamedModuleComponentDetectionStrategy extends PackageBasedComponentDetectionStrategy {
	@Override
	protected void initializeMappings() {
		this.addPackageModuleMapping("*", "Unified component", ComponentState.REGULAR_COMPONENT);
	}
}
