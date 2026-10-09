package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class InterfaceTest extends AbstractJaMoPPSimilarityTest {
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
		this.testSimilarity(getAPI().newInterface().withAddedDefaultExtends(defaultExtends1.get()).createNow(),
				getAPI().newInterface().withAddedDefaultExtends(defaultExtends2.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsSize() {
		this.testSimilarity(
				getAPI().newInterface()
						.withAddedDefaultExtends(new TypeReference[] { defaultExtends1.get(), defaultExtends2.get() })
						.createNow(),
				getAPI().newInterface().withAddedDefaultExtends(defaultExtends1.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsNullCheck() {
		this.testSimilarityNullCheck(getAPI().newInterface().withAddedDefaultExtends(defaultExtends1.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testExtends() {
		this.testSimilarity(getAPI().newInterface().withAddedExtends(extends1.get()).createNow(),
				getAPI().newInterface().withAddedExtends(extends2.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}

	@Test
	public void testExtendsSize() {
		this.testSimilarity(
				getAPI().newInterface().withAddedExtends(new TypeReference[] { extends1.get(), extends2.get() })
						.createNow(),
				getAPI().newInterface().withAddedExtends(extends1.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}

	@Test
	public void testExtendsNullCheck() {
		this.testSimilarityNullCheck(getAPI().newInterface().withAddedExtends(extends1.get()).createNow(),
				ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}
}
