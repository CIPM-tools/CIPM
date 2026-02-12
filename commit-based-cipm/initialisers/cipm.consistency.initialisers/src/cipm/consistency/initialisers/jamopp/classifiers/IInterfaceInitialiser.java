package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.types.TypeReference;

public interface IInterfaceInitialiser extends IConcreteClassifierInitialiser {
	@Override
	public Interface instantiate();

	public default boolean addDefaultExtends(Interface intfc, TypeReference defExt) {
		if (defExt != null) {
			intfc.getDefaultExtends().add(defExt);
			return intfc.getDefaultExtends().contains(defExt);
		}
		return true;
	}

	public default boolean addDefaultExtends(Interface intfc, TypeReference[] defExts) {
		return this.doMultipleModifications(intfc, defExts, this::addDefaultExtends);
	}

	public default boolean addExtends(Interface intfc, TypeReference ext) {
		if (ext != null) {
			intfc.getExtends().add(ext);
			return intfc.getExtends().contains(ext);
		}
		return true;
	}

	public default boolean addExtends(Interface intfc, TypeReference[] exts) {
		return this.doMultipleModifications(intfc, exts, this::addExtends);
	}
}
