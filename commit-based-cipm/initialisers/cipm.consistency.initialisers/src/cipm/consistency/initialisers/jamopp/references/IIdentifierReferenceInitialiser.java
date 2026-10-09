package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.IdentifierReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;
import cipm.consistency.initialisers.jamopp.arrays.IArrayTypeableInitialiser;

public interface IIdentifierReferenceInitialiser
		extends IAnnotableInitialiser, IArrayTypeableInitialiser, IElementReferenceInitialiser {
	@Override
	public IdentifierReference instantiate();
}
