package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayDimension;
import org.emftext.language.java.arrays.ArrayTypeable;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IArrayTypeableInitialiser extends ICommentableInitialiser {
	@Override
	public ArrayTypeable instantiate();

	public default boolean addArrayDimensionAfter(ArrayTypeable at, ArrayDimension arrDimAfter) {
		if (arrDimAfter != null) {
			at.getArrayDimensionsAfter().add(arrDimAfter);
			return at.getArrayDimensionsAfter().contains(arrDimAfter);
		}
		return true;
	}

	public default boolean addArrayDimensionBefore(ArrayTypeable at, ArrayDimension arrDimBefore) {
		if (arrDimBefore != null) {
			at.getArrayDimensionsBefore().add(arrDimBefore);
			return at.getArrayDimensionsBefore().contains(arrDimBefore);
		}
		return true;
	}

	public default boolean addArrayDimensionsAfter(ArrayTypeable at, ArrayDimension[] arrDimsAfter) {
		return this.doMultipleModifications(at, arrDimsAfter, this::addArrayDimensionAfter);
	}

	public default boolean addArrayDimensionsBefore(ArrayTypeable at, ArrayDimension[] arrDimsBefore) {
		return this.doMultipleModifications(at, arrDimsBefore, this::addArrayDimensionBefore);
	}
}
