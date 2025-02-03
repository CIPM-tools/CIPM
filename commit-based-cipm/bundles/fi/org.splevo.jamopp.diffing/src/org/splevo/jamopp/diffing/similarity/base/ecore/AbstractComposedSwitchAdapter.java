package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.eclipse.emf.ecore.util.Switch;

/**
 * An abstract class that complements {@link IComposedSwitchAdapter} with an
 * attribute to contain the compare element mentioned there. It also provides
 * implementations for the methods from {@link IComposedSwitchAdapter}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractComposedSwitchAdapter extends ComposedSwitch<Boolean> implements IComposedSwitchAdapter {
	/** The object to compare the switched element with. */
	private EObject compareElement = null;

	/**
	 * @see {@link ComposedSwitch#ComposedSwitch()}
	 */
	public AbstractComposedSwitchAdapter() {

	}

	/**
	 * Constructs an instance with the given switches. <br>
	 * <br>
	 * Meant to be used while testing.
	 */
	protected AbstractComposedSwitchAdapter(Collection<Switch<Boolean>> switches) {
		super(switches);
	}

	/**
	 * Constructs an instance with the given switches. <br>
	 * <br>
	 * Meant to be used while testing.
	 */
	protected AbstractComposedSwitchAdapter(Switch<Boolean>[] switches) {
		this(List.of(switches));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return The object to compare the switched element with.
	 */
	@Override
	public EObject getCompareElement() {
		return this.compareElement;
	}

	@Override
	public Boolean compare(EObject eo1, EObject eo2) {
		this.compareElement = eo2;
		return this.doSwitch(eo1);
	}

	/**
	 * The default case for not explicitly handled elements always returns null to
	 * identify the open decision.
	 * 
	 * @param object The object to compare with the compare element.
	 * @return null
	 */
	@Override
	public Boolean defaultCase(EObject object) {
		return null;
	}
}
