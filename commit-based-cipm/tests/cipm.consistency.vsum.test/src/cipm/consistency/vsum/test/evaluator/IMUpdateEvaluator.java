package cipm.consistency.vsum.test.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractLoopAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;

/**
 * Evaluated the update of the extended IM.
 * 
 * @author Martin Armbruster
 */
public class IMUpdateEvaluator {
    private ImUpdateEvalData currentEvalResult;

    public void evaluateIMUpdate(Repository repo, InstrumentationModel im, ImUpdateEvalData evalData,
            Repository previousRepository) {
        currentEvalResult = evalData;

        // find IP count
        var ips = EcoreUtil2.getAllContentsOfType(im, InstrumentationPoint.class);
        currentEvalResult.setNumberIP(ips.size());

        var aips = EcoreUtil2.getAllContentsOfType(im, ActionInstrumentationPoint.class);
        currentEvalResult.setNumberAIP(aips.size());

        var sips = EcoreUtil2.getAllContentsOfType(im, ServiceInstrumentationPoint.class);
        currentEvalResult.setNumberSIP(sips.size());

        checkServiceInstrumentationPointExistenceForRepo(repo, im);
        checkSeffExistenceForServiceInstrumentationPoint(repo, im);

        this.checkChangedActions(repo, previousRepository, im);

        // we have all the values for the fscore
        currentEvalResult.calculateDerivedValues();
        
        // clean temporary values so the dont end up in the eval file
        currentEvalResult.cleanUp();
    }

    private void checkSeffExistenceForServiceInstrumentationPoint(Repository repo, InstrumentationModel im) {
        for (var sip : im.getPoints()) {
            findSeffForSip(sip, repo);

            for (var aip : sip.getActionInstrumentationPoints()) {
                findActionForAip(aip, repo);
            }
        }
    }

    private void checkServiceInstrumentationPointExistenceForRepo(Repository repo, InstrumentationModel im) {
        for (RepositoryComponent com : repo.getComponents__Repository()) {
            if (com instanceof BasicComponent) {
                BasicComponent basicCom = (BasicComponent) com;
                for (ServiceEffectSpecification seff : basicCom.getServiceEffectSpecifications__BasicComponent()) {
                    if (seff instanceof ResourceDemandingSEFF) {
                        ResourceDemandingSEFF rdseff = (ResourceDemandingSEFF) seff;
                        var sip = findSIP(im, rdseff);
                        checkActionInstrumentationPointExistenceForSeff(sip, rdseff);
                    }
                }
            }
        }
    }

    private void checkActionInstrumentationPointExistenceForSeff(ServiceInstrumentationPoint sip,
            ResourceDemandingSEFF seff) {
        for (AbstractAction aa : seff.getSteps_Behaviour()) {
            checkActionInstrumentationPointExistenceForAction(sip, aa);
        }
    }

    private void checkActionInstrumentationPointExistenceForAction(ServiceInstrumentationPoint sip, AbstractAction aa) {
        if (!actionIsRelevant(aa)) {
            return;
        }

        if (aa instanceof AbstractLoopAction) {
            AbstractLoopAction loop = (AbstractLoopAction) aa;
            for (AbstractAction innerAA : loop.getBodyBehaviour_Loop()
                .getSteps_Behaviour()) {
                checkActionInstrumentationPointExistenceForAction(sip, innerAA);
            }
            findAIP(sip, loop);
        } else if (aa instanceof BranchAction) {
            BranchAction branch = (BranchAction) aa;
            for (var transition : branch.getBranches_Branch()) {
                for (AbstractAction innerAA : transition.getBranchBehaviour_BranchTransition()
                    .getSteps_Behaviour()) {
                    checkActionInstrumentationPointExistenceForAction(sip, innerAA);
                }
            }
            findAIP(sip, branch);
        } else if (aa instanceof InternalAction || aa instanceof ExternalCallAction
                || aa instanceof InternalCallAction) {
            findAIP(sip, aa);

        } else if (aa instanceof StartAction || aa instanceof StopAction) {
            findAIP(sip, aa);
        }
    }

    private ServiceInstrumentationPoint findSIP(InstrumentationModel im, ResourceDemandingSEFF seff) {
        for (var sip : im.getPoints()) {
            if (sip.getService()
                .getId()
                .equals(seff.getId())) {
                currentEvalResult.setNumberMatchedSIP(currentEvalResult.getNumberMatchedSIP() + 1);
                return sip;
            }
        }
        currentEvalResult.getUnmatchedSEFFs()
            .add(seff.getId());
        return null;
    }

    private void findAIP(ServiceInstrumentationPoint sip, AbstractAction aa) {
        if (sip != null) {
            for (var aip : sip.getActionInstrumentationPoints()) {
                if (aip.getAction()
                    .getId()
                    .equals(aa.getId())) {
                    currentEvalResult.setNumberMatchedAIP(currentEvalResult.getNumberMatchedAIP() + 1);
                    if (aip.isActive()) {
                        currentEvalResult.setNumberActiveAIP(currentEvalResult.getNumberActiveAIP() + 1);
                    }
                    return;
                }
            }
        }
        currentEvalResult.getUnmatchedActions()
            .add(aa.getId());
    }

    private ResourceDemandingSEFF findSeffForSip(ServiceInstrumentationPoint sip, Repository repo) {
        var sipSeffId = sip.getService()
            .getId();

        for (RepositoryComponent com : repo.getComponents__Repository()) {
            if (com instanceof BasicComponent) {
                BasicComponent basicCom = (BasicComponent) com;
                for (ServiceEffectSpecification seff : basicCom.getServiceEffectSpecifications__BasicComponent()) {
                    if (seff instanceof ResourceDemandingSEFF) {
                        ResourceDemandingSEFF rdseff = (ResourceDemandingSEFF) seff;
                        if (rdseff.getId()
                            .equals(sipSeffId)) {
                            return rdseff;
                        }
                    }
                }
            }
        }
        currentEvalResult.getUnmatchedSIPs()
            .add(sip.getId());
        return null;
    }

    private AbstractAction findActionForAip(ActionInstrumentationPoint aip, Repository repo) {
        var aipActionId = aip.getAction()
            .getId();

        for (var action : EcoreUtil2.getAllContentsOfType(repo, AbstractAction.class)) {
            if (action.getId()
                .equals(aipActionId)) {
                return action;
            }
        }
        currentEvalResult.getUnmatchedAIPs()
            .add(aip.getId());
        return null;
    }

    /*
     * We don't operate on some types of Abstract actions. We filter them out here
     */
    private boolean actionIsRelevant(AbstractAction action) {
        if (action instanceof StartAction || action instanceof StopAction) {
            return false;
        }
        return true;
    }

    private void checkChangedActions(Repository repo, Repository previousRepo, InstrumentationModel im) {
        var comparison = PCMModelComparator.compareRepositoryModelsIDBased(repo, previousRepo);
        
        
        ArrayList<String> addedActions = new ArrayList<>();
        
        // add the added actions to the changed actions
        comparison.getMatches()
            .forEach(m -> {
                m.getAllSubmatches()
                    .forEach(sm -> {
                        if (sm.getLeft() != null && sm.getRight() == null && sm.getLeft() instanceof AbstractAction) {
                            AbstractAction leftElement = (AbstractAction) sm.getLeft();
                            if (actionIsRelevant(leftElement) && this.hasResourceDemandingSEFFAsParent(leftElement)) {
                                addedActions.add(leftElement.getId());
                            }
                        }
                    });
            });
        currentEvalResult.setNumberAddedActions(addedActions.size());

        ArrayList<String> unmatchedAddedActions = new ArrayList<>();
        unmatchedAddedActions.addAll(addedActions);

        ArrayList<String> unmatchedChangedActions = new ArrayList<>();
        unmatchedChangedActions.addAll(currentEvalResult.generateChangedActions());

        List<String> unmatchedActiveAIPs = new ArrayList<>();

        for (var sip : im.getPoints()) {
            for (var aip : sip.getActionInstrumentationPoints()) {
                if (aip.isActive()) {
                    var actionId = aip.getAction()
                        .getId();

                    // added actions
                    if (unmatchedAddedActions.contains(actionId)) {
                        unmatchedAddedActions.remove(actionId);
                        currentEvalResult.setNumberMatchedActiveAIP(currentEvalResult.getNumberMatchedActiveAIP() + 1);
                    // changed actions
                    } else if (unmatchedChangedActions.contains(actionId)) {
                        unmatchedChangedActions.remove(actionId);
                        currentEvalResult.setNumberMatchedActiveAIP(currentEvalResult.getNumberMatchedActiveAIP() + 1);
                    } else {
                        unmatchedActiveAIPs.add(aip.getId());
                    }
                }
            }
        }

        currentEvalResult.getUnmatchedChangedActions()
            .addAll(unmatchedChangedActions);
        currentEvalResult.getUnmatchedAddedActions()
            .addAll(unmatchedAddedActions);
        currentEvalResult.getUnmatchedActiveAIPs()
            .addAll(unmatchedActiveAIPs);
    }

    private boolean hasResourceDemandingSEFFAsParent(AbstractAction action) {
        EObject parent = action.eContainer();
        while (parent != null && (!(parent instanceof ResourceDemandingSEFF)
                && !(parent instanceof ResourceDemandingInternalBehaviour))) {
            parent = parent.eContainer();
        }
        return parent instanceof ResourceDemandingSEFF;
    }
}
