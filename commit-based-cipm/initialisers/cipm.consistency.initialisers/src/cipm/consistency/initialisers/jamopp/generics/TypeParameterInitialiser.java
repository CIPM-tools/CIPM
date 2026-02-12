package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.TypeParameter;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.generics.GenericsFactory;

public class TypeParameterInitialiser extends AbstractInitialiserBase implements ITypeParameterInitialiser {
	@Override
	public TypeParameter instantiate() {
		return GenericsFactory.eINSTANCE.createTypeParameter();
	}

	@Override
	public ITypeParameterInitialiser newInitialiser() {
		return new TypeParameterInitialiser();
	}
}
