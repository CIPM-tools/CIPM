package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.statements.Jump;
import org.emftext.language.java.statements.JumpLabel;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class JumpTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<JumpLabel> target1 = () -> getAPI().newJumpLabel().withName("jl1").createNow();
	private final Supplier<JumpLabel> target2 = () -> getAPI().newJumpLabel().withName("jl2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Jump.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTarget(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.JUMP__TARGET, target1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.JUMP__TARGET, target2.get()).createNow(),
				StatementsPackage.Literals.JUMP__TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.JUMP__TARGET, target1.get()).createNow(),
				StatementsPackage.Literals.JUMP__TARGET);
	}
}
