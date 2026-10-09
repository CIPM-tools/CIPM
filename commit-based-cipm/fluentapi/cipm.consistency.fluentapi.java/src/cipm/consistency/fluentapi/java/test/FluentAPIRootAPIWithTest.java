package cipm.consistency.fluentapi.java.test;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;
import cipm.consistency.fluentapi.test.FluentAPITestUtils;

/**
 * A test class for the modification methods of the fluent api class:
 * xWithFeat(...), xWithoutFeat(...), xWithAddedFeat(...),
 * xWithRemovedFeat(...), xCleanFeat().
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPIWithTest extends AbstractFluentAPITest {
	private static final EStructuralFeature nameFeat = CommonsPackage.Literals.NAMED_ELEMENT__NAME;
	private static final EStructuralFeature namespaceFeat = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;
	private static final EStructuralFeature extendsFeat = ClassifiersPackage.Literals.CLASS__EXTENDS;

	/**
	 * Checks whether api.xWithFeat() works as intended on EAttributes.
	 */
	@Test
	public void testAPI_WithFeat_EAttribute() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsName = "cls";
		var cls = api.createNewClass();

		Assertions.assertNotEquals(clsName, cls.getName());
		api.xWithFeat(cls, nameFeat, clsName);
		Assertions.assertEquals(clsName, cls.getName());
	}

	/**
	 * Checks whether api.xWithFeat() works as intended on EReferences.
	 */
	@Test
	public void testAPI_WithFeat_EReference() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var extType = api.createNewClassifierReference();
		var cls = api.createNewClass();

		api.xWithFeat(cls, extendsFeat, extType);
		Assertions.assertSame(extType, cls.getExtends());
	}

	/**
	 * Checks whether api.xWithoutFeat() works as intended on EAttributes.
	 */
	@Test
	public void testAPI_WithoutFeat_EAttribute() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsName = "cls";
		var cls = api.createNewClass();
		var feat = nameFeat;

		cls.setName(clsName);

		Assertions.assertEquals(clsName, cls.getName());
		api.xWithoutFeat(cls, feat);
		Assertions.assertEquals(feat.getDefaultValueLiteral(), cls.getName());
	}

	/**
	 * Checks whether api.xWithFeat() works as intended on EReference.
	 */
	@Test
	public void testAPI_WithoutFeat_EReference() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsExtendsVal = api.createNewClassifierReference();
		var cls = api.createNewClass();
		var feat = extendsFeat;

		cls.setExtends(clsExtendsVal);

		Assertions.assertSame(clsExtendsVal, cls.getExtends());
		api.xWithoutFeat(cls, feat);
		Assertions.assertEquals(feat.getDefaultValue(), cls.getExtends());
	}

	/**
	 * Checks whether api.xWithAddedFeat(val) works as intended on many-valued
	 * features without any pre-existing values.
	 */
	@Test
	public void testAPI_WithAddedFeat_SingleValue_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var ns = "ns";

		api.xWithAddedFeat(pac, namespaceFeat, ns);
		Assertions.assertEquals(1, pac.getNamespaces().size());
		Assertions.assertEquals(ns, pac.getNamespaces().get(0));
	}

	/**
	 * Checks whether api.xWithAddedFeat(val) works as intended on many-valued
	 * features with pre-existing values.
	 */
	@Test
	public void testAPI_WithAddedFeat_SingleValue_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var pastNss = List.of("someNs1", "someNs2");
		pac.getNamespaces().addAll(pastNss);
		var newNs = "newNs";

		var expectedNss = List.of(pastNss.get(0), pastNss.get(1), newNs);

		api.xWithAddedFeat(pac, namespaceFeat, newNs);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, pac.getNamespaces());
	}

	/**
	 * Checks whether api.xWithAddedFeat(valArray) works as intended on many-valued
	 * features.
	 */
	@Test
	public void testAPI_WithAddedFeat_MultipleValuesAsArray() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var nss = new String[] { "ns1", "ns2" };

		api.xWithAddedFeat(pac, namespaceFeat, nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, pac.getNamespaces());
	}

	/**
	 * Checks whether api.xWithAddedFeat(valCollection) works as intended on
	 * many-valued features.
	 */
	@Test
	public void testAPI_WithAddedFeat_MultipleValuesAsCollection() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var nss = List.of("ns1", "ns2");

		api.xWithAddedFeat(pac, namespaceFeat, nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, pac.getNamespaces());
	}

	/**
	 * Checks whether api.xWithRemovedFeat(val) works as intended on many-valued
	 * features without any pre-existing values.
	 */
	@Test
	public void testAPI_WithRemovedFeat_SingleValue_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var ns = "ns";

		api.xWithRemovedFeat(pac, namespaceFeat, ns);
		Assertions.assertEquals(0, pac.getNamespaces().size());
	}

	/**
	 * Checks whether api.xWithRemovedFeat(val) works as intended on many-valued
	 * features with pre-existing values.
	 */
	@Test
	public void testAPI_WithRemovedFeat_SingleValue_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var pastNss = List.of("someNs1", "someNs2");
		pac.getNamespaces().addAll(pastNss);
		var nsToBeRemoved = pastNss.get(0);

		var expectedNss = List.of(pastNss.get(1));

		api.xWithRemovedFeat(pac, namespaceFeat, nsToBeRemoved);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, pac.getNamespaces());
	}

	/**
	 * Checks whether api.xWithRemovedFeat(valCollection) works as intended on
	 * many-valued features.
	 */
	@Test
	public void testAPI_WithRemovedFeat_MultipleValuesAsCollection() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var pastNss = List.of("ns1", "ns2", "ns3");
		pac.getNamespaces().addAll(pastNss);
		var nss = List.of(pastNss.get(0), pastNss.get(2));

		api.xWithRemovedFeat(pac, namespaceFeat, nss);
		FluentAPITestUtils.assertPairwiseEqual(List.of(pastNss.get(1)), pac.getNamespaces());
	}

	/**
	 * Checks whether api.xWithRemovedFeat(valArray) works as intended on
	 * many-valued features.
	 */
	@Test
	public void testAPI_WithRemovedFeat_MultipleValuesAsArray() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var pastNss = new String[] { "ns1", "ns2", "ns3" };
		pac.getNamespaces().addAll(List.of(pastNss));

		api.xWithRemovedFeat(pac, namespaceFeat, List.of(pastNss[0], pastNss[2]));
		FluentAPITestUtils.assertPairwiseEqual(List.of(pastNss[1]), pac.getNamespaces());
	}

	/**
	 * Checks whether api.xCleanFeat() works as intended on many-valued features
	 * without any pre-existing values.
	 */
	@Test
	public void testAPI_CleanFeat_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();

		Assertions.assertEquals(0, pac.getNamespaces().size());
		api.xCleanFeat(pac, namespaceFeat);
		Assertions.assertEquals(0, pac.getNamespaces().size());
	}

	/**
	 * Checks whether api.xCleanFeat() works as intended on many-valued features
	 * with pre-existing values.
	 */
	@Test
	public void testAPI_CleanFeat_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var pac = api.newPackage().createNow();
		var pastNss = new String[] { "ns1", "ns2", "ns3" };
		pac.getNamespaces().addAll(List.of(pastNss));

		FluentAPITestUtils.assertPairwiseEqual(pastNss, pac.getNamespaces());
		api.xCleanFeat(pac, namespaceFeat);
		Assertions.assertEquals(0, pac.getNamespaces().size());
	}
}
