package cipm.consistency.initialisers.tests.dummy.packages;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiserPackage;

/**
 * An {@link IInitialiserPackage} implementation, which simulates a package
 * containing 1 further sub-package and no initialiser implementations.
 * 
 * @author Alp Torac Genc
 * @see {@link cipm.consistency.initialisers.tests.dummy.packages} for the
 *      package layout.
 */
public class DummyAggregateInitialiserPackageTwo implements IInitialiserPackage {
	@Override
	public Collection<IInitialiserPackage> getSubPackages() {
		return this.initCol(new IInitialiserPackage[] { new DummyInitialiserPackageCD() });
	}
}
