package cipm.consistency.fluentapi.test.metamodel;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * An abstract test class that implements test cases for fluent api class and
 * the abstract (super) initialisation class' methods. Their purpose is to make
 * sure that those methods have been generated and that they can be used (for
 * trivial input).
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractFluentAPIMetamodelCoverageTest extends AbstractFluentAPITest
		implements IFluentAPIMetamodelTest {

	/**
	 * Uses {@link #getProvider()} to retrieve the EClasses and {@link #getFilter()}
	 * to filter them.
	 * 
	 * @return A list of all eligible concrete EClasses in the metamodel the fluent
	 *         api is generated for.
	 */
	private List<EClass> getAllEligibleConcreteEClss() {
		return getProvider().getAllConcreteEClassesInOriginalMetamodel().stream()
				.filter((eCls) -> getFilter().isEClassEligible(eCls)).collect(Collectors.toList());
	}

	/**
	 * Ensures that each concrete class within the target metamodel can be
	 * instantiated via api.newX().createNow().
	 */
	@Test
	public void concreteElementCoverageTest_API_NewX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			Assertions.assertInstanceOf(eCls.getInstanceClass(), api_newX_createNow(eCls));
			Assertions.assertInstanceOf(eCls.getInstanceClass(), api_newX_createNow(eCls.getInstanceClass()));
		}
	}

	/**
	 * Ensures that each concrete class within the target metamodel can be
	 * instantiated via api.createNewX().
	 */
	@Test
	public void concreteElementCoverageTest_API_CreateNewX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			Assertions.assertInstanceOf(eCls.getInstanceClass(), api_createNewX(eCls.getInstanceClass()));
			Assertions.assertInstanceOf(eCls.getInstanceClass(),
					api_modifyX_createNow(api_createNewX(eCls.getInstanceClass())));
		}
	}

	/**
	 * Ensures that each concrete class within the target metamodel has its own
	 * XInitialisation class.
	 */
	@Test
	public void concreteElementCoverageTest_API_XInitialisationExistence() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var initEClsViaClass = api_getInitialisationForX_getInitialisedEClass(eCls.getInstanceClass());
			var initEClsViaEClass = api_getInitialisationForX_getInitialisedEClass(eCls);
			var initEClsViaObj = api_getInitialisationForX_getInitialisedEClass(
					eCls.getEPackage().getEFactoryInstance().create(eCls));
			Assertions.assertEquals(eCls, initEClsViaClass);
			Assertions.assertEquals(eCls, initEClsViaEClass);
			Assertions.assertEquals(eCls, initEClsViaObj);

			Assertions.assertEquals(initEClsViaEClass, initEClsViaClass);
			Assertions.assertEquals(initEClsViaEClass, initEClsViaObj);
		}
	}

	/**
	 * Ensures that each concrete class within the target metamodel can be modified
	 * via the api.modifyX() method
	 */
	@Test
	public void concreteElementCoverageTest_API_ModifyX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var init = api_newX(eCls);
			var instance = init_createNow(init);
			Assertions.assertInstanceOf(init.getClass(), api_modifyX(instance));
			Assertions.assertEquals(instance, api_modifyX_createNow(instance));
		}
	}

	/**
	 * Ensures that each concrete class within the target metamodel may have its
	 * construction be continued via the api.continueX method.
	 */
	@Test
	public void concreteElementCoverageTest_API_ContinueX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var init = api_newX(eCls);
			Assertions.assertSame(init, api_continueX(eCls.getInstanceClass()));
		}
	}

	/**
	 * Ensures that each many-valued modifiable feature of each concrete class
	 * within the target metamodel can be modified via the api (with methods
	 * xWithAddedFeat(), xWithRemovedFeat(), xCleanFeat()).
	 */
	@Test
	public void concreteElementCoverageTest_API_xManyValuedFeature() {
		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var instance = (EObject) api_createNewX(eCls.getInstanceClass());
			for (var feat : getFilter().getModifiableFeatures(eCls)) {
				if (feat.isMany()) {
					api_xWithAddedFeat(instance, feat, instance.eGet(feat));
					api_xWithAddedFeat(instance, feat, ((EList<?>) instance.eGet(feat)).toArray());
					api_xWithAddedFeat(instance, feat, List.copyOf(((EList<?>) instance.eGet(feat))));

					api_xWithRemovedFeat(instance, feat, instance.eGet(feat));
					api_xWithRemovedFeat(instance, feat, ((EList<?>) instance.eGet(feat)).toArray());
					api_xWithRemovedFeat(instance, feat, List.copyOf(((EList<?>) instance.eGet(feat))));

					api_xCleanFeat(instance, feat);
				}
			}
		}
	}

	/**
	 * Ensures that each modifiable feature of each concrete class within the target
	 * metamodel can be modified via the api (with methods xWithFeat(),
	 * xWithoutFeat()).
	 */
	@Test
	public void concreteElementCoverageTest_API_xSingleValuedFeature() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var instance = (EObject) api_createNewX(eCls.getInstanceClass());
			for (var feat : getFilter().getModifiableFeatures(eCls)) {
				if (!feat.isMany()) {
					api_xWithFeat(instance, feat, instance.eGet(feat));
					api_xWithoutFeat(instance, feat);
				}
			}
		}
	}

	/**
	 * Ensures that the fluent api methods getMarkedX(), mark() and unmark() are
	 * enabled for each concrete class of the target metamodel.
	 */
	@Test
	public void concreteElementCoverageTest_API_Mark() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var keyAPI = new Object();

			var instance = eCls.getEPackage().getEFactoryInstance().create(eCls);
			Assertions.assertInstanceOf(eCls.getInstanceClass(), instance);

			api_mark(keyAPI, instance);
			Assertions.assertSame(instance, api_getMarkedX(keyAPI));

			api_unmark(keyAPI);
			Assertions.assertNull(api_getMarkedX(keyAPI));

			api_mark(keyAPI, instance);
			Assertions.assertSame(instance, api_getMarkedX(keyAPI));

			api_unmark(keyAPI, instance);
			Assertions.assertNull(api_getMarkedX(keyAPI));
		}
	}

	/**
	 * Ensures that the fluent api methods modifyMarkedX() is enabled for each
	 * concrete class of the target metamodel.
	 */
	@Test
	public void concreteElementCoverageTest_API_ModifyMarkedX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var keyAPI = new Object();

			var instance = eCls.getEPackage().getEFactoryInstance().create(eCls);
			Assertions.assertInstanceOf(eCls.getInstanceClass(), instance);

			api_mark(keyAPI, instance);

			// modifyMarkedX call creates a new initialisation instance
			var modMarkedInit = api_modifyMarkedX(keyAPI);
			Assertions.assertSame(instance, init_createNow(modMarkedInit));
		}
	}

	/**
	 * Ensures that the fluent api methods continueMarkedX() are enabled for each
	 * concrete class of the target metamodel.
	 */
	@Test
	public void concreteElementCoverageTest_API_ContinueMarkedX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var keyAPI = new Object();
			var init = api_newX(eCls);
			var instance = init_getCurrentElement(init);

			api_mark(keyAPI, instance);

			Assertions.assertSame(init, api_continueMarkedX(keyAPI));
			Assertions.assertSame(instance, init_createNow(init));
		}
	}

	/**
	 * Ensures that each single-valued modifiable feature of each concrete class
	 * within the target metamodel can be modified via the superInit (with methods
	 * xWithFeat(), xWithoutFeat()).
	 */
	@Test
	public void concreteElementCoverageTest_SuperInit_xSingleValuedFeature() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var instance = (EObject) api_createNewX(eCls.getInstanceClass());
			for (var feat : getFilter().getModifiableFeatures(eCls)) {
				if (!feat.isMany()) {
					api_modifyX_xWithFeat(instance, feat, instance.eGet(feat));
					api_modifyX_xWithoutFeat(instance, feat);
				}
			}
		}
	}

	/**
	 * Ensures that each modifiable many-valued feature of each concrete class
	 * within the target metamodel can be modified via the superInit (with methods
	 * xWithAddedFeat(), xWithRemovedFeat(), xCleanFeat()).
	 */
	@Test
	public void concreteElementCoverageTest_SuperInit_xManyValuedFeature() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var instance = (EObject) api_createNewX(eCls.getInstanceClass());
			for (var feat : getFilter().getModifiableFeatures(eCls)) {
				if (feat.isMany()) {
					api_modifyX_xWithAddedFeat(instance, feat, instance.eGet(feat));
					api_modifyX_xWithAddedFeat(instance, feat, ((EList<?>) instance.eGet(feat)).toArray());
					api_modifyX_xWithAddedFeat(instance, feat, List.copyOf(((EList<?>) instance.eGet(feat))));

					api_modifyX_xWithRemovedFeat(instance, feat, instance.eGet(feat));
					api_modifyX_xWithRemovedFeat(instance, feat, ((EList<?>) instance.eGet(feat)).toArray());
					api_modifyX_xWithRemovedFeat(instance, feat, List.copyOf(((EList<?>) instance.eGet(feat))));

					api_modifyX_xCleanFeat(instance, feat);
				}
			}
		}
	}

	/**
	 * Ensures that the super initialisation methods mark() and unmark() are enabled
	 * for each concrete class of the target metamodel
	 */
	@Test
	public void concreteElementCoverageTest_SuperInit_MarkX() {

		var allConcreteEClss = getAllEligibleConcreteEClss();
		for (var eCls : allConcreteEClss) {
			var keySuperInit = new Object();

			var init = api_newX(eCls);
			var instance = init_getCurrentElement(init);

			init_mark(init, keySuperInit);
			Assertions.assertSame(instance, api_getMarkedX(keySuperInit));

			init_unmark(init, keySuperInit);
			Assertions.assertNull(api_getMarkedX(keySuperInit));
		}
	}
}
