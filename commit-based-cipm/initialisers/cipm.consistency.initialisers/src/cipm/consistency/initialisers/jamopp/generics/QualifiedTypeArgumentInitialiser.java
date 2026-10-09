package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.GenericsFactory;
import org.emftext.language.java.generics.QualifiedTypeArgument;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class QualifiedTypeArgumentInitialiser extends AbstractInitialiserBase
		implements IQualifiedTypeArgumentInitialiser {
	@Override
	public IQualifiedTypeArgumentInitialiser newInitialiser() {
		return new QualifiedTypeArgumentInitialiser();
	}

	@Override
	public QualifiedTypeArgument instantiate() {
		return GenericsFactory.eINSTANCE.createQualifiedTypeArgument();
	}
}