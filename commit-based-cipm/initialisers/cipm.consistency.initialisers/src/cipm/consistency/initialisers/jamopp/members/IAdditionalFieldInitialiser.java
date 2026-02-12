package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.AdditionalField;

import cipm.consistency.initialisers.jamopp.instantiations.IInitializableInitialiser;
import cipm.consistency.initialisers.jamopp.references.IReferenceableElementInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IAdditionalFieldInitialiser
		extends IInitializableInitialiser, IReferenceableElementInitialiser, ITypedElementInitialiser {
	@Override
	public AdditionalField instantiate();
}
