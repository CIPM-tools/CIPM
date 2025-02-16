package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.classifiers.Classifier;

import cipm.consistency.initialisers.jamopp.annotations.AnnotationInstanceInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AnnotationInstance} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link AnnotationInstance}
 * instances.
 */
public interface UsesAnnotationInstances extends UsesAnnotations {
	/**
	 * @param aiNss        The namespaces of the instance to be constructed.
	 * @param aiAnnotation The annotation of the instance to be constructed.
	 * @return An {@link AnnotationInstance} with the given parameters.
	 */
	public default AnnotationInstance createMinimalAI(String[] aiNss, Classifier aiAnnotation) {
		var aii = new AnnotationInstanceInitialiser();
		AnnotationInstance ai = aii.instantiate();
		aii.addNamespaces(ai, aiNss);
		aii.setAnnotation(ai, aiAnnotation);
		return ai;
	}

	/**
	 * A variant of {@link #createMinimalAI(String[], Classifier)}, where
	 * {@link #createMinimalAnnotation(String)} is used to construct
	 * {@link Classifier} parameter.
	 * 
	 * @param annotationName See {@link #createMinimalAnnotation(String)}
	 */
	public default AnnotationInstance createMinimalAI(String[] aiNss, String annotationName) {
		return this.createMinimalAI(aiNss, this.createMinimalAnnotation(annotationName));
	}
}
