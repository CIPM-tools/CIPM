package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.StringReference;

public interface IStringReferenceInitialiser extends IReferenceInitialiser {
	@Override
	public StringReference instantiate();

	public default boolean setValue(StringReference sref, String val) {
		sref.setValue(val);
		return (val == null && sref.getValue() == null) || sref.getValue().equals(val);
	}
}
