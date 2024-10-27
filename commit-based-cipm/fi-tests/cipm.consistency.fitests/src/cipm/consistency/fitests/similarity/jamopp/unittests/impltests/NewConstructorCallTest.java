package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.instantiations.InstantiationsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesAnonymousClasses;
import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;
import cipm.consistency.initialisers.jamopp.initadapters.NewConstructorCallInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.instantiations.NewConstructorCallInitialiser;
import cipm.consistency.initialisers.jamopp.types.ClassifierReferenceInitialiser;

public class NewConstructorCallTest extends AbstractJaMoPPSimilarityTest implements UsesAnonymousClasses {
	protected NewConstructorCall initElement(AnonymousClass anonymousCls) {
		var nccInit = new NewConstructorCallInitialiser();
		nccInit.addAdaptingStrategy(
				new NewConstructorCallInitialiserAdapter(new ClassifierReferenceInitialiser(), new ClassInitialiser()));
		var ncc = nccInit.instantiate();
		Assertions.assertTrue(nccInit.initialise(ncc));
		Assertions.assertTrue(nccInit.setAnonymousClass(ncc, anonymousCls));
		return ncc;
	}

	@Test
	public void testAnonymousClass() {
		var objOne = this.initElement(this.createMinimalAnonymousClassWithMethod("met1"));
		var objTwo = this.initElement(this.createMinimalAnonymousClassWithMethod("met2"));

		this.testSimilarity(objOne, objTwo, InstantiationsPackage.Literals.NEW_CONSTRUCTOR_CALL__ANONYMOUS_CLASS);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testAnonymousClassNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalAnonymousClassWithMethod("met1")),
				new NewConstructorCallInitialiser(), false,
				InstantiationsPackage.Literals.NEW_CONSTRUCTOR_CALL__ANONYMOUS_CLASS);
	}
}
