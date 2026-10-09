package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.Self;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class SelfReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Self> self1 = () -> getAPI().newThis();
	private final Supplier<Self> self2 = () -> getAPI().newSuper();

	@Test
	public void testSelf() {
		this.testSimilarity(getAPI().newSelfReference().withSelf(self1.get()).createNow(),
				getAPI().newSelfReference().withSelf(self2.get()).createNow(),
				ReferencesPackage.Literals.SELF_REFERENCE__SELF);
	}

	@Test
	public void testSelfNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSelfReference().withSelf(self1.get()).createNow(),
				ReferencesPackage.Literals.SELF_REFERENCE__SELF);
	}
}
