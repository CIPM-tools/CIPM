package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.CatchBlock;

import cipm.consistency.initialisers.jamopp.statements.CatchBlockInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link CatchBlock} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link CatchBlock} instances.
 */
public interface UsesCatchBlocks extends UsesParameters {
	/**
	 * @param cbParam The parameter of the instance to be constructed
	 * @return A {@link CatchBlock} instance with the given parameter
	 */
	public default CatchBlock createMinimalCB(OrdinaryParameter cbParam) {
		var cbInit = new CatchBlockInitialiser();
		var cb = cbInit.instantiate();
		cbInit.setParameter(cb, cbParam);
		return cb;
	}

	/**
	 * A variant of {@link #createMinimalCB(OrdinaryParameter)}, where the parameter
	 * is constructed using
	 * {@link #createMinimalOrdParamWithClsTarget(String, String)}.
	 * 
	 * @param paramName  See
	 *                   {@link #createMinimalOrdParamWithClsTarget(String, String)}
	 * @param targetName See
	 *                   {@link #createMinimalOrdParamWithClsTarget(String, String)}
	 */
	public default CatchBlock createMinimalCB(String paramName, String targetName) {
		return this.createMinimalCB(this.createMinimalOrdParamWithClsTarget(paramName, targetName));
	}
}
