package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.ForLoopInitializer;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ForLoopTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ForLoopInitializer> init1 = () -> getAPI().createNewExpressionList();
	private final Supplier<ForLoopInitializer> init2 = () -> getAPI()
			.newExpressionList(getAPI().newDecimalIntegerLiteral(1));

	private final Supplier<Expression> update1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> update2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testInit() {
		this.testSimilarity(getAPI().newForLoop().withInit(init1.get()).createNow(),
				getAPI().newForLoop().withInit(init2.get()).createNow(), StatementsPackage.Literals.FOR_LOOP__INIT);
	}

	@Test
	public void testInitNullCheck() {
		this.testSimilarityNullCheck(getAPI().newForLoop().withInit(init1.get()).createNow(),
				StatementsPackage.Literals.FOR_LOOP__INIT);
	}

	@Test
	public void testUpdate() {
		this.testSimilarity(getAPI().newForLoop().withAddedUpdates(update1.get()).createNow(),
				getAPI().newForLoop().withAddedUpdates(update2.get()).createNow(),
				StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}

	@Test
	public void testUpdateSize() {
		this.testSimilarity(
				getAPI().newForLoop().withAddedUpdates(new Expression[] { update1.get(), update2.get() }).createNow(),
				getAPI().newForLoop().withAddedUpdates(update1.get()).createNow(),
				StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}

	@Test
	public void testUpdateNullCheck() {
		this.testSimilarityNullCheck(getAPI().newForLoop().withAddedUpdates(update1.get()).createNow(),
				StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}
}
