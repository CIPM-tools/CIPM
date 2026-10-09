package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.modules.ModulesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ModuleReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<org.emftext.language.java.containers.Module> target1 = () -> getAPI().newModule()
			.withName("mod1").createNow();
	private final Supplier<org.emftext.language.java.containers.Module> target2 = () -> getAPI().newModule()
			.withName("mod2").createNow();

	@Test
	public void testTarget() {
		this.testSimilarity(getAPI().newModuleReference().withTarget(target1.get()).createNow(),
				getAPI().newModuleReference().withTarget(target2.get()).createNow(),
				ModulesPackage.Literals.MODULE_REFERENCE__TARGET);
	}

	@Test
	public void testTargetNullCheck() {
		this.testSimilarityNullCheck(getAPI().newModuleReference().withTarget(target1.get()).createNow(),
				ModulesPackage.Literals.MODULE_REFERENCE__TARGET);
	}
}
