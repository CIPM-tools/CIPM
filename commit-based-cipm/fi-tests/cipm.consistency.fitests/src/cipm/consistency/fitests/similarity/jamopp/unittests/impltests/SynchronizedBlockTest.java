package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class SynchronizedBlockTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> lockProvider1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> lockProvider2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testLockProvider() {
		this.testSimilarity(getAPI().newSynchronizedBlock().withLockProvider(lockProvider1.get()).createNow(),
				getAPI().newSynchronizedBlock().withLockProvider(lockProvider2.get()).createNow(),
				StatementsPackage.Literals.SYNCHRONIZED_BLOCK__LOCK_PROVIDER);
	}

	@Test
	public void testLockProviderNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSynchronizedBlock().withLockProvider(lockProvider1.get()).createNow(),
				StatementsPackage.Literals.SYNCHRONIZED_BLOCK__LOCK_PROVIDER);
	}
}
