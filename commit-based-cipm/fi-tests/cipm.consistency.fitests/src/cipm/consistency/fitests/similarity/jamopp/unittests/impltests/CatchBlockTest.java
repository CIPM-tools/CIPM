package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class CatchBlockTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<OrdinaryParameter> parameter1 = () -> getAPI().newOrdinaryParameter().withName("param1")
			.createNow();
	private final Supplier<OrdinaryParameter> parameter2 = () -> getAPI().newOrdinaryParameter().withName("param2")
			.createNow();

	@Test
	public void testParameter() {
		this.testSimilarity(getAPI().newCatchBlock().withParameter(parameter1.get()).createNow(),
				getAPI().newCatchBlock().withParameter(parameter2.get()).createNow(),
				StatementsPackage.Literals.CATCH_BLOCK__PARAMETER);
	}

	@Test
	public void testParameterNullCheck() {
		this.testSimilarityNullCheck(getAPI().newCatchBlock().withParameter(parameter1.get()).createNow(),
				StatementsPackage.Literals.CATCH_BLOCK__PARAMETER);
	}
}
