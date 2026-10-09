package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.stream.Stream;

import org.emftext.language.java.members.MemberContainer;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

/**
 * Contains tests for {@link AdditionalField} instances, their {@link Field}s
 * and attributes thereof.
 * 
 * <p>
 * Test cases here contain some unused local variables, whose purpose is to
 * facilitate understanding the respective scenarios under test.
 * 
 * @author Alp Torac Genc
 */
@SuppressWarnings("unused")
public class AdditionalFieldStructureTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @return Parameters for the test methods in this test class. See the
	 *         documentation of parameterized test methods.
	 */
	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(MemberContainer.class);
	}

	/**
	 * Ensures that similarity checking detects it as a difference, if 2
	 * {@link AdditionalField} instances are compared and only one of them has a
	 * container. <br>
	 * <br>
	 * Let AF_i be {@link AdditionalField} instances and F_i be {@link Field}
	 * instances. Then the construction is as follows: <br>
	 * <br>
	 * AF_1 <- F_1 <br>
	 * -----------VS----------- <br>
	 * AF_2 <br>
	 * <br>
	 * Where {@code "a <- b" := a.eContainer() = b}
	 */
	@Test
	public void testDifferentContainer_OneContainer_IsNull() {
		var af1 = getAPI().createNewAdditionalField();
		var af2 = getAPI().createNewAdditionalField();

		var f1 = getAPI().newField().withAddedAdditionalFields(af1).createNow();

		this.testSimilarity(af1, af2, false);
	}

	/**
	 * Ensures that similarity checking detects it as a difference, if 2
	 * {@link AdditionalField} instances each with a {@link Field} as container are
	 * compared, where only one of the containers has a {@link TypeReference}. <br>
	 * <br>
	 * Let AF_i be {@link AdditionalField} instances, F_i be {@link Field} instances
	 * and TRef_i be {@link TypeReference}s. Then the construction is as follows:
	 * <br>
	 * <br>
	 * AF_1 <- F_1 (with TRef_1) <br>
	 * -----------VS----------- <br>
	 * AF_2 <- F_2 <br>
	 * <br>
	 * Where {@code "a <- b" := a.eContainer() = b}
	 */
	@Test
	public void testDifferentContainer_OneContainer_HasTypeReference() {
		var af1 = getAPI().createNewAdditionalField();
		var af2 = getAPI().createNewAdditionalField();

		var f1 = getAPI().newField()
				.withTypeReference(getAPI().newClassifierReference()
						.withTarget(getAPI().newClass().withName("memConCls").createNow()).createNow())
				.withAddedAdditionalFields(af1).createNow();
		var f2 = getAPI().newField().withAddedAdditionalFields(af2);

		this.testSimilarity(af1, af2, false);
	}

	/**
	 * Ensures that similarity checking detects it as a difference, if 2
	 * {@link AdditionalField} instances each with a {@link Field} as container are
	 * compared, where their containers' {@link TypeReference}s differ. <br>
	 * <br>
	 * Let AF_i be {@link AdditionalField} instances, F_i be {@link Field} instances
	 * and TRef_i be {@link TypeReference}s. Then the construction is as follows:
	 * <br>
	 * <br>
	 * AF_1 <- F_1 (with TRef_1) <br>
	 * -----------VS----------- <br>
	 * AF_2 <- F_2 (with TRef_2) <br>
	 * <br>
	 * Where {@code "a <- b" := a.eContainer() = b}
	 */
	@Test
	public void testDifferentContainer_BothContainers_HaveDifferentTypeReference() {
		var tref1 = getAPI().newClassifierReference().withTarget(getAPI().newClass().withName("cls1").createNow())
				.createNow();
		var tref2 = getAPI().newClassifierReference().withTarget(getAPI().newClass().withName("cls2").createNow())
				.createNow();

		// Make sure that the type references are different
		this.assertSimilarityResult(tref1, tref2, false);

		var af1 = getAPI().createNewAdditionalField();
		var af2 = getAPI().createNewAdditionalField();

		var f1 = getAPI().newField().withTypeReference(tref1).withAddedAdditionalFields(af1).createNow();
		var f2 = getAPI().newField().withTypeReference(tref2).withAddedAdditionalFields(af2).createNow();

		this.testSimilarity(af1, af2, false);
	}

	/**
	 * Ensures that similarity checking handles advanced constructions with
	 * {@link AdditionalField} instances as expected. <br>
	 * <br>
	 * Let AF_i be {@link AdditionalField} instances, F_i be {@link Field}
	 * instances, MC_i be {@link MemberContainer} instances and TRef_i be
	 * {@link TypeReference}s. Then the construction is as follows: <br>
	 * <br>
	 * AF_1 <- F_1 <- MC_1 <br>
	 * -----------VS----------- <br>
	 * AF_2 <br>
	 * <br>
	 * Where {@code "a <- b" := a.eContainer() = b}
	 *
	 * @param init The initialiser that constructs the container of the container of
	 *             the {@link AdditionalField} instance (MC_i).
	 */
	@ParameterizedTest(name = "ConOfConInit = {1}")
	@MethodSource("provideArguments")
	public void testDifferentConOfCon_OneContainer_IsNull(Class<? extends MemberContainer> memConCls,
			String displayName) {
		var af1 = getAPI().createNewAdditionalField();
		var af2 = getAPI().createNewAdditionalField();

		var f1 = getAPI().newField().withAddedAdditionalFields(af1).createNow();

		var mc1 = getAPI().newX(memConCls).xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, f1);

		this.testSimilarity(af1, af2, false);
	}

	/**
	 * Ensures that similarity checking handles advanced constructions with
	 * {@link AdditionalField} instances as expected. <br>
	 * <br>
	 * Let AF_i be {@link AdditionalField} instances, F_i be {@link Field}
	 * instances, MC_i be {@link MemberContainer} instances and TRef_i be
	 * {@link TypeReference}s. Then the construction is as follows: <br>
	 * <br>
	 * AF_1 <- F_1 <- MC_1 <br>
	 * -----------VS----------- <br>
	 * AF_2 <- F_2 <br>
	 * <br>
	 * Where {@code "a <- b" := a.eContainer() = b}
	 * 
	 * @param init The initialiser that constructs the container of the container of
	 *             the {@link AdditionalField} instance (MC_i).
	 */
	@ParameterizedTest(name = "ConOfConInit = {1}")
	@MethodSource("provideArguments")
	public void testDifferentConOfCon_OneConOfCon_IsNull(Class<? extends MemberContainer> memConCls,
			String displayName) {
		var af1 = getAPI().createNewAdditionalField();
		var af2 = getAPI().createNewAdditionalField();

		var f1 = getAPI().newField().withAddedAdditionalFields(af1).createNow();
		var f2 = getAPI().newField().withAddedAdditionalFields(af2).createNow();

		var mc1 = getAPI().newX(memConCls).xWithAddedFeat(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, f1);

		this.testSimilarity(af1, af2, false);
	}
}
