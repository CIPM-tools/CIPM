package cipm.consistency.cpr.pcmim;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import tools.cipm.models.instrumentation.InstrumentationModel.InstrumentationType;

/**
 * An internal utility class for the CPRs from the PCM to the extended IM.
 * 
 * @author Martin Armbruster
 */
public class InternalUtils {
    /**
     * Finds the parent ResourceDemandingSEFF for a ResourceDemandingBehaviour which is a
     * ResourceDemandingSEFF, not a ResourceDemandingInternalBehaviour, or not contained within a
     * ResourceDemandingInternalBehaviour.
     * 
     * @param behaviour
     *            the ResourceDemandingBehaviour.
     * @return the parent ResourceDemandingSEFF or null if it cannot be found, the behaviour is a
     *         ResourceDemandingInternalBehaviour, or the behaviour is contained within a
     *         ResourceDemandingInternalBehaviour.
     */
    public static ResourceDemandingSEFF getParentSEFFNotForInternalBehaviour(ResourceDemandingBehaviour behaviour) {
        EObject parent = behaviour;
        while (parent != null && !(parent instanceof RepositoryComponent)
                && !(parent instanceof ResourceDemandingInternalBehaviour)) {
            if (parent instanceof ResourceDemandingSEFF) {
                return (ResourceDemandingSEFF) parent;
            }
            parent = parent.eContainer();
        }
        return null;
    }
    
    /**
     * Returns the enum value for the type of the given action.
     * @param action
     * @return Enum value for the type of action
     */
    public static Integer abstractActionToActionTypeValue(AbstractAction action) {
        Integer actionType = -1;
        if (action instanceof LoopAction) {
            actionType = InstrumentationType.LOOP_VALUE;
        } else if (action instanceof BranchAction) {
            actionType = InstrumentationType.BRANCH_VALUE;
        } else if (action instanceof InternalAction) {
            actionType = InstrumentationType.INTERNAL_VALUE;
        } else if (action instanceof InternalCallAction) {
            actionType = InstrumentationType.INTERNAL_CALL_VALUE;
        } else if (action instanceof ExternalCallAction) {
            actionType = InstrumentationType.EXTERNAL_CALL_VALUE;
        }
        return actionType;
    }
}
