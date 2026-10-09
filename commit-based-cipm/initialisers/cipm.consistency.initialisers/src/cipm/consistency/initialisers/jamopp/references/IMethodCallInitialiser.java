package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.MethodCall;

import cipm.consistency.initialisers.jamopp.generics.ICallTypeArgumentableInitialiser;

public interface IMethodCallInitialiser
		extends IElementReferenceInitialiser, IArgumentableInitialiser, ICallTypeArgumentableInitialiser {
	@Override
	public MethodCall instantiate();
}
