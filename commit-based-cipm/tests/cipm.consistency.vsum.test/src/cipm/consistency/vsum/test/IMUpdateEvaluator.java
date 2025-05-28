package cipm.consistency.vsum.test;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.tools.evaluation.data.IMEvaluationData;

/**
 * Evaluated the update of the extended IM.
 * 
 * @author Martin Armbruster
 */
public class IMUpdateEvaluator {
	private IMEvaluationData currentEvalResult;
	
	public void evaluateIMUpdate(Repository repo, InstrumentationModel im, IMEvaluationData evalData, String rootDir) {
		currentEvalResult = evalData;
		
		for (TreeIterator<EObject> iter = im.eAllContents(); iter.hasNext(); iter.next()) {
			currentEvalResult.setNumberAllIP(currentEvalResult.getNumberAllIP() + 1);
		}
		
		for (RepositoryComponent com : repo.getComponents__Repository()) {
			if (com instanceof BasicComponent) {
				BasicComponent basicCom = (BasicComponent) com;
				for (ServiceEffectSpecification seff
						: basicCom.getServiceEffectSpecifications__BasicComponent()) {
					if (seff instanceof ResourceDemandingSEFF) {
						ResourceDemandingSEFF rdseff = (ResourceDemandingSEFF) seff;
						var sip = findSIP(im, rdseff);
						if (sip != null) {
							checkActionInstrumentationPoints(sip, rdseff);
						}
					}
				}
			}
		}
		
		currentEvalResult.setNumberAIP(currentEvalResult.getNumberAllIP() - currentEvalResult.getNumberSIP());
		currentEvalResult.setNumberDeactivatedAIP(currentEvalResult.getNumberAIP()
				- currentEvalResult.getNumberActivatedAIP());
		if (currentEvalResult.getNumberAllIP() != 0) {
			currentEvalResult.setDeactivatedIPAllIPRatio((double) currentEvalResult.getNumberDeactivatedAIP()
					/ currentEvalResult.getNumberAllIP());
		}
		if (currentEvalResult.getNumberAIP() != 0) {
			currentEvalResult.setDeactivatedAIPAllAIPRatio((double) currentEvalResult.getNumberDeactivatedAIP()
					/ currentEvalResult.getNumberAIP());
		}
		
		this.checkChangedActions(repo, rootDir, im);
	}
	
	private ServiceInstrumentationPoint findSIP(InstrumentationModel im, ResourceDemandingSEFF seff) {
		for (var sip : im.getPoints()) {
			if (sip.getService() == seff) {
				currentEvalResult.setNumberMatchedIP(currentEvalResult.getNumberMatchedIP() + 1);
				currentEvalResult.setNumberSIP(currentEvalResult.getNumberSIP() + 1);
				return sip;
			}
		}
		currentEvalResult.getUnmatchedSEFFElements().add(seff.getId());
		return null;
	}
	
	private void checkActionInstrumentationPoints(ServiceInstrumentationPoint sip, ResourceDemandingSEFF seff) {
		for (AbstractAction aa : seff.getSteps_Behaviour()) {
			checkActionInstrumentationPoint(sip, aa);
		}
	}
	
	private void checkActionInstrumentationPoint(ServiceInstrumentationPoint sip, AbstractAction aa) {
		if (aa instanceof AbstractLoopAction) {
			AbstractLoopAction loop = (AbstractLoopAction) aa;
			for (AbstractAction innerAA : loop.getBodyBehaviour_Loop().getSteps_Behaviour()) {
				checkActionInstrumentationPoint(sip, innerAA);
			}
			findAIP(sip, loop);
		} else if (aa instanceof BranchAction) {
			BranchAction branch = (BranchAction) aa;
			for (var transition : branch.getBranches_Branch()) {
				for (AbstractAction innerAA
						: transition.getBranchBehaviour_BranchTransition().getSteps_Behaviour()) {
					checkActionInstrumentationPoint(sip, innerAA);
				}
			}
			findAIP(sip, branch);
		} else if (aa instanceof InternalAction || aa instanceof ExternalCallAction
				|| aa instanceof InternalCallAction) {
			findAIP(sip, aa);
		}
	}
	
	private void findAIP(ServiceInstrumentationPoint sip, AbstractAction aa) {
		for (var aip : sip.getActionInstrumentationPoints()) {
			if (aip.getAction() == aa) {
				currentEvalResult.setNumberMatchedIP(currentEvalResult.getNumberMatchedIP() + 1);
				if (aip.isActive()) {
					currentEvalResult.setNumberActivatedAIP(currentEvalResult.getNumberActivatedAIP() + 1);
				}
				return;
			}
		}
		currentEvalResult.getUnmatchedSEFFElements().add(aa.getId());
	}
	
	private void checkChangedActions(Repository repo1, String rootDir, InstrumentationModel im) {
		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(
				new File(rootDir, "Repository.repository").getAbsolutePath()), true);
		Repository repo2 = (Repository) res.getContents().get(0);
		var comparison = PCMModelComparator.compareRepositoryModelsIDBased(repo1, repo2);
		ArrayList<String> newActions = new ArrayList<>();
		comparison.getMatches().forEach(m -> {
			m.getAllSubmatches().forEach(sm -> {
				if (sm.getLeft() != null && sm.getRight() == null && sm.getLeft() instanceof AbstractAction) {
					AbstractAction leftElement = (AbstractAction) sm.getLeft();
					if (!(leftElement instanceof StartAction) && !(leftElement instanceof StopAction)
							&& this.hasResourceDemandingSEFFAsParent(leftElement)) {
						newActions.add(leftElement.getId());
					}
				}
			});
		});
		int difference = 0;
		for (var sip : im.getPoints()) {
			for (var aip : sip.getActionInstrumentationPoints()) {
				if (aip.isActive()) {
					if (newActions.contains(aip.getAction().getId())) {
						newActions.remove(aip.getAction().getId());
					} else {
						difference++;
					}
				}
			}
		}
		difference += newActions.size();
		currentEvalResult.setDifferenceChangedActionsActivatedAIP(difference);
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
