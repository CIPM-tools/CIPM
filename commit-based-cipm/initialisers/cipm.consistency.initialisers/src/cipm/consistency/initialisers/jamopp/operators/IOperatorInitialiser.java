package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Operator;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IOperatorInitialiser extends ICommentableInitialiser {
	@Override
	public Operator instantiate();

}
