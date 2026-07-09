package cipm.consistency.fluentapi.gen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;

/**
 * Holds the state and resources required during the generation of the fluent
 * API, including generated EMF model elements.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationContext {
	private FluentAPITargetMetamodelPackageProvider targetMetamodelPackageProvider;
	private FluentAPITargetMetamodelFilter targetMetamodelFilter;

	private EPackage placeholderEDataTypesPac;

	private EPackage rootPackage;
	private EPackage apiPackage;
	private EClass fluentAPIECls;

	private EClass initSuperECls;
	private EReference initSuperEClsApiReference;
	private EReference initSuperEClsCurrentElement;

	private EPackage initsPackage;
	private final Map<EClass, EClass> initEClss = new LinkedHashMap<>();

	private String basePackageName;

	/**
	 * @return The name of the base package of the fluent API model
	 */
	public String getBasePackageName() {
		return basePackageName;
	}

	/**
	 * @see {@link #getBasePackageName()}
	 */
	public void setBasePackageName(String basePackageName) {
		this.basePackageName = basePackageName;
	}

	/**
	 * @return The EPackage, in which {@link #getInitsPackage()} and
	 *         {@link #getPlaceholderEDataTypesPac()} reside
	 */
	public EPackage getRootPackage() {
		return rootPackage;
	}

	/**
	 * @see {@link #getRootPackage()}
	 */
	public void setRootPackage(EPackage rootPackage) {
		this.rootPackage = rootPackage;
	}

	/**
	 * @return The EPackage, in which the {@link #getFluentAPIECls()} resides.
	 */
	public EPackage getApiPackage() {
		return apiPackage;
	}

	/**
	 * @see {@link #getApiPackage()}
	 */
	public void setApiPackage(EPackage rootPackage) {
		this.apiPackage = rootPackage;
	}

	/**
	 * @return The EClass modelling the fluent api class
	 */
	public EClass getFluentAPIECls() {
		return fluentAPIECls;
	}

	/**
	 * @see {@link #getFluentAPIECls()}
	 */
	public void setFluentAPIECls(EClass fluentAPIECls) {
		this.fluentAPIECls = fluentAPIECls;
	}

	/**
	 * @return The EClass modelling the abstract (super) Initialisation class
	 */
	public EClass getInitSuperECls() {
		return initSuperECls;
	}

	/**
	 * @see {@link #getInitSuperECls()}
	 */
	public void setInitSuperECls(EClass initSuperECls) {
		this.initSuperECls = initSuperECls;
	}

	/**
	 * @return The EPackage, where {@link #getAllInitEClss()} reside
	 */
	public EPackage getInitsPackage() {
		return initsPackage;
	}

	/**
	 * @see {@link #getInitsPackage()}
	 */
	public void setInitsPackage(EPackage initsPackage) {
		this.initsPackage = initsPackage;
	}

	/**
	 * Adds initECls as Initialisation class for elemToInitECls
	 * 
	 * @param elemToInitECls A given EObject sub-type
	 * @param initECls       A given Initialisation class for elemToInitECls
	 */
	public void addInitECls(EClass elemToInitECls, EClass initECls) {
		initEClss.put(elemToInitECls, initECls);
	}

	/**
	 * @param elemToInitECls A given EObject sub-type
	 * @return The Initialisation class for elemToInitECls
	 */
	public EClass getInitEClsFor(EClass elemToInitECls) {
		return initEClss.get(elemToInitECls);
	}

	/**
	 * @param initECls A given Initialisation class
	 * @return The EObject type that initECls considers
	 */
	public EClass getElemToInitFor(EClass initECls) {
		return initEClss.entrySet().stream().filter((e) -> e.getValue().equals(initECls)).map((e) -> e.getKey())
				.findFirst().orElse(null);
	}

	/**
	 * @return An unmodifiable list of all Initialisation EClasses
	 */
	public List<EClass> getAllInitEClss() {
		return List.copyOf(initEClss.values());
	}

	/**
	 * @return The object that provides access to the metamodel the fluent api is
	 *         meant for.
	 */
	public FluentAPITargetMetamodelPackageProvider getTargetMetamodelPackageProvider() {
		return targetMetamodelPackageProvider;
	}

	/**
	 * @return The object that filters the metamodel
	 *         {@link #getTargetMetamodelPackageProvider()} during the generation of
	 *         fluent api.
	 */
	public FluentAPITargetMetamodelFilter getTargetMetamodelFilter() {
		return targetMetamodelFilter;
	}

	/**
	 * Uses {@link #getTargetMetamodelPackageProvider()} in conjunction with
	 * {@link #getTargetMetamodelFilter()}.
	 * 
	 * @return A list of all concrete EClasses of the target metamodel
	 */
	public List<EClass> getAllEligibleTargetMetamodelConcreteEClasses() {
		return this.getTargetMetamodelPackageProvider().getAllTargetMetamodelConcreteEClasses().stream()
				.filter((eCls) -> this.getTargetMetamodelFilter().isEClassEligible(eCls)).collect(Collectors.toList());
	}

	/**
	 * Uses {@link #getTargetMetamodelPackageProvider()} in conjunction with
	 * {@link #getTargetMetamodelFilter()}.
	 * 
	 * @return A list of all EClasses of the target metamodel
	 */
	public List<EClass> getAllEligibleTargetMetamodelEClasses() {
		return this.getTargetMetamodelPackageProvider().getAllTargetMetamodelEClasses().stream()
				.filter((eCls) -> this.getTargetMetamodelFilter().isEClassEligible(eCls)).collect(Collectors.toList());
	}

	/**
	 * Use this EPackage to gather all non-EMF types (such as array types).
	 * 
	 * @return The EPackage, which aggregates placeholder EDataTypes during fluent
	 *         api generation.
	 */
	public EPackage getPlaceholderEDataTypesPac() {
		return placeholderEDataTypesPac;
	}

	/**
	 * @see {@link #getPlaceholderEDataTypesPac()}
	 */
	public void setPlaceholderEDataTypesPac(EPackage placeholderEDataTypesPac) {
		this.placeholderEDataTypesPac = placeholderEDataTypesPac;
	}

	/**
	 * @return The EReference in {@link #getInitSuperECls()}, which refers to the
	 *         fluent api class ({@link #getFluentAPIECls()}) that created it.
	 */
	public EReference getInitSuperEClsApiReference() {
		return initSuperEClsApiReference;
	}

	/**
	 * @see {@link #getInitSuperEClsApiReference()}
	 */
	public void setInitSuperEClsApiReference(EReference initSuperEClsApiReference) {
		this.initSuperEClsApiReference = initSuperEClsApiReference;
	}

	/**
	 * @return The EReference in {@link #getInitSuperECls()}, which refers to the
	 *         EObject that is currently being created / modified.
	 */
	public EReference getInitSuperEClsCurrentElement() {
		return initSuperEClsCurrentElement;
	}

	/**
	 * @see {@link #getInitSuperEClsCurrentElement()}
	 */
	public void setInitSuperEClsCurrentElement(EReference initSuperEClsCurrentElement) {
		this.initSuperEClsCurrentElement = initSuperEClsCurrentElement;
	}

	/**
	 * @see {@link #getTargetMetamodelPackageProvider()}
	 */
	public void setTargetMetamodelPackageProvider(
			FluentAPITargetMetamodelPackageProvider targetMetamodelPackageProvider) {
		this.targetMetamodelPackageProvider = targetMetamodelPackageProvider;
	}

	/**
	 * @see {@link #getTargetMetamodelFilter()}
	 */
	public void setTargetMetamodelFilter(FluentAPITargetMetamodelFilter targetMetamodelFilter) {
		this.targetMetamodelFilter = targetMetamodelFilter;
	}
}
