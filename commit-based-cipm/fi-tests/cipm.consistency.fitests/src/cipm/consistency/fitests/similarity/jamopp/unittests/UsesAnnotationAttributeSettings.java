package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.members.InterfaceMethod;

import cipm.consistency.initialisers.jamopp.annotations.AnnotationAttributeSettingInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AnnotationAttributeSetting} instances. <br>
 * <br>
 * Contains methods that can be used to create
 * {@link AnnotationAttributeSetting} instances.
 */
public interface UsesAnnotationAttributeSettings extends UsesAnnotationValues {
	/**
	 * @param im  The attribute of the instance to be constructed.
	 * @param val The value of the instance to be constructed.
	 * 
	 * @return An {@link AnnotationAttributeSetting} instance with the given
	 *         attribute and value.
	 */
	public default AnnotationAttributeSetting createAAS(InterfaceMethod im, AnnotationValue val) {
		var aasInit = new AnnotationAttributeSettingInitialiser();
		var aas = aasInit.instantiate();
		aasInit.setAttribute(aas, im);
		aasInit.setValue(aas, val);
		return aas;
	}

	/**
	 * A variant of {@link #createAAS(InterfaceMethod, AnnotationValue)}, where the
	 * first parameter is null and the second parameter constructed using
	 * {@link #createMinimalSR(String)}.
	 * 
	 * @param val See {@link #createMinimalSR(String)}
	 */
	public default AnnotationAttributeSetting createStringAAS(String val) {
		return this.createAAS(null, this.createMinimalSR(val));
	}

	/**
	 * A variant of {@link #createAAS(InterfaceMethod, AnnotationValue)} where both
	 * parameters are null.
	 */
	public default AnnotationAttributeSetting createEmptyAAS() {
		return this.createAAS(null, null);
	}

	/**
	 * A variant of {@link #createAAS(InterfaceMethod, AnnotationValue)}, where the
	 * first parameter is null and the second parameter is constructed using
	 * {@link #createNullLiteral()}.
	 */
	public default AnnotationAttributeSetting createNullAAS() {
		return this.createAAS(null, this.createNullLiteral());
	}
}
