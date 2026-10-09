package cipm.consistency.fluentapi.pcm.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;

/**
 * An implementation of {@link FluentAPITargetMetamodelFilter} for PCM.
 * <p>
 * <p>
 * Excludes the features ( {@link EStructuralFeature} ) that are present in
 * {@link EObject} and its super-types. All other EClasses and features under
 * the {@link PcmPackage} are included.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIPcmMetamodelFilter extends FluentAPITargetMetamodelFilter {
	/**
	 * {@inheritDoc}
	 * <p>
	 * <p>
	 * Excludes the features ( {@link EStructuralFeature} ) that are present in
	 * {@link EObject} and its super-types. All other features under the
	 * {@link PcmPackage} are included.
	 */
	@Override
	public boolean isFeatureEligible(EClass holderOfFeat, EStructuralFeature feat) {
		return isFeatureChangeable(feat)
				&& !feat.getEContainingClass().getName().equals(EcorePackage.Literals.EOBJECT.getName());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <p>
	 * All EClasses under the {@link PcmPackage} are included.
	 */
	@Override
	public boolean isEClassEligible(EClass eCls) {
		return true;
	}
}
