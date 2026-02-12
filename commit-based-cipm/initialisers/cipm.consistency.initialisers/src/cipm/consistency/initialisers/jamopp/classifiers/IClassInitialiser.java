package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.types.TypeReference;

public interface IClassInitialiser extends IConcreteClassifierInitialiser, IImplementorInitialiser {
	@Override
	public Class instantiate();

	public default boolean setDefaultExtends(Class cls, TypeReference defExt) {
		cls.setDefaultExtends(defExt);
		return (defExt == null && cls.getDefaultExtends() == null) || cls.getDefaultExtends().equals(defExt);
	}

	public default boolean setExtends(Class cls, TypeReference ext) {
		cls.setExtends(ext);
		return (ext == null && cls.getExtends() == null) || cls.getExtends().equals(ext);
	}
}
