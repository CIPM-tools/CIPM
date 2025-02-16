package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.LambdaBody;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ILambdaBodyInitialiser extends ICommentableInitialiser {
	@Override
	public LambdaBody instantiate();

}
