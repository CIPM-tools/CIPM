package cipm.consistency.initialisers.tests.dummy.packages;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

/**
 * An {@link IInitialiserPackage} implementation, which simulates a package
 * containing 1 initialiser implementation and no further sub-packages.
 * 
 * @author Alp Torac Genc
 * @see {@link cipm.consistency.initialisers.tests.dummy.packages} for the
 *      package layout.
 */
public class DummyInitialiserPackageA implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new DummyInitialiserA() });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { DummyInitialiserA.class });
	}
}
