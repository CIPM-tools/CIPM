package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.AnonymousClass;

import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypeInitialiser;

public interface IAnonymousClassInitialiser extends IMemberContainerInitialiser, ITypeInitialiser {
	@Override
	public AnonymousClass instantiate();
}
