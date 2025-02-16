package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;

/**
 * A dummy implementation of {@link IDummyTopLevelObjInitialiser} that
 * introduces nothing new.
 * 
 * @author Alp Torac Genc
 */
public class DummyTopLevelObjInitialiser implements IDummyTopLevelObjInitialiser {
	@Override
	public DummyTopLevelObjInitialiser newInitialiser() {
		return new DummyTopLevelObjInitialiser();
	}

	@Override
	public DummyTopLevelObj instantiate() {
		return new DummyTopLevelObj();
	}

	@Override
	public boolean initialise(Object obj) {
		return true;
	}
}
