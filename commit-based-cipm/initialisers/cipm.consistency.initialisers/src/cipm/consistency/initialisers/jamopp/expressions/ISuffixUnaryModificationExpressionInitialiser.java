package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.SuffixUnaryModificationExpression;

public interface ISuffixUnaryModificationExpressionInitialiser extends IUnaryModificationExpressionInitialiser {
	@Override
	public SuffixUnaryModificationExpression instantiate();

}