package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.TypeReference;

public interface IInferableTypeInitialiser extends ITypedElementExtensionInitialiser, ITypeReferenceInitialiser {
	@Override
	public InferableType instantiate();

	@Override
	public default boolean canSetTarget(TypeReference tref) {
		return true;
	}
}
