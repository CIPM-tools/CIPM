package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.ForEachLoop;

public interface IForEachLoopInitialiser extends IStatementInitialiser, IStatementContainerInitialiser {
	@Override
	public ForEachLoop instantiate();

	public default boolean setCollection(ForEachLoop fel, Expression col) {
		fel.setCollection(col);
		return (col == null && fel.getCollection() == null) || fel.getCollection().equals(col);
	}

	public default boolean setNext(ForEachLoop fel, OrdinaryParameter next) {
		fel.setNext(next);
		return (next == null && fel.getNext() == null) || fel.getNext().equals(next);
	}
}
