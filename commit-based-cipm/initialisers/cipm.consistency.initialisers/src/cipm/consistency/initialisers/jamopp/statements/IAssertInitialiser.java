package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Assert;

public interface IAssertInitialiser extends IConditionalInitialiser, IStatementInitialiser {
	@Override
	public Assert instantiate();

	public default boolean setErrorMessage(Assert asrt, Expression errMsg) {
		asrt.setErrorMessage(errMsg);
		return (errMsg == null && asrt.getErrorMessage() == null) || asrt.getErrorMessage().equals(errMsg);
	}
}
