package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Throw;

public interface IThrowInitialiser extends IStatementInitialiser {
	@Override
	public Throw instantiate();

	public default boolean setThrowable(Throw th, Expression throwable) {
		th.setThrowable(throwable);
		return (throwable == null && th.getThrowable() == null) || th.getThrowable().equals(throwable);
	}
}
