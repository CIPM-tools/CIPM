package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.parameters.CatchParameter;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.parameters.CatchParameterInitialiser;

public class CatchParameterTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {
	protected CatchParameter initElement(TypeReference[] trefs) {
		var cpInit = new CatchParameterInitialiser();
		var cp = cpInit.instantiate();
		Assertions.assertTrue(cpInit.addTypeReferences(cp, trefs));
		return cp;
	}

	@Test
	public void testTypeReference() {
		var objOne = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") });
		var objTwo = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls2") });

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}

	@Test
	public void testTypeReferenceSize() {
		var objOne = this.initElement(
				new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") });
		var objTwo = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") });

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}

	@Test
	public void testTypeReferenceNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }),
				new CatchParameterInitialiser(), false, ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES);
	}
}
