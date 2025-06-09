package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.literals.This;
import org.emftext.language.java.parameters.ReceiverParameter;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

public interface IReceiverParameterInitialiser extends IAnnotableInitialiser, IParameterInitialiser {
	@Override
	public ReceiverParameter instantiate();

	public default boolean setOuterTypeReference(ReceiverParameter rp, TypeReference otRef) {
		rp.setOuterTypeReference(otRef);
		return (otRef == null && rp.getOuterTypeReference() == null) || rp.getOuterTypeReference().equals(otRef);
	}

	public default boolean setThisReference(ReceiverParameter rp, This thisRef) {
		rp.setThisReference(thisRef);
		return (thisRef == null && rp.getThisReference() == null) || rp.getThisReference().equals(thisRef);
	}
}
