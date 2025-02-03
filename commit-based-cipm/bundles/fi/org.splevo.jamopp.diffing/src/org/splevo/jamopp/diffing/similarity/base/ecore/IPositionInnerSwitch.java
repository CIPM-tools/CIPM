package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * An interface that extends {@link IInnerSwitch} with methods, which are mutual
 * among the implementors of {@link IInnerSwitch} that additionally use a flag
 * to check statement positions in {@link EObject} instances they compare.
 * 
 * @author Alp Torac Genc
 */
public interface IPositionInnerSwitch extends IInnerSwitch {
	/**
	 * @return Whether this switch should take statement positions in
	 *         {@link EObject} instances it compares into account while comparing
	 *         them.
	 */
	public boolean shouldCheckStatementPosition();

	/**
	 * @return Whether the {@link EObject} instances are similar.
	 * @see {@link ISimilarityChecker}
	 */
	public default Boolean isSimilar(EObject eo1, EObject eo2) {
		return this.isSimilar(eo1, eo2, this.shouldCheckStatementPosition());
	}

	/**
	 * @param checkStatementPosition See {@link #shouldCheckStatementPosition()}
	 * @return Whether the {@link EObject} instances are similar, given the
	 *         checkStatementPosition flag.
	 * @see {@link ISimilarityChecker}
	 */
	public default Boolean isSimilar(EObject eo1, EObject eo2, boolean checkStatementPosition) {
		return (Boolean) this.handleSimilarityRequest(
				new SingleSimilarityCheckRequest(eo1, eo2, this.requestNewSwitch(checkStatementPosition)));
	}

	/**
	 * @return Whether the given lists are pairwise similar, using the given list of
	 *         {@link IComposedSwitchAdapter}.
	 * @see {@link ISimilarityChecker}
	 */
	public default Boolean areSimilar(Collection<? extends EObject> eos1, Collection<? extends EObject> eos2,
			Collection<? extends IComposedSwitchAdapter> sss) {
		return (Boolean) this.handleSimilarityRequest(new MultipleSimilarityCheckRequest(eos1, eos2, sss));
	}

	/**
	 * A version of {@link #areSimilar(Collection, Collection, Collection)} that
	 * first constructs new switches with the given csps.
	 * 
	 * @param csps A collection of checkStatementPosition flags (see
	 *             {@link #isSimilar(EObject, EObject, boolean)}). i-th flag in the
	 *             collection meant to be used for similarity checking i-th elements
	 *             of the given {@link EObject} collections.
	 * @return Whether the given collections are pairwise similar, using the given
	 *         collection of checkStatementPosition flags.
	 * 
	 * @see {@link ISimilarityChecker}
	 */
	public default Boolean areSimilar(Collection<? extends EObject> eos1, Collection<? extends EObject> eos2,
			List<Boolean> csps) {

		Collection<IComposedSwitchAdapter> sss = new ArrayList<IComposedSwitchAdapter>();

		if (csps == null)
			return null;

		csps.forEach((csp) -> sss.add((IComposedSwitchAdapter) this.requestNewSwitch(csp)));

		return this.areSimilar(eos1, eos2, sss);
	}

	/**
	 * @return Whether the given collections are pairwise similar, so whether i-th
	 *         element of both collections are similar for {@code i = 0, 1, ...}
	 * @see {@link ISimilarityChecker}
	 */
	public default Boolean areSimilar(Collection<? extends EObject> eos1, Collection<? extends EObject> eos2) {
		var csps = new ArrayList<Boolean>();

		if (eos1 == eos2) {
			return Boolean.TRUE;
		} else if (eos1 == null ^ eos2 == null) {
			return Boolean.FALSE;
		}

		if (eos1.size() != eos2.size()) {
			return Boolean.FALSE;
		}

		for (int i = 0; i < eos1.size(); i++) {
			csps.add(this.shouldCheckStatementPosition());
		}

		return this.areSimilar(eos1, eos2, csps);
	}

	/**
	 * @param checkStatementPosition See {@link #shouldCheckStatementPosition()}
	 * @return A new switch with the given checkStatementPosition flag.
	 */
	public IComposedSwitchAdapter requestNewSwitch(boolean checkStatementPosition);
}
