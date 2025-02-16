package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.base.AbstractSimilarityChecker;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;

/**
 * A version of {@link AbstractSimilarityChecker}, which is adapted for
 * {@link EObject}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractComposedSwitchSimilarityChecker extends AbstractSimilarityChecker {
	/**
	 * Constructs an instance with a given {@link ISimilarityToolbox}.
	 * 
	 * @param st The {@link ISimilarityToolbox}, to which all incoming
	 *           {@link ISimilarityRequest} instances should be delegated to.
	 */
	public AbstractComposedSwitchSimilarityChecker(ISimilarityToolbox st) {
		super(st);
	}

	/**
	 * Creates and returns a new {@link IComposedSwitchAdapter}.
	 */
	protected IComposedSwitchAdapter createDefaultNewSwitch() {
		return (IComposedSwitchAdapter) this.handleSimilarityRequest(this.makeDefaultSwitchRequest());
	}

	@Override
	public Boolean isSimilar(Object element1, Object element2) {
		return (Boolean) this.handleSimilarityRequest(new SingleSimilarityCheckRequest((EObject) element1,
				(EObject) element2, this.createDefaultNewSwitch()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean areSimilar(Collection<Object> elements1, Collection<Object> elements2) {
		Collection<IComposedSwitchAdapter> sss = new ArrayList<IComposedSwitchAdapter>();

		// Null check to avoid NullPointerExceptions
		if (elements1 == elements2) {
			return Boolean.TRUE;
		}
		else if (elements1 == null ^ elements2 == null) {
			return Boolean.FALSE;
		}
		
		if (elements1.size() != elements2.size()) {
			return Boolean.FALSE;
		}
		
		for (int i = 0; i < elements1.size(); i++) {
			sss.add(this.createDefaultNewSwitch());
		}

		return (Boolean) this.handleSimilarityRequest(new MultipleSimilarityCheckRequest(
				(Collection<? extends EObject>) elements1, (Collection<? extends EObject>) elements2, sss));
	}

	/**
	 * Used by other methods in this instance and concrete implementors that create
	 * switches. This method can be overridden in the concrete implementors to
	 * create different {@link ISimilarityRequest} instances, which can be used to
	 * create different switches ({@link IComposedSwitchAdapter} instances).
	 * 
	 * @return A {@link ISimilarityRequest} to create a new switch.
	 */
	protected abstract ISimilarityRequest makeDefaultSwitchRequest();
}
