package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy {@link IInitialiser} interface that is in the middle of the type
 * hierarchy defined within this package. Introduces one default modification
 * method that only takes the object to be modified.
 * 
 * @author Alp Torac Genc
 */
public interface IDummyNonTerminalObjInitialiser extends IDummyTopLevelObjInitialiser {
	@Override
	public DummyNonTerminalObj instantiate();

	public default boolean someNonTerminalObjModificationMethod(DummyNonTerminalObj obj) {
		return true;
	}
}
