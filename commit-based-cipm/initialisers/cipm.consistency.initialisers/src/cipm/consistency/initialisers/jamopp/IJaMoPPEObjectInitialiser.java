package cipm.consistency.initialisers.jamopp;

import cipm.consistency.initialisers.eobject.IEObjectInitialiser;

/**
 * An interface meant to be implemented by {@link IEObjectInitialiser}
 * sub-types, whose purpose is to create {@link EObject} implementors within
 * JaMoPP.
 * 
 * @author Alp Torac Genc
 */
public interface IJaMoPPEObjectInitialiser extends IEObjectInitialiser {
	@Override
	public IJaMoPPEObjectInitialiser newInitialiser();
}
