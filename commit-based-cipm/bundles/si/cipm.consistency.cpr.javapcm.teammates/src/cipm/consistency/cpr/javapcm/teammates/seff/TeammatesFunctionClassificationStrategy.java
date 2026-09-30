package cipm.consistency.cpr.javapcm.teammates.seff;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.pojotransformations.code2seff.FunctionClassificationStrategyForPackageMapping;
import tools.vitruv.applications.util.temporary.other.UriUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class TeammatesFunctionClassificationStrategy extends FunctionClassificationStrategyForPackageMapping {
    private final BasicComponentFinding basicComponentFinding;
    private final EditableCorrespondenceModelView<Correspondence> correspondenceModel;
    private final BasicComponent basicComponent;

    public TeammatesFunctionClassificationStrategy(final BasicComponentFinding basicComponentFinding,
            final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final BasicComponent basicComponent) {
        super(basicComponentFinding, correspondenceModel, basicComponent);
        this.basicComponentFinding = basicComponentFinding;
        this.correspondenceModel = correspondenceModel;
        this.basicComponent = basicComponent;
    }

    /**
     * A call is an external call if the call's destination is a method in another component.
     */
    @Override
    protected boolean isExternalCall(final Method method) {
        if (!UriUtil.normalizeURI(method)) {
            return false;
        }
        final BasicComponent basicComponent = this.basicComponentFinding.findBasicComponentForMethod(method,
                this.correspondenceModel);
        if (null == basicComponent || basicComponent.getId().equals(this.basicComponent.getId())) {
            return false;
        }
        return true;
    }
}
