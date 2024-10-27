package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.arrays.ArrayDimension;

import cipm.consistency.initialisers.jamopp.arrays.ArrayDimensionInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ArrayDimension} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ArrayDimension}
 * instances.
 */
public interface UsesArrayDimensions extends UsesAnnotationInstances {
	/**
	 * @return An uninitialised {@link ArrayDimension} instance.
	 */
	public default ArrayDimension createMinimalArrayDimension() {
		var init = new ArrayDimensionInitialiser();
		var result = init.instantiate();
		return result;
	}

	/**
	 * @return An {@link ArrayDimension} instance with the given {@link AnnotationInstance} array.
	 */
	public default ArrayDimension createArrayDimension(AnnotationInstance[] ais) {
		var result = this.createMinimalArrayDimension();
		var init = new ArrayDimensionInitialiser();
		init.addAnnotations(result, ais);
		return result;
	}

	/**
	 * @return An {@link ArrayDimension} instance that has an {@link AnnotationInstance} with
	 * the given namespace and instance name.
	 */
	public default ArrayDimension createArrayDimension(String[] annotationInstanceNamespaces,
			String annotationInstanceName) {
		return this.createArrayDimension(new AnnotationInstance[] {
				this.createMinimalAI(annotationInstanceNamespaces, annotationInstanceName) });
	}
}
