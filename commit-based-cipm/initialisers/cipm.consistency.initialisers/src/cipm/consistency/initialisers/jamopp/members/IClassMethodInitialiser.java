package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;

import cipm.consistency.initialisers.jamopp.statements.IStatementListContainerInitialiser;

/**
 * An interface meant for {@link IInitialiser} implementors that are supposed to
 * create {@link ClassMethod} instances. <br>
 * <br>
 * Due to inconsistencies regarding {@link ClassMethod}, it provides 2 methods
 * for adding {@link Statement} instances to it. Adding multiple statements is
 * only possible via {@code classMethod.getStatement().add(...)}, which only
 * works if classMethod has a {@link Block} set as its statement. This can be
 * achieved by passing a {@link Block} instance to
 * {@link #setStatement(org.emftext.language.java.statements.StatementContainer, Statement)}.
 * <br>
 * <br>
 * Otherwise, one may only add a single statement to {@link ClassMethod} via
 * {@code classMethod.setStatement(...)}. <b>Note that the said method will
 * REPLACE the former statement when used. </b>
 * 
 * @author Alp Torac Genc
 *
 */
public interface IClassMethodInitialiser extends IMethodInitialiser, IStatementListContainerInitialiser {
	@Override
	public ClassMethod instantiate();

	/**
	 * A {@link Block} instance must be added to slc (a {@link ClassMethod} in this
	 * case) for this method to function as intended. This can be done via
	 * {@link #setStatement(org.emftext.language.java.statements.StatementContainer, Statement)}.
	 * If slc already has its {@link Statement} set via
	 * {@link #setStatement(org.emftext.language.java.statements.StatementContainer, Statement)},
	 * that statement must be saved in a temporal variable before calling the said
	 * method. It can then be re-added to slc via calling this method. <br>
	 * <br>
	 * Otherwise, {@code slc.setStatement(st)} can be used to add a single
	 * {@link Statement} to slc. <b>Note that the {@link Statement} added to slc
	 * this way WILL BE REPLACED, if {@code slc.setStatement(anotherSt)} is
	 * called</b>. <br>
	 * <br>
	 * Overridden only to provide commentary.
	 */
	@Override
	public default boolean addStatement(StatementListContainer slc, Statement st) {
		return IStatementListContainerInitialiser.super.addStatement(slc, st);
	}

	@Override
	public default boolean canContainStatements(StatementListContainer slc) {
		return ((ClassMethod) slc).getBlock() != null;
	}
}
