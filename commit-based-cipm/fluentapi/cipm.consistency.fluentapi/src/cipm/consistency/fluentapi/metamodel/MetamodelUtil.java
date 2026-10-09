package cipm.consistency.fluentapi.metamodel;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * A utility class for metamodel operations, such as accessing their EClasses.
 * 
 * @author Alp Torac Genc
 */
public final class MetamodelUtil {
	/**
	 * @return All {@link EClass}es accessible under the topPac as well as its
	 *         sub-packages.
	 */
	public static Collection<EClass> getAllEClasses(EPackage topPac) {
		var res = new ArrayList<EClass>();
		var topPacEClsfiers = topPac.getEClassifiers();
		if (topPacEClsfiers != null)
			topPacEClsfiers.stream().filter((cls) -> cls instanceof EClass).map((cls) -> (EClass) cls)
					.forEach((cls) -> res.add(cls));
		var ePacs = topPac.getESubpackages();
		ePacs.forEach((pac) -> res.addAll(getAllEClasses(pac)));
		return res;
	}

	/**
	 * @return All concrete {@link EClass}es accessible under the topPac as well as
	 *         its sub-packages.
	 */
	public static Collection<EClass> getAllConcreteEClasses(EPackage topPac) {
		var res = getAllEClasses(topPac);
		res.removeIf((c) -> c.isAbstract() || c.isInterface());
		return res;
	}
}
