package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.Member;

import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;

public interface IMemberInitialiser extends INamedElementInitialiser {
	@Override
	public Member instantiate();
}
