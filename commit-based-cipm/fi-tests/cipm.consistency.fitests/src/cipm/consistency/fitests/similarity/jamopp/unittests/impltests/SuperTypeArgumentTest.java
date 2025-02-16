package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.SuperTypeArgument;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.generics.SuperTypeArgumentInitialiser;

public class SuperTypeArgumentTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {
	protected SuperTypeArgument initElement(TypeReference superType) {
		var staInit = new SuperTypeArgumentInitialiser();
		var sta = staInit.instantiate();
		Assertions.assertTrue(staInit.setSuperType(sta, superType));
		return sta;
	}

	@Test
	public void testSuperType() {
		var objOne = this.initElement(this.createMinimalClsRef("cls1"));
		var objTwo = this.initElement(this.createMinimalClsRef("cls2"));

		this.testSimilarity(objOne, objTwo, GenericsPackage.Literals.SUPER_TYPE_ARGUMENT__SUPER_TYPE);
	}

	@Test
	public void testSuperTypeNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalClsRef("cls1")),
				new SuperTypeArgumentInitialiser(), false, GenericsPackage.Literals.SUPER_TYPE_ARGUMENT__SUPER_TYPE);
	}
}
