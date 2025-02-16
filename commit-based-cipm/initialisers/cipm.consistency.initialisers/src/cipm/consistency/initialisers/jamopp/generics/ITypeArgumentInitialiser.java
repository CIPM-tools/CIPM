package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.TypeArgument;

import cipm.consistency.initialisers.jamopp.arrays.IArrayTypeableInitialiser;

public interface ITypeArgumentInitialiser extends IArrayTypeableInitialiser {
	@Override
	public TypeArgument instantiate();

}
