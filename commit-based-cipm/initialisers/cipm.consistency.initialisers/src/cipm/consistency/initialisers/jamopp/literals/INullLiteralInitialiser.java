package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.NullLiteral;

public interface INullLiteralInitialiser extends ILiteralInitialiser {
	@Override
	public NullLiteral instantiate();

}
