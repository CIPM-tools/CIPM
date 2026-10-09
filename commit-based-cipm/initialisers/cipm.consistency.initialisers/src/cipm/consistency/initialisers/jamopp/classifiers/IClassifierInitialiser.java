package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.PackageImport;

import cipm.consistency.initialisers.jamopp.references.IReferenceableElementInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypeInitialiser;

/**
 * An interface meant for {@link IInitialiser} implementors that are supposed to
 * create {@link Classifier} instances. <br>
 * <br>
 * <b>Note: The methods addImport/addPackageImport of {@link Classifier} add the
 * imports to its {@link CompilationUnit}. {@link Classifier} has no attributes
 * itself. The same holds for their corresponding methods in this
 * initialiser.</b>. Use the methods {@link #canAddImports(Classifier)} and
 * {@link #canAddPackageImports(Classifier)} to determine, whether certain
 * imports can be added.
 * 
 * @author Alp Torac Genc
 */

public interface IClassifierInitialiser extends ITypeInitialiser, IReferenceableElementInitialiser {
	@Override
	public Classifier instantiate();

	/**
	 * Adds the given {@link Import} to the {@link CompilationUnit} containing the
	 * given {@link Classifier}. <br>
	 * <br>
	 * Attempting to add null imports will return true, since there is no
	 * modification to perform.
	 * 
	 * @see {@link IClassifierInitialiser}
	 */
	public default boolean addImport(Classifier cls, Import imp) {
		if (imp != null) {
			if (!this.canAddImports(cls)) {
				return false;
			}
			var cu = cls.getContainingCompilationUnit();
			cu.getImports().add(imp);
			return cu.getImports().stream().anyMatch((i) -> i.equals(imp));
		}
		return true;
	}

	public default boolean addImports(Classifier cls, Import[] imps) {
		return this.doMultipleModifications(cls, imps, this::addImport);
	}

	/**
	 * Whether {@link Import}s can be added via
	 * {@link #addImport(Classifier, Import)}
	 */
	public default boolean canAddImports(Classifier cls) {
		return cls.getContainingCompilationUnit() != null;
	}

	/**
	 * Whether {@link PackageImport}s can be added via
	 * {@link #addPackageImport(Classifier, PackageImport)}
	 */
	public default boolean canAddPackageImports(Classifier cls) {
		return cls.getContainingCompilationUnit() != null;
	}

	/**
	 * Adds the given {@link PackageImport} to the {@link CompilationUnit}
	 * containing the given {@link Classifier}. <br>
	 * <br>
	 * Attempting to add null package imports will return true, since there is no
	 * modification to perform.
	 * 
	 * @see {@link IClassifierInitialiser}
	 */
	public default boolean addPackageImport(Classifier cls, PackageImport imp) {
		if (imp != null) {
			if (!this.canAddPackageImports(cls)) {
				return false;
			}
			var cu = cls.getContainingCompilationUnit();
			cu.getImports().add(imp);
			return cu.getImports().stream().anyMatch((i) -> i.equals(imp));
		}
		return true;
	}

	public default boolean addPackageImports(Classifier cls, PackageImport[] imps) {
		return this.doMultipleModifications(cls, imps, this::addPackageImport);
	}
}
