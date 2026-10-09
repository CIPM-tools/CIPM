package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class TypeParameterTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> extendTypes1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> extendTypes2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testExtendType() {
		this.testSimilarity(getAPI().newTypeParameter().withAddedExtendTypes(extendTypes1.get()).createNow(),
				getAPI().newTypeParameter().withAddedExtendTypes(extendTypes2.get()).createNow(),
				GenericsPackage.Literals.TYPE_PARAMETER__EXTEND_TYPES);
	}

	@Test
	public void testExtendTypeSize() {
		this.testSimilarity(getAPI().newTypeParameter()
				.withAddedExtendTypes(new TypeReference[] { extendTypes1.get(), extendTypes2.get() }).createNow(),
				getAPI().newTypeParameter().withAddedExtendTypes(extendTypes1.get()).createNow(),
				GenericsPackage.Literals.TYPE_PARAMETER__EXTEND_TYPES);
	}

	@Test
	public void testExtendTypeNullCheck() {
		this.testSimilarityNullCheck(getAPI().newTypeParameter().withAddedExtendTypes(extendTypes1.get()).createNow(),
				GenericsPackage.Literals.TYPE_PARAMETER__EXTEND_TYPES);
	}
}
