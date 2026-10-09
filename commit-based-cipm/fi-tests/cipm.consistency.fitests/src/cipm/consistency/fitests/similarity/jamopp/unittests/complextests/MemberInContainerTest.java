package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;
import org.emftext.language.java.members.MembersPackage;
import org.emftext.language.java.members.Method;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

/**
 * Tests whether {@link Member} implementors' similarity is computed as
 * expected, if they are contained in different types of {@link MemberContainer}
 * instances. {@link Member} instances are added as members to
 * {@link MemberContainer}s in some tests and as default members in tests in
 * others. <br>
 * <br>
 * There are differences between this test class and the
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests.MemberContainerTest}:
 * This test class checks the similarity of 2 {@link Member} instances of the
 * same type but with varying {@link MemberContainer} instances as their
 * container.<br>
 * <br>
 * <b>This test class is overshadowed by neither
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.impltests} nor
 * {@link cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests},
 * because the similarity of {@link Member} instances can be indirectly
 * influenced by their container. This is the case if Member instances support
 * qualified names and similarity checking accounts for their qualified name,
 * for instance. The reason is that the type of their container can change their
 * qualified name. </b>
 * 
 * @author Alp Torac Genc
 */
public class MemberInContainerTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @return Parameters for the test methods in this test class. See the
	 *         documentation of the class for more information.
	 * 
	 * @see {@link MemberInContainerTest}
	 */
	private static Stream<Arguments> genTestParams() {
		var res = new ArrayList<Arguments>();
		var memConClss = JaMoPPArguments.getAllConcreteClassesBySuper(MemberContainer.class);
		for (var memCls : JaMoPPArguments.getAllConcreteClassesBySuper(Member.class)) {
			for (var memConCls11 : memConClss) {
				for (var memConCls2 : memConClss) {
					res.add(Arguments.of(memCls, memConCls11, memConCls2,
							String.format("%s inside different containers (%s vs %s)", memCls.getSimpleName(),
									memConCls11.getSimpleName(), memConCls2.getSimpleName())));
				}
			}
		}

		return res.stream();
	}

	/**
	 * @see {@link MemberInContainerTest}
	 */
	@ParameterizedTest(name = "Member: {3}")
	@MethodSource("genTestParams")
	public void testMembersInContainers(Class<? extends Member> memCls, Class<? extends MemberContainer> memConCls1,
			Class<? extends MemberContainer> memConCls2, String displayName) {
		var member1 = getAPI().createNewX(memCls);
		var member2 = getAPI().createNewX(memCls);

		var memCon1 = getAPI().newX(memConCls1)
				.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, member1).createNow(memConCls1);
		var memCon2 = getAPI().newX(memConCls2)
				.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, member2).createNow(memConCls2);

		this.testSimilarity(member1, member2,
				this.getExpectedSimilarityResultForMembers(member1, member2, memCon1, memCon2));
	}

	/**
	 * @see {@link MemberInContainerTest}
	 */
	@ParameterizedTest(name = "Default member: {3}")
	@MethodSource("genTestParams")
	public void testDefaultMembersInContainers(Class<? extends Member> memCls,
			Class<? extends MemberContainer> memConCls1, Class<? extends MemberContainer> memConCls2,
			String displayName) {
		var member1 = getAPI().createNewX(memCls);
		var member2 = getAPI().createNewX(memCls);

		var memCon1 = getAPI().newX(memConCls1)
				.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS, member1)
				.createNow(memConCls1);
		var memCon2 = getAPI().newX(memConCls2)
				.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS, member2)
				.createNow(memConCls2);

		this.testSimilarity(member1, member2,
				this.getExpectedSimilarityResultForMembers(member1, member2, memCon1, memCon2));
	}

	/**
	 * Containers are needed too, since they can indirectly influence the outcome.
	 * 
	 * @return The expected result of similarity checking member1 and member2.
	 */
	private Boolean getExpectedSimilarityResultForMembers(Member member1, Member member2, MemberContainer memCon1,
			MemberContainer memCon2) {
		var memberCls1 = member1.getClass();
		var memberCls2 = member2.getClass();
		if (!memberCls1.equals(memberCls2)) {
			return false;
		}
		var containerMatters = AnnotationInstance.class.isAssignableFrom(memberCls1)
				|| Method.class.isAssignableFrom(memberCls1) || Constructor.class.isAssignableFrom(memberCls1);
		var containerClssEqual = memCon1.getClass().equals(memCon2.getClass());

		return containerClssEqual || (!containerMatters &&

		/*
		 * ConcreteClassifier indirectly cares about its eContainer, because its
		 * qualified name can be influenced by its container.
		 */
				(!ConcreteClassifier.class.isAssignableFrom(memberCls1) || ((ConcreteClassifier) member1)
						.getQualifiedName().equals(((ConcreteClassifier) member2).getQualifiedName())));
	}
}
