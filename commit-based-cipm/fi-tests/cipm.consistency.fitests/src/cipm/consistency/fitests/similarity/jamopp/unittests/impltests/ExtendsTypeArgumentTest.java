package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ExtendsTypeArgumentTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> extendType1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> extendType2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testExtendType() {
		this.testSimilarity(getAPI().newExtendsTypeArgument().withExtendType(extendType1.get()).createNow(),
				getAPI().newExtendsTypeArgument().withExtendType(extendType2.get()).createNow(),
				GenericsPackage.Literals.EXTENDS_TYPE_ARGUMENT__EXTEND_TYPE);
	}

	@Test
	public void testExtendTypeNullCheck() {
		this.testSimilarityNullCheck(getAPI().newExtendsTypeArgument().withExtendType(extendType1.get()).createNow(),
				GenericsPackage.Literals.EXTENDS_TYPE_ARGUMENT__EXTEND_TYPE);
	}
}
