package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.MembersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.members.InterfaceMethod;

public class InterfaceMethodInitialiser extends AbstractInitialiserBase implements IInterfaceMethodInitialiser {
	@Override
	public InterfaceMethod instantiate() {
		return MembersFactory.eINSTANCE.createInterfaceMethod();
	}

	@Override
	public InterfaceMethodInitialiser newInitialiser() {
		return new InterfaceMethodInitialiser();
	}
}
