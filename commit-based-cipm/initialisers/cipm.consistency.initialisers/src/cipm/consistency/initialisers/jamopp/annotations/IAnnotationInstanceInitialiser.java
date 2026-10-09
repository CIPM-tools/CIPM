package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.AnnotationParameter;
import org.emftext.language.java.classifiers.Classifier;

import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;

public interface IAnnotationInstanceInitialiser extends INamespaceAwareElementInitialiser {
	@Override
	public AnnotationInstance instantiate();

	public default boolean setAnnotation(AnnotationInstance ai, Classifier anno) {
		ai.setAnnotation(anno);
		return (anno == null && ai.getAnnotation() == null) || ai.getAnnotation().equals(anno);
	}

	public default boolean setParameter(AnnotationInstance ai, AnnotationParameter param) {
		ai.setParameter(param);
		return (param == null && ai.getParameter() == null) || ai.getParameter().equals(param);
	}
}
