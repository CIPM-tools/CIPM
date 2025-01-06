package tools.cipm.seff.finegrained;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * Describes the difference of two ResourceDemandingBehaviours regarding their contained actions.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class ResourceDemandingBehaviourDiff {
	private List<AbstractAction> deletedAbstractActions;
	private List<AbstractAction> addedAbstractActions;
	private List<AbstractActionMatching> modifiedAbstractActions;
	private List<AbstractActionMatching> unmodifiedAbstractActions;
	
	public ResourceDemandingBehaviourDiff() {
		deletedAbstractActions =  new ArrayList<>();
		addedAbstractActions = new ArrayList<>();
		modifiedAbstractActions = new ArrayList<>();
		unmodifiedAbstractActions = new ArrayList<>();
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
	
	public boolean hasOldAbstractActionMatching(AbstractAction oldAbstractAction) {
		for (AbstractActionMatching matching : getModifiedAbstractActions()) {
			if (oldAbstractAction == matching.getOldAbstractAction()) {
				return true;
			}
		}
		for (AbstractActionMatching matching : getUnmodifiedAbstractActions()) {
			if (oldAbstractAction == matching.getOldAbstractAction()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasNewAbstractActionMatching(AbstractAction newAbstractAction) {
		for (AbstractActionMatching matching : getModifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAbstractAction) {
				return true;
			}
		}
		for (AbstractActionMatching matching : getUnmodifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAbstractAction) {
				return true;
			}
		}
		return false;
	}
	
	public AbstractActionMatching getNewAbstractActionMatching(AbstractAction newAction) {
		for (var matching : getModifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAction) {
				return matching;
			}
		}
		for (var matching : getUnmodifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAction) {
				return matching;
			}
		}
		return null;
	}
}
