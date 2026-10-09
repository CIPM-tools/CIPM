package cipm.consistency.fluentapi.gen;

import java.util.List;

import org.eclipse.emf.ecore.EOperation;

/**
 * Utility class for operations on EOperation parameters (EParameters) during
 * fluent API generation.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIParameterUtil {
	/**
	 * @param op A given EOperation
	 * @return A string containing the names of op's EParameters, delimited with
	 *         commas: {@code op_param1,op_param2,...,op_paramN}
	 */
	public static String getSerialisedParametersFor(EOperation op) {
		return String.join(",", op.getEParameters().stream().map((p) -> p.getName()).toArray(String[]::new));
	}

	/**
	 * Use to check, whether EOperation signatures are duplicated in a given set of
	 * EOperations.
	 * 
	 * @param allOps              EOperations, among which matching signatures will
	 *                            be sought (must not contain opToCheckForClashes
	 *                            itself)
	 * @param opToCheckForClashes The EOperation, for which EOperations with
	 *                            matching signatures will be sought
	 * @return Whether the signature of opToCheckForClashes is duplicated within
	 *         allOps.
	 */
	public static boolean hasClashingMethods(List<EOperation> allOps, EOperation opToCheckForClashes) {
		return allOps.stream()
				// Look for EOperations with the same name
				.filter((op) -> op.getName().equals(opToCheckForClashes.getName()))
				// Look for EOperations with the same parameter count
				.filter((op) -> op.getEParameters().size() == opToCheckForClashes.getEParameters().size())
				// Look for EOperations with the same parameter names
				.anyMatch((op) -> op.getEParameters().stream()
						// Check equity of all parameters
						.allMatch((opParam) -> opToCheckForClashes.getEParameters().stream().anyMatch((clashOpParam) ->
						// Check parameter name equity
						opParam.getName().equals(clashOpParam.getName()) &&
						// Check parameter type equity
								opParam.getEType().getInstanceClass()
										.equals(clashOpParam.getEType().getInstanceClass()))));
	}
}
