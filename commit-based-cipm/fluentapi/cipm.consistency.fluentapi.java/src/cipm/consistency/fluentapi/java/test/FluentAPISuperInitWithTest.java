package cipm.consistency.fluentapi.java.test;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;
import cipm.consistency.fluentapi.test.FluentAPITestUtils;

/**
 * A test class containing tests for modification methods of the abstract
 * (super) initialisation class within the fluent api model.
 * <p>
 * <p>
 * Although those modification methods are also accessible under the concrete
 * initialisations, they are not intended to be used from the concrete
 * initialisations.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPISuperInitWithTest extends AbstractFluentAPITest {
	private static final EStructuralFeature nameFeat = CommonsPackage.Literals.NAMED_ELEMENT__NAME;
	private static final EStructuralFeature namespaceFeat = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;

	/**
	 * Checks whether superInit.xWithFeat(feat, val) works as intended.
	 */
	@Test
	public void testSuperInit_WithFeat() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var name = "cuName";
		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		Assertions.assertNull(cu.getName());

		init.xWithFeat(nameFeat, name);
		Assertions.assertEquals(name, cu.getName());
	}

	/**
	 * Checks whether superInit.xWithoutFeat(feat, val) works as intended.
	 */
	@Test
	public void testSuperInit_WithoutFeat() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var name = "cuName";
		var cu = api.createNewCompilationUnit();
		cu.setName(name);
		var init = api.modifyX(cu);
		Assertions.assertEquals(name, cu.getName());

		init.xWithoutFeat(nameFeat);
		Assertions.assertNull(cu.getName());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, val) works as intended, if
	 * val is not an eligible value.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_RemoveNonExistentValue() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithRemovedFeat(namespaceFeat, "ns4");
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, val) works as intended.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_SingularParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var expectedNss = List.of(ns2, ns3);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithRemovedFeat(namespaceFeat, ns1);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, valArray) works as intended.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_ArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var toRemove = new String[] { ns1, ns3 };
		var expectedNss = List.of(ns2);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithRemovedFeat(namespaceFeat, toRemove);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, valArray) works as intended,
	 * if valArray were empty.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_EmptyArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		init.xWithRemovedFeat(namespaceFeat, new String[] {});
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, valCollection) works as
	 * intended.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_CollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var toRemove = List.of(ns1, ns3);
		var expectedNss = List.of(ns2);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithRemovedFeat(namespaceFeat, toRemove);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithRemovedFeat(feat, valCollection) works as
	 * intended, if valCollection were empty.
	 */
	@Test
	public void testSuperInit_WithRemovedFeat_EmptyCollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		init.xWithRemovedFeat(namespaceFeat, List.of());
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether superInit.xCleanFeat(feat) works as intended.
	 */
	@Test
	public void testSuperInit_CleanFeat() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xCleanFeat(namespaceFeat);
		Assertions.assertEquals(0, cu.getNamespaces().size());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, val) works as intended, if
	 * there were no prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_SingularParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var ns = "ns";

		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.xWithAddedFeat(namespaceFeat, ns);
		Assertions.assertEquals(1, cu.getNamespaces().size());
		Assertions.assertEquals(ns, cu.getNamespaces().get(0));
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, val) works as intended, if
	 * there were prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_SingularParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nsToAdd = "ns";
		var expectedNss = List.of("ns1", "ns2", nsToAdd);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithAddedFeat(namespaceFeat, nsToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valArray) works as intended, if
	 * there were no prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_ArrayParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = new String[] { "ns1", "ns2" };

		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.xWithAddedFeat(namespaceFeat, nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valArray) works as intended, if
	 * there were prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_ArrayParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nssToAdd = new String[] { "ns3", "ns4" };
		var expectedNss = List.of("ns1", "ns2", "ns3", "ns4");

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithAddedFeat(namespaceFeat, nssToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valArray) works as intended, if
	 * valArray were empty.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_EmptyArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		init.xWithAddedFeat(namespaceFeat, new String[] {});
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valCollection) works as
	 * intended, if there were no prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_CollectionParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");

		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.xWithAddedFeat(namespaceFeat, nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valCollection) works as
	 * intended, if there were prior values.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_CollectionParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nssToAdd = List.of("ns3", "ns4");
		var expectedNss = List.of("ns1", "ns2", "ns3", "ns4");

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyX(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.xWithAddedFeat(namespaceFeat, nssToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether superInit.xWithAddedFeat(feat, valCollection) works as
	 * intended, if valCollection were empty.
	 */
	@Test
	public void testSuperInit_WithAddedFeat_EmptyCollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyX(cu);
		init.xWithAddedFeat(namespaceFeat, List.of());
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}
}
