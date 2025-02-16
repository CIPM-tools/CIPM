package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.IntegerLiteral;

public interface IIntegerLiteralInitialiser extends ILiteralInitialiser {
	@Override
	public IntegerLiteral instantiate();

}
