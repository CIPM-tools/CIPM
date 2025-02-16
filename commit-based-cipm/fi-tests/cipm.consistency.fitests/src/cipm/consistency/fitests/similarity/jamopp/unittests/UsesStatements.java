package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Assert;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.JumpLabel;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.variables.LocalVariable;

import cipm.consistency.initialisers.jamopp.statements.AssertInitialiser;
import cipm.consistency.initialisers.jamopp.statements.BlockInitialiser;
import cipm.consistency.initialisers.jamopp.statements.JumpLabelInitialiser;
import cipm.consistency.initialisers.jamopp.statements.LocalVariableStatementInitialiser;
import cipm.consistency.initialisers.jamopp.statements.ReturnInitialiser;
import cipm.consistency.initialisers.jamopp.variables.LocalVariableInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link Statement} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Statement} instances.
 */
public interface UsesStatements extends UsesLiterals {
	/**
	 * @param returnVal The return value of the instance to be constructed
	 * @return A {@link Return} instance with the given {@link Expression} as its
	 *         return value.
	 */
	public default Return createMinimalReturn(Expression returnVal) {
		var init = new ReturnInitialiser();
		Return result = init.instantiate();
		init.setReturnValue(result, returnVal);
		return result;
	}

	/**
	 * @param lvName The name of the instance to be constructed
	 * @return A {@link LocalVariableStatement} instance with the given name.
	 */
	public default LocalVariableStatement createMinimalLVS(String lvName) {
		var init = new LocalVariableInitialiser();
		var res = init.instantiate();
		init.setName(res, lvName);
		return this.createMinimalLVS(res);
	}

	/**
	 * @param lvName The {@link LocalVariable} of the instance to be constructed
	 * @return A {@link LocalVariableStatement} instance with the given
	 *         {@link LocalVariable}.
	 */
	public default LocalVariableStatement createMinimalLVS(LocalVariable lv) {
		var init = new LocalVariableStatementInitialiser();
		var res = init.instantiate();
		init.setVariable(res, lv);
		return res;
	}

	/**
	 * @param jlName   The name of the instance to be constructed
	 * @param targetSt The target statement of the instance to be constructed
	 * @return A {@link JumpLabel} instance with the given parameter
	 */
	public default JumpLabel createMinimalJL(String jlName, Statement targetSt) {
		var init = new JumpLabelInitialiser();
		var jl = init.instantiate();
		init.setName(jl, jlName);
		init.setStatement(jl, targetSt);
		return jl;
	}

	/**
	 * @param sts The statements that will be added to the instance to be
	 *            constructed
	 * @return A {@link Block} instance with the given parameters
	 */
	public default Block createMinimalBlock(Statement[] sts) {
		var init = new BlockInitialiser();
		Block result = init.instantiate();
		init.addStatements(result, sts);
		return result;
	}

	/**
	 * @return An {@link Assert} instance, whose {@link Condition} is constructed
	 *         with {@link #createBooleanLiteral(boolean)} using the value true.
	 */
	public default Assert createMinimalTrivialAssert() {
		var init = new AssertInitialiser();
		Assert result = init.instantiate();
		init.setCondition(result, this.createBooleanLiteral(true));
		return result;
	}

	/**
	 * A variant of {@link #createMinimalReturn(Expression)}, where the parameter is
	 * constructed with {@link #createNullLiteral()}.
	 * 
	 * @see {@link #createNullLiteral()}
	 */
	public default Return createMinimalNullReturn() {
		return this.createMinimalReturn(this.createNullLiteral());
	}

	/**
	 * A variant of {@link #createMinimalJLToNullReturn(String)}, where the
	 * constructed instance has no name (null).
	 */
	public default JumpLabel createMinimalJLToNullReturn() {
		return this.createMinimalJLToNullReturn(null);
	}

	/**
	 * A variant of {@link #createMinimalJLToTrivialAssert(String)}, where the
	 * constructed instance has no name (null).
	 */
	public default JumpLabel createMinimalJLToTrivialAssert() {
		return this.createMinimalJLToTrivialAssert(null);
	}

	/**
	 * A variant of {@link #createMinimalJL(String, Statement)}, where the target
	 * statement is generated with {@link #createMinimalNullReturn()}.
	 */
	public default JumpLabel createMinimalJLToNullReturn(String jlName) {
		return this.createMinimalJL(jlName, this.createMinimalNullReturn());
	}

	/**
	 * A variant of {@link #createMinimalJL(String, Statement)}, where the target
	 * statement is generated with {@link #createMinimalTrivialAssert()}.
	 */
	public default JumpLabel createMinimalJLToTrivialAssert(String jlName) {
		return this.createMinimalJL(jlName, this.createMinimalTrivialAssert());
	}

	/**
	 * A variant of {@link #createMinimalBlock(Statement[])} that uses a single
	 * statement generated by {@link #createMinimalNullReturn()}.
	 */
	public default Block createMinimalBlockWithNullReturn() {
		return this.createMinimalBlock(new Statement[] { this.createMinimalNullReturn() });
	}

	/**
	 * A variant of {@link #createMinimalBlock(Statement[])} that uses a single
	 * statement generated by {@link #createMinimalTrivialAssert()}.
	 */
	public default Block createMinimalBlockWithTrivialAssert() {
		return this.createMinimalBlock(new Statement[] { this.createMinimalTrivialAssert() });
	}
}
