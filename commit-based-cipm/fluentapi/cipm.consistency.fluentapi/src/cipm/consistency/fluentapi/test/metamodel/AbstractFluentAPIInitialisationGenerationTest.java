package cipm.consistency.fluentapi.test.metamodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.fluentapi.gen.ModelConstants;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * An abstract test class that implements test cases for the initialisations of
 * the generated fluent api model, which ensure that certain initialisation
 * operations (used for modifying model elements) are generated. Also contains a
 * mutation test to check whether the other test cases work as intended.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractFluentAPIInitialisationGenerationTest extends AbstractFluentAPITest
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
		var eClssToMutate = new FluentAPIMutationTestRepresentativesGenerator()
				.getRepresentativeTargetMetamodelConcreteEClasses_BasedOnModifiability().stream()
				.map((eCls) -> FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls))
				.collect(Collectors.toSet());

		var opsToRemove = new LinkedHashMap<EClass, List<EOperation>>();
		eClssToMutate.stream().forEach((eCls) -> opsToRemove.put(eCls, new ArrayList<>()));
		eClssToMutate.stream()
				.forEach((eCls) -> eCls.getEOperations().stream()
						.filter((op) -> op.getName().startsWith(ModelConstants.Initialiation.With.NAME.getEmpty()))
						.forEach((op) -> opsToRemove.get(eCls).add(op)));
		eClssToMutate.stream()
				.forEach((eCls) -> eCls.getEOperations().stream()
						.filter((op) -> op.getName().startsWith(ModelConstants.Initialiation.Without.NAME.getEmpty()))
						.forEach((op) -> opsToRemove.get(eCls).add(op)));
		eClssToMutate.stream()
				.forEach((eCls) -> eCls.getEOperations().stream()
						.filter((op) -> op.getName().startsWith(ModelConstants.Initialiation.WithAdded.NAME.getEmpty()))
						.forEach((op) -> opsToRemove.get(eCls).add(op)));
		eClssToMutate.stream()
				.forEach((eCls) -> eCls.getEOperations().stream().filter(
						(op) -> op.getName().startsWith(ModelConstants.Initialiation.WithRemoved.NAME.getEmpty()))
						.forEach((op) -> opsToRemove.get(eCls).add(op)));
		eClssToMutate.stream()
				.forEach((eCls) -> eCls.getEOperations().stream()
						.filter((op) -> op.getName().startsWith(ModelConstants.Initialiation.Clean.NAME.getEmpty()))
						.forEach((op) -> opsToRemove.get(eCls).add(op)));

		var oldOps = new LinkedHashMap<EClass, List<EOperation>>();
		eClssToMutate.stream().forEach((eCls) -> oldOps.put(eCls, List.copyOf(eCls.getEOperations())));

		// Mutate the existing EMF model by removing the methods from above
		opsToRemove.forEach((eCls, toRemove) -> eCls.getEOperations().removeAll(toRemove));

		var mutTestRes = performMutationTesting(info);

		// Revert the mutation to EMF model
		oldOps.forEach((eCls, ops) -> {
			eCls.getEOperations().clear();
			eCls.getEOperations().addAll(ops);
		});

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
	 * Ensures that the Initialisation EClass initECls that is meant to create and
	 * modify instances of elemToInitECls contains certain methods. These methods
	 * should have the mutual name prefix methodNamePrefix. There should be at least
	 * one method for each feature in expectedFeats.
	 */
	private void methodTestTemplate(EClass elemToInitECls, EClass initECls, String methodNamePrefix,
			String expectedParamName, List<EStructuralFeature> expectedFeats) {
		for (var feature : expectedFeats) {
			var paramName = expectedParamName;
			var hasParams = paramName != null;
			var paramType = feature.getEType();
			var expectMultiValueVariants = feature.isMany();
			var expectBigNumberVariants = FluentAPIGenerationTestSettings.getBigNumberVariantsFunc()
					.apply(elemToInitECls);

			var currentMetName = methodNamePrefix + StringUtils.capitalize(feature.getName());
			var currentEClssOps = initECls.getEOperations().stream().filter((op) -> op.getName().equals(currentMetName))
//					.filter((op) -> op.getEType().equals(initECls))
					.collect(Collectors.toList());

			var paramNames = hasParams ? List.of(paramName) : List.<String>of();
			var paramTypes = hasParams ? List.of(paramType) : List.<EClassifier>of();

			// Ensure that the original method is present
			FluentAPIGenerationTestAssertions.assertOriginalMethodExists(initECls, currentEClssOps, paramNames,
					paramTypes);
			if (hasParams && expectMultiValueVariants) {
				FluentAPIGenerationTestAssertions.assertMultiValueVariantsExist(initECls, currentEClssOps, paramNames,
						paramTypes);
			}
			if (hasParams && expectBigNumberVariants) {
				FluentAPIGenerationTestAssertions.assertBigNumberVariantsExist(initECls, currentEClssOps, paramNames,
						paramTypes);
			}
		}
	}

	/**
	 * Checks whether{@code withX(featVal) : XInitialisation} methods for all
	 * supported EClasses exist in XInitialisation
	 */
	@Test
	public void methodTest_Initialisation_WithX() {
		for (var eCls : FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel()) {
			methodTestTemplate(eCls, FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls),
					ModelConstants.Initialiation.With.NAME.getFor(""),
					ModelConstants.Initialiation.With.PARAMETER_NAME.get(),
					FluentAPIGenerationTestSettings.getMetamodelFilter().getModifiableFeatures(eCls).stream()
							.filter((f) -> !f.isMany()).collect(Collectors.toList()));
		}
	}

	/**
	 * Checks whether{@code withoutX() : XInitialisation} methods for all supported
	 * EClasses exist in XInitialisation
	 */
	@Test
	public void methodTest_Initialisation_WithoutX() {
		for (var eCls : FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel()) {
			methodTestTemplate(eCls, FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls),
					ModelConstants.Initialiation.Without.NAME.getFor(""), null,
					FluentAPIGenerationTestSettings.getMetamodelFilter().getModifiableFeatures(eCls).stream()
							.filter((f) -> !f.isMany()).collect(Collectors.toList()));
		}
	}

	/**
	 * Checks whether{@code withAddedX(featVals) : XInitialisation} methods for all
	 * supported EClasses exist in XInitialisation
	 */
	@Test
	public void methodTest_Initialisation_WithAddedX() {
		for (var eCls : FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel()) {
			methodTestTemplate(eCls, FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls),
					ModelConstants.Initialiation.WithAdded.NAME.getFor(""),
					ModelConstants.Initialiation.WithAdded.PARAMETER_NAME.get(),
					FluentAPIGenerationTestSettings.getMetamodelFilter().getModifiableFeatures(eCls).stream()
							.filter((f) -> f.isMany()).collect(Collectors.toList()));
		}
	}

	/**
	 * Checks whether{@code withRemovedX(featVals) : XInitialisation} methods for
	 * all supported EClasses exist in XInitialisation
	 */
	@Test
	public void methodTest_Initialisation_WithRemovedX() {
		for (var eCls : FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel()) {
			methodTestTemplate(eCls, FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls),
					ModelConstants.Initialiation.WithRemoved.NAME.getFor(""),
					ModelConstants.Initialiation.WithRemoved.PARAMETER_NAME.get(),
					FluentAPIGenerationTestSettings.getMetamodelFilter().getModifiableFeatures(eCls).stream()
							.filter((f) -> f.isMany()).collect(Collectors.toList()));
		}
	}

	/**
	 * Checks whether{@code cleanX() : XInitialisation} methods for all supported
	 * EClasses exist in XInitialisation
	 */
	@Test
	public void methodTest_Initialisation_CleanX() {
		for (var eCls : FluentAPIGenerationTestSettings.getAllSupportedConcreteEClssInTargetMetamodel()) {
			methodTestTemplate(eCls, FluentAPIGenerationTestSettings.getTargetMetamodelEClsToInitEClsFunc().apply(eCls),
					ModelConstants.Initialiation.Clean.NAME.getFor(""), null,
					FluentAPIGenerationTestSettings.getMetamodelFilter().getModifiableFeatures(eCls).stream()
							.filter((f) -> f.isMany()).collect(Collectors.toList()));
		}
	}
}
