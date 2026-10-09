package cipm.consistency.initialisers.jamopp.variables;

import org.emftext.language.java.variables.LocalVariable;
import org.emftext.language.java.variables.VariablesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class LocalVariableInitialiser extends AbstractInitialiserBase implements ILocalVariableInitialiser {
	@Override
	public LocalVariable instantiate() {
		return VariablesFactory.eINSTANCE.createLocalVariable();
	}

	@Override
	public ILocalVariableInitialiser newInitialiser() {
		return new LocalVariableInitialiser();
	}
}
