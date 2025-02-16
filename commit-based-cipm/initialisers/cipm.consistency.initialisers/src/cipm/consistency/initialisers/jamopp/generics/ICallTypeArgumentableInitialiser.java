package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.CallTypeArgumentable;
import org.emftext.language.java.generics.TypeArgument;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ICallTypeArgumentableInitialiser extends ICommentableInitialiser {
	@Override
	public CallTypeArgumentable instantiate();

	public default boolean addCallTypeArgument(CallTypeArgumentable cta, TypeArgument callTypeArg) {
		if (callTypeArg != null) {
			cta.getCallTypeArguments().add(callTypeArg);
			return cta.getCallTypeArguments().contains(callTypeArg);
		}
		return true;
	}

	public default boolean addCallTypeArguments(CallTypeArgumentable cta, TypeArgument[] callTypeArgs) {
		return this.doMultipleModifications(cta, callTypeArgs, this::addCallTypeArgument);
	}
}
