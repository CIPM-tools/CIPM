package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.UnknownTypeArgument;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

public interface IUnknownTypeArgumentInitialiser extends IAnnotableInitialiser, ITypeArgumentInitialiser {
	@Override
	public UnknownTypeArgument instantiate();
}
