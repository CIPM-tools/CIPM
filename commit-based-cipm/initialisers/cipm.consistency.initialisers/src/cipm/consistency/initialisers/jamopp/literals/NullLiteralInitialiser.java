package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.literals.NullLiteral;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class NullLiteralInitialiser extends AbstractInitialiserBase implements INullLiteralInitialiser {
	@Override
	public INullLiteralInitialiser newInitialiser() {
		return new NullLiteralInitialiser();
	}

	@Override
	public NullLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createNullLiteral();
	}
}