package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.modifiers.ModuleRequiresModifier;
import org.emftext.language.java.modules.ModuleReference;
import org.emftext.language.java.modules.ModulesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class RequiresModuleDirectiveTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ModuleRequiresModifier> modifier1 = () -> getAPI().newStatic();
	private final Supplier<ModuleRequiresModifier> modifier2 = () -> getAPI().newTransitive();

	private final Supplier<ModuleReference> requiredModule1 = () -> getAPI().newModuleReference()
			.withTarget(getAPI().newModule().withName("mod1").createNow()).withAddedNamespaces("ns1").createNow();
	private final Supplier<ModuleReference> requiredModule2 = () -> getAPI().newModuleReference()
			.withTarget(getAPI().newModule().withName("mod2").createNow()).withAddedNamespaces("ns2").createNow();

	@Test
	public void testModifier() {
		this.testSimilarity(getAPI().newRequiresModuleDirective().withModifier(modifier1.get()).createNow(),
				getAPI().newRequiresModuleDirective().withModifier(modifier2.get()).createNow(),
				ModulesPackage.Literals.REQUIRES_MODULE_DIRECTIVE__MODIFIER);
	}

	@Test
	public void testModifierNullCheck() {
		this.testSimilarityNullCheck(getAPI().newRequiresModuleDirective().withModifier(modifier1.get()).createNow(),
				ModulesPackage.Literals.REQUIRES_MODULE_DIRECTIVE__MODIFIER);
	}

	@Test
	public void testRequiredModule() {
		this.testSimilarity(getAPI().newRequiresModuleDirective().withRequiredModule(requiredModule1.get()).createNow(),
				getAPI().newRequiresModuleDirective().withRequiredModule(requiredModule2.get()).createNow(),
				ModulesPackage.Literals.REQUIRES_MODULE_DIRECTIVE__REQUIRED_MODULE);
	}

	@Test
	public void testRequiredModuleNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newRequiresModuleDirective().withRequiredModule(requiredModule1.get()).createNow(),
				ModulesPackage.Literals.REQUIRES_MODULE_DIRECTIVE__REQUIRED_MODULE);
	}
}
