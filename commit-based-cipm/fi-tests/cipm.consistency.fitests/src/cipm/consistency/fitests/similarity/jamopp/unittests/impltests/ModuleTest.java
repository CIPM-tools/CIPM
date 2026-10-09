package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.modifiers.Open;
import org.emftext.language.java.modules.ModuleDirective;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ModuleTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<org.emftext.language.java.containers.Package> packages1 = () -> getAPI().newPackage()
			.withAddedNamespaces("ns1").createNow();
	private final Supplier<org.emftext.language.java.containers.Package> packages2 = () -> getAPI().newPackage()
			.withAddedNamespaces("ns2").createNow();

	private final Supplier<ModuleDirective> target1 = () -> getAPI().createNewExportsModuleDirective();
	private final Supplier<ModuleDirective> target2 = () -> getAPI().createNewOpensModuleDirective();

	private final Supplier<Open> open = () -> getAPI().newOpen();

	@Test
	public void testOpen() {
		this.testSimilarity(getAPI().newModule().withOpen(open.get()).createNow(), getAPI().createNewModule(),
				ContainersPackage.Literals.MODULE__OPEN);
	}

	@Test
	public void testOpenNullCheck() {
		this.testSimilarityNullCheck(getAPI().newModule().withOpen(open.get()).createNow(),
				ContainersPackage.Literals.MODULE__OPEN);
	}

	@Test
	public void testPackages() {
		this.testSimilarity(getAPI().newModule().withAddedPackages(packages1.get()).createNow(),
				getAPI().newModule().withAddedPackages(packages2.get()).createNow(),
				ContainersPackage.Literals.MODULE__PACKAGES);
	}

	@Test
	public void testPackagesSize() {
		this.testSimilarity(
				getAPI().newModule()
						.withAddedPackages(
								new org.emftext.language.java.containers.Package[] { packages1.get(), packages2.get() })
						.createNow(),
				getAPI().newModule().withAddedPackages(packages1.get()).createNow(),
				ContainersPackage.Literals.MODULE__PACKAGES);
	}

	@Test
	public void testPackagesNullCheck() {
		this.testSimilarityNullCheck(getAPI().newModule().withAddedPackages(packages1.get()).createNow(),
				ContainersPackage.Literals.MODULE__PACKAGES);
	}

	@Test
	public void testTargets() {
		this.testSimilarity(getAPI().newModule().withAddedTarget(target1.get()).createNow(),
				getAPI().newModule().withAddedTarget(target2.get()).createNow(),
				ContainersPackage.Literals.MODULE__TARGET);
	}

	@Test
	public void testTargetsSize() {
		this.testSimilarity(
				getAPI().newModule().withAddedTarget(new ModuleDirective[] { target1.get(), target2.get() })
						.createNow(),
				getAPI().newModule().withAddedTarget(target1.get()).createNow(),
				ContainersPackage.Literals.MODULE__TARGET);
	}

	@Test
	public void testTargetsNullCheck() {
		this.testSimilarityNullCheck(getAPI().newModule().withAddedTarget(target1.get()).createNow(),
				ContainersPackage.Literals.MODULE__TARGET);
	}
}
