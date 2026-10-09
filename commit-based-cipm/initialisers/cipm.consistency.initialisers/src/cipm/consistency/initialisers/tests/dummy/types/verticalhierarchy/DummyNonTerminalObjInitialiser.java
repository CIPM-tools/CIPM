package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

/**
 * A dummy implementation of {@link IDummyNonTerminalObjInitialiser} that
 * introduces nothing new.
 * 
 * @author Alp Torac Genc
 */
public class DummyNonTerminalObjInitialiser implements IDummyNonTerminalObjInitialiser {
	@Override
	public DummyNonTerminalObjInitialiser newInitialiser() {
		return new DummyNonTerminalObjInitialiser();
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