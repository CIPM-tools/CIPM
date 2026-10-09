package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.SuperTypeArgument;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

public interface ISuperTypeArgumentInitialiser extends IAnnotableInitialiser, ITypeArgumentInitialiser {
	@Override
	public SuperTypeArgument instantiate();

	public default boolean setSuperType(SuperTypeArgument sta, TypeReference superType) {
		sta.setSuperType(superType);
		return (superType == null && sta.getSuperType() == null) || sta.getSuperType().equals(superType);
	}
}
