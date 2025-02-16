package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.RelationOperator;

public interface IRelationOperatorInitialiser extends IOperatorInitialiser {
	@Override
	public RelationOperator instantiate();

}