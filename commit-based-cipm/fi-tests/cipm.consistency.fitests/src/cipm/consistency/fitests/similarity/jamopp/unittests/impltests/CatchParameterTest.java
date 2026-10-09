package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class CatchParameterTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> typeReference1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> typeReference2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testTypeReference() {
		this.testSimilarity(getAPI().newCatchParameter().withAddedTypeReferences(typeReference1.get()).createNow(),
				getAPI().newCatchParameter().withAddedTypeReferences(typeReference2.get()).createNow(),
				ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}

	@Test
	public void testTypeReferenceSize() {
		this.testSimilarity(
				getAPI().newCatchParameter()
						.withAddedTypeReferences(new TypeReference[] { typeReference1.get(), typeReference2.get() })
						.createNow(),
				getAPI().newCatchParameter().withAddedTypeReferences(typeReference1.get()).createNow(),
				ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}

	@Test
	public void testTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newCatchParameter().withAddedTypeReferences(typeReference1.get()).createNow(),
				ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}
}
