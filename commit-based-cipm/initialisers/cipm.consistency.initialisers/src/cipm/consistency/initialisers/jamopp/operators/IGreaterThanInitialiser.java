package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.GreaterThan;

public interface IGreaterThanInitialiser extends IRelationOperatorInitialiser {
	@Override
	public GreaterThan instantiate();

}
