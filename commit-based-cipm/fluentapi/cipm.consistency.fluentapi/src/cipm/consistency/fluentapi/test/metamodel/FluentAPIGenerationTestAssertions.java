package cipm.consistency.fluentapi.test.metamodel;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Assertions;

/**
 * An utility class that contains assertion methods for the test classes, which
 * test the fluent api model.
 * <p>
 * <p>
 * Currently, checking types of EParameters of EOperations does not work, due to
 * them being proxy objects.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationTestAssertions {
	/**
	 * Asserts that eCls has an EOperation, whose signature matches the given
	 * paramNames and paramTypes.
	 */
	public static void assertOriginalMethodExists(EClass eCls, List<EOperation> currentEClssOps,
			List<String> paramNames, List<EClassifier> paramTypes) {
		Assertions.assertTrue(currentEClssOps.stream().anyMatch((op) -> assertParamsEqual(op, paramNames, paramTypes)),
				"For eCls " + eCls.getName());
	}

	/**
	 * Asserts that eCls has an EOperation op, whose signature matches the given
	 * paramNames and paramTypes. Further asserts that there is a variant of op for
	 * array-valued parameters, as well as another variant of op for
	 * collection-valued parameters.
	 */
	public static void assertMultiValueVariantsExist(EClass eCls, List<EOperation> currentEClssOps,
			List<String> paramNames, List<EClassifier> paramTypes) {
		Assertions.assertTrue(3 <= currentEClssOps.size(), "For eCls " + eCls.getName());
		Assertions.assertTrue(
				currentEClssOps.stream().anyMatch((op) -> assertArrayValuedParamsEqual(op, paramNames, paramTypes)));
		Assertions.assertTrue(currentEClssOps.stream()
				.anyMatch((op) -> assertCollectionValuedParamsEqual(op, paramNames, paramTypes)));
	}

	/**
	 * Asserts that eCls has an EOperation op, whose signature matches the given
	 * paramNames and paramTypes. Further asserts that there are variants of op for
	 * the primitive types int and long if op considers BigInteger parameters, or
	 * float and double if op considers BigDecimal parameters.
	 */
	public static void assertBigNumberVariantsExist(EClass eCls, List<EOperation> currentEClssOps,
			List<String> paramNames, List<EClassifier> paramTypes) {
		Assertions.assertTrue(3 <= currentEClssOps.size(), "For eCls " + eCls.getName());
		Assertions.assertTrue(currentEClssOps.stream()
				.anyMatch((op) -> assertParamsEqual(op, paramNames,
						paramTypes.stream()
								.map((t) -> t == EcorePackage.Literals.EBIG_INTEGER ? EcorePackage.Literals.EINT : t)
								.collect(Collectors.toList()))));
		Assertions
				.assertTrue(currentEClssOps.stream()
						.anyMatch((op) -> assertParamsEqual(op, paramNames, paramTypes.stream()
								.map((t) -> t == EcorePackage.Literals.EBIG_INTEGER ? EcorePackage.Literals.ELONG : t)
								.collect(Collectors.toList()))));
		Assertions
				.assertTrue(currentEClssOps.stream()
						.anyMatch((op) -> assertParamsEqual(op, paramNames, paramTypes.stream()
								.map((t) -> t == EcorePackage.Literals.EBIG_DECIMAL ? EcorePackage.Literals.EFLOAT : t)
								.collect(Collectors.toList()))));
		Assertions
				.assertTrue(currentEClssOps.stream()
						.anyMatch((op) -> assertParamsEqual(op, paramNames, paramTypes.stream()
								.map((t) -> t == EcorePackage.Literals.EBIG_DECIMAL ? EcorePackage.Literals.EDOUBLE : t)
								.collect(Collectors.toList()))));
	}

	/**
	 * @return Whether the EParameters of op have the given parameter names and
	 *         parameter types (in the given order).
	 */
	public static boolean assertParamsEqual(EOperation op, List<String> expectedParamNames,
			List<EClassifier> expectedParamTypes) {
		for (int i = 0; i < expectedParamNames.size(); i++) {
			var currentParam = op.getEParameters().get(i);
			if (!expectedParamNames.get(i).equals(currentParam.getName()))
				return false;

			// Add return type checking here in the future, if possible
		}
		return true;
	}

	/**
	 * @return Whether the EParameters of op have the given parameter names and
	 *         array-type versions of the given parameter types (in the given
	 *         order).
	 */
	public static boolean assertArrayValuedParamsEqual(EOperation op, List<String> expectedParamNames,
			List<EClassifier> expectedParamTypes) {
		for (int i = 0; i < expectedParamNames.size(); i++) {
			var currentParam = op.getEParameters().get(i);
			if (!expectedParamNames.get(i).equals(currentParam.getName()))
				return false;

			// Add return type checking here in the future, if possible.
			// Either take the array-valued EDataTypes as another parameter, or derive them
			// from expectedParamTypes
		}
		return true;
	}

	/**
	 * @return Whether the EParameters of op have the given parameter names and
	 *         collection-type versions of the given parameter types (in the given
	 *         order).
	 */
	public static boolean assertCollectionValuedParamsEqual(EOperation op, List<String> expectedParamNames,
			List<EClassifier> expectedParamTypes) {
		for (int i = 0; i < expectedParamNames.size(); i++) {
			var currentParam = op.getEParameters().get(i);
			if (!expectedParamNames.get(i).equals(currentParam.getName()))
				return false;

			// Add return type checking here in the future, if possible.
			// Either take the collection-valued EDataTypes as another parameter, or derive
			// them from expectedParamTypes
		}
		return true;
	}
}
