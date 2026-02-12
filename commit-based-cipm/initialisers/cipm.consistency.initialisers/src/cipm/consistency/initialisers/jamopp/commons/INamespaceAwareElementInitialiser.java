package cipm.consistency.initialisers.jamopp.commons;

import org.emftext.language.java.commons.NamespaceAwareElement;

public interface INamespaceAwareElementInitialiser extends ICommentableInitialiser {
	@Override
	public NamespaceAwareElement instantiate();

	public default boolean addNamespaces(NamespaceAwareElement nae, String[] namespaces) {
		return this.doMultipleModifications(nae, namespaces, this::addNamespace);
	}

	public default boolean addNamespace(NamespaceAwareElement nae, String namespace) {
		if (namespace != null) {
			nae.getNamespaces().add(namespace);
			return nae.getNamespaces().contains(namespace);
		}
		return true;
	}
}
