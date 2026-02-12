package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class InferableTypeInitialiser extends AbstractInitialiserBase implements IInferableTypeInitialiser {
	@Override
	public IInferableTypeInitialiser newInitialiser() {
		return new InferableTypeInitialiser();
	}

	@Override
	public InferableType instantiate() {
		return TypesFactory.eINSTANCE.createInferableType();
	}
}