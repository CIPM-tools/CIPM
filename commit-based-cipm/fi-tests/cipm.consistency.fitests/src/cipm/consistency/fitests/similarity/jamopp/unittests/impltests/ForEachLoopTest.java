package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ForEachLoopTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> collection1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> collection2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<OrdinaryParameter> next1 = () -> getAPI().newOrdinaryParameter().withName("param1")
			.createNow();
	private final Supplier<OrdinaryParameter> next2 = () -> getAPI().newOrdinaryParameter().withName("param2")
			.createNow();

	@Test
	public void testCollection() {
		this.testSimilarity(getAPI().newForEachLoop().withCollection(collection1.get()).createNow(),
				getAPI().newForEachLoop().withCollection(collection2.get()).createNow(),
				StatementsPackage.Literals.FOR_EACH_LOOP__COLLECTION);
	}

	@Test
	public void testCollectionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newForEachLoop().withCollection(collection1.get()).createNow(),
				StatementsPackage.Literals.FOR_EACH_LOOP__COLLECTION);
	}

	@Test
	public void testNext() {
		this.testSimilarity(getAPI().newForEachLoop().withNext(next1.get()).createNow(),
				getAPI().newForEachLoop().withNext(next2.get()).createNow(),
				StatementsPackage.Literals.FOR_EACH_LOOP__NEXT);
	}

	@Test
	public void testNextNullCheck() {
		this.testSimilarityNullCheck(getAPI().newForEachLoop().withNext(next1.get()).createNow(),
				StatementsPackage.Literals.FOR_EACH_LOOP__NEXT);
	}
}
