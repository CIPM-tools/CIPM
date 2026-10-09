package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.ContainersPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class CompilationUnitTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ConcreteClassifier> classifier1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<ConcreteClassifier> classifier2 = () -> getAPI().newClass().withName("cls2").createNow();

	@Test
	public void testClassifier() {
		this.testSimilarity(getAPI().newCompilationUnit().withAddedClassifiers(classifier1.get()).createNow(),
				getAPI().newCompilationUnit().withAddedClassifiers(classifier2.get()).createNow(),
				ContainersPackage.Literals.COMPILATION_UNIT__CLASSIFIERS);
	}

	@Test
	public void testClassifierSize() {
		this.testSimilarity(getAPI().newCompilationUnit()
				.withAddedClassifiers(new ConcreteClassifier[] { classifier1.get(), classifier2.get() }).createNow(),
				getAPI().newCompilationUnit().withAddedClassifiers(classifier1.get()).createNow(),
				ContainersPackage.Literals.COMPILATION_UNIT__CLASSIFIERS);
	}

	@Test
	public void testClassifierNullCheck() {
		this.testSimilarityNullCheck(getAPI().newCompilationUnit().withAddedClassifiers(classifier1.get()).createNow(),
				ContainersPackage.Literals.COMPILATION_UNIT__CLASSIFIERS);
	}
}
