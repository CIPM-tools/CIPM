package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.JumpLabel;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class JumpLabelInitialiser extends AbstractInitialiserBase implements IJumpLabelInitialiser {
	@Override
	public IJumpLabelInitialiser newInitialiser() {
		return new JumpLabelInitialiser();
	}

	@Override
	public JumpLabel instantiate() {
		return StatementsFactory.eINSTANCE.createJumpLabel();
	}
}