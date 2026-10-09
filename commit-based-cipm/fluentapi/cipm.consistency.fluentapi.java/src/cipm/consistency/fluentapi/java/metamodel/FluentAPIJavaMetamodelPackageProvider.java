package cipm.consistency.fluentapi.java.metamodel;

import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.commons.layout.LayoutPackage;
import org.emftext.language.java.JavaPackage;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;
import cipm.consistency.fluentapi.metamodel.MetamodelUtil;

/**
 * An implementation of {@link FluentAPITargetMetamodelPackageProvider} for
 * JaMoPP.
 * <p>
 * <p>
 * This class internally "fixes" the Ecore and GenModel of JaMoPP that it
 * parses, in order to avoid having duplicated Resource instances during fluent
 * api generation. This is due to Eclipse plug-in limitations.
 * <p>
 * <p>
 * Note: The original JaMoPP metamodel considers both {@link JavaPackage} and
 * {@link LayoutPackage}. In order to keep the parsed metamodels valid, this
 * class parses both of them. For filtering out {@link LayoutPackage},
 * {@link FluentAPIJavaMetamodelFilter} can be used.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIJavaMetamodelPackageProvider extends FluentAPITargetMetamodelPackageProvider {
	/**
	 * The (plug-in based) URI to JaMoPP's genmodel file
	 */
	private static final URI jaMoPPGenModelURI = URI
			.createURI("platform:/plugin/org.emftext.language.java/metamodel/java.genmodel");
	/**
	 * The (plug-in based) URI to JaMoPP's ecore file
	 */
	private static final URI jaMoPPEcoreModelURI = URI
			.createURI("platform:/plugin/org.emftext.language.java/metamodel/java.ecore");

	/**
	 * The ResourceSet, which will contain the Resources of the GenModel and the
	 * Ecore model parsed by this class ( {@link #ecoreRes} and {@link #genModelRes}
	 * ). Note that those Resources are not the original Resources of the JaMoPP
	 * model.
	 */
	private final ResourceSet metamodelResSet = new ResourceSetImpl();
	/**
	 * The Resource instance containing the parsed Ecore model of JaMoPP. This is
	 * NOT the Resource instance of {@code JavaPackage.eINSTANCE} nor
	 * {@code LayoutPackage.eINSTANCE}.
	 */
	private Resource ecoreRes;
	/**
	 * The Resource instance containing the parsed GenModel of JaMoPP. This does NOT
	 * use the Resource instance of {@code JavaPackage.eINSTANCE} nor
	 * {@code LayoutPackage.eINSTANCE}, but {@link #ecoreRes}.
	 */
	private Resource genModelRes;
	/**
	 * The list containing the original JaMoPP EClasses that are available under
	 * {@code JavaPackage.eINSTANCE} and {@code LayoutPackage.eINSTANCE}. These
	 * EClasses are NOT the same as those in {@link #ecoreRes}.
	 */
	private List<EClass> originalEClss;

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

	/**
	 * Caches the (original) EClasses found under the JaMoPP metamodel ( under
	 * {@code JavaPackage.eINSTANCE} and {@code LayoutPackage.eINSTANCE}) in
	 * {@link #originalEClss}, in order to spare constantly retrieving them from the
	 * Resource instances.
	 */
	private void cacheOriginalEClasses() {
		if (originalEClss == null) {
			originalEClss = new ArrayList<EClass>(MetamodelUtil.getAllEClasses(JavaPackage.eINSTANCE));
			originalEClss.addAll(MetamodelUtil.getAllEClasses(LayoutPackage.eINSTANCE));
		}
	}

	/**
	 * Changes the instance classes within the EClasses under parsedJaMoPPEcoreModel
	 * to the original EClasses from JaMoPP ( {@link #originalEClss} ).
	 * 
	 * @param parsedJaMoPPEcoreModel The Ecore model of JaMoPP, which has been
	 *                               parsed by this class.
	 */
	private void fixInstanceClasses(EPackage parsedJaMoPPEcoreModel) {
		var jaMoPPParsedEClss = MetamodelUtil.getAllEClasses(parsedJaMoPPEcoreModel);
		cacheOriginalEClasses();

		if (jaMoPPParsedEClss.size() != originalEClss.size())
			throw new IllegalStateException(
					"Parsed Java package and the actual Java package contain different amounts of EClasses");

		for (var parsedECls : jaMoPPParsedEClss) {
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

	@Override
	public List<EPackage> getTargetMetamodelEcoreEPackages() {
		if (ecoreRes == null) {
			ecoreRes = metamodelResSet.getResource(jaMoPPEcoreModelURI, true);
			var parsedJaMoPPEcoreModel = (EPackage) ecoreRes.getContents().get(0);
			fixInstanceClasses(parsedJaMoPPEcoreModel);
		}

		return List.of((EPackage) ecoreRes.getContents().get(0));
	}

	@Override
	public List<GenModel> getTargetMetamodelGenModels() {
		if (genModelRes == null) {
			genModelRes = metamodelResSet.getResource(jaMoPPGenModelURI, true);
		}

		var javaGenModel = (GenModel) genModelRes.getContents().get(0);
		javaGenModel.setCanGenerate(false);

		return List.of(javaGenModel);
	}

	@Override
	public List<EClass> getAllEClassesInOriginalMetamodel() {
		cacheOriginalEClasses();
		return originalEClss;
	}
}
