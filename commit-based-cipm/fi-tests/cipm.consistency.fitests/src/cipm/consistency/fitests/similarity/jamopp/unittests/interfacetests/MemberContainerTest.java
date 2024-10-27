package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesFields;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesMethods;
import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;

public class MemberContainerTest extends AbstractJaMoPPSimilarityTest
		implements UsesMethods, UsesFields, UsesConcreteClassifiers {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IMemberContainerInitialiser.class);
	}

	protected MemberContainer initElement(IMemberContainerInitialiser init, Member[] members, Member[] defMembers) {
		MemberContainer result = init.instantiate();
		Assertions.assertTrue(init.addMembers(result, members));
		Assertions.assertTrue(init.addDefaultMembers(result, defMembers));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testMember(IMemberContainerInitialiser init) {
		var objOne = this.initElement(init, new Member[] { this.createMinimalClass("cls1") }, null);
		var objTwo = this.initElement(init, new Member[] { this.createMinimalClass("cls2") }, null);

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testMemberSize(IMemberContainerInitialiser init) {
		var objOne = this.initElement(init,
				new Member[] { this.createMinimalClass("cls1"), this.createMinimalClass("cls2") }, null);
		var objTwo = this.initElement(init, new Member[] { this.createMinimalClass("cls1") }, null);

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testMemberNullCheck(IMemberContainerInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, new Member[] { this.createMinimalClass("cls1") }, null),
				init, false, MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testDefaultMember(IMemberContainerInitialiser init) {
		var objOne = this.initElement(init, null, new Member[] { this.createMinimalClass("cls1") });
		var objTwo = this.initElement(init, null, new Member[] { this.createMinimalClass("cls2") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testDefaultMemberSize(IMemberContainerInitialiser init) {
		var objOne = this.initElement(init, null,
				new Member[] { this.createMinimalClass("cls1"), this.createMinimalClass("cls2") });
		var objTwo = this.initElement(init, null, new Member[] { this.createMinimalClass("cls1") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testDefaultMemberNullCheck(IMemberContainerInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, null, new Member[] { this.createMinimalClass("cls1") }),
				init, false, MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS);
	}
}
