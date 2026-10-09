package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

public class ClassTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> defaultExtends1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> defaultExtends2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	private final Supplier<TypeReference> extends1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> extends2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testDefaultExtends() {
		this.testSimilarity(getAPI().newClass().withDefaultExtends(defaultExtends1.get()).createNow(),
				getAPI().newClass().withDefaultExtends(defaultExtends2.get()).createNow(),
				ClassifiersPackage.Literals.CLASS__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsNullCheck() {
		this.testSimilarityNullCheck(getAPI().newClass().withDefaultExtends(defaultExtends1.get()).createNow(),
				ClassifiersPackage.Literals.CLASS__DEFAULT_EXTENDS);
	}

	@Test
	public void testExtends() {
		this.testSimilarity(getAPI().newClass().withExtends(extends1.get()).createNow(),
				getAPI().newClass().withExtends(extends2.get()).createNow(),
				ClassifiersPackage.Literals.CLASS__EXTENDS);
	}

	@Test
	public void testExtendsNullCheck() {
		this.testSimilarityNullCheck(getAPI().newClass().withExtends(extends1.get()).createNow(),
				ClassifiersPackage.Literals.CLASS__EXTENDS);
	}
}
