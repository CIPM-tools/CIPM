package cipm.consistency.cpr.javapcm.teammates.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class TeammatesCodeToSeffFactory extends CommitIntegrationCodeToSeffFactory {
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponentFinding basicComponentFinding,
			BasicComponent basicComponent) {
		return new TeammatesFunctionClassificationStrategy(basicComponentFinding, correspondenceModel, basicComponent);
	}
}
