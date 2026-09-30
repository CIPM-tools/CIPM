package tools.cipm.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Bundles the pluggable parts the method-body-to-SEFF reconstruction needs: the component
 * finder, the external-call and RD-behaviour finders SoMoX uses, and the
 * function-classification strategy. Each concrete factory represents one reconstruction
 * variant (plain package mapping, commit integration, ...).
 *
 * <p>The parts must be obtained in order: {@link #createBasicComponentFinding()} first,
 * because its result - and the {@link BasicComponent} it locates for the method under
 * analysis - are inputs to {@link #createInterfaceOfExternalCallFindingFactory} and
 * {@link #createAbstractFunctionClassificationStrategy}.
 */
public interface Code2SeffFactory {

    /** Creates the finder that maps a method onto its {@link BasicComponent}. */
    BasicComponentFinding createBasicComponentFinding();

    /** Creates the factory SoMoX uses to resolve the interface behind an external call. */
    InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponent basicComponent);

    /** Creates the finder that locates the RD behaviour / SEFF corresponding to a class method. */
    ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModel);

    /** Creates the strategy that classifies each call in a method body (internal, external, library). */
    AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
            EditableCorrespondenceModelView<Correspondence> correspondenceModel, BasicComponentFinding basicComponentFinding,
            BasicComponent basicComponent);
}
