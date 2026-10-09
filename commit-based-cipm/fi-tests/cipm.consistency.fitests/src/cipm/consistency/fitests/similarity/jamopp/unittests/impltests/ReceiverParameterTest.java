package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.This;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ReceiverParameterTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<This> thisReference = () -> getAPI().newThis();

	private final Supplier<TypeReference> outerTypeReference1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> outerTypeReference2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testThisTypeReference() {
		this.testSimilarity(getAPI().newReceiverParameter().withThisReference(thisReference.get()).createNow(),
				getAPI().createNewReceiverParameter(), ParametersPackage.Literals.RECEIVER_PARAMETER__THIS_REFERENCE);
	}

	@Test
	public void testThisTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(getAPI().newReceiverParameter().withThisReference(thisReference.get()).createNow(),
				ParametersPackage.Literals.RECEIVER_PARAMETER__THIS_REFERENCE);
	}

	@Test
	public void testOuterTypeReference() {
		this.testSimilarity(
				getAPI().newReceiverParameter().withOuterTypeReference(outerTypeReference1.get()).createNow(),
				getAPI().newReceiverParameter().withOuterTypeReference(outerTypeReference2.get()).createNow(),
				ParametersPackage.Literals.RECEIVER_PARAMETER__OUTER_TYPE_REFERENCE);
	}

	@Test
	public void testOuterTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newReceiverParameter().withOuterTypeReference(outerTypeReference1.get()).createNow(),
				ParametersPackage.Literals.RECEIVER_PARAMETER__OUTER_TYPE_REFERENCE);
	}
}
