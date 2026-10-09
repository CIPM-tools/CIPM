package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.CatchBlock;
import org.emftext.language.java.statements.Conditional;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Jump;
import org.emftext.language.java.statements.JumpLabel;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.Switch;
import org.emftext.language.java.statements.SynchronizedBlock;
import org.emftext.language.java.statements.Throw;
import org.emftext.language.java.statements.util.StatementsSwitch;
import org.emftext.language.java.variables.Variable;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;
import org.splevo.jamopp.util.JaMoPPElementUtil;

/**
 * Similarity decisions for the statement elements.
 */
public class StatementsSimilaritySwitch extends StatementsSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}
	
	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

    public StatementsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
	 * Checks the similarity of 2 expression statements. Similarity is checked by comparing
	 * their expressions ({@link ExpressionStatement#getExpression()}).
     * <br><br>
     * Note: Positions of the statements are checked as well.
     * 
     * @param statement1
     *            The expression statement to compare with the compare element.
     * @return False if expressions are not similar or their positions are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseExpressionStatement(ExpressionStatement statement1) {
    	this.logInfoMessage("caseExpressionStatement");

        ExpressionStatement statement2 = (ExpressionStatement) this.getCompareElement();

        Expression exp1 = statement1.getExpression();
        Expression exp2 = statement2.getExpression();

        Boolean expSimilarity = this.isSimilar(exp1, exp2);
        if (JaMoPPBooleanUtil.isFalse(expSimilarity)) {
            return Boolean.FALSE;
        }

        // check predecessor similarity
        if (this.shouldCheckStatementPosition()) {
            if (differentPredecessor(statement1, statement2) && differentSuccessor(statement1, statement2)) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Checks the similarity of 2 local variable statements. Similarity is checked by comparing:
     * <ol>
     * <li> Variable ({@link LocalVariableStatement#getVariable()})
     * <li> Container ({@code varStmt.eContainer()})
     * </ol>
     * 
     * Note: Positions of the local variable statements are also checked.
     * 
     * @param varStmt1 The local variable statement to compare with compareElement
     * @return False if not similar or positions are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseLocalVariableStatement(LocalVariableStatement varStmt1) {
    	this.logInfoMessage("caseLocalVariableStatement");
    	
        LocalVariableStatement varStmt2 = (LocalVariableStatement) this.getCompareElement();

        Variable var1 = varStmt1.getVariable();
        Variable var2 = varStmt2.getVariable();
        Boolean varSimilarity = this.isSimilar(var1, var2);
        if (JaMoPPBooleanUtil.isFalse(varSimilarity)) {
            return Boolean.FALSE;
        }
        
        if (this.shouldCheckStatementPosition()) {
        	var con1 = JaMoPPElementUtil.getFirstContainerNotOfGivenType(varStmt1, Block.class);
        	var con2 = JaMoPPElementUtil.getFirstContainerNotOfGivenType(varStmt2, Block.class);
        	varSimilarity = this.isSimilar(con1, con2, false);
        	if (JaMoPPBooleanUtil.isFalse(varSimilarity)) {
        		return Boolean.FALSE;
        	}
        	if (differentPredecessor(varStmt1, varStmt2) && differentSuccessor(varStmt1, varStmt2)) {
        		return Boolean.FALSE;
        	}
        }

        return Boolean.TRUE;
    }

    /**
     * Checks the similarity of 2 return statements. Similarity is checked by comparing their
     * return values ({@link Return#getReturnValue()}).
     * 
     * @param returnStatement1
     *            The return statement to compare with the compare element.
     * @return Result of similarity checking the return values.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseReturn(Return returnStatement1) {
    	this.logInfoMessage("caseReturn");

        Return returnStatement2 = (Return) this.getCompareElement();

        Expression exp1 = returnStatement1.getReturnValue();
        Expression exp2 = returnStatement2.getReturnValue();

        return this.isSimilar(exp1, exp2);
    }

    /**
     * Checks the similarity of 2 synchronized blocks. Similarity is checked by comparing
     * their lock providers ({@link SynchronizedBlock#getLockProvider()}).
     * <br><br>
     * Note: Positions of the synchronized blocks are checked as well.
     * 
     * @param statement1
     *            The synchronized statement to compare with the compare element.
     * @return False if not similar or positions are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseSynchronizedBlock(SynchronizedBlock statement1) {
    	this.logInfoMessage("caseSynchronizedBlock");

        SynchronizedBlock statement2 = (SynchronizedBlock) this.getCompareElement();

        Expression exp1 = statement1.getLockProvider();
        Expression exp2 = statement2.getLockProvider();
        Boolean similarity = this.isSimilar(exp1, exp2);
        if (JaMoPPBooleanUtil.isFalse(similarity)) {
            return Boolean.FALSE;
        }

        if (this.shouldCheckStatementPosition()) {
            if (differentPredecessor(statement1, statement2) && differentSuccessor(statement1, statement2)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Check throw statement similarity.<br>
     * 
     * Only one throw statement can exist at the same code location. As a result the container
     * similarity checked implicitly is enough for this.
     * 
     * @param throwStatement1
     *            The throw statement to compare with the compare element.
     * @return True
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseThrow(Throw throwStatement1) {
    	this.logInfoMessage("caseThrow");
    	
        return Boolean.TRUE;
    }

    /**
     * Checks the similarity of 2 catch blocks. Similarity is checked by comparing
     * their parameters ({@link CatchBlock#getParameter()}).
     * 
     * @param catchBlock1 The catch block to compare with compareElement
     * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseCatchBlock(CatchBlock catchBlock1) {
    	this.logInfoMessage("caseCatchBlock");

        CatchBlock catchBlock2 = (CatchBlock) this.getCompareElement();

        OrdinaryParameter catchedException1 = catchBlock1.getParameter();
        OrdinaryParameter catchedException2 = catchBlock2.getParameter();

        Boolean exceptionSimilarity = this.isSimilar(catchedException1, catchedException2);
        return JaMoPPBooleanUtil.isNotFalse(exceptionSimilarity);
    }

    /**
     * Check if two conditional statements are similar.
     * 
     * Similarity is checked by:
     * <ul>
     * <li> Conditions ({@link Conditional#getCondition()}) </li>
     * </ul>
     * 
     * The then and else statements are not checked as part of the condition statement check
     * because this is only about the container if statement similarity. The contained
     * statements are checked in a separate step of the compare process if the enclosing
     * condition statement matches.
     * 
     * @param conditional1
     *            The conditional to compare with the compare element.
     * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseConditional(Conditional conditional1) {
    	this.logInfoMessage("caseConditional");

        Conditional conditional2 = (Conditional) this.getCompareElement();

        Expression expression1 = conditional1.getCondition();
        Expression expression2 = conditional2.getCondition();
        Boolean expressionSimilarity = this.isSimilar(expression1, expression2);
        return JaMoPPBooleanUtil.isNotFalse(expressionSimilarity);
    }

    /**
     * Checks the similarity of 2 jumps. Similarity is checked by comparing
     * their target ({@link Jump#getTarget()}).
     * 
     * @param jump1 The jump to compare with compareElement
     * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseJump(Jump jump1) {
    	this.logInfoMessage("caseJump");
    	
        Jump jump2 = (Jump) this.getCompareElement();

        Boolean targetSimilarity = this.isSimilar(jump1.getTarget(), jump2.getTarget());
        return JaMoPPBooleanUtil.isNotFalse(targetSimilarity);
    }

    /**
     * Checks the similarity of 2 jump labels. Similarity is checked by comparing
     * their names ({@link JumpLabel#getName()}).
     * 
     * @param label1 The jump label to compare with compareElement
     * @return True if names are similar, false otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseJumpLabel(JumpLabel label1) {
    	this.logInfoMessage("caseJumpLabel");

        JumpLabel label2 = (JumpLabel) this.getCompareElement();
        return JaMoPPNameComparisonUtil.namesEqual(label1, label2);
    }

    /**
     * Checks the similarity of 2 switch statements. Similarity is checked by comparing
     * their variables ({@link Switch#getVariable()}).
     * 
     * @param switch1 The switch statement to compare with compareElement
     * @return Result of similarity checking their variables.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseSwitch(Switch switch1) {
    	this.logInfoMessage("caseSwitch");
    	
    	Switch switch2 = (Switch) this.getCompareElement();
    	
    	return this.isSimilar(switch1.getVariable(), switch2.getVariable());
    }

    @Override
    public Boolean defaultCase(EObject object) {
    	this.logInfoMessage("defaultCase for Statement");
    	
        return Boolean.TRUE;
    }

    /**
     * Check if two statements have differing predecessor statements.
     * 
     * @param statement1
     *            The first statement to check the predecessor of.
     * @param statement2
     *            The second statement to check the predecessor of.
     * @return True if their predecessors differ, false otherwise.
     */
    private boolean differentPredecessor(Statement statement1, Statement statement2) {
        Statement pred1 = getPredecessor(statement1);
        Statement pred2 = getPredecessor(statement2);
        Boolean similarity = this.isSimilar(pred1, pred2, false);
        return JaMoPPBooleanUtil.isNotTrue(similarity);
    }

    /**
     * Check if two statements have differing successor statements.
     * 
     * @param statement1
     *            The first statement to check the successor of.
     * @param statement2
     *            The second statement to check the successor of.
     * @return True if their successors differ, false otherwise.
     */
    private boolean differentSuccessor(Statement statement1, Statement statement2) {
        Statement succ1 = getSuccessor(statement1);
        Statement succ2 = getSuccessor(statement2);
        Boolean similarity = this.isSimilar(succ1, succ2, false);
        return JaMoPPBooleanUtil.isNotTrue(similarity);
    }

    /**
     * Get the predecessor statement of a statement within the parents container statement list.<br>
     * If a statement is the first, the only one, or the container is not a
     * {@link StatementListContainer}, or no predecessor exists, null will be returned.
     * 
     * @param statement
     *            The statement to get the predecessor for.
     * @return The predecessor or null if no predecessor exists.
     */
    private Statement getPredecessor(Statement statement) {

        int pos = JaMoPPElementUtil.getPositionInContainer(statement);
        if (pos > 0) {
            StatementListContainer container = (StatementListContainer) statement.eContainer();
            var sts = container.getStatements();
            
            return sts != null ? sts.get(pos - 1) : null;
        }

        return null;
    }

    /**
     * Get the successor statement of a statement within the parents container statement list.<br>
     * If a statement is the last, the only one, or the container is not a
     * {@link StatementListContainer}, no successor exists, null will be returned.
     * 
     * @param statement
     *            The statement to get the successor for.
     * @return The successor or null if no successor exists.
     */
    private Statement getSuccessor(Statement statement) {

        int pos = JaMoPPElementUtil.getPositionInContainer(statement);
        if (pos != -1) {
            StatementListContainer container = (StatementListContainer) statement.eContainer();
            var sts = container.getStatements();
            if (sts != null && sts.size() > pos + 1) {
                return sts.get(pos + 1);
            }
        }

        return null;
    }
}