package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.BinaryIntegerLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class BinaryIntegerLiteralInitialiser extends AbstractInitialiserBase
		implements IBinaryIntegerLiteralInitialiser {
	@Override
	public IBinaryIntegerLiteralInitialiser newInitialiser() {
		return new BinaryIntegerLiteralInitialiser();
	}

	@Override
	public BinaryIntegerLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createBinaryIntegerLiteral();
	}
}