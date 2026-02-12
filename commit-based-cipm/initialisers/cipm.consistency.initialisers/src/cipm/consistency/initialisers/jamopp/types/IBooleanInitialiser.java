package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Boolean;

public interface IBooleanInitialiser extends IPrimitiveTypeInitialiser {
	@Override
	public Boolean instantiate();

}