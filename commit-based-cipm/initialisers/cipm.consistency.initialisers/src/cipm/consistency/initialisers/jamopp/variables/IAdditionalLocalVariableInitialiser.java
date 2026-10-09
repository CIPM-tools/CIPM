package cipm.consistency.initialisers.jamopp.variables;

import org.emftext.language.java.variables.AdditionalLocalVariable;

import cipm.consistency.initialisers.jamopp.instantiations.IInitializableInitialiser;
import cipm.consistency.initialisers.jamopp.references.IReferenceableElementInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IAdditionalLocalVariableInitialiser
		extends IInitializableInitialiser, IReferenceableElementInitialiser, ITypedElementInitialiser {
	@Override
	public AdditionalLocalVariable instantiate();
}
