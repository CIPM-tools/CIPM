package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.QualifiedTypeArgument;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IQualifiedTypeArgumentInitialiser extends ITypeArgumentInitialiser, ITypedElementInitialiser {
	@Override
	public QualifiedTypeArgument instantiate();
}
