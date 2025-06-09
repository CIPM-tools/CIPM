package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInitializationValue;
import org.emftext.language.java.arrays.ArrayInitializer;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotationValueInitialiser;

public interface IArrayInitializerInitialiser
		extends IAnnotationValueInitialiser, IArrayInitializationValueInitialiser {
	@Override
	public ArrayInitializer instantiate();

	public default boolean addInitialValue(ArrayInitializer ai, ArrayInitializationValue initVal) {
		if (initVal != null) {
			ai.getInitialValues().add(initVal);
			return ai.getInitialValues().contains(initVal);
		}
		return true;
	}

	public default boolean addInitialValues(ArrayInitializer ai, ArrayInitializationValue[] initVals) {
		return this.doMultipleModifications(ai, initVals, this::addInitialValue);
	}
}
