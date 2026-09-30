package tools.cipm.seff.extended;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.Code2SeffFactory;
import tools.cipm.seff.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Provides a CodeToSeffFactory implementation for the commit-based integration.
 *
 * @author Martin Armbruster
 */
public class CommitIntegrationCodeToSeffFactory implements Code2SeffFactory {

	private final Code2SeffFactory packageMappingDefaults = new PojoJava2PcmCodeToSeffFactory();

	@Override
	public BasicComponentFinding createBasicComponentFinding() {
		return new BasicComponentForCommitIntegrationFinder();
	}

	@Override
	public InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
			EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponent basicComponent) {
		return packageMappingDefaults.createInterfaceOfExternalCallFindingFactory(correspondenceModel, basicComponent);
	}

	@Override
	public ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
			EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		return packageMappingDefaults.createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
	}

	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponentFinding basicComponentFinding,
			BasicComponent basicComponent) {
		return new FunctionClassificationStrategyForCommitIntegration(basicComponentFinding, correspondenceModel, basicComponent);
	}
}
