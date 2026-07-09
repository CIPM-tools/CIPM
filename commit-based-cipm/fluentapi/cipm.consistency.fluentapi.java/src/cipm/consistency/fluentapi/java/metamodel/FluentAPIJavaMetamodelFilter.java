package cipm.consistency.fluentapi.java.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.commons.layout.LayoutPackage;
import org.emftext.language.java.commons.CommonsPackage;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;

/**
 * An implementation of {@link FluentAPITargetMetamodelFilter} for
 * JaMoPP.
 * <p>
 * <p>
 * Excludes the features ( {@link EStructuralFeature} ) that are present in
 * {@link Commentable} and its super-types. Also excludes EClasses found under
 * {@link LayoutPackage}. All other EClasses and features under
 * {@link JavaPackage} are included.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIJavaMetamodelFilter extends FluentAPITargetMetamodelFilter {
	/**
	 * {@inheritDoc}
	 * <p>
	 * <p>
	 * Excludes the features ( {@link EStructuralFeature} ) that are present in
	 * {@link Commentable} and its super-types. All other features under
	 * {@link JavaPackage} are included.
	 */
	@Override
	public boolean isFeatureEligible(EClass holderOfFeat, EStructuralFeature feat) {
		return isFeatureChangeable(feat)
				&& !feat.getEContainingClass().getName().equals(CommonsPackage.Literals.COMMENTABLE.getName());
//				!CommonsPackage.Literals.COMMENTABLE.isSuperTypeOf(feat.getEContainingClass())
//				!feat.getEContainingClass().getInstanceClass().isAssignableFrom(Commentable.class)
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <p>
	 * Excludes EClasses found under the {@link LayoutPackage}. All other EClasses
	 * under {@link JavaPackage} are included.
	 */
	@Override
	public boolean isEClassEligible(EClass eCls) {
		return eCls.getEPackage() == null || !eCls.getEPackage().getName().equals(LayoutPackage.eNAME);
	}
}
