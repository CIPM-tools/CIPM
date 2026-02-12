package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.SingleAnnotationParameter;

public interface ISingleAnnotationParameterInitialiser extends IAnnotationParameterInitialiser {
	@Override
	public SingleAnnotationParameter instantiate();

	public default boolean setValue(SingleAnnotationParameter sap, AnnotationValue val) {
		sap.setValue(val);
		return (val == null && sap.getValue() == null) || sap.getValue().equals(val);
	}
}
