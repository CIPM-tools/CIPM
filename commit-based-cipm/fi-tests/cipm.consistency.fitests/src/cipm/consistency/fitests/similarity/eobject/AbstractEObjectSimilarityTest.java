package cipm.consistency.fitests.similarity.eobject;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.fitests.similarity.params.InitialiserTestSettingsProvider;

/**
 * An abstract class that extends {@link AbstractResourceSimilarityTest} with
 * various methods that can be used to test the similarity checking of
 * {@link EObject} instances. <br>
 * <br>
 * Integrates {@link InitialiserTestSettingsProvider}, in order to grant access
 * to expected similarity values.
 * 
 * @author Alp Torac Genc
 * @see {@link InitialiserTestSettingsProvider#getSimilarityValues()} and
 *      further related methods and classes for more information on expected
 *      similarity values.
 */
public abstract class AbstractEObjectSimilarityTest extends AbstractResourceSimilarityTest {
	/**
	 * @see {@link #getEcoreUtilHelper()}
	 */
	private EcoreUtilHelper ecoreHelper;

	@BeforeEach
	@Override
	public void setUp(TestInfo info) {
		super.setUp(info);

		this.setEcoreUtilHelper(new EcoreUtilHelper());
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * <b>Note: Does not reset the used {@link InitialiserTestSettingsProvider},
	 * since initialising it can be very expensive.</b>
	 */
	@AfterEach
	@Override
	public void tearDown() {
		this.cleanUpEcoreUtilHelper();

		super.tearDown();
	}

	/**
	 * Consider adding default initialisation for
	 * {@link InitialiserTestSettingsProvider} if plausible to spare code
	 * duplication.
	 * 
	 * @return The {@link InitialiserTestSettingsProvider} that will be used in
	 *         tests.
	 */
	public abstract InitialiserTestSettingsProvider getInitialiserTestSettingsProvider();

	/**
	 * Sets up the {@link EcoreUtilHelper} instance that will be used with the given
	 * one.
	 */
	protected void setEcoreUtilHelper(EcoreUtilHelper ecoreHelper) {
		this.ecoreHelper = ecoreHelper;
	}

	/**
	 * Sets the used {@link EcoreUtilHelper} to null, in order to make sure that
	 * each test method has a fresh instance.
	 */
	protected void cleanUpEcoreUtilHelper() {
		this.ecoreHelper = null;
	}

	/**
	 * @return A helper class instance that can be used to perform various
	 *         operations on {@link EObject} instances.
	 */
	protected EcoreUtilHelper getEcoreUtilHelper() {
		return this.ecoreHelper;
	}

	/**
	 * Resets the used instance from {@link #getInitialiserTestSettingsProvider()}.
	 */
	protected void resetInitialiserTestSettingsProvider() {
		this.getInitialiserTestSettingsProvider().reset();
	}

	/**
	 * @param objCls  The type of the {@link EObject} instances being compared
	 * @param attrKey The attribute, based on which the said instances are compared
	 * @return The expected similarity value for cases, where 2 instances of objCls
	 *         are compared, whose attribute (attrKey) is different.
	 */
	public Boolean getExpectedSimilarityResult(Class<? extends EObject> objCls, Object attrKey) {
		return this.getInitialiserTestSettingsProvider().getSimilarityValues().getExpectedSimilarityResult(objCls,
				attrKey);
	}

	@SuppressWarnings("unchecked")
	public Boolean getExpectedSimilarityResult(EObject obj, Object attrKey) {
		return this.getExpectedSimilarityResult((Class<? extends EObject>) obj.eClass().getInstanceClass(), attrKey);
	}

	/**
	 * The variant of
	 * {@link #getExpectedSimilarityResult(Class, EStructuralFeature)} that uses the
	 * type, which introduces attrKey to the {@link EObject} hierarchy first.
	 */
	public Boolean getExpectedSimilarityResult(EStructuralFeature attrKey) {
		return this.getInitialiserTestSettingsProvider().getSimilarityValues()
				.getExpectedSimilarityResult(attrKey.getContainerClass(), attrKey);
	}

	/**
	 * See {@link EcoreUtilHelper#cloneEObj(EObject)}
	 */
	public <T extends EObject> T cloneEObj(T obj) {
		return this.getEcoreUtilHelper().cloneEObj(obj);
	}

	/**
	 * See {@link EcoreUtilHelper#cloneEObjWithContainers(EObject)}
	 */
	public <T extends EObject> T cloneEObjWithContainers(T obj) {
		return this.getEcoreUtilHelper().cloneEObjWithContainers(obj);
	}

	/**
	 * See {@link EcoreUtilHelper#cloneEObjList(Collection)}
	 */
	public <T extends EObject> Collection<T> cloneEObjList(Collection<T> objs) {
		return this.getEcoreUtilHelper().cloneEObjList(objs);
	}

	/**
	 * See {@link EcoreUtilHelper#getActualEquality(EObject, EObject)}
	 */
	public boolean getActualEquality(EObject elem1, EObject elem2) {
		return this.getEcoreUtilHelper().getActualEquality(elem1, elem2);
	}

	/**
	 * See {@link EcoreUtilHelper#getActualEquality(List, List)}
	 */
	public boolean getActualEquality(List<? extends EObject> elems1, List<? extends EObject> elems2) {
		return this.getEcoreUtilHelper().getActualEquality(elems1, elems2);
	}

	/**
	 * Clones elem and compares it with its clone. They are expected to be similar.
	 */
	public void assertSimilar(EObject elem) {
		this.assertSimilarityResult(elem, elem, Boolean.TRUE);
	}

	/**
	 * Compares elem1 with elem2, expects the similarity result to be the same with
	 * the given expected value.
	 */
	public void assertSimilarityResult(EObject elem1, EObject elem2, Boolean expectedSimilarityResult) {
		if (expectedSimilarityResult == null) {
			this.getLogger().debug("No expected similarity result present");
		} else if ((!expectedSimilarityResult.booleanValue() && this.getActualEquality(elem1, elem2))) {
			this.getLogger().debug("Elements are expected to be different" + " in " + this.getCurrentTestMethodName()
					+ " but are similar according to EcoreUtilHelper");
		}

		// Check the similarity of the given EObjects themselves
		Assertions.assertEquals(expectedSimilarityResult, this.isSimilar(elem1, elem2),
				"EcoreUtilHelper comparison (elem1, elem2) result: " + this.getActualEquality(elem1, elem2));
	}

	/**
	 * Tests the similarity as follows:
	 * <ol>
	 * <li>Compares elem1 with itself,
	 * <li>Compares elem2 with itself,
	 * <li>Compares elem1 with elem2
	 * <li>Compares elem2 with elem1
	 * </ol>
	 * 
	 * @param expectedSimilarityValue The expected result of the similarity
	 *                                checking.
	 */
	public void testSimilarity(EObject elem1, EObject elem2, Boolean expectedSimilarityValue) {
		this.assertSimilar(elem1);
		this.assertSimilar(elem2);

		this.assertSimilarityResult(elem1, elem2, expectedSimilarityValue);
		this.assertSimilarityResult(elem2, elem1, expectedSimilarityValue);
	}

	/**
	 * A variant of {@link #testSimilarity(EObject, EObject, Boolean)}, where the
	 * last parameter is computed using the given attrKey.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for objCls and
	 *      attrKey.
	 */
	public void testSimilarity(EObject elem1, EObject elem2, Class<? extends EObject> objCls, Object attrKey) {
		this.testSimilarity(elem1, elem2, this.getExpectedSimilarityResult(objCls, attrKey));
	}

	/**
	 * The variant of {@link #testSimilarity(EObject, EObject, Class, Object)} that
	 * uses the same class as the given elems.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for attrKey.
	 */
	@SuppressWarnings("unchecked")
	public void testSimilarity(EObject elem1, EObject elem2, Object attrKey) {
		this.testSimilarity(elem1, elem2, (Class<? extends EObject>) elem1.eClass().getInstanceClass(), attrKey);
	}

	public void testSimilarityNullCheck(EObject elem, Boolean expectedSimilarityValue) {
		var elem2 = elem.eClass().getEPackage().getEFactoryInstance().create(elem.eClass());
		this.testSimilarity(elem, elem2, expectedSimilarityValue);
	}

	public void testSimilarityNullCheck(EObject elem, Class<? extends EObject> objCls, Object attrKey) {
		this.testSimilarityNullCheck(elem, this.getExpectedSimilarityResult(objCls, (Object) attrKey));
	}

	@SuppressWarnings("unchecked")
	public void testSimilarityNullCheck(EObject elem, Object attrKey) {
		this.testSimilarityNullCheck(elem, (Class<? extends EObject>) elem.eClass().getInstanceClass(), attrKey);
	}
}
