package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.expressions.AdditiveExpression;
import org.emftext.language.java.expressions.AndExpression;
import org.emftext.language.java.expressions.AndExpressionChild;
import org.emftext.language.java.expressions.AssignmentExpression;
import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.ConditionalAndExpression;
import org.emftext.language.java.expressions.ConditionalAndExpressionChild;
import org.emftext.language.java.expressions.ConditionalOrExpression;
import org.emftext.language.java.expressions.ConditionalOrExpressionChild;
import org.emftext.language.java.expressions.EqualityExpression;
import org.emftext.language.java.expressions.EqualityExpressionChild;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.InstanceOfExpression;
import org.emftext.language.java.expressions.InstanceOfExpressionChild;
import org.emftext.language.java.expressions.NestedExpression;
import org.emftext.language.java.expressions.RelationExpression;
import org.emftext.language.java.expressions.RelationExpressionChild;
import org.emftext.language.java.expressions.UnaryExpression;
import org.emftext.language.java.expressions.UnaryExpressionChild;
import org.emftext.language.java.expressions.util.ExpressionsSwitch;
import org.emftext.language.java.operators.AssignmentOperator;
import org.emftext.language.java.operators.EqualityOperator;
import org.emftext.language.java.operators.RelationOperator;
import org.emftext.language.java.operators.UnaryOperator;
import org.emftext.language.java.types.TypeReference;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;

/**
 * Similarity decisions for expression elements.
 * <p>
 * All expression elements are strong typed with no identifying attributes or non-containment
 * references. Their location and runtime types are assumed to be checked before this switch is
 * called.
 * </p>
 */
public class ExpressionsSimilaritySwitch extends ExpressionsSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public ExpressionsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
    public Boolean caseAssignmentExpression(AssignmentExpression exp1) {
		this.logInfoMessage("caseAssignmentExpression");

        AssignmentExpression exp2 = (AssignmentExpression) this.getCompareElement();

        AssignmentExpressionChild child1 = exp1.getChild();
        AssignmentExpressionChild child2 = exp2.getChild();
        Boolean childSimilarity = this.isSimilar(child1, child2);
        if (JaMoPPBooleanUtil.isFalse(childSimilarity)) {
            return Boolean.FALSE;
        }

        AssignmentOperator op1 = exp1.getAssignmentOperator();
        AssignmentOperator op2 = exp2.getAssignmentOperator();
        Boolean operatorSimilarity = this.isSimilar(op1, op2);
        if (JaMoPPBooleanUtil.isFalse(operatorSimilarity)) {
            return Boolean.FALSE;
        }

        Expression value1 = exp1.getValue();
        Expression value2 = exp2.getValue();
        Boolean valueSimilarity = this.isSimilar(value1, value2);
        return JaMoPPBooleanUtil.isNotFalse(valueSimilarity);
    }

    @Override
    public Boolean caseEqualityExpression(EqualityExpression exp1) {
    	this.logInfoMessage("caseEqualityExpression");
    	
        EqualityExpression exp2 = (EqualityExpression) this.getCompareElement();

        // check operator equality
        EList<EqualityOperator> operators1 = exp1.getEqualityOperators();
        EList<EqualityOperator> operators2 = exp2.getEqualityOperators();
        Boolean operatorSimilarity = this.areSimilar(operators1, operators2);
        if (JaMoPPBooleanUtil.isFalse(operatorSimilarity)) {
            return Boolean.FALSE;
        }

        // check expression equality
        EList<EqualityExpressionChild> children1 = exp1.getChildren();
        EList<EqualityExpressionChild> children2 = exp2.getChildren();
        Boolean childSimilarity = this.areSimilar(children1, children2);
        return JaMoPPBooleanUtil.isNotFalse(childSimilarity);
    }

    @Override
    public Boolean caseRelationExpression(RelationExpression exp1) {
    	this.logInfoMessage("caseRelationExpression");

        RelationExpression exp2 = (RelationExpression) this.getCompareElement();

        // check operator equality
        EList<RelationOperator> operators1 = exp1.getRelationOperators();
        EList<RelationOperator> operators2 = exp2.getRelationOperators();
        Boolean operatorSimilarity = this.areSimilar(operators1, operators2);
        if (JaMoPPBooleanUtil.isFalse(operatorSimilarity)) {
            return Boolean.FALSE;
        }

        // check expression equality
        EList<RelationExpressionChild> children1 = exp1.getChildren();
        EList<RelationExpressionChild> children2 = exp2.getChildren();
        Boolean childSimilarity = this.areSimilar(children1, children2);
        return JaMoPPBooleanUtil.isNotFalse(childSimilarity);
    }

    @Override
    public Boolean caseAndExpression(AndExpression exp1) {
    	this.logInfoMessage("caseAndExpression");

        AndExpression exp2 = (AndExpression) this.getCompareElement();

        // check expression equality
        EList<AndExpressionChild> children1 = exp1.getChildren();
        EList<AndExpressionChild> children2 = exp2.getChildren();
        Boolean childSimilarity = this.areSimilar(children1, children2);
        return JaMoPPBooleanUtil.isNotFalse(childSimilarity);
    }

    @Override
    public Boolean caseUnaryExpression(UnaryExpression exp1) {
    	this.logInfoMessage("caseUnaryExpression");

        UnaryExpression exp2 = (UnaryExpression) this.getCompareElement();

        // check operator equality
        EList<UnaryOperator> operators1 = exp1.getOperators();
        EList<UnaryOperator> operators2 = exp2.getOperators();
        Boolean operatorSimilarity = this.areSimilar(operators1, operators2);
        if (JaMoPPBooleanUtil.isFalse(operatorSimilarity)) {
            return Boolean.FALSE;
        }

        // check expression equality
        UnaryExpressionChild child1 = exp1.getChild();
        UnaryExpressionChild child2 = exp2.getChild();
        return this.isSimilar(child1, child2);
    }
    
    @Override
    public Boolean caseAdditiveExpression(AdditiveExpression exp1) {
    	this.logInfoMessage("caseAdditiveExpression");
    	
    	AdditiveExpression exp2 = (AdditiveExpression) this.getCompareElement();
    	
    	Boolean opSimilarity = this.areSimilar(exp1.getAdditiveOperators(), exp2.getAdditiveOperators());
    	if (JaMoPPBooleanUtil.isFalse(opSimilarity)) {
    		return Boolean.FALSE;
    	}
    	
    	return this.areSimilar(exp1.getChildren(), exp2.getChildren());
    }

    @Override
    public Boolean caseInstanceOfExpression(InstanceOfExpression exp1) {
    	this.logInfoMessage("caseInstanceOfExpression");

        InstanceOfExpression exp2 = (InstanceOfExpression) this.getCompareElement();

        // check type equality
        TypeReference typeReference1 = exp1.getTypeReference();
        TypeReference typeReference2 = exp2.getTypeReference();
        Boolean typeSimilarity = this.isSimilar(typeReference1, typeReference2);
        if (JaMoPPBooleanUtil.isFalse(typeSimilarity)) {
            return Boolean.FALSE;
        }

        // check expression equality
        InstanceOfExpressionChild child1 = exp1.getChild();
        InstanceOfExpressionChild child2 = exp2.getChild();
        return this.isSimilar(child1, child2);
    }

    @Override
    public Boolean caseConditionalOrExpression(ConditionalOrExpression exp1) {
    	this.logInfoMessage("caseConditionalOrExpression");

        ConditionalOrExpression exp2 = (ConditionalOrExpression) this.getCompareElement();

        // check expression equality
        EList<ConditionalOrExpressionChild> children1 = exp1.getChildren();
        EList<ConditionalOrExpressionChild> children2 = exp2.getChildren();
        return this.areSimilar(children1, children2);
    }

    @Override
    public Boolean caseConditionalAndExpression(ConditionalAndExpression exp1) {
    	this.logInfoMessage("caseConditionalAndExpression");

        ConditionalAndExpression exp2 = (ConditionalAndExpression) this.getCompareElement();

        // check expression equality
        EList<ConditionalAndExpressionChild> children1 = exp1.getChildren();
        EList<ConditionalAndExpressionChild> children2 = exp2.getChildren();
        return this.areSimilar(children1, children2);
    }

    @Override
    public Boolean caseNestedExpression(NestedExpression exp1) {
    	this.logInfoMessage("caseNestedExpression");

        NestedExpression exp2 = (NestedExpression) this.getCompareElement();

        // check expression equality
        Expression childExp1 = exp1.getExpression();
        Expression childExp2 = exp2.getExpression();
        return this.isSimilar(childExp1, childExp2);
    }

    @Override
    public Boolean defaultCase(EObject object) {
    	this.logInfoMessage("defaultCase for Expression");
    	
        return Boolean.TRUE;
    }
}