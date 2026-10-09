package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

/**
 * A dummy implementation of {@link IDummyAlternateInitialiser} that introduces
 * nothing new. Serves as an alternative to
 * {@link DummyNonTerminalObjInitialiser} (does not have any dependencies to
 * it).
 * 
 * @author Alp Torac Genc
 */
public class DummyAlternateInitialiser implements IDummyAlternateInitialiser {
	@Override
	public DummyAlternateInitialiser newInitialiser() {
		return new DummyAlternateInitialiser();
	}

	@Override
	public DummyNonTerminalObj instantiate() {
		return new DummyNonTerminalObj();
	}

	@Override
	public boolean initialise(Object obj) {
		return true;
	}
}