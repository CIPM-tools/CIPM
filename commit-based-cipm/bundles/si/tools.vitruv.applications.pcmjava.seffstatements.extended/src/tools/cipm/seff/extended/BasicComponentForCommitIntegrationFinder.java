package tools.cipm.seff.extended;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.cipm.seff.BasicComponentFinding;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

/**
 * Finds the component for a method in the case of a commit-based integration.
 *
 * @author Martin Armbruster
 */
public class BasicComponentForCommitIntegrationFinder implements BasicComponentFinding {
    @Override
    public BasicComponent findBasicComponentForMethod(final Method newMethod, final CorrespondenceModel ci) {
    	var correspondences = CorrespondenceModelUtil.getCorrespondingEObjects(ci,
    			newMethod.getContainingConcreteClassifier(), BasicComponent.class);
    	if (correspondences != null && !correspondences.isEmpty()) {
    		return correspondences.iterator().next();
    	}
    	return null;
    }
}
