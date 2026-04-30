package tools.cipm.seff;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public interface BasicComponentFinding {

    BasicComponent findBasicComponentForMethod(Method newMethod, EditableCorrespondenceModelView<Correspondence> ci);

}
