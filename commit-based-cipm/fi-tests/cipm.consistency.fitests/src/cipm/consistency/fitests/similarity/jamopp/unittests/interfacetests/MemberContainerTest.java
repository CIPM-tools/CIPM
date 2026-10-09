package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class MemberContainerTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Member> members1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<Member> members2 = () -> getAPI().newClass().withName("cls2").createNow();

	private final Supplier<Member> defaultMembers1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<Member> defaultMembers2 = () -> getAPI().newClass().withName("cls2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(MemberContainer.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testMember(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, members1.get())
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, members2.get())
						.createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testMemberSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
								new Member[] { members1.get(), members2.get() })
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, members1.get())
						.createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testMemberNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, members1.get()).createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testDefaultMember(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
								defaultMembers1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
								defaultMembers2.get())
						.createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testDefaultMemberSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
								new Member[] { defaultMembers1.get(), defaultMembers2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
								defaultMembers1.get())
						.createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testDefaultMemberNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
								defaultMembers1.get())
						.createNow(),
				MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}
}
