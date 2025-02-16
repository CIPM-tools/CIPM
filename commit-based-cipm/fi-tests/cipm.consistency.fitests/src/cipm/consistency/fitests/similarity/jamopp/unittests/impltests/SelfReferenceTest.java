package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.Self;
import org.emftext.language.java.references.SelfReference;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLiterals;
import cipm.consistency.initialisers.jamopp.references.SelfReferenceInitialiser;

public class SelfReferenceTest extends AbstractJaMoPPSimilarityTest implements UsesLiterals {
	protected SelfReference initElement(Self self) {
		var srInit = new SelfReferenceInitialiser();
		var sr = srInit.instantiate();
		Assertions.assertTrue(srInit.setSelf(sr, self));
		return sr;
	}

	@Test
	public void testSelf() {
		var objOne = this.initElement(this.createThis());
		var objTwo = this.initElement(this.createSuper());

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.SELF_REFERENCE__SELF);
	}

	@Test
	public void testSelfNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createThis()), new SelfReferenceInitialiser(), false,
				ReferencesPackage.Literals.SELF_REFERENCE__SELF);
	}
}
