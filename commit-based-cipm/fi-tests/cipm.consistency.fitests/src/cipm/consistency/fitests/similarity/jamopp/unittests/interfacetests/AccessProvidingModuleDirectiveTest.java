package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.modules.AccessProvidingModuleDirective;
import org.emftext.language.java.modules.ModuleReference;
import org.emftext.language.java.modules.ModulesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class AccessProvidingModuleDirectiveTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ModuleReference> modules1 = () -> getAPI().newModuleReference()
			.withTarget(getAPI().newModule().withName("mod1").createNow()).createNow();
	private final Supplier<ModuleReference> modules2 = () -> getAPI().newModuleReference()
			.withTarget(getAPI().newModule().withName("mod2").createNow()).createNow();

	private final Supplier<org.emftext.language.java.containers.Package> accessablePackage1 = () -> getAPI()
			.newPackage().withAddedNamespaces("ns1").createNow();
	private final Supplier<org.emftext.language.java.containers.Package> accessablePackage2 = () -> getAPI()
			.newPackage().withAddedNamespaces("ns2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(AccessProvidingModuleDirective.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModule(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES,
								modules1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES,
								modules2.get())
						.createNow(),
				ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModuleSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES,
								new ModuleReference[] { modules1.get(), modules2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES,
								modules1.get())
						.createNow(),
				ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModuleNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES,
								modules1.get())
						.createNow(),
				ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAccessablePackage(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE,
								accessablePackage1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE,
								accessablePackage2.get())
						.createNow(),
				ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAccessablePackageNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithFeat(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE,
								accessablePackage1.get())
						.createNow(),
				ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE);
	}
}
