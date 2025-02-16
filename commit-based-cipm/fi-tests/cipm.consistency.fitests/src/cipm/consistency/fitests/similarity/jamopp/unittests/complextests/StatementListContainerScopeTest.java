package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.emftext.language.java.statements.BlockContainer;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementListContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.IJaMoPPEObjectInitialiser;
import cipm.consistency.initialisers.jamopp.statements.BlockInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IStatementInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IStatementListContainerInitialiser;

/**
 * Tests whether similarity checking detects differences originating from
 * changes to validity scopes, such as those concerning
 * {@link LocalVariableStatement}.
 * 
 * @author Alp Torac Genc
 */
@Disabled("Until these scenarios are properly addressed")
public class StatementListContainerScopeTest extends AbstractJaMoPPSimilarityTest implements UsesStatements {

	/**
	 * Generates all possible combinations (nestedConInit, placeholderConInit). <br>
	 * <br>
	 * Check other test methods' commentary for more information.
	 */
	private static Stream<Arguments> genTestParams() {
		var args = new ArrayList<Arguments>();
		var nestedConInits = List
				.of(AbstractJaMoPPSimilarityTest.getNonAdaptedInitialisersFor(IStatementListContainerInitialiser.class)
						.stream().filter((i) -> IStatementInitialiser.class.isAssignableFrom(i.getClass()))
						.toArray(IJaMoPPEObjectInitialiser[]::new));
		var placeholderConInits = AbstractJaMoPPSimilarityTest
				.getNonAdaptedInitialisersFor(IStatementListContainerInitialiser.class);
		for (var nestedCon : nestedConInits) {
			var displayName = nestedCon.getClass().getSimpleName() + " (nestedCon) in ";
			for (var placeholderCon : placeholderConInits) {
				args.add(Arguments.of(nestedCon, placeholderCon, displayName + placeholderCon.getClass().getSimpleName() + " (placeholderCon)"));
			}
		}
		return args.stream();
	}

	/**
	 * @return Creates "count" many {@link Statement} instances that are pairwise
	 *         not similar, currently {@link LocalVariableStatement} instances.
	 */
	private Statement[] createDistinctSts(int count) {
		var sts = new Statement[count];

		for (int i = 0; i < sts.length; i++) {
			sts[i] = this.createMinimalLVS("lvs" + i);
		}

		for (int i = 0; i < sts.length; i++) {
			for (int j = 0; j < sts.length; j++) {
				Assertions.assertEquals(this.isSimilar(sts[i], sts[j]), i == j);
			}
		}

		return sts;
	}

	/**
	 * Adds a {@link Block} instance to slc, so that multiple statements can be
	 * added to it. Does nothing if there already is a block in slc.
	 */
	private void addBlockIfNecessary(StatementListContainer slc, IStatementListContainerInitialiser slcInit) {
		if (!slcInit.canContainStatements(slc)) {
			var block = new BlockInitialiser().instantiate();
			if (slc instanceof StatementContainer) {
				((StatementContainer) slc).setStatement(block);
			} else if (slc instanceof BlockContainer) {
				((BlockContainer) slc).setBlock(block);
			}
		}
	}

	/**
	 * Adds statements in sts within the range [start, end) (start included, end
	 * excluded) to slc. <br>
	 * <br>
	 * Adds a block to slc first, if statements cannot be added to it in its current
	 * form.
	 */
	private void addStatementsInRange(StatementListContainer slc, IStatementListContainerInitialiser slcInit,
			Statement[] sts, int start, int end) {
		this.addBlockIfNecessary(slc, slcInit);
		for (int i = start; i < end; i++) {
			slcInit.addStatement(slc, sts[i]);
		}
		Assertions.assertEquals(end - start, slc.getStatements().size());
	}

	/**
	 * Generates:
	 * <ul>
	 * <li>An array of {@link LocalVariableStatement} instances {@code sts} of size
	 * {@code stsLen}
	 * <li>A container ({@code nestedCon}) and adds
	 * {@code sts[nestedConStart]...sts[nestedConEnd - 1]} to it. nestedCon is only
	 * then generated, if {@code nestedConEnd > nestedConStart}.
	 * <li>A container ({@code placeholderCon}) and adds remaining elements of
	 * {@code sts} and {@code nestedCon} to it, in the following order:
	 * {@code sts[0] ... sts[nestedConStart-1] nestedCon sts[nestedConEnd] ... sts[stsLen]}
	 * </ul>
	 */
	private Statement[] setupForTest(IStatementListContainerInitialiser placeholderConInit,
			IStatementListContainerInitialiser nestedConInit, int nestedConStart, int nestedConEnd, int stsLen) {

		var placeholderCon = placeholderConInit.instantiate();
		this.addBlockIfNecessary(placeholderCon, placeholderConInit);

		// Only generate nestedCon, if any statements are to be in its scope
		StatementListContainer nestedCon = null;
		if (nestedConEnd - nestedConStart > 0) {
			nestedCon = nestedConInit.instantiate();
		}

		var sts = this.createDistinctSts(stsLen);

		/*
		 * Place the elements of sts, which are not supposed to be in nestedCon, as well
		 * as nestedCon into placeholderCon, so that similarity checking can consider
		 * statement positions. Make sure to add them in the correct order to not mess
		 * up the intended ordering.
		 */
		for (int i = 0; i < nestedConStart; i++) {
			placeholderConInit.addStatement(placeholderCon, sts[i]);
		}
		if (nestedCon != null) {
			placeholderConInit.addStatement(placeholderCon, (Statement) nestedCon);
			this.addStatementsInRange(nestedCon, nestedConInit, sts, nestedConStart, nestedConEnd);
		}
		for (int i = nestedConEnd; i < stsLen; i++) {
			placeholderConInit.addStatement(placeholderCon, sts[i]);
		}

		// Make sure that the statements are in the correct container
		for (int i = 0; i < stsLen; i++) {
			var stCon = sts[i].eContainer();
			if (i >= nestedConStart && i < nestedConEnd) {
				// Account for the potential Block instance between nestedCon and sts[i]
				Assertions.assertTrue(stCon == nestedCon || stCon.eContainer() == nestedCon);
			} else {
				// Account for the potential Block instance between nestedCon and sts[i]
				Assertions.assertTrue(stCon == placeholderCon || stCon.eContainer() == placeholderCon);
			}
		}

		// Make sure that the statement count of containers is correct
		var expectedNestedConStCount = nestedConEnd - nestedConStart;
		Assertions.assertTrue((nestedCon != null) == expectedNestedConStCount > 0);
		// Account for placeholderCon also containing nestedCon (if it exists)
		var expectedPlaceholderConStCount = expectedNestedConStCount == 0 ? stsLen
				: stsLen - expectedNestedConStCount + 1;
		if (nestedCon != null) {
			// Account for the potential Block instance between placeholderCon and nestedCon
			Assertions.assertTrue(
					nestedCon.eContainer() == placeholderCon || nestedCon.eContainer().eContainer() == placeholderCon);
			Assertions.assertEquals(expectedNestedConStCount, nestedCon.getStatements().size());
		}
		Assertions.assertEquals(expectedPlaceholderConStCount, placeholderCon.getStatements().size());

		return sts;
	}

	/**
	 * Ensures that similarity checking detects differences caused by changes to the
	 * validity scope of {@link LocalVariableStatement} (LVS). <br>
	 * <br>
	 * For both sides: <br>
	 * <br>
	 * Generates a group of LVS instances, places them in a placeholderCon (so that
	 * they have a position within a container) and then nests a sub-group of the
	 * LVS instances in a further container (nestedCon) instantiated by
	 * containerInit. Then checks the similarity of all possible such sub-group
	 * combinations. <br>
	 * <br>
	 * The size of the said group of LVS instances (stsLen) should be {@code >= 5}:
	 * <br>
	 * <br>
	 * Similarity checking considers the statement itself, its predecessor and its
	 * successor. With 3 statements, it is possible to cover all basic cases.
	 * However, since the statements are nested within nestedCon, all said cases
	 * also have to be covered within nestedCon, as well as with the nestedCon
	 * itself as a statement within placeholderCon. This makes the following the
	 * most general form of statement placement, where st_i are statements: <br>
	 * <br>
	 * {@code placeholderCon(st_0 ... st_n nestedCon(st_n+1 ... st_m) st_m+1 ... st_k)}
	 * <br>
	 * <br>
	 * With 5 statements:
	 * {@code placeholderCon(st_0 nestedCon(st_1 st_2 st_3) st_4)} <br>
	 * <br>
	 * {@code st_i} are {@link Statement} instances and can therefore be replaced
	 * with instances that implement both {@link StatementListContainer} and
	 * {@link Statement}. Since similarity checking currently only considers the
	 * direct container of such statements ({@code st.eContainer()}) during
	 * statement position checks, this construction covers all possible ways
	 * statements can be nested within one another.
	 * 
	 * @param displayName        The name of the tests' display
	 * @param nestedConInit      The initialiser responsible for instantiating the
	 *                           nestedCon. <b>Must also implement
	 *                           {@link IStatementInitialiser}, because nestedCon
	 *                           itself will also be used as a Statement and nested
	 *                           within placeholderCon.</b>
	 * @param placeholderConInit The initialiser responsible for instantiating the
	 *                           placeholderCon
	 */
	@ParameterizedTest(name = "{2}")
	@MethodSource("genTestParams")
	public void testStatementListContainingStatementScope(IStatementListContainerInitialiser nestedConInit,
			IStatementListContainerInitialiser placeholderConInit, String displayName) {

		/*
		 * Block causes issues, because block instances are always assumed to be
		 * similar. Fixing the scope issue of LocalVariableStatement should fix these
		 * cases too.
		 * 
		 * If types of nestedConInit and placeholderConInit are equal test fails,
		 * because the containers (on their own) are similar. Fixing the scope issue of
		 * LocalVariableStatement should fix these cases too.
		 */

		var stsLen = 5;

		/*
		 * Include stsLen as a value for nestedConXEnd variables, since stsLen case will
		 * be sorted out in test setup.
		 */
		for (int nestedCon1Start = 0; nestedCon1Start <= stsLen; nestedCon1Start++) {
			for (int nestedCon1End = nestedCon1Start; nestedCon1End <= stsLen; nestedCon1End++) {

				var sts1 = this.setupForTest(placeholderConInit, nestedConInit, nestedCon1Start, nestedCon1End, stsLen);

				for (int nestedCon2Start = 0; nestedCon2Start <= stsLen; nestedCon2Start++) {
					for (int nestedCon2End = nestedCon2Start; nestedCon2End <= stsLen; nestedCon2End++) {

						var sts2 = this.setupForTest(placeholderConInit, nestedConInit, nestedCon2Start, nestedCon2End,
								stsLen);

						for (int i = 0; i < stsLen; i++) {
							var st1 = sts1[i];
							var st2 = sts2[i];

							var blockRelationSame =
									// Whether both st1 and st2 are inside / outside the block
									!(inIndexRange(i, nestedCon1Start, nestedCon1End)
											^ inIndexRange(i, nestedCon2Start, nestedCon2End)) && (
									// Whether both st1 and st2 have a / have no preceding statement in block range
									!(inIndexRange(i - 1, nestedCon1Start, nestedCon1End)
											^ inIndexRange(i - 1, nestedCon2Start, nestedCon2End)) ||
									// Whether both st1 and st2 have a / have no proceeding statement in block
									// range
											!(inIndexRange(i + 1, nestedCon1Start, nestedCon1End)
													^ inIndexRange(i + 1, nestedCon2Start, nestedCon2End)));

							Assertions.assertEquals(blockRelationSame, this.isSimilar(st1, st2));
							Assertions.assertEquals(blockRelationSame, this.isSimilar(st2, st1));
						}
					}
				}
			}
		}
	}

	private boolean inIndexRange(int idx, int start, int end) {
		return idx >= start && idx < end;
	}
}
