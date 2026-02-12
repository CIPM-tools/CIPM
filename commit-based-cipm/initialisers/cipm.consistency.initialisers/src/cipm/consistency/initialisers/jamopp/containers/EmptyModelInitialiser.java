package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.EmptyModel;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class EmptyModelInitialiser extends AbstractInitialiserBase implements IEmptyModelInitialiser {
	@Override
	public IEmptyModelInitialiser newInitialiser() {
		return new EmptyModelInitialiser();
	}

	@Override
	public EmptyModel instantiate() {
		return ContainersFactory.eINSTANCE.createEmptyModel();
	}
}