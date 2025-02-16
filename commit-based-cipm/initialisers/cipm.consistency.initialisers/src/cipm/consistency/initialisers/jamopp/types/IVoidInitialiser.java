package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Void;

public interface IVoidInitialiser extends IPrimitiveTypeInitialiser {
	@Override
	public Void instantiate();

}