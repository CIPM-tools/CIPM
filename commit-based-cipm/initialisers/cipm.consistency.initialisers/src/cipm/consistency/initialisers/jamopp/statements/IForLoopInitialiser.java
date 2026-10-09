package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.ForLoop;
import org.emftext.language.java.statements.ForLoopInitializer;

public interface IForLoopInitialiser
		extends IConditionalInitialiser, IStatementInitialiser, IStatementContainerInitialiser {
	@Override
	public ForLoop instantiate();

	public default boolean setInit(ForLoop fl, ForLoopInitializer init) {
		fl.setInit(init);
		return (init == null && fl.getInit() == null) || fl.getInit().equals(init);
	}

	public default boolean addUpdate(ForLoop fl, Expression update) {
		if (update != null) {
			fl.getUpdates().add(update);
			return fl.getUpdates().contains(update);
		}
		return true;
	}

	public default boolean addUpdates(ForLoop fl, Expression[] updates) {
		return this.doMultipleModifications(fl, updates, this::addUpdate);
	}
}
