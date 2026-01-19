package cipm.consistency.cpr.measurementspcm;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

/**
 * Utility class to find PCM actions by ID in a repository.
 */
public final class PcmActionFinder {

    private PcmActionFinder() {
        // Utility class
    }

    /**
     * Finds an InternalAction by ID in the repository.
     *
     * @param repository the PCM repository
     * @param actionId the action ID to find
     * @return the found InternalAction, or null if not found
     */
    public static InternalAction findInternalAction(Repository repository, String actionId) {
        if (repository == null || actionId == null) {
            return null;
        }

        for (RepositoryComponent component : repository.getComponents__Repository()) {
            if (component instanceof BasicComponent) {
                BasicComponent basicComponent = (BasicComponent) component;
                for (ServiceEffectSpecification seff : basicComponent.getServiceEffectSpecifications__BasicComponent()) {
                    if (seff instanceof ResourceDemandingSEFF) {
                        InternalAction found = findInternalActionInBehaviour((ResourceDemandingSEFF) seff, actionId);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds an InternalAction by ID in a behaviour (recursive).
     */
    private static InternalAction findInternalActionInBehaviour(ResourceDemandingBehaviour behaviour, String actionId) {
        if (behaviour == null || behaviour.getSteps_Behaviour() == null) {
            return null;
        }

        for (AbstractAction step : behaviour.getSteps_Behaviour()) {
            if (step instanceof InternalAction) {
                InternalAction internalAction = (InternalAction) step;
                if (actionId.equals(internalAction.getId())) {
                    return internalAction;
                }
            }
            // Check nested behaviours in BranchAction
            if (step instanceof BranchAction) {
                BranchAction branchAction = (BranchAction) step;
                for (AbstractBranchTransition branch : branchAction.getBranches_Branch()) {
                    InternalAction found = findInternalActionInBehaviour(branch.getBranchBehaviour_BranchTransition(), actionId);
                    if (found != null) {
                        return found;
                    }
                }
            }
            // Check nested behaviours in LoopAction
            if (step instanceof LoopAction) {
                LoopAction loopAction = (LoopAction) step;
                InternalAction found = findInternalActionInBehaviour(loopAction.getBodyBehaviour_Loop(), actionId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Finds a LoopAction by ID in the repository.
     *
     * @param repository the PCM repository
     * @param actionId the action ID to find
     * @return the found LoopAction, or null if not found
     */
    public static LoopAction findLoopAction(Repository repository, String actionId) {
        if (repository == null || actionId == null) {
            return null;
        }

        for (RepositoryComponent component : repository.getComponents__Repository()) {
            if (component instanceof BasicComponent) {
                BasicComponent basicComponent = (BasicComponent) component;
                for (ServiceEffectSpecification seff : basicComponent.getServiceEffectSpecifications__BasicComponent()) {
                    if (seff instanceof ResourceDemandingSEFF) {
                        LoopAction found = findLoopActionInBehaviour((ResourceDemandingSEFF) seff, actionId);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a LoopAction by ID in a behaviour (recursive).
     */
    private static LoopAction findLoopActionInBehaviour(ResourceDemandingBehaviour behaviour, String actionId) {
        if (behaviour == null || behaviour.getSteps_Behaviour() == null) {
            return null;
        }

        for (AbstractAction step : behaviour.getSteps_Behaviour()) {
            if (step instanceof LoopAction) {
                LoopAction loopAction = (LoopAction) step;
                if (actionId.equals(loopAction.getId())) {
                    return loopAction;
                }
                // Also check nested behaviours
                LoopAction found = findLoopActionInBehaviour(loopAction.getBodyBehaviour_Loop(), actionId);
                if (found != null) {
                    return found;
                }
            }
            // Check nested behaviours in BranchAction
            if (step instanceof BranchAction) {
                BranchAction branchAction = (BranchAction) step;
                for (AbstractBranchTransition branch : branchAction.getBranches_Branch()) {
                    LoopAction found = findLoopActionInBehaviour(branch.getBranchBehaviour_BranchTransition(), actionId);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a BranchAction by ID in the repository.
     *
     * @param repository the PCM repository
     * @param actionId the action ID to find
     * @return the found BranchAction, or null if not found
     */
    public static BranchAction findBranchAction(Repository repository, String actionId) {
        if (repository == null || actionId == null) {
            return null;
        }

        for (RepositoryComponent component : repository.getComponents__Repository()) {
            if (component instanceof BasicComponent) {
                BasicComponent basicComponent = (BasicComponent) component;
                for (ServiceEffectSpecification seff : basicComponent.getServiceEffectSpecifications__BasicComponent()) {
                    if (seff instanceof ResourceDemandingSEFF) {
                        BranchAction found = findBranchActionInBehaviour((ResourceDemandingSEFF) seff, actionId);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a BranchAction by ID in a behaviour (recursive).
     */
    private static BranchAction findBranchActionInBehaviour(ResourceDemandingBehaviour behaviour, String actionId) {
        if (behaviour == null || behaviour.getSteps_Behaviour() == null) {
            return null;
        }

        for (AbstractAction step : behaviour.getSteps_Behaviour()) {
            if (step instanceof BranchAction) {
                BranchAction branchAction = (BranchAction) step;
                if (actionId.equals(branchAction.getId())) {
                    return branchAction;
                }
                // Also check nested behaviours
                for (AbstractBranchTransition branch : branchAction.getBranches_Branch()) {
                    BranchAction found = findBranchActionInBehaviour(branch.getBranchBehaviour_BranchTransition(), actionId);
                    if (found != null) {
                        return found;
                    }
                }
            }
            // Check nested behaviours in LoopAction
            if (step instanceof LoopAction) {
                LoopAction loopAction = (LoopAction) step;
                BranchAction found = findBranchActionInBehaviour(loopAction.getBodyBehaviour_Loop(), actionId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
