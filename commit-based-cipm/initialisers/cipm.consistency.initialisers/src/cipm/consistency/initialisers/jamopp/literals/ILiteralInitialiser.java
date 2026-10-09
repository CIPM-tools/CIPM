package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.Literal;

import cipm.consistency.initialisers.jamopp.expressions.IPrimaryExpressionInitialiser;

public interface ILiteralInitialiser extends IPrimaryExpressionInitialiser {
	@Override
	public Literal instantiate();

}