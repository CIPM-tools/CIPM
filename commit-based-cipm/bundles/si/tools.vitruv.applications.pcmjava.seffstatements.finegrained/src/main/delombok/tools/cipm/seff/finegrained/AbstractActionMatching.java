package tools.cipm.seff.finegrained;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * A matching of two AbstractActions which represent the same or similar action.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class AbstractActionMatching {
	private AbstractAction newAbstractAction;
	private AbstractAction oldAbstractAction;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param newAbstractAction the new action of the matching.
	 * @param oldAbstractAction the old action of the matching.
	 */
	public AbstractActionMatching(AbstractAction newAbstractAction, AbstractAction oldAbstractAction) {
		this.newAbstractAction = newAbstractAction;
		this.oldAbstractAction = oldAbstractAction;
	}
	
	public AbstractAction getNewAbstractAction() {
		return newAbstractAction;
	}
	
	public void setNewAbstractAction(AbstractAction newAbstractAction) {
		this.newAbstractAction = newAbstractAction;
	}
	
	public AbstractAction getOldAbstractAction() {
		return oldAbstractAction;
	}
	
	public void setOldAbstractAction(AbstractAction oldAbstractAction) {
		this.oldAbstractAction = oldAbstractAction;
	}
}
