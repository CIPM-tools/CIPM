package cipm.consistency.fluentapi.test.metamodel;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.fluentapi.test.AbstractFluentAPITest;
import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * An abstract test class that implements test cases for the fluent api class of
 * the generated fluent api model, which ensure that certain fluent api clss
 * operations are generated. Also contains a mutation test to check whether the
 * other test cases work as intended.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractFluentAPIRootAPIGenerationTest extends AbstractFluentAPITest
		implements IFluentAPIMetamodelTest, IFluentAPIMutationTest {

	/**
	 * A mutation test to make sure that the other test cases within this class
	 * function as intended.
	 * 
	 * @param info An object that contains information on the currently running test
	 *             case
	 */
	@Test
	public void mutationTest(TestInfo info) {
		var api = getAPI();
		var eClssToMutate = new FluentAPIMutationTestRepresentativesGenerator()
				.getRepresentativeTargetMetamodelConcreteEClasses_BasedOnModifiability();

		var eClssToMutateNames = eClssToMutate.stream().map((eCls) -> eCls.getName()).collect(Collectors.toList());

		// Remove EOperations for certain types
		var opsToRemove = api.eClass().getEOperations().stream()
				.filter((op) -> eClssToMutateNames.stream().anyMatch((eClsName) -> op.getName().endsWith(eClsName)))
				.collect(Collectors.toList());

		// Mutate the existing EMF model by removing the methods from above
		var oldOps = List.copyOf(api.eClass().getEOperations());
		api.eClass().getEOperations().removeAll(opsToRemove);
		FluentAPIGenerationTestSettings.setFluentAPI(api);

		var mutTestRes = performMutationTesting(info);

		// Revert the mutation to EMF model
		api.eClass().getEOperations().clear();
		api.eClass().getEOperations().addAll(oldOps);
		FluentAPIGenerationTestSettings.setFluentAPI(api);

		assertTestsFailed(mutTestRes);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <p>
	 * AbstractFluentAPIInitialisationGenerationTest: Sets up
	 * {@link FluentAPIGenerationTestSettings} for the current fluent api and the
	 * metamodel it is meant for.
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();
		FluentAPIGenerationTestSettings.setFluentAPI(getAPI());
		FluentAPIGenerationTestSettings.setTargetMetamodelEClsToInitEClsFunc((eCls) -> api_getInitialisationForX(eCls).eClass());
		FluentAPIGenerationTestSettings.setMetamodelFilter(getFilter());
		FluentAPIGenerationTestSettings.setPackageProvider(getProvider());
	}

	/**
	 * Ensures that certain methods have been generated for the fluent api class
	 * according to the given testData.
	 */
	private void methodTestTemplate(FluentAPIMethodTestData testData) {
		Assertions.assertFalse(testData.getEClssToCheckFor().isEmpty());

		for (var eCls : testData.getEClssToCheckFor()) {
			var paramNames = testData.getParamNames(eCls);
			var paramTypes = testData.getParamTypes(eCls);
			var expectMultiValueVariants = testData.getExpectMultiValueVariants(eCls);
			var expectBigNumberVariants = testData.getExpectBigNumberVariants(eCls);
			if (paramNames.size() != paramTypes.size())
				throw new IllegalArgumentException();

			var currentEClssOpName = testData.getMethodName(eCls);
			var currentEClssOps = FluentAPIGenerationTestSettings.getAllFluentAPIOps().stream()
					.filter((op) -> op.getName().equals(currentEClssOpName))
					.filter((op) -> op.getEParameters().size() == paramNames.size())
//					.filter((op) -> op.getEType().equals(returnTypeOfOpFunc.apply(eCls))
					.collect(Collectors.toList());

			// Ensure that the original method is present
			FluentAPIGenerationTestAssertions.assertOriginalMethodExists(eCls, currentEClssOps, paramNames, paramTypes);

			if (expectMultiValueVariants) {
				FluentAPIGenerationTestAssertions.assertMultiValueVariantsExist(eCls, currentEClssOps, paramNames,
						paramTypes);
			}
			if (expectBigNumberVariants) {
				FluentAPIGenerationTestAssertions.assertBigNumberVariantsExist(eCls, currentEClssOps, paramNames,
						paramTypes);
			}
		}
	}

	/**
	 * Checks whether{@code createNewX() : X} methods for all supported EClasses
	 * exist in fluent api class.
	 */
	@Test
	public void methodTest_API_CreateNewX() {

		var testData = new FluentAPIMethodTestData();
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel());
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.CreateNew.NAME.getFor(eCls.getName()));
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code newX() : XInitialisation} methods for all supported
	 * EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_NewX_WithoutParameters() {

		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.New.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code newX() : X} methods for all supported EClasses (without
	 * any modifiable features according to the metamodel feature filter) exist in
	 * fluent api class.
	 */
	@Test
	public void methodTest_API_NewX_WithoutParameters_NoModifiableFeatures() {

		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.New.NAME.getFor(eCls.getName()));
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code newX(Y) : X} methods for all supported EClasses (with
	 * exactly one modifiable feature according to the metamodel feature filter)
	 * exist in fluent api class.
	 */
	@Test
	public void methodTest_API_NewX_WithParameter_SingleModifiableFeature() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.New.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setParamNameFunc((eCls) -> List.of(ModelConstants.GeneralParameters.FEATURE_VALUE_PARAMETER_NAME.get()));
		testData.setParamTypeFunc((eCls) -> List.of(eCls));
		testData.setEClssToCheckFor(
				FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat());
		testData.setExpectMultiValueVariantsFunc(FluentAPIGenerationTestSettings.getMultiValFunc());
		testData.setExpectBigNumberVariantsFunc(FluentAPIGenerationTestSettings.getBigNumberVariantsFunc());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code modifyX(X) : XInitialisation} methods for all supported
	 * EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_modifyX() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.Modify.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setParamNameFunc((eCls) -> List.of(ModelConstants.GeneralParameters.USED_EOBJECT_PARAMETER_NAME.get()));
		testData.setParamTypeFunc((eCls) -> List.of(eCls));
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code modifyMarkedX(markKey) : XInitialisation} methods for
	 * all supported EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_modifyMarkedX() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.ModifyMarked.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setParamNameFunc((eCls) -> List.of(ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()));
		testData.setParamTypeFunc((eCls) -> List.of(EcorePackage.Literals.EJAVA_OBJECT));
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code continueX(X) : XInitialisation} methods for all
	 * supported EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_continueX() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.Continue.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodelWithModifiableFeats());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code continueMarkedX(markKey) : XInitialisation} methods for
	 * all supported EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_continueMarkedX() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.ContinueMarked.NAME.getFor(eCls.getName()));
		testData.setReturnTypeOfOpFunc(FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc());
		testData.setParamNameFunc((eCls) -> List.of(ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()));
		testData.setParamTypeFunc((eCls) -> List.of(EcorePackage.Literals.EJAVA_OBJECT));
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodelWithModifiableFeats());
		methodTestTemplate(testData);
	}

	/**
	 * Checks whether {@code getMarkedX(markKey) : X} methods for all supported
	 * EClasses exist in fluent api class.
	 */
	@Test
	public void methodTest_API_getMarkedX() {
		var testData = new FluentAPIMethodTestData();
		testData.setMethodNamePrefixFunc((eCls) -> ModelConstants.FluentAPI.GetMarked.NAME.getFor(eCls.getName()));
		testData.setParamNameFunc((eCls) -> List.of(ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()));
		testData.setParamTypeFunc((eCls) -> List.of(EcorePackage.Literals.EJAVA_OBJECT));
		testData.setEClssToCheckFor(FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel());
		methodTestTemplate(testData);
	}
}
