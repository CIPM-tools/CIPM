package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.SingleAnnotationParameter;

import cipm.consistency.initialisers.jamopp.annotations.SingleAnnotationParameterInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AnnotationParameter} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link AnnotationParameter}
 * instances.
 */
public interface UsesAnnotationParameters extends UsesAnnotationValues {
	/**
	 * @param val The value of the instance to be constructed
	 * @return A {@link SingleAnnotationParameter} instance with the given
	 *         parameter.
	 */
	public default SingleAnnotationParameter createSingleAnnoParam(AnnotationValue val) {
		var init = new SingleAnnotationParameterInitialiser();
		SingleAnnotationParameter result = init.instantiate();
		init.setValue(result, val);
		return result;
	}

	/**
	 * A variant of {@link #createSingleAnnoParam(AnnotationValue)}, where the
	 * parameter is constructed using {@link #createMinimalSR(String)}.
	 * 
	 * @param val See {@link #createMinimalSR(String)}
	 */
	public default SingleAnnotationParameter createSingleStrAnnoParam(String val) {
		return this.createSingleAnnoParam(this.createMinimalSR(val));
	}

	/**
	 * A variant of {@link #createSingleAnnoParam(AnnotationValue)}, where the
	 * parameter is a {@link NullLiteral}.
	 * 
	 * @see {@link #createNullLiteral()}
	 */
	public default SingleAnnotationParameter createSingleNullAnnoParam() {
		return this.createSingleAnnoParam(this.createNullLiteral());
	}

}
