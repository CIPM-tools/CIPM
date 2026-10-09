package cipm.consistency.fluentapi.pcm.metamodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.pcm.PcmPackage;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;
import cipm.consistency.fluentapi.metamodel.MetamodelUtil;

/**
 * An implementation of {@link FluentAPITargetMetamodelPackageProvider} for PCM.
 * <p>
 * <p>
 * This class internally "fixes" the Ecore and GenModel of JaMoPP that it
 * parses, in order to avoid having duplicated Resource instances during fluent
 * api generation. This is due to Eclipse plug-in limitations.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIPcmMetamodelPackageProvider extends FluentAPITargetMetamodelPackageProvider {
	/**
	 * The (plug-in based) URI to PCM's genmodel file
	 */
	private static final URI pcmMetamodelGenModelURI = URI
			.createURI("platform:/plugin/org.palladiosimulator.pcm/model/pcm.genmodel");
	/**
	 * The (plug-in based) URI to PCM's ecore file
	 */
	private static final URI pcmMetamodelEcoreModelURI = URI
			.createURI("platform:/plugin/org.palladiosimulator.pcm/model/pcm.ecore");

	/**
	 * The ResourceSet, which will contain the Resources of the GenModel and the
	 * Ecore model parsed by this class ( {@link #ecoreRes} and {@link #genModelRes}
	 * ). Note that those Resources are not the original Resources of the JaMoPP
	 * model.
	 */
	private final ResourceSet metamodelResSet = new ResourceSetImpl();
	/**
	 * The Resource instance containing the parsed Ecore model of PCM. This is NOT
	 * the Resource instance of {@code PcmPackage.eINSTANCE}.
	 */
	private Resource ecoreRes;
	/**
	 * The Resource instance containing the parsed GenModel of PCM. This does NOT
	 * use the Resource instance of {@code PcmPackage.eINSTANCE}, but
	 * {@link #ecoreRes}.
	 */
	private Resource genModelRes;
	/**
	 * The list containing the original PCM EClasses that are available under
	 * {@code PcmPackage.eINSTANCE}. These EClasses are NOT the same as those in
	 * {@link #ecoreRes}.
	 */
	private List<EClass> originalEClss;

	/**
	 * Caches the (original) EClasses found under the PCM metamodel ( under
	 * {@code PcmPackage.eINSTANCE} ) in {@link #originalEClss}, in order to spare
	 * constantly retrieving them from the Resource instances.
	 */
	private void cacheOriginalEClasses() {
		if (originalEClss == null) {
			originalEClss = new ArrayList<EClass>(MetamodelUtil.getAllEClasses(PcmPackage.eINSTANCE));
		}
	}

	@Override
	public String getTargetMetamodelName() {
		var topPac = getTargetMetamodelEcoreEPackages().get(0);
		return topPac.getName();
	}

	@Override
	public List<EClass> getAllTargetMetamodelEClasses() {
		var topPac = getTargetMetamodelEcoreEPackages().get(0);
		return List.copyOf(MetamodelUtil.getAllEClasses(topPac));
	}

	@Override
	public List<EClass> getAllEClassesInOriginalMetamodel() {
		cacheOriginalEClasses();
		return originalEClss;
	}

	@Override
	public List<GenModel> getTargetMetamodelGenModels() {
		if (genModelRes == null) {
			genModelRes = metamodelResSet.getResource(pcmMetamodelGenModelURI, true);
		}

		var pcmGenModel = (GenModel) genModelRes.getContents().get(0);
		pcmGenModel.setCanGenerate(false);

		return List.of(pcmGenModel);
	}

	@Override
	public List<EPackage> getTargetMetamodelEcoreEPackages() {
		if (ecoreRes == null) {
			ecoreRes = metamodelResSet.getResource(pcmMetamodelEcoreModelURI, true);
			var parsedPcmPac = (EPackage) ecoreRes.getContents().get(0);
			fixInstanceClasses(parsedPcmPac);
		}

		return List.of((EPackage) ecoreRes.getContents().get(0));
	}

	/**
	 * Changes the instance classes within the EClasses under parsedPcmPac to the
	 * original EClasses from PCM ( {@link #originalEClss} ).
	 * 
	 * @param parsedPcmPac The Ecore model of PCM, which has been parsed by this
	 *                     class.
	 */
	private void fixInstanceClasses(EPackage parsedPcmPac) {
		var parsedEClss = MetamodelUtil.getAllEClasses(parsedPcmPac);
		cacheOriginalEClasses();

		if (parsedEClss.size() != originalEClss.size())
			throw new IllegalStateException(
					"Parsed PCM package and the actual PCM package contain different amounts of EClasses");

		for (var parsedECls : parsedEClss) {
			var matchingActualECls = originalEClss.stream()
					.filter((cls) -> cls.getEPackage().getName().equals(parsedECls.getEPackage().getName()))
					.filter((cls) -> cls.getName().equals(parsedECls.getName())).toArray(EClass[]::new);
			if (matchingActualECls.length != 1)
				throw new IllegalStateException("Unknown EClass has been parsed");

			var actualECls = matchingActualECls[0];
			parsedECls.setInstanceClassName(actualECls.getInstanceClassName());
			parsedECls.setInstanceTypeName(actualECls.getInstanceTypeName());
			parsedECls.setInstanceClass(actualECls.getInstanceClass());
		}
	}
}
