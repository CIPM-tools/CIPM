package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.CatchBlock;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.TryBlock;
import org.emftext.language.java.variables.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesCatchBlocks;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLocalVariables;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.statements.TryBlockInitialiser;

public class TryBlockTest extends AbstractJaMoPPSimilarityTest implements UsesCatchBlocks, UsesStatements, UsesLocalVariables {
	protected TryBlock initElement(Resource[] ress, CatchBlock[] catchBlocks, Block finallyBlock) {
		var tbInit = new TryBlockInitialiser();
		var tb = tbInit.instantiate();
		Assertions.assertTrue(tbInit.addResources(tb, ress));
		Assertions.assertTrue(tbInit.addCatchBlocks(tb, catchBlocks));
		Assertions.assertTrue(tbInit.setFinallyBlock(tb, finallyBlock));
		return tb;
	}

	@Test
	public void testResource() {
		var objOne = this.initElement(new Resource[] { this.createMinimalLV("lv1") }, null, null);
		var objTwo = this.initElement(new Resource[] { this.createMinimalLV("lv2") }, null, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testResourceSize() {
		var objOne = this.initElement(new Resource[] { this.createMinimalLV("lv1"), this.createMinimalLV("lv2") }, null,
				null);
		var objTwo = this.initElement(new Resource[] { this.createMinimalLV("lv1") }, null, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testResourceNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new Resource[] { this.createMinimalLV("lv1") }, null, null),
				new TryBlockInitialiser(), false, StatementsPackage.Literals.TRY_BLOCK__RESOURCES);
	}

	@Test
	public void testCatchBlock() {
		var objOne = this.initElement(null, new CatchBlock[] { this.createMinimalCB("p1", "t1") }, null);
		var objTwo = this.initElement(null, new CatchBlock[] { this.createMinimalCB("p2", "t2") }, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testCatchBlockSize() {
		var objOne = this.initElement(null,
				new CatchBlock[] { this.createMinimalCB("p1", "t1"), this.createMinimalCB("p2", "t2") }, null);
		var objTwo = this.initElement(null, new CatchBlock[] { this.createMinimalCB("p1", "t1") }, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testCatchBlockNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(null, new CatchBlock[] { this.createMinimalCB("p1", "t1") }, null),
				new TryBlockInitialiser(), false, StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS);
	}

	@Test
	public void testFinallyBlock() {
		var objOne = this.initElement(null, null, this.createMinimalBlockWithNullReturn());
		var objTwo = this.initElement(null, null, this.createMinimalBlockWithTrivialAssert());

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.TRY_BLOCK__FINALLY_BLOCK);
	}

	@Test
	public void testFinallyBlockNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, null, this.createMinimalBlockWithNullReturn()),
				new TryBlockInitialiser(), false, StatementsPackage.Literals.TRY_BLOCK__FINALLY_BLOCK);
	}
}
