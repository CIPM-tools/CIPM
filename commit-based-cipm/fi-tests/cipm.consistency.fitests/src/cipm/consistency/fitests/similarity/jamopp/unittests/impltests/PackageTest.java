package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.containers.Module;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesModules;
import cipm.consistency.initialisers.jamopp.containers.PackageInitialiser;

public class PackageTest extends AbstractJaMoPPSimilarityTest implements UsesModules, UsesConcreteClassifiers {
	protected Package initElement(Module mod, ConcreteClassifier[] clss) {
		var initialiser = new PackageInitialiser();
		Package pac = initialiser.instantiate();
		Assertions.assertTrue(initialiser.setModule(pac, mod));
		Assertions.assertTrue(initialiser.addClassifiers(pac, clss));

		return pac;
	}

	@Test
	public void testModule() {
		var objOne = this.initElement(this.createMinimalModule("mod1"), null);
		var objTwo = this.initElement(this.createMinimalModule("mod2"), null);

		this.testSimilarity(objOne, objTwo, ContainersPackage.Literals.PACKAGE__MODULE);
	}

	@Test
	public void testModuleNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalModule("mod1"), null), new PackageInitialiser(),
				false, ContainersPackage.Literals.PACKAGE__MODULE);
	}

	@Test
	public void testClassifiers() {
		var objOne = this.initElement(null, new ConcreteClassifier[] { this.createMinimalClass("cls1") });
		var objTwo = this.initElement(null, new ConcreteClassifier[] { this.createMinimalClass("cls2") });

		this.testSimilarity(objOne, objTwo, ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}

	@Test
	public void testClassifiersSize() {
		var objOne = this.initElement(null,
				new ConcreteClassifier[] { this.createMinimalClass("cls1"), this.createMinimalClass("cls2") });
		var objTwo = this.initElement(null, new ConcreteClassifier[] { this.createMinimalClass("cls1") });

		this.testSimilarity(objOne, objTwo, ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}

	@Test
	public void testClassifiersNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(null, new ConcreteClassifier[] { this.createMinimalClass("cls1") }),
				new PackageInitialiser(), false, ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}
}
