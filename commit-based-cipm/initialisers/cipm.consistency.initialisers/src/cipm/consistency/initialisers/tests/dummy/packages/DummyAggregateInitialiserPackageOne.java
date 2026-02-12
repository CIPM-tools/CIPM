package cipm.consistency.initialisers.tests.dummy.packages;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

/**
 * An {@link IInitialiserPackage} implementation, which simulates a package
 * containing 1 initialiser implementation and 2 further sub-packages.
 * 
 * @author Alp Torac Genc
 * @see {@link cipm.consistency.initialisers.tests.dummy.packages} for the
 *      package layout.
 */
public class DummyAggregateInitialiserPackageOne implements IInitialiserPackage {
	@Override
	public Collection<IInitialiserPackage> getSubPackages() {
		return this
				.initCol(new IInitialiserPackage[] { new DummyInitialiserPackageA(), new DummyInitialiserPackageB() });
	}

	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new DummyInitialiserE() });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { DummyInitialiserE.class });
	}
}
