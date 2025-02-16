package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.references.PrimitiveTypeReference;
import org.emftext.language.java.references.ReferencesPackage;
import org.emftext.language.java.types.PrimitiveType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.references.PrimitiveTypeReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.types.BooleanInitialiser;
import cipm.consistency.initialisers.jamopp.types.IntInitialiser;

public class PrimitiveTypeReferenceTest extends AbstractJaMoPPSimilarityTest {
	protected PrimitiveTypeReference initElement(PrimitiveType pType) {
		var ptInit = new PrimitiveTypeReferenceInitialiser();
		var pt = ptInit.instantiate();
		Assertions.assertTrue(ptInit.setPrimitiveType(pt, pType));
		return pt;
	}

	@Test
	public void testPrimitiveType() {
		var objOne = this.initElement(new BooleanInitialiser().instantiate());
		var objTwo = this.initElement(new IntInitialiser().instantiate());

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.PRIMITIVE_TYPE_REFERENCE__PRIMITIVE_TYPE);
	}

	@Test
	public void testPrimitiveTypeNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new BooleanInitialiser().instantiate()),
				new PrimitiveTypeReferenceInitialiser(), false,
				ReferencesPackage.Literals.PRIMITIVE_TYPE_REFERENCE__PRIMITIVE_TYPE);
	}
}
