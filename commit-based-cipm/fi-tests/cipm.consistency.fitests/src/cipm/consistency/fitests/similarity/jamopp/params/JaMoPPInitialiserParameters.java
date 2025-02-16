package cipm.consistency.fitests.similarity.jamopp.params;

import cipm.consistency.initialisers.IInitialiserPackage;
import cipm.consistency.initialisers.jamopp.JaMoPPInitialiserPackage;
import cipm.consistency.fitests.similarity.params.IInitialiserParameterAdaptationStrategy;
import cipm.consistency.fitests.similarity.params.IInitialiserParameters;

/**
 * A class that provides central access to concrete initialiser instances, which
 * construct model elements used in JaMoPP.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPInitialiserParameters implements IInitialiserParameters {
	@Override
	public IInitialiserPackage getUsedInitialiserPackage() {
		return new JaMoPPInitialiserPackage();
	}

	@Override
	public IInitialiserParameterAdaptationStrategy getAdaptationStrategy() {
		return new JaMoPPInitialiserParameterAdaptationStrategy();
	}
}
