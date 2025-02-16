package cipm.consistency.initialisers.jamopp.imports;

import org.emftext.language.java.imports.StaticClassifierImport;

public interface IStaticClassifierImportInitialiser extends IStaticImportInitialiser {
	@Override
	public StaticClassifierImport instantiate();
}
