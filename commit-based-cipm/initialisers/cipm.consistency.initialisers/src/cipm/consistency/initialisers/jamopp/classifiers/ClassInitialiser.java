package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.ClassifiersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.classifiers.Class;

public class ClassInitialiser extends AbstractInitialiserBase implements IClassInitialiser {
	@Override
	public Class instantiate() {
		var fac = ClassifiersFactory.eINSTANCE;
		return fac.createClass();
	}

	@Override
	public ClassInitialiser newInitialiser() {
		return new ClassInitialiser();
	}
}
