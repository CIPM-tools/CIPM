package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.classifiers.Enumeration;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class EnumerationInitialiser extends AbstractInitialiserBase implements IEnumerationInitialiser {
	@Override
	public Enumeration instantiate() {
		var fac = ClassifiersFactory.eINSTANCE;
		return fac.createEnumeration();
	}

	@Override
	public EnumerationInitialiser newInitialiser() {
		return new EnumerationInitialiser();
	}
}
