package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.modules.ModulesPackage;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ProvidesModuleDirectiveTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> serviceProvider1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> serviceProvider2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testServiceProviders() {
		this.testSimilarity(
				getAPI().newProvidesModuleDirective().withAddedServiceProviders(serviceProvider1.get()).createNow(),
				getAPI().newProvidesModuleDirective().withAddedServiceProviders(serviceProvider2.get()).createNow(),
				ModulesPackage.Literals.PROVIDES_MODULE_DIRECTIVE__SERVICE_PROVIDERS);
	}

	@Test
	public void testServiceProvidersSize() {
		this.testSimilarity(
				getAPI().newProvidesModuleDirective()
						.withAddedServiceProviders(
								new TypeReference[] { serviceProvider1.get(), serviceProvider2.get() })
						.createNow(),
				getAPI().newProvidesModuleDirective().withAddedServiceProviders(serviceProvider1.get()).createNow(),
				ModulesPackage.Literals.PROVIDES_MODULE_DIRECTIVE__SERVICE_PROVIDERS);
	}

	@Test
	public void testServiceProvidersNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newProvidesModuleDirective().withAddedServiceProviders(serviceProvider1.get()).createNow(),
				ModulesPackage.Literals.PROVIDES_MODULE_DIRECTIVE__SERVICE_PROVIDERS);
	}
}
