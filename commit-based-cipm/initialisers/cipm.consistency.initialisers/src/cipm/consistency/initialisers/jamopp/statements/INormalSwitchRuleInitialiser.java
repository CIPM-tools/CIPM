package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.NormalSwitchRule;

public interface INormalSwitchRuleInitialiser extends IConditionalInitialiser, ISwitchRuleInitialiser {
	@Override
	public NormalSwitchRule instantiate();

	public default boolean addAdditionalCondition(NormalSwitchRule nsr, Expression additionalCond) {
		if (additionalCond != null) {
			nsr.getAdditionalConditions().add(additionalCond);
			return nsr.getAdditionalConditions().contains(additionalCond);
		}
		return true;
	}

	public default boolean addAdditionalConditions(NormalSwitchRule nsr, Expression[] additionalConds) {
		return this.doMultipleModifications(nsr, additionalConds, this::addAdditionalCondition);
	}
}
