package cipm.consistency.initialisers.tests.dummy.packages;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

/**
 * An {@link IInitialiserPackage} implementation, which simulates a package
 * containing 2 initialiser implementation and no further sub-packages.
 * 
 * @author Alp Torac Genc
 * @see {@link cipm.consistency.initialisers.tests.dummy.packages} for the
 *      package layout.
 */
public class DummyInitialiserPackageCD implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new DummyInitialiserC(), new DummyInitialiserD() });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { DummyInitialiserC.class, DummyInitialiserD.class });
	}
}
