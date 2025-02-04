package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.SynchronizedBlock;

/**
 * An interface that provides method for tests that check relative positions of
 * statements to one another, i.e. their preceding/proceeding statements.
 * 
 * @author Alp Torac Genc
 */
public interface IStatementPositionTest {
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
}
