package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;
import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;
import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;
import cipm.consistency.initialisers.jamopp.imports.IImportingElementInitialiser;

public interface IJavaRootInitialiser extends INamedElementInitialiser, INamespaceAwareElementInitialiser,
		IAnnotableInitialiser, IImportingElementInitialiser {
	@Override
	public JavaRoot instantiate();

	public default boolean setOrigin(JavaRoot jr, Origin origin) {
		jr.setOrigin(origin);
		return (origin == null && jr.getOrigin() == null) || jr.getOrigin().equals(origin);
	}
}
