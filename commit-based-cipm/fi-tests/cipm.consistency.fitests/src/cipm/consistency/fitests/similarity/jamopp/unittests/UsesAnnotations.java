package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.classifiers.Annotation;

import cipm.consistency.initialisers.jamopp.classifiers.AnnotationInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link Annotation} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Annotation} instances.
 */
public interface UsesAnnotations {
	/**
	 * @param annotationName The name of the instance to be constructed
	 * @return An {@link Annotation} instance with the given parameter.
	 */
	public default Annotation createMinimalAnnotation(String annotationName) {
		var ai = new AnnotationInitialiser();
		Annotation result = ai.instantiate();
		ai.setName(result, annotationName);
		return result;
	}
}
