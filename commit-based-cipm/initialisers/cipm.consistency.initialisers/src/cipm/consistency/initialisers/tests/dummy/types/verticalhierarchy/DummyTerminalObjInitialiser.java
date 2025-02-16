package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

/**
 * A dummy implementation of {@link IDummyTerminalObjInitialiser} that
 * introduces nothing new.
 * 
 * @author Alp Torac Genc
 */
public class DummyTerminalObjInitialiser implements IDummyTerminalObjInitialiser {
	@Override
	public DummyTerminalObjInitialiser newInitialiser() {
		return new DummyTerminalObjInitialiser();
	}

	@Override
	public DummyTerminalObj instantiate() {
		return new DummyTerminalObj();
	}

	@Override
	public boolean initialise(Object obj) {
		return true;
	}
}