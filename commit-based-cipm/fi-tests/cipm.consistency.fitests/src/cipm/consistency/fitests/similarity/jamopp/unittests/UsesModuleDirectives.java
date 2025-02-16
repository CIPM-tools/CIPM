package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.containers.Package;
import org.emftext.language.java.modules.AccessProvidingModuleDirective;
import org.emftext.language.java.modules.ExportsModuleDirective;
import org.emftext.language.java.modules.OpensModuleDirective;

import cipm.consistency.initialisers.jamopp.modules.ExportsModuleDirectiveInitialiser;
import cipm.consistency.initialisers.jamopp.modules.IAccessProvidingModuleDirectiveInitialiser;
import cipm.consistency.initialisers.jamopp.modules.OpensModuleDirectiveInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ModuleDirective} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ModuleDirective}
 * instances.
 */
public interface UsesModuleDirectives extends UsesPackages, UsesModuleReferences {
	/**
	 * @param init The initialiser that will be used to construct the instance
	 * @param pac  The accessable package of the instance to be constructed
	 * @return An {@link AccessProvidingModuleDirective} instance with the given
	 *         parameters
	 */
	public default AccessProvidingModuleDirective createMinimalAPMD(IAccessProvidingModuleDirectiveInitialiser init,
			Package pac) {
		var result = init.instantiate();
		init.setAccessablePackage(result, pac);
		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalAPMD(IAccessProvidingModuleDirectiveInitialiser, Package)}
	 * that constructs a {@link ExportsModuleDirective} instance.
	 */
	public default ExportsModuleDirective createMinimalEMD(Package pac) {
		return (ExportsModuleDirective) this.createMinimalAPMD(new ExportsModuleDirectiveInitialiser(), pac);
	}

	/**
	 * A variant of
	 * {@link #createMinimalAPMD(IAccessProvidingModuleDirectiveInitialiser, Package)}
	 * that constructs a {@link OpensModuleDirective} instance.
	 */
	public default OpensModuleDirective createMinimalOMD(Package pac) {
		return (OpensModuleDirective) this.createMinimalAPMD(new OpensModuleDirectiveInitialiser(), pac);
	}

	/**
	 * A variant of {@link #createMinimalEMD(Package)}, where
	 * {@link #createMinimalPackage(String[])} is used to construct the parameter.
	 * 
	 * @param pacNss See {@link #createMinimalPackage(String[])}
	 */
	public default ExportsModuleDirective createMinimalEMD(String[] pacNss) {
		return this.createMinimalEMD(this.createMinimalPackage(pacNss));
	}

	/**
	 * A variant of {@link #createMinimalOMD(Package)}, where
	 * {@link #createMinimalPackage(String[])} is used to construct the parameter.
	 * 
	 * @param pacNss See {@link #createMinimalPackage(String[])}
	 */
	public default OpensModuleDirective createMinimalOMD(String[] pacNss) {
		return this.createMinimalOMD(this.createMinimalPackage(pacNss));
	}
}
