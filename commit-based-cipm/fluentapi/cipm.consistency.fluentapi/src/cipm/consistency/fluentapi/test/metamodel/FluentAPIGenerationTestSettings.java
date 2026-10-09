package cipm.consistency.fluentapi.test.metamodel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;

/**
 * A singleton class for encapsulating details on the fluent api model and those
 * of the metamodel the fluent api was generated for.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationTestSettings {
	private static FluentAPITargetMetamodelFilter targetMetamodelFilter;
	private static FluentAPITargetMetamodelPackageProvider targetMetamodelProvider;

	private static List<EOperation> allFluentAPIOps;

	private static List<EClass> allSupportedConcreteEClssInTargetMetamodel;

	private static List<EClass> allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats;
	private static List<EClass> allSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat;
	private static List<EClass> allSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat;

	private static Function<EClass, EClass> targetMetamodelEClsToInitEClsFunc;

	private static Function<EClass, Boolean> multiValFunc;
	private static Function<EClass, Boolean> bigNumberVariantsFunc;

	/**
	 * @see {@link #getTargetMetamodelEClsToInitEClsFunc()}
	 */
	public static void setTargetMetamodelEClsToInitEClsFunc(Function<EClass, EClass> func) {
		targetMetamodelEClsToInitEClsFunc = func;
	}

	/**
	 * Takes a fluent api instance and derives its relevant attributes. Currently
	 * derives all EOperations in api and saves them in this class.
	 * 
	 * @param api The fluent api instance to be considered
	 */
	public static void setFluentAPI(EObject api) {
		allFluentAPIOps = List.copyOf(api.eClass().getEOperations());
	}

	/**
	 * @see {@link #getMetamodelFilter()}
	 */
	public static void setMetamodelFilter(FluentAPITargetMetamodelFilter filter) {
		targetMetamodelFilter = filter;

		computeVariantFunctions();
		computeAllSupportedConcreteEClss();
	}

	/**
	 * @see {@link #getMetamodelProvider()}
	 */
	public static void setPackageProvider(FluentAPITargetMetamodelPackageProvider provider) {
		targetMetamodelProvider = provider;
		computeAllSupportedConcreteEClss();
	}

	private static void computeAllSupportedConcreteEClss() {
		if (targetMetamodelProvider != null && targetMetamodelFilter != null) {
			// Compute all concrete EClasses within the metamodel that the fluent api was
			// generated for
			allSupportedConcreteEClssInTargetMetamodel = targetMetamodelProvider.getAllTargetMetamodelConcreteEClasses()
					.stream().filter((eCls) -> targetMetamodelFilter.isEClassEligible(eCls))
					.collect(Collectors.toList());
		}
		if (allSupportedConcreteEClssInTargetMetamodel != null && targetMetamodelFilter != null) {
			// Compute all concrete EClasses with modifiable features
			allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats = allSupportedConcreteEClssInTargetMetamodel
					.stream().filter(targetMetamodelFilter::hasModifiableFeatures).collect(Collectors.toList());
			// Compute all concrete EClasses with only one modifiable feature
			// Re-use allSupportedConcreteEClssWithModifiableFeats
			allSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat = allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats
					.stream().filter((eCls) -> targetMetamodelFilter.getModifiableFeatureCount(eCls) == 1)
					.collect(Collectors.toList());
			// Compute all concrete EClasses without any modifiable features
			// Re-use allSupportedConcreteEClssWithModifiableFeats
			allSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat = allSupportedConcreteEClssInTargetMetamodel
					.stream()
					.filter((eCls) -> !allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats.contains(eCls))
					.collect(Collectors.toList());
		}
	}

	private static void computeVariantFunctions() {
		if (targetMetamodelFilter != null) {
			multiValFunc = (eCls) -> targetMetamodelFilter.getModifiableFeatures(eCls).stream()
					.anyMatch((f) -> f.isMany());
			bigNumberVariantsFunc = (eCls) -> targetMetamodelFilter.getModifiableFeatures(eCls).stream()
					.anyMatch((f) -> f.getEType().equals(EcorePackage.Literals.EBIG_INTEGER))
					|| targetMetamodelFilter.getModifiableFeatures(eCls).stream()
							.anyMatch((f) -> f.getEType().equals(EcorePackage.Literals.EBIG_DECIMAL));
		}
	}

	/**
	 * @return The object that filters the metamodel the fluent api was generated
	 *         for.
	 */
	public static FluentAPITargetMetamodelFilter getMetamodelFilter() {
		return targetMetamodelFilter;
	}

	/**
	 * @return The object that provides access to the metamodel the fluent api was
	 *         generated for.
	 */
	public static FluentAPITargetMetamodelPackageProvider getMetamodelProvider() {
		return targetMetamodelProvider;
	}

	/**
	 * @return A list of all EOperations that the fluent api instance has.
	 * @see {@link #setFluentAPI(EObject)}
	 */
	public static List<EOperation> getAllFluentAPIOps() {
		return allFluentAPIOps;
	}

	/**
	 * @return A list of all concrete EClasses within the metamodel that the fluent
	 *         api instance was generated for.
	 * @see {@link #getMetamodelFilter()} for what EClasses and features are
	 *      supported
	 * @see {@link #getMetamodelProvider()} for the metamodel
	 */
	public static List<EClass> getAllSupportedConcreteEClssInTargetMetamodel() {
		return allSupportedConcreteEClssInTargetMetamodel;
	}

	/**
	 * @return A list of all concrete EClasses within the metamodel that the fluent
	 *         api was generated for, which have modifiable features.
	 * @see {@link #getMetamodelFilter()} for what EClasses and features are
	 *      supported
	 * @see {@link #getMetamodelProvider()} for the metamodel
	 */
	public static List<EClass> getAllSupportedConcreteEClssInTargetMetamodelWithModifiableFeats() {
		return allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats;
	}

	/**
	 * @return A list of all concrete EClasses within the metamodel that the fluent
	 *         api was generated for, which have exactly one modifiable feature.
	 * @see {@link #getMetamodelFilter()} for what EClasses and features are
	 *      supported
	 * @see {@link #getMetamodelProvider()} for the metamodel
	 */
	public static List<EClass> getAllSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat() {
		return allSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat;
	}

	/**
	 * The returned map can be used to map EClasses to their corresponding
	 * initialisation EClass.
	 * 
	 * @return The mapping between the EClasses within the metamodel that the fluent
	 *         api was generated for and the initialisation EClasses within the
	 *         fluent api model.
	 */
	public static Function<EClass, EClass> getTargetMetamodelEClsToInitEClsFunc() {
		return targetMetamodelEClsToInitEClsFunc;
	}

	/**
	 * The returned map can be used to determine, whether to expect overloading
	 * modification methods (with array or collection types) in corresponding
	 * initialisation classes for individual EClasses of the metamodel, which the
	 * fluent api was generated for.
	 * 
	 * @return A map that denotes for EClasses of the metamodel, which the fluent
	 *         api was generated for, whether any of their features should have
	 *         overloading modification methods (with array and collection types) in
	 *         their corresponding initialisation class.
	 */
	public static Function<EClass, Boolean> getMultiValFunc() {
		return multiValFunc;
	}

	/**
	 * The returned map can be used to determine, whether to expect overloading
	 * modification methods (with primitive types, such as int or long) in
	 * corresponding initialisation classes for individual EClasses of the
	 * metamodel, which the fluent api was generated for.
	 * 
	 * @return A map that denotes for EClasses of the metamodel, which the fluent
	 *         api was generated for, whether any of their features should have
	 *         overloading modification methods (with primitive types, such as int
	 *         or long) in their corresponding initialisation class.
	 */
	public static Function<EClass, Boolean> getBigNumberVariantsFunc() {
		return bigNumberVariantsFunc;
	}

	/**
	 * @return A list of all concrete EClasses within the metamodel that the fluent
	 *         api was generated for, which have no modifiable features.
	 * @see {@link #getMetamodelFilter()} for what EClasses and features are
	 *      supported
	 * @see {@link #getMetamodelProvider()} for the metamodel
	 */
	public static List<EClass> getAllSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat() {
		return allSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat;
	}

	/**
	 * Resets all attributes of this class.
	 */
	public static void clear() {
		allFluentAPIOps = null;
		allSupportedConcreteEClssInTargetMetamodel = null;
		allSupportedConcreteEClssInTargetMetamodelWithModifiableFeats = null;
		allSupportedConcreteEClssInTargetMetamodelWithNoModifiableFeat = null;
		allSupportedConcreteEClssInTargetMetamodelWithOnlyOneModifiableFeat = null;
		bigNumberVariantsFunc = null;
		targetMetamodelEClsToInitEClsFunc = null;
		targetMetamodelFilter = null;
		targetMetamodelProvider = null;
		multiValFunc = null;
	}
}
