package cipm.consistency.initialisers.tests.dummy.packages;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiserPackage;

/**
 * An {@link IInitialiserPackage} implementation, which simulates a top-level
 * package containing 2 further sub-packages.
 * 
 * @author Alp Torac Genc
 * @see {@link cipm.consistency.initialisers.tests.dummy.packages} for the
 *      package layout.
 */
public class DummyTopLevelInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiserPackage> getSubPackages() {
		return this.initCol(new IInitialiserPackage[] { new DummyAggregateInitialiserPackageOne(),
				new DummyAggregateInitialiserPackageTwo() });
	}
}
