package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.MembersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.members.ClassMethod;

public class ClassMethodInitialiser extends AbstractInitialiserBase implements IClassMethodInitialiser {
	@Override
	public ClassMethod instantiate() {
		return MembersFactory.eINSTANCE.createClassMethod();
	}

	@Override
	public ClassMethodInitialiser newInitialiser() {
		return new ClassMethodInitialiser();
	}
}
