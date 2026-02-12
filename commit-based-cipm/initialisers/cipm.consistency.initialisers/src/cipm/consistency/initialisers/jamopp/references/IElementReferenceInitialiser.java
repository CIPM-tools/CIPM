package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.ElementReference;
import org.emftext.language.java.references.ReferenceableElement;

public interface IElementReferenceInitialiser extends IReferenceInitialiser {
	@Override
	public ElementReference instantiate();

	public default boolean setContainedTarget(ElementReference eref, ReferenceableElement conTarget) {
		eref.setContainedTarget(conTarget);
		return (conTarget == null && eref.getContainedTarget() == null) || eref.getContainedTarget().equals(conTarget);
	}

	public default boolean setTarget(ElementReference eref, ReferenceableElement target) {
		eref.setTarget(target);
		return (target == null && eref.getTarget() == null) || eref.getTarget().equals(target);
	}
}
