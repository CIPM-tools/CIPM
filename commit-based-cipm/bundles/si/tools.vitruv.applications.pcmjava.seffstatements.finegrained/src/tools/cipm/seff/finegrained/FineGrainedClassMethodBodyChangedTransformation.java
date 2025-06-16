package tools.cipm.seff.finegrained;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.gast2seff.visitors.VisitorUtils;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import de.uka.ipd.sdq.identifier.Identifier;
import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.CorrespondenceModelUtil;
import tools.cipm.seff.extended.ExtendedClassMethodBodyChangedTransformation;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractor;

/**
 * Extends the incremental SEFF reconstruction by a fine-grained reconstruction
 * approach.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 * @author langhamm
 */
public class FineGrainedClassMethodBodyChangedTransformation extends ExtendedClassMethodBodyChangedTransformation {
	private static final Logger LOGGER = Logger
			.getLogger(FineGrainedClassMethodBodyChangedTransformation.class.getSimpleName());
	private final Method newMethod;
	private final BasicComponentFinding basicComponentFinder;
	private ResourceDemandingBehaviourDiff rdbDifference;
	private final SimilarityChecker similarityChecker;

	public FineGrainedClassMethodBodyChangedTransformation(final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		super(newMethod, basicComponentFinder, iFunctionClassificationStrategy, interfaceOfExternalCallFindingFactory,
				resourceDemandingBehaviourForClassMethodFinding);
		this.newMethod = newMethod;
		this.basicComponentFinder = basicComponentFinder;
		this.similarityChecker = new SimilarityChecker();
	}

	/**
	 * This method is called after a Java method body has been changed. It
	 * reconstructs the new method and merges the differences into a possible
	 * existing SEFF.
	 */
	@Override
	public void execute(final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final UserInteractor userInteracting) {
		if (!this.isArchitectureRelevantChange(correspondenceModel)) {
			LOGGER.debug("Change within the method " + this.newMethod + " is not an architecture-relevant change.");
			return;
		}

		// 1) Get old ResourceDemandingBehaviour.
		final ResourceDemandingBehaviour oldSEFF = this.findRdBehaviorToInsertElements(correspondenceModel);

		// 2) Create the new ResourceDemandingBehaviour.
		final ResourceDemandingBehaviour newSEFF = this.createNewResourceDemandingBehaviour(correspondenceModel);

		// 3) Calculate the difference between the old and new SEFF.
		this.calculateResourceDemandingBehaviourDiff(oldSEFF, newSEFF, correspondenceModel);

		// 4) Update the old ResourceDemandingBehaviour by merging the differences.
		this.mergeDifferences(oldSEFF, newSEFF);

		// 5) Create new correspondences between the SEFF elements and the method.
		this.createNewCorrespondences(correspondenceModel, oldSEFF);

		// 6) Create correspondences between the SEFF elements and statements.
		this.createCorrespondencesForAbstractActionsAndStatements(correspondenceModel, oldSEFF);
	}

	private ResourceDemandingBehaviour createNewResourceDemandingBehaviour(
			final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		ResourceDemandingBehaviour newSEFF = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);

		this.executeSoMoXForMethod(basicComponent, newSEFF);

		return newSEFF;
	}

	private void calculateResourceDemandingBehaviourDiff(ResourceDemandingBehaviour oldSEFF,
			ResourceDemandingBehaviour newSEFF, EditableCorrespondenceModelView<Correspondence> ci) {
		rdbDifference = new ResourceDemandingBehaviourDiff();

		List<AbstractAction> listOldAbstractActions = this.getRelevantAbstractActions(oldSEFF);
		List<AbstractAction> listNewAbstractActions = this.getRelevantAbstractActions(newSEFF);

		// If the old SEFF is empty, all new AbstractActions are added.
		if (listOldAbstractActions.size() == 0) {
			rdbDifference.getAddedAbstractActions().addAll(listNewAbstractActions);
			return;
		}

		// Find modified AbstractActions.
		this.matchNewAndOldSeff(oldSEFF, newSEFF, ci);

		// Find deleted AbstractActions.
		for (AbstractAction oldAbstractAction : listOldAbstractActions) {
			if (!rdbDifference.hasOldAbstractActionMatching(oldAbstractAction)) {
				rdbDifference.getDeletedAbstractActions().add(oldAbstractAction);
			}
		}

		// Find added AbstractActions.
		for (AbstractAction newAbstractAction : listNewAbstractActions) {
			if (!rdbDifference.hasNewAbstractActionMatching(newAbstractAction)) {
				rdbDifference.getAddedAbstractActions().add(newAbstractAction);
			}
		}
	}

	private List<AbstractAction> getRelevantAbstractActions(ResourceDemandingBehaviour seff) {
		List<AbstractAction> listAbstractActions = new ArrayList<>();
		for (AbstractAction aa : seff.getSteps_Behaviour()) {
			if (!(aa instanceof StartAction || aa instanceof StopAction)) {
				listAbstractActions.add(aa);
			}
		}
		return listAbstractActions;
	}

	private void matchNewAndOldSeff(ResourceDemandingBehaviour oldSEFF, ResourceDemandingBehaviour newSEFF,
			EditableCorrespondenceModelView<Correspondence> ci) {

		List<AbstractAction> oldAbstractActions = this.getRelevantAbstractActions(oldSEFF);
		Map<AbstractAction, List<Statement>> newSeffStatements = this.getNewSeffElementStatements(newSEFF);

		for (Map.Entry<AbstractAction, List<Statement>> entry : newSeffStatements.entrySet()) {
			AbstractAction newAbstractAction = entry.getKey();
			List<Statement> newAbstractActionStatements = entry.getValue();

			for (AbstractAction oldAbstractAction : oldAbstractActions) {
				// Get corresponding statements for old AbstractAction.
				List<Statement> oldAbstractActionStatements = CorrespondenceModelUtil.getCorrespondingEObjects(ci,
						oldAbstractAction, Statement.class);

				int similarStatementsCount = this.compareAbstractActions(newAbstractAction, oldAbstractAction,
						newAbstractActionStatements, oldAbstractActionStatements);

				if (newAbstractActionStatements.size() == similarStatementsCount) {
					rdbDifference.getUnmodifiedAbstractActions()
							.add(new AbstractActionMatching(newAbstractAction, oldAbstractAction));
					break;
				} else if (similarStatementsCount != 0) {
					rdbDifference.getModifiedAbstractActions()
							.add(new AbstractActionMatching(newAbstractAction, oldAbstractAction));
					break;
				}
			}
		}
	}

	private Map<AbstractAction, List<Statement>> getNewSeffElementStatements(ResourceDemandingBehaviour newSEFF) {
		Map<AbstractAction, List<Statement>> newSeffStatements = new HashMap<>();
		List<SeffElementSourceCodeLink> seffStatementLinks = this.getSourceCodeDecoratorRepository()
				.getSeffElementsSourceCodeLinks();
		for (SeffElementSourceCodeLink seffStatementLink : seffStatementLinks) {
			List<Statement> listStatement = seffStatementLink.getStatement();
			newSeffStatements.put((AbstractAction) seffStatementLink.getSeffElement(), listStatement);
		}
		return newSeffStatements;
	}

	private int compareAbstractActions(AbstractAction newAbstractAction, AbstractAction oldAbstractAction,
			List<Statement> newAbstractActionStatements, List<Statement> oldAbstractActionStatements) {
		if (oldAbstractAction.getClass() != newAbstractAction.getClass()) {
			return 0;
		} else {
			// Compare statements.
			return this.compareStatements(newAbstractActionStatements, oldAbstractActionStatements);
		}
	}

	/**
	 * Returns the number of similar statements.
	 * 
	 * @param newStatements statements of the new method.
	 * @param oldStatements statements of the old method.
	 * @return the number of similar statements.
	 */
	private int compareStatements(List<Statement> newStatements, List<Statement> oldStatements) {
		int similarStatementsCount = 0;
		for (Statement newStatement : newStatements) {
			for (Statement oldStatement : oldStatements) {
				if (this.similarityChecker.isSimilar(newStatement, oldStatement)) {
					similarStatementsCount++;
				}
			}
		}
		return similarStatementsCount;
	}

	private void mergeDifferences(ResourceDemandingBehaviour rdBehavior, ResourceDemandingBehaviour newSeff) {
		final List<AbstractAction> steps = rdBehavior.getSteps_Behaviour();

		final boolean addStartAction = 0 == steps.size() || !(steps.get(0) instanceof StartAction);
		final boolean addStopAction = 0 == steps.size() || !(steps.get(steps.size() - 1) instanceof StopAction);
		if (addStartAction) {
			rdBehavior.getSteps_Behaviour().add(0, SeffFactory.eINSTANCE.createStartAction());
		}
		if (addStopAction) {
			rdBehavior.getSteps_Behaviour().add(SeffFactory.eINSTANCE.createStopAction());
		}

		this.removeOldAbstractActions(steps);

		this.addNewAbstractActions(steps, newSeff);

		this.addChangedAbstractActions(steps, newSeff);

		VisitorUtils.connectActions(rdBehavior);

		this.updateResourceDemandingInternalBehaviours(rdBehavior);
	}

	private void removeOldAbstractActions(List<AbstractAction> steps) {
		for (var action : rdbDifference.getDeletedAbstractActions()) {
			steps.remove(action);
		}
	}

	private void addNewAbstractActions(List<AbstractAction> steps, ResourceDemandingBehaviour newSeff) {
		List<AbstractAction> newSeffAbstractActions = new ArrayList<>(newSeff.getSteps_Behaviour());
		AbstractAction newAbstractActionPredecessor = null;
		for (AbstractAction newAbstractAction : newSeffAbstractActions) {
			if (!rdbDifference.hasNewAbstractActionMatching(newAbstractAction)) {
				if (newAbstractActionPredecessor == null) {
					// Case 1: the current element in the new SEFF has no matching and has no
					// predecessor.
					// ==> Add the element at the first position of the old SEFF.
					steps.add(1, newAbstractAction);
				} else {
					AbstractActionMatching predecessorMatching = rdbDifference
							.getNewAbstractActionMatching(newAbstractActionPredecessor);
					if (predecessorMatching != null) {
						// Case 2: the predecessor of the current element in the new SEFF has a matching
						// in the old SEFF.
						// ==> Use this matching of the predecessor to find a position for the current
						// element in the old SEFF.
						int predecessorMatchingLocation = steps.indexOf(
								predecessorMatching.getOldAbstractAction());
						steps.add(predecessorMatchingLocation + 1, newAbstractAction);
					} else {
						// Case 3: the predecessor of the current element has no matching in the old
						// SEFF.
						// ==> Use the predecessor as the predecessor in the old SEFF because the
						// predecessor
						// has already been added to the old SEFF in a previous iteration of this loop.
						int predecessorOldSeffLocation = steps.indexOf(newAbstractActionPredecessor);
						steps.add(predecessorOldSeffLocation + 1, newAbstractAction);
					}
				}
			}
			newAbstractActionPredecessor = newAbstractAction;
		}
	}

	private void addChangedAbstractActions(List<AbstractAction> oldActions, ResourceDemandingBehaviour newSeff) {
		for (AbstractAction newAction : newSeff.getSteps_Behaviour()) {
			var matching = rdbDifference.getNewAbstractActionMatching(newAction);
			if (matching != null && rdbDifference.getModifiedAbstractActions().contains(matching)) {
				int oldIndex = oldActions.indexOf(matching.getOldAbstractAction());
				oldActions.set(oldIndex, newAction);
			}
		}
	}

	private void createCorrespondencesForAbstractActionsAndStatements(EditableCorrespondenceModelView<Correspondence> correspondenceModel,
			ResourceDemandingBehaviour oldSeff) {
		List<SeffElementSourceCodeLink> seffElementSourceCodeLinks = this.getSourceCodeDecoratorRepository()
				.getSeffElementsSourceCodeLinks();
		for (SeffElementSourceCodeLink seffElementSourceCodeLink : seffElementSourceCodeLinks) {
			List<Statement> listStatements = seffElementSourceCodeLink.getStatement();
			Identifier seffElement = seffElementSourceCodeLink.getSeffElement();
			if (oldSeff.getSteps_Behaviour().contains(seffElement)) {
				correspondenceModel.addCorrespondenceBetween(
						List.of(seffElement), new ArrayList<>(listStatements), "");
			} else {
				var matching = rdbDifference.getNewAbstractActionMatching((AbstractAction) seffElement);
				if (matching != null) {
					correspondenceModel.addCorrespondenceBetween(List.of(matching.getOldAbstractAction()),
							new ArrayList<>(listStatements), "");
				}
			}
		}
	}

	private void updateResourceDemandingInternalBehaviours(ResourceDemandingBehaviour oldRDB) {
		if (!(oldRDB instanceof ResourceDemandingSEFF)) {
			return;
		}
		var oldSEFF = (ResourceDemandingSEFF) oldRDB;
		oldSEFF.getResourceDemandingInternalBehaviours().clear();
		for (var link : this.getSourceCodeDecoratorRepository().getMethodLevelResourceDemandingInternalBehaviorLink()) {
			InternalCallAction correspondingAction = null;
			for (var actionLink : this.getSourceCodeDecoratorRepository().getAbstractActionClassMethodLink()) {
				if (actionLink.getAbstractAction() instanceof InternalCallAction) {
					var iac = (InternalCallAction) actionLink.getAbstractAction();
					if (iac.getCalledResourceDemandingInternalBehaviour() == link
							.getResourceDemandingInternalBehaviour()) {
						correspondingAction = iac;
						break;
					}
				}
			}
			if (!oldSEFF.getSteps_Behaviour().contains(correspondingAction)) {
				AbstractActionMatching match = this.rdbDifference
						.getNewAbstractActionMatching(correspondingAction);
				if (match != null) {
					InternalCallAction oldCorrespondingAction =
							(InternalCallAction) match.getOldAbstractAction();
					oldCorrespondingAction
							.setCalledResourceDemandingInternalBehaviour(
									link.getResourceDemandingInternalBehaviour());
				}
			}
			oldSEFF.getResourceDemandingInternalBehaviours().add(link.getResourceDemandingInternalBehaviour());
		}
	}
}
