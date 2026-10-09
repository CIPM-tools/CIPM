package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class SuperTypeArgumentTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> superType1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> superType2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testSuperType() {
		this.testSimilarity(getAPI().newSuperTypeArgument().withSuperType(superType1.get()).createNow(),
				getAPI().newSuperTypeArgument().withSuperType(superType2.get()).createNow(),
				GenericsPackage.Literals.SUPER_TYPE_ARGUMENT__SUPER_TYPE);
	}

	@Test
	public void testSuperTypeNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSuperTypeArgument().withSuperType(superType1.get()).createNow(),
				GenericsPackage.Literals.SUPER_TYPE_ARGUMENT__SUPER_TYPE);
	}
}
