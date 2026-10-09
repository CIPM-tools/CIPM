package cipm.consistency.initialisers.jamopp.instantiations;

import org.emftext.language.java.instantiations.ExplicitConstructorCall;
import org.emftext.language.java.literals.Self;

public interface IExplicitConstructorCallInitialiser extends IInstantiationInitialiser {
	@Override
	public ExplicitConstructorCall instantiate();

	public default boolean setCallTarget(ExplicitConstructorCall ecc, Self callTarget) {
		ecc.setCallTarget(callTarget);
		return (callTarget == null && ecc.getCallTarget() == null) || ecc.getCallTarget().equals(callTarget);
	}
}
