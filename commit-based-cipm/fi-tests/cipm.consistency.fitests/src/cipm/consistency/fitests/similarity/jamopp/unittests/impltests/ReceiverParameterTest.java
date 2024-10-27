package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.parameters.ReceiverParameter;
import org.emftext.language.java.literals.This;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLiterals;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.parameters.ReceiverParameterInitialiser;

public class ReceiverParameterTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences, UsesLiterals {
	protected ReceiverParameter initElement(This thisRef, TypeReference otRef) {
		var rpInit = new ReceiverParameterInitialiser();
		var rp = rpInit.instantiate();
		Assertions.assertTrue(rpInit.setThisReference(rp, thisRef));
		Assertions.assertTrue(rpInit.setOuterTypeReference(rp, otRef));
		return rp;
	}

	@Test
	public void testThisTypeReference() {
		var objOne = this.initElement(this.createThis(), null);
		var objTwo = this.initElement(null, null);

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.RECEIVER_PARAMETER__THIS_REFERENCE);
	}

	@Test
	public void testThisTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createThis(), null), new ReceiverParameterInitialiser(),
				false, ParametersPackage.Literals.RECEIVER_PARAMETER__THIS_REFERENCE);
	}

	@Test
	public void testOuterTypeReference() {
		var objOne = this.initElement(null, this.createMinimalClsRef("cls1"));
		var objTwo = this.initElement(null, this.createMinimalClsRef("cls2"));

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.RECEIVER_PARAMETER__OUTER_TYPE_REFERENCE);
	}

	@Test
	public void testOuterTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalClsRef("cls1")),
				new ReceiverParameterInitialiser(), false,
				ParametersPackage.Literals.RECEIVER_PARAMETER__OUTER_TYPE_REFERENCE);
	}
}
