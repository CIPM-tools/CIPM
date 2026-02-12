package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInstantiationBySize;
import org.emftext.language.java.expressions.Expression;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IArrayInstantiationBySizeInitialiser extends IArrayInstantiationInitialiser, ITypedElementInitialiser {

	@Override
	public ArrayInstantiationBySize instantiate();

	public default boolean addSize(ArrayInstantiationBySize arrIns, Expression size) {
		if (size != null) {
			arrIns.getSizes().add(size);
			return arrIns.getSizes().contains(size);
		}
		return true;
	}

	public default boolean addSizes(ArrayInstantiationBySize arrIns, Expression[] sizes) {
		return this.doMultipleModifications(arrIns, sizes, this::addSize);
	}
}
