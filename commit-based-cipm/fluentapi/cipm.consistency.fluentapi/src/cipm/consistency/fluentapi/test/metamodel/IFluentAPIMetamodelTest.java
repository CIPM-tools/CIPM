package cipm.consistency.fluentapi.test.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;

/**
 * An interface that hides the concrete elements of the fluent api. Implementors
 * can then implement the methods of this interface and re-use the tests for the
 * generated fluent api model. Most methods within this interface hide fluent
 * api operations (or combinations of fluent api operations), whose names denote
 * their purpose.
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPIMetamodelTest {

	/**
	 * @return The object that grants access to the metamodel, for which the fluent
	 *         api was generated.
	 */
	public FluentAPITargetMetamodelPackageProvider getProvider();

	/**
	 * @return The object that filters the metamodel, for which the fluent api was
	 *         generated.
	 */
	public FluentAPITargetMetamodelFilter getFilter();

	/**
	 * @return The concrete fluent api class instance
	 */
	public EObject getAPI();

	public EObject init_getCurrentElement(EObject init);

	public void init_mark(EObject init, Object key);

	public EObject init_unmark(EObject init, Object key);

	public EObject init_createNow(EObject init);

	public EObject api_newX(EClass eCls);

	public EObject api_newX_createNow(EClass eCls);

	public EObject api_newX_createNow(Class<?> cls);

	public EObject api_createNewX(Class<?> cls);

	public EObject api_modifyX(EObject obj);

	public EObject api_modifyX_createNow(EObject obj);

	public void api_modifyX_xWithAddedFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_modifyX_xWithRemovedFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_modifyX_xCleanFeat(EObject obj, EStructuralFeature feat);

	public void api_modifyX_xWithFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_modifyX_xWithoutFeat(EObject obj, EStructuralFeature feat);

	public EObject api_getInitialisationForX(EClass eCls);

	public EClass api_getInitialisationForX_getInitialisedEClass(Class<?> cls);

	public EClass api_getInitialisationForX_getInitialisedEClass(EClass cls);

	public EClass api_getInitialisationForX_getInitialisedEClass(EObject obj);

	public EObject api_continueX(Class<?> cls);

	public void api_xWithFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_xWithoutFeat(EObject obj, EStructuralFeature feat);

	public void api_xWithAddedFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_xWithRemovedFeat(EObject obj, EStructuralFeature feat, Object val);

	public void api_xCleanFeat(EObject obj, EStructuralFeature feat);

	public void api_mark(Object key, EObject val);

	public EObject api_unmark(Object key);

	public EObject api_unmark(Object key, EObject val);

	public EObject api_getMarkedX(Object key);

	public EObject api_modifyMarkedX(Object key);

	public EObject api_continueMarkedX(Object key);
}
