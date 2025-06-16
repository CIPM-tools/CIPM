package tools.cipm.seff.extended;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.CorrespondenceModelUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Finds the component for a method in the case of a commit-based integration.
 *
 * @author Martin Armbruster
 */
public class BasicComponentForCommitIntegrationFinder implements BasicComponentFinding {
    @Override
    public BasicComponent findBasicComponentForMethod(final Method newMethod, final EditableCorrespondenceModelView<Correspondence> ci) {
    	var correspondences = CorrespondenceModelUtil.getCorrespondingEObjects(ci,
    			newMethod.getContainingConcreteClassifier(), BasicComponent.class);
    	if (correspondences != null && !correspondences.isEmpty()) {
    		return correspondences.iterator().next();
    	}
    	return null;
    }
}
