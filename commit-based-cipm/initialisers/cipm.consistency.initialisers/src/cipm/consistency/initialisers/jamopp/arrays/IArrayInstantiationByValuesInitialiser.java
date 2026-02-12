package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInitializer;
import org.emftext.language.java.arrays.ArrayInstantiationByValues;

public interface IArrayInstantiationByValuesInitialiser extends IArrayInstantiationInitialiser {

	@Override
	public ArrayInstantiationByValues instantiate();

	public default boolean setArrayInitializer(ArrayInstantiationByValues arrIns, ArrayInitializer arrInit) {
		arrIns.setArrayInitializer(arrInit);
		return (arrInit == null && arrIns.getArrayInitializer() == null)
				|| arrIns.getArrayInitializer().equals(arrInit);
	}
}
