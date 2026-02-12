package cipm.consistency.initialisers.tests.dummy.types;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy {@link IInitialiser} implementation for {@link DummyModifiableObj},
 * which also offers methods to add them attributes.
 * 
 * @author Alp Torac Genc
 */
public class DummyModifiableObjInitialiser implements IInitialiser {

	@Override
	public DummyModifiableObjInitialiser newInitialiser() {
		return new DummyModifiableObjInitialiser();
	}

	@Override
	public DummyModifiableObj instantiate() {
		return new DummyModifiableObj();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * For this implementor, does nothing and returns true.
	 */
	@Override
	public boolean initialise(Object obj) {
		return true;
	}

	/**
	 * @see {@link DummyModifiableObj#addAttr(Object)}
	 */
	public boolean addAttr(DummyModifiableObj obj, Object attrToAdd) {
		return obj.addAttr(attrToAdd);
	}

	/**
	 * A variant of {@link #addAttr(DummyModifiableObj, Object)} for adding an array
	 * of attributes (each element within the given array will be added
	 * individually).
	 */
	public boolean addAttrs(DummyModifiableObj obj, Object[] attrsToAdd) {
		return this.doMultipleModifications(obj, attrsToAdd, this::addAttr);
	}
}