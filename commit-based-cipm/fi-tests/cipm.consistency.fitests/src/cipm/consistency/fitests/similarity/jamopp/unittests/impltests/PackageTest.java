package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.containers.ContainersPackage;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class PackageTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<org.emftext.language.java.containers.Module> module1 = () -> getAPI().newModule()
			.withName("mod1").createNow();
	private final Supplier<org.emftext.language.java.containers.Module> module2 = () -> getAPI().newModule()
			.withName("mod2").createNow();

	private final Supplier<ConcreteClassifier> classifiers1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<ConcreteClassifier> classifiers2 = () -> getAPI().newClass().withName("cls2").createNow();

	@Test
	public void testModule() {
		this.testSimilarity(getAPI().newPackage().withModule(module1.get()).createNow(),
				getAPI().newPackage().withModule(module2.get()).createNow(),
				ContainersPackage.Literals.PACKAGE__MODULE);
	}

	@Test
	public void testModuleNullCheck() {
		this.testSimilarityNullCheck(getAPI().newPackage().withModule(module1.get()).createNow(),
				ContainersPackage.Literals.PACKAGE__MODULE);
	}

	@Test
	public void testClassifiers() {
		this.testSimilarity(getAPI().newPackage().withAddedClassifiers(classifiers1.get()).createNow(),
				getAPI().newPackage().withAddedClassifiers(classifiers2.get()).createNow(),
				ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}

	@Test
	public void testClassifiersSize() {
		this.testSimilarity(getAPI().newPackage()
				.withAddedClassifiers(new ConcreteClassifier[] { classifiers1.get(), classifiers2.get() }).createNow(),
				getAPI().newPackage().withAddedClassifiers(classifiers1.get()).createNow(),
				ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}

	@Test
	public void testClassifiersNullCheck() {
		this.testSimilarityNullCheck(getAPI().newPackage().withAddedClassifiers(classifiers1.get()).createNow(),
				ContainersPackage.Literals.PACKAGE__CLASSIFIERS);
	}
}
