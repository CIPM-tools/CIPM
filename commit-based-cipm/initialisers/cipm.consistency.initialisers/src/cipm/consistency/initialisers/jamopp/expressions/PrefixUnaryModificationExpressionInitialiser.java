package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.PrefixUnaryModificationExpression;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PrefixUnaryModificationExpressionInitialiser extends AbstractInitialiserBase
		implements IPrefixUnaryModificationExpressionInitialiser {
	@Override
	public IPrefixUnaryModificationExpressionInitialiser newInitialiser() {
		return new PrefixUnaryModificationExpressionInitialiser();
	}

	@Override
	public PrefixUnaryModificationExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createPrefixUnaryModificationExpression();
	}
}