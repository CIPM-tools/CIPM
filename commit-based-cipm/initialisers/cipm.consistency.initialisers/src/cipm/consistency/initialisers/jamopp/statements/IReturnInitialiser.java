package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Return;

public interface IReturnInitialiser extends IStatementInitialiser {
	@Override
	public Return instantiate();

	public default boolean setReturnValue(Return ret, Expression retVal) {
		ret.setReturnValue(retVal);
		return (retVal == null && ret.getReturnValue() == null) || ret.getReturnValue().equals(retVal);
	}
}
