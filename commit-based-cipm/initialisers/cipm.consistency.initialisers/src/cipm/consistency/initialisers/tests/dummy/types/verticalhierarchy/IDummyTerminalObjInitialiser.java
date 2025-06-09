package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy {@link IInitialiser} interface that is at the bottom of the type
 * hierarchy defined within this package. Introduces one default modification
 * method that only takes the object to be modified.
 * 
 * @author Alp Torac Genc
 */
public interface IDummyTerminalObjInitialiser extends IDummyNonTerminalObjInitialiser {
	@Override
	public DummyTerminalObj instantiate();

	public default boolean someTerminalObjModificationMethod(DummyTerminalObj obj) {
		return true;
	}
}
