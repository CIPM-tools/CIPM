package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.parameters.OrdinaryParameter;
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
import org.splevo.jamopp.util.JaMoPPElementUtil;

import com.google.common.base.Strings;

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
     * Check expression statement similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>similarity statements expressions</li>
     * </ul>
     * 
     * @param statement1
     *            The expression statement to compare with the compare element.
     * @return True/False if the expression statements are similar or not.
     */
    @Override
    public Boolean caseExpressionStatement(ExpressionStatement statement1) {
    	this.logMessage("caseExpressionStatement");

        ExpressionStatement statement2 = (ExpressionStatement) this.getCompareElement();

        Expression exp1 = statement1.getExpression();
        Expression exp2 = statement2.getExpression();

        Boolean expSimilarity = this.isSimilar(exp1, exp2);
        if (expSimilarity == Boolean.FALSE) {
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
     * Check the similarity of a variable declaration.
     * 
     * The similarity is decided by the declared variables name only. A changed variable type or
     * value initialization should lead to a changed statement not a new one.
     * 
     * @param varStmt1
     *            The variable to compare with the original / right-side one
     * @return True/False if they are similar or not.
     */
    @Override
    public Boolean caseLocalVariableStatement(LocalVariableStatement varStmt1) {
    	this.logMessage("caseLocalVariableStatement");
    	
        LocalVariableStatement varStmt2 = (LocalVariableStatement) this.getCompareElement();

        Variable var1 = varStmt1.getVariable();
        Variable var2 = varStmt2.getVariable();
        Boolean varSimilarity = this.isSimilar(var1, var2);
        if (varSimilarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }
        
        if (this.shouldCheckStatementPosition()) {
        	varSimilarity = this.isSimilar(varStmt1.eContainer(), varStmt2.eContainer(), false);
        	if (!varSimilarity) {
        		return Boolean.FALSE;
        	}
        	if (differentPredecessor(varStmt1, varStmt2) && differentSuccessor(varStmt1, varStmt2)) {
        		return Boolean.FALSE;
        	}
        }

        return Boolean.TRUE;
    }

    /**
     * Check return statement similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>expressions similarity</li>
     * </ul>
     * 
     * @param returnStatement1
     *            The return statement to compare with the compare element.
     * @return True/False if the return statements are similar or not.
     */
    @Override
    public Boolean caseReturn(Return returnStatement1) {
    	this.logMessage("caseReturn");

        Return returnStatement2 = (Return) this.getCompareElement();

        Expression exp1 = returnStatement1.getReturnValue();
        Expression exp2 = returnStatement2.getReturnValue();

        return this.isSimilar(exp1, exp2);
    }

    /**
     * Check synchronized statement similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>expression similarity</li>
     * </ul>
     * 
     * @param statement1
     *            The synchronized statement to compare with the compare element.
     * @return True/False if the synchronized statements are similar or not.
     */
    @Override
    public Boolean caseSynchronizedBlock(SynchronizedBlock statement1) {
    	this.logMessage("caseSynchronizedBlock");

        SynchronizedBlock statement2 = (SynchronizedBlock) this.getCompareElement();

        Expression exp1 = statement1.getLockProvider();
        Expression exp2 = statement2.getLockProvider();
        Boolean similarity = this.isSimilar(exp1, exp2);
        if (similarity == Boolean.FALSE) {
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
     * @return True/False if the throw statements are similar or not.
     */
    @Override
    public Boolean caseThrow(Throw throwStatement1) {
    	this.logMessage("caseThrow");
    	
        return Boolean.TRUE;
    }

    @Override
    public Boolean caseCatchBlock(CatchBlock catchBlock1) {
    	this.logMessage("caseCatchBlock");

        CatchBlock catchBlock2 = (CatchBlock) this.getCompareElement();

        OrdinaryParameter catchedException1 = catchBlock1.getParameter();
        OrdinaryParameter catchedException2 = catchBlock2.getParameter();

        Boolean exceptionSimilarity = this.isSimilar(catchedException1, catchedException2);
        if (exceptionSimilarity == Boolean.FALSE) {
            return exceptionSimilarity;
        }

        return Boolean.TRUE;
    }

    /**
     * Check if two conditional statements are similar.
     * 
     * Similarity is checked by:
     * <ul>
     * <li>similarity of the expressions</li>
     * </ul>
     * 
     * The then and else statements are not checked as part of the condition statement check
     * because this is only about the container if statement similarity. The contained
     * statements are checked in a separate step of the compare process if the enclosing
     * condition statement matches.
     * 
     * @param conditional1
     *            The statement to compare with the compare element.
     * @return True/False whether they are similar or not.
     */
    @Override
    public Boolean caseConditional(Conditional conditional1) {
    	this.logMessage("caseConditional");

        Conditional conditional2 = (Conditional) this.getCompareElement();

        Expression expression1 = conditional1.getCondition();
        Expression expression2 = conditional2.getCondition();
        Boolean expressionSimilarity = this.isSimilar(expression1, expression2);
        if (expressionSimilarity == Boolean.FALSE) {
            return expressionSimilarity;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean caseJump(Jump jump1) {
    	this.logMessage("caseJump");
    	
        Jump jump2 = (Jump) this.getCompareElement();

        Boolean similarity = this.isSimilar(jump1.getTarget(), jump2.getTarget());
        if (similarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean caseJumpLabel(JumpLabel label1) {
    	this.logMessage("caseJumpLabel");

        JumpLabel label2 = (JumpLabel) this.getCompareElement();

        String name1 = Strings.nullToEmpty(label1.getName());
        String name2 = Strings.nullToEmpty(label2.getName());

        return (name1.equals(name2));
    }
    
    @Override
    public Boolean caseSwitch(Switch switch1) {
    	this.logMessage("caseSwitch");
    	
    	Switch switch2 = (Switch) this.getCompareElement();
    	
    	return this.isSimilar(switch1.getVariable(), switch2.getVariable());
    }

    @Override
    public Boolean defaultCase(EObject object) {
    	this.logMessage("defaultCase for Statement");
    	
        return Boolean.TRUE;
    }

    /**
     * Decide of two statements differ from each other or not.
     * 
     * @param statement1
     *            The first statement to compare
     * @param statement2
     *            The second statement to compare.
     * @return True if they differ, null if not.
     */
    private boolean differentPredecessor(Statement statement1, Statement statement2) {
        Statement pred1 = getPredecessor(statement1);
        Statement pred2 = getPredecessor(statement2);
        Boolean similarity = this.isSimilar(pred1, pred2, false);
        return similarity == Boolean.FALSE;
    }

    /**
     * Check if two statements have differing successor statements.
     * 
     * @param statement1
     *            The first statement to check.
     * @param statement2
     *            The second statement to check.
     * @return True if their successor differ, false if not.
     */
    private boolean differentSuccessor(Statement statement1, Statement statement2) {
        Statement pred1 = getSuccessor(statement1);
        Statement pred2 = getSuccessor(statement2);
        Boolean similarity = this.isSimilar(pred1, pred2, false);
        return similarity == Boolean.FALSE;
    }

    /**
     * Get the predecessor statement of a statement within the parents container statement list.<br>
     * If a statement is the first, the only one, or the container is not a
     * {@link StatementListContainer}, or no predecessor exists, null will be returned.
     * 
     * @param statement
     *            The statement to get the predecessor for.
     * @return The predecessor or null if non exists.
     */
    private Statement getPredecessor(Statement statement) {

        int pos = JaMoPPElementUtil.getPositionInContainer(statement);
        if (pos > 0) {
            StatementListContainer container = (StatementListContainer) statement.eContainer();
            return container.getStatements().get(pos - 1);
        }

        return null;
    }

    /**
     * Get the successor statement of a statement within the parents container statement list.<br>
     * If a statement is the last, the only one, or the container is not a
     * {@link StatementListContainer}, no successor exists, null will be returned.
     * 
     * @param statement
     *            The statement to get the predecessor for.
     * @return The predecessor or null if non exists.
     */
    private Statement getSuccessor(Statement statement) {

        int pos = JaMoPPElementUtil.getPositionInContainer(statement);
        if (pos != -1) {
            StatementListContainer container = (StatementListContainer) statement.eContainer();
            if (container.getStatements().size() > pos + 1) {
                return container.getStatements().get(pos + 1);
            }
        }

        return null;
    }
}