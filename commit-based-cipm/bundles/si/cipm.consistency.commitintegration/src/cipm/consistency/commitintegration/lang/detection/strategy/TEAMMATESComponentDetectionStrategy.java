package cipm.consistency.commitintegration.detection;

/**
 * A component detection strategy for TEAMMATES.
 * 
 * @author Martin Armbruster
 */
public class TEAMMATESComponentDetectionStrategy extends PackageBasedComponentDetectionStrategy {
	@Override
	protected void initializeMappings() {
		this.addPackageModuleMapping("teammates\\.common\\..*?", "teammates.common", ModuleState.REGULAR_COMPONENT);
		this.addPackageModuleMapping("teammates\\.logic\\.api\\..*?", "teammates.logic", ModuleState.REGULAR_COMPONENT);
		this.addPackageModuleMapping("teammates\\.storage\\.api\\..*?", "teammates.storage", ModuleState.REGULAR_COMPONENT);
		// TODO: Replace Microservice component with Servlet-based component.
		this.addPackageModuleMapping("teammates\\.ui\\..*?", "teammates.ui", ModuleState.MICROSERVICE_COMPONENT);
	}
}
