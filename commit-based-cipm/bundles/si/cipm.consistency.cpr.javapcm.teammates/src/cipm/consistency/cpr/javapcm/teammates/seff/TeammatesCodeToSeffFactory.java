package cipm.consistency.cpr.javapcm.teammates.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

public class TeammatesCodeToSeffFactory extends CommitIntegrationCodeToSeffFactory {
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			BasicComponentFinding componentFinding, CorrespondenceModel cm,
			BasicComponent com) {
		return new TeammatesFunctionClassificationStrategy(componentFinding, cm, com);
	}
}
