package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.BlockContainer;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class BlockContainerTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Block> block1 = () -> getAPI().newBlock().withAddedStatements(getAPI().newEmptyStatement())
			.createNow();
	private final Supplier<Block> block2 = () -> getAPI().newBlock().withAddedStatements(getAPI().createNewAssert())
			.createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(BlockContainer.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testBlock(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK, block1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK, block2.get())
						.createNow(),
				StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testBlockNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK, block1.get()).createNow(),
				StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK);
	}
}
