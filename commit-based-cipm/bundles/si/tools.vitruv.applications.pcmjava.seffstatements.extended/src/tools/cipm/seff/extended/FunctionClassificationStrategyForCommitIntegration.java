package tools.cipm.seff.extended;

import java.util.List;

import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.pojotransformations.code2seff.FunctionClassificationStrategyForPackageMapping;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * A function classification strategy for the commit-based integration.
 * 
 * @author Martin Armbruster
 */
public class FunctionClassificationStrategyForCommitIntegration
		extends FunctionClassificationStrategyForPackageMapping {

	private final List<String> restClientApiPackages;

	public FunctionClassificationStrategyForCommitIntegration(BasicComponentFinding basicComponentFinding,
			EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponent basicComponent) {
		super(basicComponentFinding, correspondenceModel, basicComponent);
		this.restClientApiPackages = readRestClientApiPackages();
	}

	/**
	 * Reads the packages containing REST client APIs from the settings. The
	 * settings container or the setting itself can be absent, in which case no
	 * package is treated as a REST client API package.
	 *
	 * @return the configured packages, never null.
	 */
	private static List<String> readRestClientApiPackages() {
		CommitIntegrationSettingsContainer settings = CommitIntegrationSettingsContainer.getSettingsContainer();
		if (settings == null) {
			return List.of();
		}
		String configuredPackages = settings.getProperty(SettingKeys.REST_CLIENT_API_PACKAGES);
		if (configuredPackages == null || configuredPackages.isBlank()) {
			return List.of();
		}
		return List.of(configuredPackages.split(";"));
	}

	/**
	 * Classifies external calls according to the superclass. In addition, methods
	 * in specific packages which contain REST client APIs are considered as
	 * external calls.
	 */
	@Override
	protected boolean isExternalCall(Method method) {
		if (super.isExternalCall(method)) {
			return true;
		}

		String namespaces = method.getContainingCompilationUnit().getNamespacesAsString();
		if (namespaces == null || namespaces.isBlank()) {
			return false;
		}

		if (namespaces.endsWith(LogicalJavaURIGenerator.CLASSIFIER_SEPARATOR)
				|| namespaces.endsWith(LogicalJavaURIGenerator.PACKAGE_SEPARATOR)) {
			namespaces = namespaces.substring(0, namespaces.length() - 1);
		}

		return restClientApiPackages.contains(namespaces);
	}
}
