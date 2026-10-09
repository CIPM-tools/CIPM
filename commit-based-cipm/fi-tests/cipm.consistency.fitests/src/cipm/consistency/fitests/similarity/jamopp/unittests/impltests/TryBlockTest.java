package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.CatchBlock;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class TryBlockTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<org.emftext.language.java.variables.Resource> resources1 = () -> getAPI().newLocalVariable()
			.withName("lv1").createNow();
	private final Supplier<org.emftext.language.java.variables.Resource> resources2 = () -> getAPI().newLocalVariable()
			.withName("lv2").createNow();

	private final Supplier<CatchBlock> catchBlocks1 = () -> getAPI().newCatchBlock()
			.withParameter(getAPI().newOrdinaryParameter().withName("param1").createNow()).createNow();
	private final Supplier<CatchBlock> catchBlocks2 = () -> getAPI().newCatchBlock()
			.withParameter(getAPI().newOrdinaryParameter().withName("param2").createNow()).createNow();

	private final Supplier<Block> finallyBlock1 = () -> getAPI().newBlock()
			.withAddedStatements(getAPI().newEmptyStatement()).createNow();
	private final Supplier<Block> finallyBlock2 = () -> getAPI().newBlock()
			.withAddedStatements(getAPI().createNewAssert()).createNow();

	@Test
	public void testResource() {
		this.testSimilarity(getAPI().newTryBlock().withAddedResources(resources1.get()).createNow(),
				getAPI().newTryBlock().withAddedResources(resources2.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testResourceSize() {
		this.testSimilarity(
				getAPI().newTryBlock().withAddedResources(
						new org.emftext.language.java.variables.Resource[] { resources1.get(), resources2.get() })
						.createNow(),
				getAPI().newTryBlock().withAddedResources(resources1.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testResourceNullCheck() {
		this.testSimilarityNullCheck(getAPI().newTryBlock().withAddedResources(resources1.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testCatchBlock() {
		this.testSimilarity(getAPI().newTryBlock().withAddedCatchBlocks(catchBlocks1.get()).createNow(),
				getAPI().newTryBlock().withAddedCatchBlocks(catchBlocks2.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testCatchBlockSize() {
		this.testSimilarity(
				getAPI().newTryBlock().withAddedCatchBlocks(new CatchBlock[] { catchBlocks1.get(), catchBlocks2.get() })
						.createNow(),
				getAPI().newTryBlock().withAddedCatchBlocks(catchBlocks1.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testCatchBlockNullCheck() {
		this.testSimilarityNullCheck(getAPI().newTryBlock().withAddedCatchBlocks(catchBlocks1.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testFinallyBlock() {
		this.testSimilarity(getAPI().newTryBlock().withFinallyBlock(finallyBlock1.get()).createNow(),
				getAPI().newTryBlock().withFinallyBlock(finallyBlock2.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__FINALLY_BLOCK);
	}

	@Test
	public void testFinallyBlockNullCheck() {
		this.testSimilarityNullCheck(getAPI().newTryBlock().withFinallyBlock(finallyBlock1.get()).createNow(),
				StatementsPackage.Literals.TRY_BLOCK__FINALLY_BLOCK);
	}
}
