package cipm.consistency.fluentapi.metamodel;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * An abstract class meant to be implemented by classes, which provide access to
 * (EMF-based) metamodels. It is recommended to have the implementing classes
 * parse the entire (or a valid subset) of the metamodel, in order to avoid
 * potential EMF errors due to model invalidity. Instead,
 * {@link FluentAPITargetMetamodelFilter} can be implemented and used to
 * filter undesired EClasses and EStructuralFeatures.
 * <p>
 * <p>
 * Note: Implementors of this class may or may not use the original metamodel
 * packages, due to Eclipse limitations. Attempting to use
 * {@code originalECls.isSuperTypeOf(givenECls)} or vice versa may result in
 * false, due to the original EClass and the given EClass being in different
 * Resources entirely. Instead, use their EAttributes for type-checking (such as
 * their name); excluding {@code eCls.getInstanceClass()} and related methods,
 * since they are not guaranteed to exist in parsed models.
 * 
 * @author Alp Torac Genc
 */
public abstract class FluentAPITargetMetamodelPackageProvider {
	/**
	 * @return The name of the metamodel
	 */
	public abstract String getTargetMetamodelName();

	/**
	 * Considers the Resource instance(s) of the metamodel that this object is
	 * actually using. Due to limitations, this object is not guaranteed to use the
	 * original metamodel.
	 * 
	 * @return All EClasses of the metamodel (including those for abstract classes
	 *         and interfaces)
	 */
	public abstract List<EClass> getAllTargetMetamodelEClasses();

	/**
	 * Considers the Resource instance(s) of the metamodel that this object is
	 * actually using. Due to limitations, this object is not guaranteed to use the
	 * original metamodel.
	 * 
	 * @return All concrete EClasses of the metamodel, i.e. EClasses of classes
	 *         within the metamodel that can be instantiated.
	 */
	public List<EClass> getAllTargetMetamodelConcreteEClasses() {
		var topPac = getTargetMetamodelEcoreEPackages().get(0);
		return List.copyOf(MetamodelUtil.getAllConcreteEClasses(topPac));
	}

	/**
	 * Implemented as non-static, in order to enable implementors to override the
	 * EClass seeking logic.
	 * 
	 * @param eClss    A given collection of EClasses
	 * @param eClsName The name of the EClass to look for in eClss
	 * @return The sought EClass in eClss, if it exists; otherwise null
	 */
	protected EClass getEClassIn(Collection<EClass> eClss, String eClsName) {
		return eClss.stream().filter((eCls) -> eCls.getName().equals(eClsName)).findFirst().orElse(null);
	}

	/**
	 * @param eClsName The name of the EClass to look for in
	 *                 {@link #getAllTargetMetamodelEClasses()}
	 * @return The sought EClass in {@link #getAllTargetMetamodelEClasses()}, if it
	 *         exists; otherwise null
	 */
	public EClass getEClass(String eClsName) {
		return this.getEClassIn(this.getAllTargetMetamodelEClasses(), eClsName);
	}

	/**
	 * Similar to {@link #getAllTargetMetamodelEClasses()}, but retrieves EClasses
	 * from the original metamodel instead. Due to limitations, this object is not
	 * guaranteed to use the original metamodel.
	 * 
	 * @return All EClasses of the original metamodel (including those for abstract
	 *         classes and interfaces)
	 */
	public abstract List<EClass> getAllEClassesInOriginalMetamodel();

	/**
	 * Similar to {@link #getAllTargetMetamodelConcreteEClasses()}, but retrieves
	 * concrete EClasses from the original metamodel instead. Due to limitations,
	 * this object is not guaranteed to use the original metamodel.
	 * 
	 * @return All concrete EClasses of the original metamodel (including those for
	 *         abstract classes and interfaces)
	 */
	public List<EClass> getAllConcreteEClassesInOriginalMetamodel() {
		var allEClss = getAllEClassesInOriginalMetamodel();
		return List
				.of(allEClss.stream().filter((cls) -> !cls.isInterface() && !cls.isAbstract()).toArray(EClass[]::new));
	}

	/**
	 * Meant to provide access to foreign GenModels, which must be integrated to the
	 * GenModel of the fluent api.
	 * <p>
	 * <p>
	 * Considers the Resource instance(s) of the metamodel that this object is
	 * actually using. Due to limitations, this object is not guaranteed to use the
	 * original metamodel.
	 * 
	 * @return A list of all GenModels of the metamodel, which encapsulate the means
	 *         to generate code from the model.
	 */
	public abstract List<GenModel> getTargetMetamodelGenModels();

	/**
	 * Considers the Resource instance(s) of the metamodel that this object is
	 * actually using. Due to limitations, this object is not guaranteed to use the
	 * original metamodel.
	 * 
	 * @return A list of all EPackages from the ecore file(s) of the metamodel
	 */
	public abstract List<EPackage> getTargetMetamodelEcoreEPackages();
}
