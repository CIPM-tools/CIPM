package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser interface that introduces nothing new. It only overrides
 * the return type of {@link IDummyObjThreeInitialiser#instantiate()}.
 * 
 * @author Alp Torac Genc
 */
public interface IDummyObjThreeInitialiser extends IInitialiser {
	@Override
	public DummyObjThree instantiate();
}
