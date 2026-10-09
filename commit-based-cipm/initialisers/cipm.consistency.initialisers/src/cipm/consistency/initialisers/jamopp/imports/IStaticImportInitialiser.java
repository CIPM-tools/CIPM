package cipm.consistency.initialisers.jamopp.imports;

import org.emftext.language.java.imports.StaticImport;
import org.emftext.language.java.modifiers.Static;

public interface IStaticImportInitialiser extends IImportInitialiser {
	@Override
	public StaticImport instantiate();

	public default boolean setStatic(StaticImport sImp, Static st) {
		sImp.setStatic(st);
		return (st == null && sImp.getStatic() == null) || sImp.getStatic().equals(st);
	}
}
