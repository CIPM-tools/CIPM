package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy {@link IInitialiser} interface that is at the top of the type
 * hierarchy defined within this package. Introduces one default modification
 * method that only takes the object to be modified.
 * 
 * @author Alp Torac Genc
 */
public interface IDummyTopLevelObjInitialiser extends IInitialiser {
	@Override
	public DummyTopLevelObj instantiate();

	public default boolean someTopLevelObjModificationMethod(DummyTopLevelObj obj) {
		return true;
	}
}
