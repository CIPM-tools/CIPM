package cipm.consistency.initialisers.eobject;

import org.eclipse.emf.ecore.EObject;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;

/**
 * An interface for {@link IInitialiser} sub-types, whose purpose is to create
 * and modify {@link EObject} instances.
 * 
 * @author Alp Torac Genc
 */
public interface IEObjectInitialiser extends IInitialiser {
	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * <b>Note: The created instance may not be "valid" due to certain attributes
	 * not being set. Using proper {@link IInitialiserAdapter} instances on
	 * implementors can circumvent potential issues.</b>
	 * 
	 * @see {@link IInitialiserAdapter}, {@link IInitialiserAdapterStrategy}
	 */
	@Override
	public EObject instantiate();

	@Override
	public IEObjectInitialiser newInitialiser();

	/**
	 * {@inheritDoc}
	 * 
	 * @return The instance class associated with the EClass of the return value of
	 *         {@link #instantiate()}, or null if the said method returns null.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public default Class<? extends EObject> getInstanceClassOfInitialiser() {
		var obj = this.instantiate();
		return obj != null ? (Class<? extends EObject>) obj.eClass().getInstanceClass() : null;
	}
}
