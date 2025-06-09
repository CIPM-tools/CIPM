package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.TypeArgument;
import org.emftext.language.java.generics.TypeArgumentable;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ITypeArgumentableInitialiser extends ICommentableInitialiser {
	@Override
	public TypeArgumentable instantiate();

	public default boolean addTypeArgument(TypeArgumentable ta, TypeArgument tArg) {
		if (tArg != null) {
			ta.getTypeArguments().add(tArg);
			return ta.getTypeArguments().contains(tArg);
		}
		return true;
	}

	public default boolean addTypeArguments(TypeArgumentable ta, TypeArgument[] tArgs) {
		return this.doMultipleModifications(ta, tArgs, this::addTypeArgument);
	}
}
