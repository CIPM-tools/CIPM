package cipm.consistency.fluentapi.metamodel;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * An abstract class meant to be implemented by classes, which filter the
 * (EMF-based) metamodels with respect to their EClasses and EStructuralFeatures
 * thereof. If constraints and invariants are considered within the metamodel,
 * they have to be explicitly checked.
 * <p>
 * <p>
 * Note: Do not use the original metamodel packages while type-checking or
 * filtering, because {@link FluentAPITargetMetamodelPackageProvider} may or may
 * not use the original metamodel packages. Attempting to use
 * {@code originalECls.isSuperTypeOf(givenECls)} or vice versa may result in
 * false, due to the original EClass and the given EClass being in different
 * Resources entirely. Instead, use their EAttributes for type-checking (such as
 * their name); excluding {@code eCls.getInstanceClass()} and related methods,
 * since they are not guaranteed to exist in parsed models.
 * 
 * @author Alp Torac Genc
 */
public abstract class FluentAPITargetMetamodelFilter {
	/**
	 * @param eCls A given EClass
	 * @return Whether eCls should be considered in the fluent api
	 */
	public abstract boolean isEClassEligible(EClass eCls);

	/**
	 * @param holderOfFeat The EClass that contains the feat. Must be specified,
	 *                     since feat may also be declared in a super EClass.
	 * @param feat         A feature of the metamodel
	 * @return Whether the given feature should be considered in fluent api for the
	 *         given EClass (holderOfFeat)
	 */
	public abstract boolean isFeatureEligible(EClass holderOfFeat, EStructuralFeature feat);

	/**
	 * @param eObjEClass An EClass within the metamodel.
	 * @return All features of the given EClass that are considered in fluent api
	 *         (according to {@link #isFeatureEligible(EClass, EStructuralFeature)})
	 */
	public List<EStructuralFeature> getModifiableFeatures(EClass eObjEClass) {
		return eObjEClass.getEAllStructuralFeatures().stream().filter((f) -> this.isFeatureEligible(eObjEClass, f))
				.collect(Collectors.toList());
	}

	/**
	 * @param eObjEClass An EClass within the metamodel.
	 * @return The number of features of the given EClass that are considered in
	 *         fluent api (according to
	 *         {@link #isFeatureEligible(EClass, EStructuralFeature)})
	 */
	public long getModifiableFeatureCount(EClass eObjEClass) {
		return eObjEClass.getEAllStructuralFeatures().stream().filter((f) -> this.isFeatureEligible(eObjEClass, f))
				.count();
	}

	/**
	 * @param eObjEClass An EClass within the metamodel.
	 * @return Whether the given EClass has any features that are considered in
	 *         fluent api (according to
	 *         {@link #isFeatureEligible(EClass, EStructuralFeature)})
	 */
	public boolean hasModifiableFeatures(EClass eObjEClass) {
		return eObjEClass.getEAllStructuralFeatures().stream().anyMatch((f) -> this.isFeatureEligible(eObjEClass, f));
	}

	/**
	 * Not declared as static, because the underlying metamodel could introduce
	 * constraints regarding this. This is a default implementation.
	 * 
	 * @param feat A feature within the metamodel
	 * @return Whether the given feat can be modified
	 */
	public boolean isFeatureChangeable(EStructuralFeature feat) {
		return feat.isChangeable() && !feat.isDerived();
	}
}
