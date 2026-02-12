package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.members.InterfaceMethod;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotationAttributeSettingInitialiser extends ICommentableInitialiser {
	@Override
	public AnnotationAttributeSetting instantiate();

	public default boolean setAttribute(AnnotationAttributeSetting aas, InterfaceMethod attr) {
		aas.setAttribute(attr);
		return (attr == null && aas.getAttribute() == null) || aas.getAttribute().equals(attr);
	}

	public default boolean setValue(AnnotationAttributeSetting aas, AnnotationValue val) {
		aas.setValue(val);
		return (val == null && aas.getValue() == null) || aas.getValue().equals(val);
	}
}
