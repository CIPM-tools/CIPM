package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.ReferenceableElement;

import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;

public interface IReferenceableElementInitialiser extends INamedElementInitialiser {
	@Override
	public ReferenceableElement instantiate();
}
