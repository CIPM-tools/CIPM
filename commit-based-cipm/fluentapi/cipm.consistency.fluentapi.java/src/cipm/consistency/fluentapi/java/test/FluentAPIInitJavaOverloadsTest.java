package cipm.consistency.fluentapi.java.test;

import org.emftext.language.java.classifiers.Classifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for checking whether certain methods in initialisations within
 * the fluent api class for Java are overloaded as expected and that the
 * overloaded versions work as intended.
 * <p>
 * <p>
 * Currently only methods that consider {@link TypeReference} as parameter.
 * 
 * @author Alp Torac Genc
 * @see {@link FluentAPIGenerationJavaMetamodelPostProcessor} for more details
 *      on overloaded methods
 */
public class FluentAPIInitJavaOverloadsTest extends AbstractFluentAPITest {
	/**
	 * Checks whether initialisation methods in fluent api for Java have an
	 * overloading variant that takes (a singular) Classifier parameters instead of
	 * TypeReferences and converts them into ClassifierTypeReferences.
	 */
	@Test
	public void testInit_WithTypeReferenceOverloadTest_SingleValued() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var cls = api.createNewClass();

		var clsMet = api.newClassMethod().withTypeReference(cls).createNow();
		Assertions.assertSame(cls, clsMet.getTypeReference().getPureClassifierReference().getTarget());
	}

	/**
	 * Checks whether initialisation methods in fluent api for Java have an
	 * overloading variant that takes multiple Classifier parameters instead of
	 * TypeReferences and converts them into ClassifierTypeReferences.
	 */
	@Test
	public void testInit_WithTypeReferenceOverloadTest_ManyValued() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsOne = api.createNewClass();
		var clsTwo = api.createNewClass();

		var ai = api.newAnnotationInstance().withAddedActualTargets(new Classifier[] { clsOne, clsTwo }).createNow();

		Assertions.assertSame(clsOne, ai.getActualTargets().get(0).getPureClassifierReference().getTarget());
		Assertions.assertSame(clsTwo, ai.getActualTargets().get(1).getPureClassifierReference().getTarget());
	}
}
