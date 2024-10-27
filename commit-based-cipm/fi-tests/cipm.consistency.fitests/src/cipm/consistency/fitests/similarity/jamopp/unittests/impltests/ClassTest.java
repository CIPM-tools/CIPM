package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;

import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {
	protected Class initElement(TypeReference defExt, TypeReference ext) {
		var clsInit = new ClassInitialiser();
		var cls = clsInit.instantiate();
		Assertions.assertTrue(clsInit.setDefaultExtends(cls, defExt));
		Assertions.assertTrue(clsInit.setExtends(cls, ext));
		return cls;
	}

	@Test
	public void testDefaultExtends() {
		var objOne = this.initElement(this.createMinimalClsRef("cls1"), null);
		var objTwo = this.initElement(this.createMinimalClsRef("cls2"), null);

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.CLASS__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalClsRef("cls1"), null), new ClassInitialiser(),
				false, ClassifiersPackage.Literals.CLASS__DEFAULT_EXTENDS);
	}

	@Test
	public void testExtends() {
		var objOne = this.initElement(null, this.createMinimalClsRef("cls1"));
		var objTwo = this.initElement(null, this.createMinimalClsRef("cls2"));

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.CLASS__EXTENDS);
	}

	@Test
	public void testExtendsNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalClsRef("cls1")), new ClassInitialiser(),
				false, ClassifiersPackage.Literals.CLASS__EXTENDS);
	}
}
