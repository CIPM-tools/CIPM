package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.classifiers.InterfaceInitialiser;

public class InterfaceTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {
	protected Interface initElement(TypeReference[] defExts, TypeReference[] exts) {
		var intfcInit = new InterfaceInitialiser();
		var intfc = intfcInit.instantiate();
		Assertions.assertTrue(intfcInit.addDefaultExtends(intfc, defExts));
		Assertions.assertTrue(intfcInit.addExtends(intfc, exts));
		return intfc;
	}

	@Test
	public void testDefaultExtends() {
		var objOne = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null);
		var objTwo = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls2") }, null);

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsSize() {
		var objOne = this.initElement(
				new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") }, null);
		var objTwo = this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null);

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testDefaultExtendsNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null),
				new InterfaceInitialiser(), false, ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS);
	}

	@Test
	public void testExtends() {
		var objOne = this.initElement(null, new TypeReference[] { this.createMinimalClsRef("cls1") });
		var objTwo = this.initElement(null, new TypeReference[] { this.createMinimalClsRef("cls2") });

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}

	@Test
	public void testExtendsSize() {
		var objOne = this.initElement(null,
				new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") });
		var objTwo = this.initElement(null, new TypeReference[] { this.createMinimalClsRef("cls1") });

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}

	@Test
	public void testExtendsNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, new TypeReference[] { this.createMinimalClsRef("cls1") }),
				new InterfaceInitialiser(), false, ClassifiersPackage.Literals.INTERFACE__EXTENDS);
	}
}
