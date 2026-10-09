package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy {@link IInitialiser} interface that is in the middle of the type
 * hierarchy defined within this package. It represents an alternative for
 * {@link IDummyNonTerminalObjInitialiser} (does not have any dependencies to
 * it).
 * 
 * @author Alp Torac Genc
 */
public interface IDummyAlternateInitialiser extends IDummyTopLevelObjInitialiser {
	@Override
	public DummyNonTerminalObj instantiate();
}