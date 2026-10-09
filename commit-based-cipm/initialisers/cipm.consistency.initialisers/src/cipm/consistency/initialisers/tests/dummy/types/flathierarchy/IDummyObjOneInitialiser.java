package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser interface with a default modification method taking
 * multiple parameters, as well as the object to be modified.
 * 
 * @author Alp Torac Genc
 */
public interface IDummyObjOneInitialiser extends IInitialiser {
	@Override
	public DummyObjOne instantiate();

	public default boolean modificationMethodInInterface(DummyObjOne obj, Object param1, Object param2) {
		return true;
	}
}
