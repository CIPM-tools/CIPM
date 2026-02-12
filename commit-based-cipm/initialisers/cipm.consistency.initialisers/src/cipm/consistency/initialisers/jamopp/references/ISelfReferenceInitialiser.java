package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.literals.Self;
import org.emftext.language.java.references.SelfReference;

public interface ISelfReferenceInitialiser extends IReferenceInitialiser {
	@Override
	public SelfReference instantiate();

	public default boolean setSelf(SelfReference sref, Self self) {
		sref.setSelf(self);
		return (self == null && sref.getSelf() == null) || sref.getSelf().equals(self);
	}
}
