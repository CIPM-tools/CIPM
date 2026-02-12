package cipm.consistency.initialisers.jamopp.commons;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class CommonsInitialiserPackage implements IInitialiserPackage {
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { ICommentableInitialiser.class, INamedElementInitialiser.class,
				INamespaceAwareElementInitialiser.class });
	}
}
