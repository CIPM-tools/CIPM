package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.vsum.test.pcm.cprunittests.dummy.PcmCprAssertions;
import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceInputConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.FeatureInputConflictResolutionStrategy;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class InterfaceTest extends AbstractClassifierTest {
	public void interfaceCreationTestTemplate(Supplier<OperationInterface> opIfcFac,
			Function<OperationInterface, ConflictResolutionStrategy[]> createdOpIfcToStratFunc, String opIfcName,
			List<String> expectedJavaIfcNss) {

		var javaResource = this.getJavaModelResourceFromJavaFacade();

		final var opIfc = new OperationInterface[1];

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			var ifc = opIfcFac.get();
			ifc.setEntityName(opIfcName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(ifc);
			opIfc[0] = ifc;
		});

		var opIfcFragment = opIfc[0].eResource().getURIFragment(opIfc[0]);

		if (createdOpIfcToStratFunc != null) {
			var strats = createdOpIfcToStratFunc.apply(opIfc[0]);
			for (var strat : strats) {
				PcmUserInteractionManager.addConflictResolutionStrategy(strat);
			}
		}

		this.propagateChangesToResource(originalRepoRes, changes);

		var propagatedRepoResource = this.getResourceFromPcmFacade(repositoryFileName);
		var loadedRepoResource = this.loadNewResourceInstance(propagatedRepoResource);

		// Ensure that the change was actually applied to PCM
		// Ensure that the Resource is saved after changes are applied
		// Ensure that the loaded Resource has the expected contents
		PcmCprAssertions.assertForAllEqualResources((r) -> {
			var rOpIfc = r.getEObject(opIfcFragment);
			PcmCprAssertions.assertFeatureValueEquals(rOpIfc, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
					opIfcName);
		}, originalRepoRes, propagatedRepoResource, loadedRepoResource);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		var persistedJavaResource = this.getJavaModelResourceFromJavaFacade();
		var persistedOpIfc = propagatedRepoResource.getEObject(opIfcFragment);
		var persistedJavaClsCU = persistedJavaResource.getContents().stream()
				.filter((c) -> (c instanceof CompilationUnit)
						&& PcmCprAssertions.namespacesEqual(((CompilationUnit) c).getNamespaces(), expectedJavaIfcNss))
				.map((c) -> ((CompilationUnit) c)).findFirst();
		Assertions.assertTrue(persistedJavaClsCU.isPresent());
		Assertions.assertEquals(1, persistedJavaClsCU.get().getClassifiers().size());
		var persistedJavaIfc = persistedJavaClsCU.get().getClassifiers().get(0);
		Assertions.assertEquals(opIfcName, persistedJavaIfc.getName());

		PcmCprAssertions.assertForAllEqualResources((r) -> PcmCprAssertions.assertJavaClassLayoutCorrect(r),
				javaResource, persistedJavaResource);

		// Ensure that correspondences are persistent
		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedOpIfc, persistedJavaIfc,
				"");
	}

	@Test
	public void withoutExistingJavaInterface() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");

		final var opIfc = new OperationInterface[1];
		this.interfaceCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createOperationInterface(), (pcmIfc) -> {
			opIfc[0] = pcmIfc;
			return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(pcmIfc), null,
					List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
		}, dtName, nss);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(opIfc[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);
	}

	@Test
	public void withExistingJavaInterface() {
		var listCls = List.class;
		var ifcName = listCls.getSimpleName();
		var nss = List.of(listCls.getPackageName().split(namespaceSeparatorRegex));

		this.removePlaceholderInJavaModelResource();
		this.addInterfaceToJavaModelResource(JavaModelAccess.getJavaModel(), ifcName, nss);

		saveAndReloadJavaModelResource();

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.interfaceCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createOperationInterface(), null, ifcName,
				nss);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
		PcmCprAssertions.assertNoFeaturesInPcmManager();
	}

	@Test
	public void withMultipleExistingJavaInterfaces() {
		var listCls = List.class;
		var ifcName = listCls.getSimpleName();
		var extraNs = "special";
		var listPacNss = List.of(listCls.getPackageName().split(namespaceSeparatorRegex));
		var nss1 = List.copyOf(listPacNss);
		var nss2 = new ArrayList<>(listPacNss);
		nss2.remove(nss2.size() - 1);
		nss2.add(extraNs);

		this.removePlaceholderInJavaModelResource();

		// Add Java clss to a resource, so that their URI fragments can be found

		var ifc1 = this.addInterfaceToJavaModelResource(JavaModelAccess.getJavaModel(), ifcName, nss1);
		var ifc2 = this.addInterfaceToJavaModelResource(JavaModelAccess.getJavaModel(), ifcName, nss2);

		var ifc1Fragment = ifc1.eResource().getURIFragment(ifc1);
		var ifc2Fragment = ifc2.eResource().getURIFragment(ifc2);

		saveAndReloadJavaModelResource();

		final var ifc1Final = (org.emftext.language.java.classifiers.Interface) JavaModelAccess.getJavaModel()
				.getEObject(ifc1Fragment);
		final var ifc2Final = (org.emftext.language.java.classifiers.Interface) JavaModelAccess.getJavaModel()
				.getEObject(ifc2Fragment);

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.interfaceCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createOperationInterface(),
				(opIfc) -> new ConflictResolutionStrategy[] {
						new CorrespondenceInputConflictResolutionStrategy(List.of(opIfc), List.of(ifc1Final, ifc2Final),
								Map.of(opIfc.eResource().getURIFragment(opIfc), List.of(ifc2Fragment))) },
				ifcName, nss2);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
	}

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
	}
}
