package tools.cipm.seff.extended;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Provides a CodeToSeffFactory implementation for the commit-based integration.
 * 
 * @author Martin Armbruster
 */
public class CommitIntegrationCodeToSeffFactory extends PojoJava2PcmCodeToSeffFactory {
	@Override
	public BasicComponentFinding createBasicComponentFinding() {
		return new BasicComponentForCommitIntegrationFinder();
	}
	
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			BasicComponentFinding componentFinding, EditableCorrespondenceModelView<Correspondence> cm,
			BasicComponent com) {
		return new FunctionClassificationStrategyForCommitIntegration(componentFinding, cm, com);
	}
}
