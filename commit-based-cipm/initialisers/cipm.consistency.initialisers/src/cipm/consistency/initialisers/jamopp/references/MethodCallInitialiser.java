package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class MethodCallInitialiser extends AbstractInitialiserBase implements IMethodCallInitialiser {
	@Override
	public IMethodCallInitialiser newInitialiser() {
		return new MethodCallInitialiser();
	}

	@Override
	public MethodCall instantiate() {
		return ReferencesFactory.eINSTANCE.createMethodCall();
	}
}