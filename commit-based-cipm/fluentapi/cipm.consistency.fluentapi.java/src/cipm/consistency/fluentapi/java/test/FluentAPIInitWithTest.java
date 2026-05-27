package cipm.consistency.fluentapi.java.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;
import cipm.consistency.fluentapi.test.FluentAPITestUtils;

/**
 * A test class containing tests for modification methods of the initialisation
 * classes within the fluent api model.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIInitWithTest extends AbstractFluentAPITest {
	/**
	 * Checks whether init.withX(...) works as intended.
	 */
	@Test
	public void testInit_With() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var name = "cuName";
		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		Assertions.assertNull(cu.getName());

		init.withName(name);
		Assertions.assertEquals(name, cu.getName());
	}

	/**
	 * Checks whether init.withoutX(...) works as intended.
	 */
	@Test
	public void testInit_Without() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var name = "cuName";
		var cu = api.createNewCompilationUnit();
		cu.setName(name);
		var init = api.modifyCompilationUnit(cu);
		Assertions.assertEquals(name, cu.getName());

		init.withoutName();
		Assertions.assertNull(cu.getName());
	}

	/**
	 * Checks whether init.withRemovedX(val) works as intended, if val is not an
	 * eligible value.
	 */
	@Test
	public void testInit_WithRemoved_RemoveNonExistentValue() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withRemovedNamespaces("ns4");
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withRemovedX(val) works as intended.
	 */
	@Test
	public void testInit_WithRemoved_SingularParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var expectedNss = List.of(ns2, ns3);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withRemovedNamespaces(ns1);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withRemovedX(valArray) works as intended.
	 */
	@Test
	public void testInit_WithRemoved_ArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var toRemove = new String[] { ns1, ns3 };
		var expectedNss = List.of(ns2);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withRemovedNamespaces(toRemove);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withRemovedX(valArray) works as intended, if valArray
	 * were empty.
	 */
	@Test
	public void testInit_WithRemoved_EmptyArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		init.withRemovedNamespaces(new String[] {});
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether init.withRemovedX(valCollection) works as intended.
	 */
	@Test
	public void testInit_WithRemoved_CollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };
		var toRemove = List.of(ns1, ns3);
		var expectedNss = List.of(ns2);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withRemovedNamespaces(toRemove);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withRemovedX(valCollection) works as intended, if
	 * valCollection were empty.
	 */
	@Test
	public void testInit_WithRemoved_EmptyCollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		init.withRemovedNamespaces(List.of());
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether init.clean() works as intended.
	 */
	@Test
	public void testInit_Clean() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var ns1 = "ns1";
		var ns2 = "ns2";
		var ns3 = "ns3";

		var nss = new String[] { ns1, ns2, ns3 };

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(List.of(nss));
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.cleanNamespaces();
		Assertions.assertEquals(0, cu.getNamespaces().size());
	}

	/**
	 * Checks whether init.withAddedX(val) works as intended, if there were no prior
	 * values.
	 */
	@Test
	public void testInit_WithAdded_SingularParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var ns = "ns";

		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.withAddedNamespaces(ns);
		Assertions.assertEquals(1, cu.getNamespaces().size());
		Assertions.assertEquals(ns, cu.getNamespaces().get(0));
	}

	/**
	 * Checks whether init.withAddedX(val) works as intended, if there were prior
	 * values.
	 */
	@Test
	public void testInit_WithAdded_SingularParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nsToAdd = "ns";
		var expectedNss = List.of("ns1", "ns2", nsToAdd);

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withAddedNamespaces(nsToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withAddedX(valArray) works as intended, if there were no
	 * prior values.
	 */
	@Test
	public void testInit_WithAdded_ArrayParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = new String[] { "ns1", "ns2" };

		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.withAddedNamespaces(nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withAddedX(valArray) works as intended, if there were
	 * prior values.
	 */
	@Test
	public void testInit_WithAdded_ArrayParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nssToAdd = new String[] { "ns3", "ns4" };
		var expectedNss = List.of("ns1", "ns2", "ns3", "ns4");

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withAddedNamespaces(nssToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withAddedX(valArray) works as intended, if valArray were
	 * empty.
	 */
	@Test
	public void testInit_WithAdded_EmptyArrayParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		init.withAddedNamespaces(new String[] {});
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}

	/**
	 * Checks whether init.withAddedX(valCollection) works as intended, if there
	 * were no prior values.
	 */
	@Test
	public void testInit_WithAdded_CollectionParameter_NoPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");

		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		Assertions.assertTrue(cu.getNamespaces().isEmpty());

		init.withAddedNamespaces(nss);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withAddedX(valCollection) works as intended, if there
	 * were prior values.
	 */
	@Test
	public void testInit_WithAdded_CollectionParameter_WithPriorValues() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var nss = List.of("ns1", "ns2");
		var nssToAdd = List.of("ns3", "ns4");
		var expectedNss = List.of("ns1", "ns2", "ns3", "ns4");

		var cu = api.createNewCompilationUnit();
		cu.getNamespaces().addAll(nss);
		var init = api.modifyCompilationUnit(cu);
		FluentAPITestUtils.assertPairwiseEqual(nss, cu.getNamespaces());

		init.withAddedNamespaces(nssToAdd);
		FluentAPITestUtils.assertPairwiseEqual(expectedNss, cu.getNamespaces());
	}

	/**
	 * Checks whether init.withAddedX(valCollection) works as intended, if
	 * valCollection were empty.
	 */
	@Test
	public void testInit_WithAdded_EmptyCollectionParameter() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cu = api.createNewCompilationUnit();
		var init = api.modifyCompilationUnit(cu);
		init.withAddedNamespaces(List.of());
		Assertions.assertTrue(cu.getNamespaces().isEmpty());
	}
}
