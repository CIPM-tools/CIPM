package tools.cipm.seff.finegrained;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * Describes the difference of two ResourceDemandingBehaviours regarding their contained actions.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class ResourceDemandingBehaviourDiff {
	private final List<AbstractAction> deletedAbstractActions = new ArrayList<>();
	private final List<AbstractAction> addedAbstractActions = new ArrayList<>();
	private final List<AbstractActionMatching> modifiedAbstractActions = new ArrayList<>();
	private final List<AbstractActionMatching> unmodifiedAbstractActions = new ArrayList<>();

	public void addDeletedAbstractAction(AbstractAction deletedAbstractAction) {
		deletedAbstractActions.add(deletedAbstractAction);
	}

	public void addAddedAbstractAction(AbstractAction addedAbstractAction) {
		addedAbstractActions.add(addedAbstractAction);
	}

	public void addAddedAbstractActions(List<AbstractAction> newAddedAbstractActions) {
		addedAbstractActions.addAll(newAddedAbstractActions);
	}

	public void addModifiedAbstractAction(AbstractActionMatching matching) {
		modifiedAbstractActions.add(matching);
	}

	public void addUnmodifiedAbstractAction(AbstractActionMatching matching) {
		unmodifiedAbstractActions.add(matching);
	}

	public List<AbstractAction> getDeletedAbstractActions() {
		return deletedAbstractActions;
	}

	public List<AbstractAction> getAddedAbstractActions() {
		return addedAbstractActions;
	}

	public List<AbstractActionMatching> getModifiedAbstractActions() {
		return modifiedAbstractActions;
	}

	public List<AbstractActionMatching> getUnmodifiedAbstractActions() {
		return unmodifiedAbstractActions;
	}

	public boolean isModified(AbstractActionMatching matching) {
		return modifiedAbstractActions.contains(matching);
	}

	public boolean hasOldAbstractActionMatching(AbstractAction oldAbstractAction) {
		return findMatching(matching -> matching.getOldAbstractAction() == oldAbstractAction) != null;
	}

	public boolean hasNewAbstractActionMatching(AbstractAction newAbstractAction) {
		return getNewAbstractActionMatching(newAbstractAction) != null;
	}

	public AbstractActionMatching getNewAbstractActionMatching(AbstractAction newAction) {
		return findMatching(matching -> matching.getNewAbstractAction() == newAction);
	}

	/**
	 * Searches the modified and, afterwards, the unmodified matchings for the first
	 * one satisfying the given condition.
	 *
	 * @param condition the condition a matching has to satisfy.
	 * @return the first matching satisfying the condition, or null if there is none.
	 */
	private AbstractActionMatching findMatching(Predicate<AbstractActionMatching> condition) {
		for (AbstractActionMatching matching : modifiedAbstractActions) {
			if (condition.test(matching)) {
				return matching;
			}
		}
		for (AbstractActionMatching matching : unmodifiedAbstractActions) {
			if (condition.test(matching)) {
				return matching;
			}
		}
		return null;
	}
}
