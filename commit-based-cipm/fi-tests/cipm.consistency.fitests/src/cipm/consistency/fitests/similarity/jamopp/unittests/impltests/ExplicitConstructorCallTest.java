package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.instantiations.InstantiationsPackage;
import org.emftext.language.java.literals.Self;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ExplicitConstructorCallTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Self> callTarget1 = () -> getAPI().newSuper();
	private final Supplier<Self> callTarget2 = () -> getAPI().newThis();

	@Test
	public void testCallTarget() {
		this.testSimilarity(getAPI().newExplicitConstructorCall().withCallTarget(callTarget1.get()).createNow(),
				getAPI().newExplicitConstructorCall().withCallTarget(callTarget2.get()).createNow(),
				InstantiationsPackage.Literals.EXPLICIT_CONSTRUCTOR_CALL__CALL_TARGET);
	}

	@Test
	public void testCallTargetNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newExplicitConstructorCall().withCallTarget(callTarget1.get()).createNow(),
				InstantiationsPackage.Literals.EXPLICIT_CONSTRUCTOR_CALL__CALL_TARGET);
	}
}
