package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * create {@link StatementListContainer} instances. <br>
 * <br>
 * Not all implementors of {@link StatementListContainer} allow adding
 * statements via {@code .getStatements().add(...)} as expected. Attempting to
 * add statements that way can result in exceptions under circumstances. The
 * method {@link #canContainStatements(StatementListContainer)} can be used to
 * determine, whether this is possible.
 * 
 * @author Alp Torac Genc
 */
public interface IStatementListContainerInitialiser extends ICommentableInitialiser {
	@Override
	public StatementListContainer instantiate();

	/**
	 * Attempts to add the given {@link Statement} st to the given
	 * {@link StatementListContainer} slc. Uses {@code slc.getStatements().add(st)}
	 * to do so. <br>
	 * <br>
	 * Note: Attempting to add a null statement will still return true, since there
	 * is no modification to be performed.
	 * 
	 * @see {@link IStatementListContainerInitialiser}
	 * @see {@link #addStatementAssertion(StatementListContainer, Statement)}
	 */
	public default boolean addStatement(StatementListContainer slc, Statement st) {
		if (st != null) {
			if (!this.canContainStatements(slc)) {
				return false;
			}
			slc.getStatements().add(st);
			return slc.getStatements().contains(st);
		}
		return true;
	}

	/**
	 * Extracted from {@link #addStatement(StatementListContainer, Statement)}
	 * because there are some implementors of {@link StatementListContainer}, which
	 * throw {@link NullPointerException} if {@code slc.getStatements().add(...)} is
	 * called without proper initialisation. Extracting this method allows such
	 * implementors to indicate, whether {@link Statement}s can be added to them via
	 * {@link #addStatement(StatementListContainer, Statement)} without issues.
	 * 
	 * @return Whether {@link Statement}s can be added to the given
	 *         {@link StatementListContainer} via
	 *         {@link #addStatement(StatementListContainer, Statement)}.
	 */
	public boolean canContainStatements(StatementListContainer slc);

	public default boolean addStatements(StatementListContainer slc, Statement[] sts) {
		return this.doMultipleModifications(slc, sts, this::addStatement);
	}
}
