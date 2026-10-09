package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.TextBlockReference;

public interface ITextBlockReferenceInitialiser extends IReferenceInitialiser {
	@Override
	public TextBlockReference instantiate();

	public default boolean setValue(TextBlockReference tbref, String val) {
		tbref.setValue(val);
		return (val == null && tbref.getValue() == null) || tbref.getValue().equals(val);
	}
}
