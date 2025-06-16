package tools.cipm.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public interface Code2SeffFactory {

    BasicComponentFinding createBasicComponentFinding();

    InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponent basicComponent);

    ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModel);

    AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
            BasicComponentFinding basicComponentFinding, EditableCorrespondenceModelView<Correspondence> correspondenceModel,
            BasicComponent basicComponent);
}
