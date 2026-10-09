package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.BlockContainer;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.SynchronizedBlock;
import org.junit.jupiter.api.Assertions;

/**
 * An interface that provides method for tests that check relative positions of
 * statements to one another, i.e. their preceding/proceeding statements.
 * 
 * @author Alp Torac Genc
 */
public interface IStatementTest {
	/**
	 * The return value of this method was derived from the implementation of the
	 * current similarity checker.
	 * 
	 * @return Whether the position of an instance of the given class within its
	 *         container matters.
	 */
	public default boolean doesStatementPositionMatter(Class<?> cls) {
		return ExpressionStatement.class.isAssignableFrom(cls) || LocalVariableStatement.class.isAssignableFrom(cls)
				|| SynchronizedBlock.class.isAssignableFrom(cls);
	}

	/**
	 * Adds a {@link Block} instance to slc, so that multiple statements can be
	 * added to it. Does nothing if there already is a block in slc.
	 */
	public default void addBlockIfNecessary(StatementListContainer slc) {
		if (slc.eClass()
				.getEStructuralFeature(StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT.getName()) != null
				&& ((StatementContainer) slc).getStatement() == null) {
			((StatementContainer) slc).setStatement(StatementsFactory.eINSTANCE.createBlock());
		}

		if (slc.eClass().getEStructuralFeature(StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK.getName()) != null
				&& ((BlockContainer) slc).getBlock() == null) {
			((BlockContainer) slc).setBlock(StatementsFactory.eINSTANCE.createBlock());
		}
	}

	/**
	 * A variant of {@link #addStatement(StatementListContainer, Statement)} for
	 * adding multiple Statements to the given StatementListContainer. If one of the
	 * given Statement instances cannot be added to the given
	 * StatementListContainer, this method will terminate and return false
	 */
	public default boolean addStatements(StatementListContainer slc, Statement... sts) {
		var result = true;
		for (var st : sts) {
			result = result && addStatement(slc, st);
		}
		return result;
	}

	/**
	 * Adds the given Statement to the given StatementListContainer.
	 * <p>
	 * Some Statement types must have one of their certain features to be set to a
	 * Block instance beforehand. To ensure this,
	 * {@link #addBlockIfNecessary(StatementListContainer)} method is called.
	 * 
	 * @param slc The StatementListContainer to contain the given Statement
	 * @param st  The Statement to be contained by the given StatementListContainer
	 * 
	 * @return Whether the given Statement could be added to the given
	 *         StatementListContainer
	 */
	public default boolean addStatement(StatementListContainer slc, Statement st) {
		addBlockIfNecessary(slc);
		Block slcBlock = null;

		if (slc instanceof BlockContainer) {
			slcBlock = ((BlockContainer) slc).getBlock();
		} else if (slc instanceof StatementContainer) {
			slcBlock = (Block) ((StatementContainer) slc).getStatement();
		} else if (slc instanceof Block) {
			slcBlock = (Block) slc;
		}

		if (slcBlock != null) {
			slcBlock.getStatements().add(st);
		} else {
			slc.getStatements().add(st);
		}

		return st.eContainer() == slc || (slcBlock != null && st.eContainer() == slcBlock);
	}

	/**
	 * Adds statements in sts within the range [start, end) (start included, end
	 * excluded) to slc. <br>
	 * <br>
	 * Adds a block to slc first, if statements cannot be added to it in its current
	 * form.
	 */
	public default void addStatementsInRange(StatementListContainer slc, Class<? extends StatementListContainer> slcCls,
			Statement[] sts, int start, int end) {
		this.addBlockIfNecessary(slc);
		for (int i = start; i < end; i++) {
			addStatement(slc, sts[i]);
		}
		Assertions.assertEquals(end - start, slc.getStatements().size());
	}
}
