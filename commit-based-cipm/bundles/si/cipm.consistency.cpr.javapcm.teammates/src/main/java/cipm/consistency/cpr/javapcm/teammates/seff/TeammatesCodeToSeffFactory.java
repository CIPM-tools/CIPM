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
			BasicComponentFinding componentFinding, EditableCorrespondenceModelView<Correspondence> cm,
			BasicComponent com) {
		return new TeammatesFunctionClassificationStrategy(componentFinding, cm, com);
	}
}
