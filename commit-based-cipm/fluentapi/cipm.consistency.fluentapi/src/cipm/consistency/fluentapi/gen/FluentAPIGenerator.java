package cipm.consistency.fluentapi.gen;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import cipm.consistency.fluentapi.gen.init.FluentAPIInitialisationEClassGenerator;
import cipm.consistency.fluentapi.gen.rootapi.FluentAPIRootAPIEClassGenerator;
import cipm.consistency.fluentapi.gen.superinit.FluentAPISuperInitialisationEClassGenerator;

/**
 * The top-most generator class responsible for creating the EMF model of the
 * fluent API in a hierarchical way by working with further generator classes.
 * <p>
 * <p>
 * During EMF model generation, uses a {@link FluentAPIGenerationContext} object
 * that stores the (so far) generated EMF model elements for the fluent API as
 * well as further information.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerator {
	/**
	 * Generates the fluent api model.
	 * 
	 * @param context An object that contains state information on the fluent api
	 *                generation
	 * @return A list of generated root packages, currently only one
	 */
	public List<EPackage> generateRootAPIPackages(FluentAPIGenerationContext context) {
		// Generate fluent API packages
		var rootPac = generateFluentAPIRootPackage(context);

		// Add fluent API packages into context
		context.setRootPackage(rootPac);
		context.setApiPackage(rootPac);
		context.setInitsPackage(generateInitialisationsPackage(context));
		context.setPlaceholderEDataTypesPac(generatePlaceholderTypesPackage(context));

		// Generate the facade class (without setting it up) and add it into context
		var rootAPIEClassGen = new FluentAPIRootAPIEClassGenerator();
		context.setFluentAPIECls(rootAPIEClassGen.generateRootAPIEClass(context));
		context.getApiPackage().getEClassifiers().add(context.getFluentAPIECls());

		// Generate the super initialisation class (without setting it up) and add it
		// into context
		var superInitEClassGen = new FluentAPISuperInitialisationEClassGenerator();
		context.setInitSuperECls(superInitEClassGen.generateSuperInitialisationEClass());
		context.getApiPackage().getEClassifiers().add(context.getInitSuperECls());

		// Generate the super initialisation class (without setting them up) and add
		// them into context
		var initEClassesGen = new FluentAPIInitialisationEClassGenerator();
		initEClassesGen.generateFluentAPIInitialisationClasses(context);
		context.getInitsPackage().getEClassifiers().addAll(context.getAllInitEClss());

		// Setup the facade class (now that the classes required to do so exist)
		rootAPIEClassGen.setupRootAPIEClass(context);

		// Setup the super initialisation class (now that the classes required to do so
		// exist and are set up)
		superInitEClassGen.setupSuperInitialisationEClass(context);

		// Setup the concrete initialisation classes (now that the classes required to
		// do so exist and are set up)
		context.getAllInitEClss().forEach((cls) -> {
			initEClassesGen.setupFluentAPIInitialisationFor(cls, context.getElemToInitFor(cls), context);
			cls.getESuperTypes().add(context.getInitSuperECls());
		});

		return List.of(rootPac);
	}

	/**
	 * Generates the top-most EPackage of the fluent api model.
	 * 
	 * @param context An object that contains state information on the fluent api
	 *                generation
	 * @return The root EPackage
	 */
	private EPackage generateFluentAPIRootPackage(FluentAPIGenerationContext context) {
		var pac = EcoreFactory.eINSTANCE.createEPackage();
		var pacName = ModelConstants.ROOT_PACKAGE_NAME.get();
		var pacURI = URI.createURI(ModelConstants.ROOT_PACKAGE_URI
				.getFor(context.getTargetMetamodelPackageProvider().getTargetMetamodelName()));

		pac.setName(pacName);
		pac.setNsPrefix(pacName);
		pac.setNsURI(pacURI.toString());

		return pac;
	}

	/**
	 * @param context An object that contains state information on the fluent api
	 *                generation
	 * @return The EPackage that contains the generated Initialisation classes
	 */
	private EPackage generateInitialisationsPackage(FluentAPIGenerationContext context) {
		return FluentAPIGenerationUtil.generateSubPackage(context.getApiPackage(),
				ModelConstants.INITIALISATIONS_PACKAGE_NAME.get());
	}

	/**
	 * @param context An object that contains state information on the fluent api
	 *                generation
	 * @return The EPackage that contains the generated EDataTypes, which wrap
	 *         non-EMF types and adapt them
	 */
	private EPackage generatePlaceholderTypesPackage(FluentAPIGenerationContext context) {
		return FluentAPIGenerationUtil.generateSubPackage(context.getApiPackage(),
				ModelConstants.EDATATYPE_WRAPPERS_PACKAGE_NAME.get());
	}
}
