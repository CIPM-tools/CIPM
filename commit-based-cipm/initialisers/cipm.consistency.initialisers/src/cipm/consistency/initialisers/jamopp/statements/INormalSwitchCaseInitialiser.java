package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.NormalSwitchCase;

public interface INormalSwitchCaseInitialiser extends IConditionalInitialiser, ISwitchCaseInitialiser {
	@Override
	public NormalSwitchCase instantiate();

	public default boolean addAdditionalCondition(NormalSwitchCase nsc, Expression additionalCond) {
		if (additionalCond != null) {
			nsc.getAdditionalConditions().add(additionalCond);
			return nsc.getAdditionalConditions().contains(additionalCond);
		}
		return true;
	}

	public default boolean addAdditionalConditions(NormalSwitchCase nsc, Expression[] additionalConds) {
		return this.doMultipleModifications(nsc, additionalConds, this::addAdditionalCondition);
	}
}
