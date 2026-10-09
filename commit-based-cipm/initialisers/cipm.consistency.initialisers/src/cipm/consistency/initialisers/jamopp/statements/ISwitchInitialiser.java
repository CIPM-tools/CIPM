package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Switch;
import org.emftext.language.java.statements.SwitchCase;

import cipm.consistency.initialisers.jamopp.expressions.IUnaryModificationExpressionChildInitialiser;

public interface ISwitchInitialiser extends IStatementInitialiser, IUnaryModificationExpressionChildInitialiser {
	@Override
	public Switch instantiate();

	public default boolean addCase(Switch sw, SwitchCase swCase) {
		if (swCase != null) {
			sw.getCases().add(swCase);
			return sw.getCases().contains(swCase);
		}
		return true;
	}

	public default boolean addCases(Switch sw, SwitchCase[] swCases) {
		return this.doMultipleModifications(sw, swCases, this::addCase);
	}

	public default boolean setVariable(Switch sw, Expression var) {
		sw.setVariable(var);
		return (var == null && sw.getVariable() == null) || sw.getVariable().equals(var);
	}
}
