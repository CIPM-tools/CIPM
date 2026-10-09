package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.references.ReferencesPackage;
import org.emftext.language.java.types.PrimitiveType;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class PrimitiveTypeReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<PrimitiveType> primitiveType1 = () -> getAPI().createNewBoolean();
	private final Supplier<PrimitiveType> primitiveType2 = () -> getAPI().createNewInt();

	@Test
	public void testPrimitiveType() {
		this.testSimilarity(getAPI().newPrimitiveTypeReference().withPrimitiveType(primitiveType1.get()).createNow(),
				getAPI().newPrimitiveTypeReference().withPrimitiveType(primitiveType2.get()).createNow(),
				ReferencesPackage.Literals.PRIMITIVE_TYPE_REFERENCE__PRIMITIVE_TYPE);
	}

	@Test
	public void testPrimitiveTypeNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newPrimitiveTypeReference().withPrimitiveType(primitiveType1.get()).createNow(),
				ReferencesPackage.Literals.PRIMITIVE_TYPE_REFERENCE__PRIMITIVE_TYPE);
	}
}
