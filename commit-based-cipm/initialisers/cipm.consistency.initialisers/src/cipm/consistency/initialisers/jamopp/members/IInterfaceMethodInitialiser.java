package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.members.InterfaceMethod;

public interface IInterfaceMethodInitialiser extends IMethodInitialiser {
	@Override
	public InterfaceMethod instantiate();

	public default boolean setDefaultValue(InterfaceMethod im, AnnotationValue defVal) {
		im.setDefaultValue(defVal);
		return (defVal == null && im.getDefaultValue() == null) || im.getDefaultValue().equals(defVal);
	}
}
