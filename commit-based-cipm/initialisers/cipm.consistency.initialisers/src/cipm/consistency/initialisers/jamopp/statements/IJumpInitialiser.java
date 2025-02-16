package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Jump;
import org.emftext.language.java.statements.JumpLabel;

public interface IJumpInitialiser extends IStatementInitialiser {
	@Override
	public Jump instantiate();

	public default boolean setTarget(Jump jump, JumpLabel target) {
		jump.setTarget(target);
		return (target == null && jump.getTarget() == null) || jump.getTarget().equals(target);
	}
}
