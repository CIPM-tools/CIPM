package tools.cipm.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;

import tools.vitruv.framework.correspondence.CorrespondenceModel;

public interface Code2SeffFactory {

    BasicComponentFinding createBasicComponentFinding();

    InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
    		CorrespondenceModel correspondenceModel, BasicComponent basicComponent);

    ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
    		CorrespondenceModel correspondenceModel);

    AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
            BasicComponentFinding basicComponentFinding, CorrespondenceModel correspondenceModel,
            BasicComponent basicComponent);
}
