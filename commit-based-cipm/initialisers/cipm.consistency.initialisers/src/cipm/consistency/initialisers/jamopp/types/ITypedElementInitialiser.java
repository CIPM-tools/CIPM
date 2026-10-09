package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypedElement;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ITypedElementInitialiser extends ICommentableInitialiser {
	@Override
	public TypedElement instantiate();

	public default boolean setTypeReference(TypedElement te, TypeReference tRef) {
		te.setTypeReference(tRef);
		return (tRef == null && te.getTypeReference() == null) || te.getTypeReference().equals(tRef);
	}
}
