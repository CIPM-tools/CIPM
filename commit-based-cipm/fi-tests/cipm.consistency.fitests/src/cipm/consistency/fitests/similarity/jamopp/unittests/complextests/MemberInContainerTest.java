package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.params.JaMoPPInitialiserParameters;
import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;
import cipm.consistency.initialisers.jamopp.members.IMemberInitialiser;

/**
 * Tests whether {@link MemberContainer} implementors' similarity is computed as
 * expected, if they contain different types of {@link Member} instances.
 * {@link Member} instances are added as members to {@link MemberContainer}s in
 * some tests and as default members in tests in others. <br>
 * <br>
 * There are differences between this test class and the
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests.MemberContainerTest}.
 * This test class checks the similarity of 2 {@link MemberContainer} instances
 * of the same type but with varying {@link Member} instances. The latter only
 * tests the similarity of {@link MemberContainer} instances of the same type
 * with the same {@link Member} instances.<br>
 * <br>
 * <b>This test class is overshadowed by neither
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.impltests} nor
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests},
 * because the type of the {@link MemberContainer} containing a certain
 * {@link Member} can indirectly influence the similarity checking result of
 * both {@link MemberContainer} instances and {@link Member} instances (via
 * qualified name differences for instance).</b>
 * 
 * @author Alp Torac Genc
 */
public class MemberInContainerTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @return A list of all initialisers that implement {@link IMemberInitialiser}.
	 *         If an initialiser is adaptable, it will be adapted. Non-adaptable
	 *         initialisers will be unaffected.
	 */
	private static List<IMemberInitialiser> getAllMemberInitInstances() {
		var res = new ArrayList<IMemberInitialiser>();
		var inits = new JaMoPPInitialiserParameters().getEachInitialiserOnceBySuper(IMemberInitialiser.class);
		inits.forEach((i) -> res.add(((IMemberInitialiser) i)));
		return res;
	}

	/**
	 * @return A list of all initialisers that implement
	 *         {@link IMemberContainerInitialiser}. If an initialiser is adaptable,
	 *         it will be adapted. Non-adaptable initialisers will be unaffected.
	 */
	private static List<IMemberContainerInitialiser> getAllMemberContainerInitInstances() {
		var res = new ArrayList<IMemberContainerInitialiser>();
		var inits = new JaMoPPInitialiserParameters().getEachInitialiserOnceBySuper(IMemberContainerInitialiser.class);
		inits.forEach((i) -> res.add(((IMemberContainerInitialiser) i)));
		return res;
	}

	/**
	 * @return Parameters for the test methods in this test class. Refer to their
	 *         documentation for more information.
	 */
	private static Stream<Arguments> getMemConMemPairs() {
		var res = new ArrayList<Arguments>();

		for (var memConInit : getAllMemberContainerInitInstances()) {
			for (var memInit1 : getAllMemberInitInstances()) {
				for (var memInit2 : getAllMemberInitInstances()) {
					var displayName = memInit1.getClass().getSimpleName() + " - " + memInit2.getClass().getSimpleName()
							+ " in " + memConInit.getClass().getSimpleName();
					res.add(Arguments.of(displayName, memConInit, memInit1, memInit2));
				}
			}
		}

		return res.stream();
	}

	/**
	 * Tests whether 2 {@link MemberContainer} instances of the same type are
	 * considered to be similar, if certain {@link Member} instances are added to
	 * them as ordinary members ({@code via memConInit.addMember(member)}).
	 * 
	 * @param displayName The display name of the test
	 * @param memConInit  The initialiser that will be used to instantiate both
	 *                    member containers
	 * @param memInit1    The member that will be added to the first member
	 *                    container instance (as ordinary member)
	 * @param memInit2    The member that will be added to the second member
	 *                    container instance (as ordinary member)
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("getMemConMemPairs")
	public void testMembersInContainers(String displayName, IMemberContainerInitialiser memConInit,
			IMemberInitialiser memInit1, IMemberInitialiser memInit2) {
		var mem1 = memInit1.instantiate();
		var mem2 = memInit2.instantiate();

		var memCon1 = memConInit.instantiate();
		Assertions.assertTrue(memConInit.initialise(memCon1));
		var memCon2 = memConInit.instantiate();
		Assertions.assertTrue(memConInit.initialise(memCon2));

		this.assertSimilarityResult(memCon1, memCon2, true);

		Assertions.assertTrue(memConInit.addMember(memCon1, mem1));
		Assertions.assertTrue(memConInit.addMember(memCon2, mem2));

		this.assertSimilarityResult(memCon1, memCon2,
				mem1.getClass().equals(mem2.getClass()) || (this.getExpectedSimilarityResult(memCon1.getClass(),
						MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS)
						&& this.getExpectedSimilarityResult(memCon2.getClass(),
								MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS)));
	}

	/**
	 * Tests whether 2 {@link MemberContainer} instances of the same type are
	 * considered to be similar, if certain {@link Member} instances are added to
	 * them as default members ({@code via memConInit.addDefaultMember(member)}).
	 *
	 * @param displayName The display name of the test
	 * @param memConInit  The initialiser that will be used to instantiate both
	 *                    member containers
	 * @param memInit1    The member that will be added to the first member
	 *                    container instance (as default member)
	 * @param memInit2    The member that will be added to the second member
	 *                    container instance (as default member)
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("getMemConMemPairs")
	public void testDefaultMembersInContainers(String displayName, IMemberContainerInitialiser memConInit,
			IMemberInitialiser memInit1, IMemberInitialiser memInit2) {
		var mem1 = memInit1.instantiate();
		var mem2 = memInit2.instantiate();

		var memCon1 = memConInit.instantiate();
		Assertions.assertTrue(memConInit.initialise(memCon1));
		var memCon2 = memConInit.instantiate();
		Assertions.assertTrue(memConInit.initialise(memCon2));

		this.assertSimilarityResult(memCon1, memCon2, true);

		Assertions.assertTrue(memConInit.addDefaultMember(memCon1, mem1));
		Assertions.assertTrue(memConInit.addDefaultMember(memCon2, mem2));

		this.assertSimilarityResult(memCon1, memCon2,
				mem1.getClass().equals(mem2.getClass()) || (this.getExpectedSimilarityResult(memCon1.getClass(),
						MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS)
						&& this.getExpectedSimilarityResult(memCon2.getClass(),
								MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS)));
	}
}
