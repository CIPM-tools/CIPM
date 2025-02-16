package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.variables.LocalVariable;

public interface ILocalVariableStatementInitialiser extends IStatementInitialiser {
	@Override
	public LocalVariableStatement instantiate();

	public default boolean setVariable(LocalVariableStatement lvs, LocalVariable var) {
		lvs.setVariable(var);
		return (var == null && lvs.getVariable() == null) || lvs.getVariable().equals(var);
	}
}
