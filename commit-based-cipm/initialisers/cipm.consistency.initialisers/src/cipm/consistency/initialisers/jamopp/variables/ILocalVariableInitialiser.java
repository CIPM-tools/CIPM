package cipm.consistency.initialisers.jamopp.variables;

import org.emftext.language.java.variables.AdditionalLocalVariable;
import org.emftext.language.java.variables.LocalVariable;

import cipm.consistency.initialisers.jamopp.instantiations.IInitializableInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.IAnnotableAndModifiableInitialiser;

public interface ILocalVariableInitialiser
		extends IAnnotableAndModifiableInitialiser, IInitializableInitialiser, IVariableInitialiser {
	@Override
	public LocalVariable instantiate();

	public default boolean addAdditionalLocalVariable(LocalVariable lv, AdditionalLocalVariable alv) {
		if (alv != null) {
			lv.getAdditionalLocalVariables().add(alv);
			return lv.getAdditionalLocalVariables().contains(alv);
		}
		return true;
	}

	public default boolean addAdditionalLocalVariables(LocalVariable lv, AdditionalLocalVariable[] alvs) {
		return this.doMultipleModifications(lv, alvs, this::addAdditionalLocalVariable);
	}
}
