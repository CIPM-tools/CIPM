package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Double;

public interface IDoubleInitialiser extends IPrimitiveTypeInitialiser {
	@Override
	public Double instantiate();

}