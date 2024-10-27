package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportsPackage;
import org.emftext.language.java.imports.PackageImport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesImports;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesPackageImports;
import cipm.consistency.initialisers.jamopp.classifiers.IClassifierInitialiser;

/**
 * Classifier has no modifiable attributes on its own.
 * 
 * @author Alp Torac Genc
 */
public class ClassifierTest extends AbstractJaMoPPSimilarityTest implements UsesImports, UsesPackageImports {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IClassifierInitialiser.class);
	}

	protected Classifier initElement(IClassifierInitialiser init, Import[] imps, PackageImport[] pImps) {

		var result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));

		// If there are no imports to add, add(Package)Imports will return true
		Assertions.assertEquals(init.canAddImports(result) || imps == null, init.addImports(result, imps));
		Assertions.assertEquals(init.canAddPackageImports(result) || pImps == null,
				init.addPackageImports(result, pImps));

		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImports(IClassifierInitialiser init) {
		var objOne = this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") }, null);
		var objTwo = this.initElement(init, new Import[] { this.createMinimalClsImport("cls2") }, null);

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne) && !init.canAddImports(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImportsSize(IClassifierInitialiser init) {
		var objOne = this.initElement(init,
				new Import[] { this.createMinimalClsImport("cls1"), this.createMinimalClsImport("cls2") }, null);
		var objTwo = this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") }, null);

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne) && !init.canAddImports(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImportsNullCheck(IClassifierInitialiser init) {
		var objOne = this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") }, null);

		this.testSimilarityNullCheck(objOne, init, true,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne)));
	}

	/**
	 * Package import differences do not break similarity
	 */
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testPackageImports(IClassifierInitialiser init) {
		var objOne = this.initElement(init, null,
				new PackageImport[] { this.createMinimalPackageImport(new String[] { "ns1", "ns2" }) });
		var objTwo = this.initElement(init, null,
				new PackageImport[] { this.createMinimalPackageImport(new String[] { "ns3", "ns4" }) });

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne) && !init.canAddImports(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testPackageImportsSize(IClassifierInitialiser init) {
		var objOne = this.initElement(init, null,
				new PackageImport[] { this.createMinimalPackageImport(new String[] { "ns1", "ns2" }),
						this.createMinimalPackageImport(new String[] { "ns3", "ns4" }) });
		var objTwo = this.initElement(init, null,
				new PackageImport[] { this.createMinimalPackageImport(new String[] { "ns1", "ns2" }) });

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne) && !init.canAddImports(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testPackageImportsNullCheck(IClassifierInitialiser init) {
		var objOne = this.initElement(init, null,
				new PackageImport[] { this.createMinimalPackageImport(new String[] { "ns1", "ns2" }) });

		this.testSimilarityNullCheck(objOne, init, true,
				this.getExpectedSimilarityResult(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS).booleanValue()
						|| (!init.canAddImports(objOne)));
	}
}
