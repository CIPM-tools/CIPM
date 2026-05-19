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
    private final BasicComponent myBasicComponent;

    public TeammatesFunctionClassificationStrategy(final BasicComponentFinding basicComponentFinding,
            final EditableCorrespondenceModelView<Correspondence> ci, final BasicComponent myBasicComponent) {
        super(basicComponentFinding, ci, myBasicComponent);
        this.basicComponentFinding = basicComponentFinding;
        this.correspondenceModel = ci;
        this.myBasicComponent = myBasicComponent;
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
        if (null == basicComponent || basicComponent.getId().equals(this.myBasicComponent.getId())) {
            return false;
        }
        return true;
    }
}
