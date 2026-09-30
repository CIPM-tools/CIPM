package tools.cipm.seff;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Locates the PCM {@link BasicComponent} that a Java method belongs to, using the
 * correspondence model to map the method's classifier or package onto a component.
 * Implementations differ in how that mapping is resolved (package structure vs.
 * commit-integration correspondences).
 */
public interface BasicComponentFinding {

    /**
     * Returns the component the given method belongs to.
     *
     * @param newMethod           the method whose component is sought.
     * @param correspondenceModel the correspondence model to resolve the mapping against.
     * @return the corresponding {@link BasicComponent}, or {@code null} if none corresponds.
     */
    BasicComponent findBasicComponentForMethod(Method newMethod, EditableCorrespondenceModelView<Correspondence> correspondenceModel);

}
