package cipm.consistency.commitintegration.diff.util.pcm.switches;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.IComposedSwitchAdapter;

import cipm.consistency.commitintegration.diff.util.pcm.IPCMRepositorySimilaritySwitch;

public class SimilaritySeffSwitch extends SeffSwitch<Boolean> implements IPCMPositionInnerSwitch {
	private IPCMRepositorySimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public IComposedSwitchAdapter getContainingSwitch() {
		return this.similaritySwitch;
	}
	
	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}
	
	public SimilaritySeffSwitch(
			IPCMRepositorySimilaritySwitch similaritySwitch,
			boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	private Boolean checkPositionInContainer(AbstractAction action1, AbstractAction action2) {
		ResourceDemandingBehaviour parent1 = (ResourceDemandingBehaviour) action1.eContainer();
		ResourceDemandingBehaviour parent2 = (ResourceDemandingBehaviour) action2.eContainer();

		return parent1.getSteps_Behaviour().indexOf(action1)
				== parent2.getSteps_Behaviour().indexOf(action2);
	}

	@Override
	public Boolean caseAbstractAction(AbstractAction action1) {
		AbstractAction action2 = (AbstractAction) getCompareElement();
		return checkPositionInContainer(action1, action2);
	}

	@Override
	public Boolean caseResourceDemandingBehaviour(ResourceDemandingBehaviour behav1) {
		ResourceDemandingBehaviour behav2 = (ResourceDemandingBehaviour) getCompareElement();
		return areSimilar(behav1.getSteps_Behaviour(), behav2.getSteps_Behaviour());
	}

	@Override
	public Boolean caseResourceDemandingSEFF(ResourceDemandingSEFF seff1) {
		ResourceDemandingSEFF seff2 = (ResourceDemandingSEFF) getCompareElement();

		return isSimilar(seff1.getDescribedService__SEFF(),
				seff2.getDescribedService__SEFF());
	}

	@Override
	public Boolean caseCollectionIteratorAction(CollectionIteratorAction action1) {
		CollectionIteratorAction action2 = (CollectionIteratorAction) getCompareElement();

		var result = isSimilar(
				action1.getParameter_CollectionIteratorAction(),
				action2.getParameter_CollectionIteratorAction());

		if (!result) {
			return Boolean.FALSE;
		}

		return checkPositionInContainer(action1, action2);
	}

	@Override
	public Boolean caseAbstractBranchTransition(AbstractBranchTransition transition1) {
		AbstractBranchTransition transition2 = (AbstractBranchTransition) getCompareElement();

		BranchAction parent1 = (BranchAction) transition1.eContainer();
		BranchAction parent2 = (BranchAction) transition2.eContainer();

		return parent1.getBranches_Branch().indexOf(transition1) == parent2.getBranches_Branch()
				.indexOf(transition2);
	}

	@Override
	public Boolean caseExternalCallAction(ExternalCallAction action1) {
		ExternalCallAction action2 = (ExternalCallAction) getCompareElement();

		var result = isSimilar(
				action1.getCalledService_ExternalService(),
				action2.getCalledService_ExternalService());

		if (!result) {
			return Boolean.FALSE;
		}

		return checkPositionInContainer(action1, action2);
	}
}