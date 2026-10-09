package cipm.consistency.fluentapi.pcm.test.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;
import cipm.consistency.fluentapi.pcm.api.ApiFactory;
import cipm.consistency.fluentapi.pcm.api.FluentAPISuperInitialisation;
import cipm.consistency.fluentapi.pcm.api.FluentPcmAPI;
import cipm.consistency.fluentapi.pcm.metamodel.FluentAPIPcmMetamodelFilter;
import cipm.consistency.fluentapi.pcm.metamodel.FluentAPIPcmMetamodelPackageProvider;
import cipm.consistency.fluentapi.test.metamodel.IFluentAPIMetamodelTest;

/**
 * An extension of {@link IFluentAPIMetamodelTest} for PCM.
 * 
 * @author Alp Torac Genc
 */
public interface IFluentPcmAPIMetamodelTest extends IFluentAPIMetamodelTest {
	static final FluentAPITargetMetamodelFilter filter = new FluentAPIPcmMetamodelFilter();
	static final FluentAPITargetMetamodelPackageProvider metamodelProvider = new FluentAPIPcmMetamodelPackageProvider();
	static final FluentPcmAPI api = ApiFactory.eINSTANCE.createFluentPcmAPI();

	private static FluentAPISuperInitialisation toSupInit(EObject init) {
		return (FluentAPISuperInitialisation) init;
	}

	@Override
	public default FluentAPITargetMetamodelPackageProvider getProvider() {
		return metamodelProvider;
	}

	@Override
	public default FluentAPITargetMetamodelFilter getFilter() {
		return filter;
	}

	@Override
	public default EObject init_getCurrentElement(EObject init) {
		return toSupInit(init).getCurrentElement();
	}

	@Override
	public default void init_mark(EObject init, Object key) {
		toSupInit(init).markCurrentElement(key);
	}

	@Override
	public default EObject init_unmark(EObject init, Object key) {
		return toSupInit(init).unmarkCurrentElement(key);
	}

	@Override
	public default EObject init_createNow(EObject init) {
		return toSupInit(init).createNow();
	}

	@Override
	public default EObject api_newX(EClass eCls) {
		return api.newX(eCls);
	}

	@Override
	public default EObject api_newX_createNow(EClass eCls) {
		return api.newX(eCls).createNow();
	}

	@Override
	public default EObject api_newX_createNow(Class<?> cls) {
		return api.newX(cls).createNow();
	}

	@Override
	public default EObject api_createNewX(Class<?> cls) {
		return (EObject) api.createNewX(cls);
	}

	@Override
	public default EObject api_modifyX(EObject obj) {
		return api.modifyX(obj);
	}

	@Override
	public default EObject api_modifyX_createNow(EObject obj) {
		return api.modifyX(obj).createNow();
	}

	@Override
	public default void api_modifyX_xWithAddedFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.modifyX(obj).xWithAddedFeat(feat, val);
	}

	@Override
	public default void api_modifyX_xWithRemovedFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.modifyX(obj).xWithRemovedFeat(feat, val);
	}

	@Override
	public default void api_modifyX_xCleanFeat(EObject obj, EStructuralFeature feat) {
		api.modifyX(obj).xCleanFeat(feat);
	}

	@Override
	public default void api_modifyX_xWithFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.modifyX(obj).xWithFeat(feat, val);
	}

	@Override
	public default void api_modifyX_xWithoutFeat(EObject obj, EStructuralFeature feat) {
		api.modifyX(obj).xWithoutFeat(feat);
	}

	@Override
	public default EClass api_getInitialisationForX_getInitialisedEClass(Class<?> cls) {
		return api.getInitialisationForX(cls).getInitialisedEClass();
	}

	@Override
	public default EClass api_getInitialisationForX_getInitialisedEClass(EClass cls) {
		return api.getInitialisationForX(cls).getInitialisedEClass();
	}

	@Override
	public default EClass api_getInitialisationForX_getInitialisedEClass(EObject obj) {
		return api.getInitialisationForX(obj).getInitialisedEClass();
	}

	@Override
	public default EObject api_continueX(Class<?> cls) {
		return api.continueX(cls);
	}

	@Override
	public default void api_xWithFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.xWithFeat(obj, feat, val);
	}

	@Override
	public default void api_xWithoutFeat(EObject obj, EStructuralFeature feat) {
		api.xWithoutFeat(obj, feat);
	}

	@Override
	public default void api_xWithAddedFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.xWithAddedFeat(obj, feat, val);
	}

	@Override
	public default void api_xWithRemovedFeat(EObject obj, EStructuralFeature feat, Object val) {
		api.xWithRemovedFeat(obj, feat, val);
	}

	@Override
	public default void api_xCleanFeat(EObject obj, EStructuralFeature feat) {
		api.xCleanFeat(obj, feat);
	}

	@Override
	public default void api_mark(Object key, EObject val) {
		api.mark(key, val);
	}

	@Override
	public default EObject api_unmark(Object key) {
		return api.unmark(key);
	}

	@Override
	public default EObject api_unmark(Object key, EObject val) {
		return api.unmark(key, val);
	}

	@Override
	public default EObject api_getMarkedX(Object key) {
		return api.getMarkedX(key);
	}

	@Override
	public default EObject api_modifyMarkedX(Object key) {
		return api.modifyMarkedX(key);
	}

	@Override
	public default EObject api_continueMarkedX(Object key) {
		return api.continueMarkedX(key);
	}

	@Override
	public default EObject getAPI() {
		return api;
	}

	@Override
	public default EObject api_getInitialisationForX(EClass eCls) {
		return api.getInitialisationForX(eCls);
	}
}
