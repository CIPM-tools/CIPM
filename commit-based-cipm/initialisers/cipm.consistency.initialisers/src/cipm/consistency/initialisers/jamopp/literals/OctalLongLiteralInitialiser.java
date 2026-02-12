package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.literals.OctalLongLiteral;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class OctalLongLiteralInitialiser extends AbstractInitialiserBase implements IOctalLongLiteralInitialiser {
	@Override
	public IOctalLongLiteralInitialiser newInitialiser() {
		return new OctalLongLiteralInitialiser();
	}

	@Override
	public OctalLongLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createOctalLongLiteral();
	}
}