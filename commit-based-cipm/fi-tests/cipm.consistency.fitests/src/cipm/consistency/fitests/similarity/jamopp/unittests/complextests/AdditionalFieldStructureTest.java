package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesFields;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.members.AdditionalFieldInitialiser;
import cipm.consistency.initialisers.jamopp.members.FieldInitialiser;
import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;

/**
 * Contains tests for {@link AdditionalField} instances, their {@link Field}s
 * and attributes thereof.
 * 
 * @author Alp Torac Genc
 */
public class AdditionalFieldStructureTest extends AbstractJaMoPPSimilarityTest
		implements UsesFields, UsesTypeReferences {
	/**
	 * @return Parameters for the test methods in this test class. See the
	 *         documentation of parameterized test methods.
	 */
	private static Stream<Arguments> genTestParams() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IMemberContainerInitialiser.class);
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
		var afInit = new AdditionalFieldInitialiser();
		var fieldInit = new FieldInitialiser();

		var af1 = afInit.instantiate();
		var af2 = afInit.instantiate();

		var field1 = fieldInit.instantiate();

		fieldInit.addAdditionalField(field1, af1);

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
		var tref = this.createMinimalClsRef("cls");

		var afInit = new AdditionalFieldInitialiser();
		var fieldInit = new FieldInitialiser();

		var af1 = afInit.instantiate();
		var af2 = afInit.instantiate();

		var field1 = fieldInit.instantiate();
		fieldInit.setTypeReference(field1, tref);
		var field2 = fieldInit.instantiate();

		fieldInit.addAdditionalField(field1, af1);
		fieldInit.addAdditionalField(field2, af2);

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
		var tref1 = this.createMinimalClsRef("cls1");
		var tref2 = this.createMinimalClsRef("cls2");

		// Make sure that the type references are different
		this.assertSimilarityResult(tref1, tref2, false);

		var fieldInit = new FieldInitialiser();

		var field1 = fieldInit.instantiate();
		fieldInit.setTypeReference(field1, tref1);
		var field2 = fieldInit.instantiate();
		fieldInit.setTypeReference(field2, tref2);

		var afInit = new AdditionalFieldInitialiser();
		var af1 = afInit.instantiate();
		var af2 = afInit.instantiate();

		fieldInit.addAdditionalField(field1, af1);
		fieldInit.addAdditionalField(field2, af2);

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
	@MethodSource("genTestParams")
	public void testDifferentConOfCon_OneContainer_IsNull(IMemberContainerInitialiser init, String displayName) {
		var afInit = new AdditionalFieldInitialiser();
		var fieldInit = new FieldInitialiser();

		var af1 = afInit.instantiate();
		var af2 = afInit.instantiate();

		var field1 = fieldInit.instantiate();

		fieldInit.addAdditionalField(field1, af1);

		var conOfCon = init.instantiate();
		Assertions.assertTrue(init.initialise(conOfCon));
		init.addMember(conOfCon, field1);

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
	@MethodSource("genTestParams")
	public void testDifferentConOfCon_OneConOfCon_IsNull(IMemberContainerInitialiser init, String displayName) {
		var afInit = new AdditionalFieldInitialiser();
		var fieldInit = new FieldInitialiser();

		var af1 = afInit.instantiate();
		var af2 = afInit.instantiate();

		var field1 = fieldInit.instantiate();
		var field2 = fieldInit.instantiate();

		fieldInit.addAdditionalField(field1, af1);
		fieldInit.addAdditionalField(field2, af2);

		var conOfCon = init.instantiate();
		Assertions.assertTrue(init.initialise(conOfCon));
		init.addMember(conOfCon, field1);

		this.testSimilarity(af1, af2, false);
	}
}
