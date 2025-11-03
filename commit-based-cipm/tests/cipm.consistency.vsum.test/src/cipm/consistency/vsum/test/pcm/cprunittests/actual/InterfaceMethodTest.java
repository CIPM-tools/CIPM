package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;
import java.util.function.Supplier;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.cpr.pcmjava.userinteraction.FeatureInputConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.vsum.test.pcm.cprunittests.dummy.PcmCprAssertions;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class InterfaceMethodTest extends AbstractClassifierTest {
	private static final String opIfcName = "opIfcName";
	private static final List<String> javaIfcNss = List.of("ns1", "ns2");

	public void interfaceMethodCreationTestTemplate(Supplier<OperationSignature> opIfcSigFac) {
		var javaResource = this.getJavaModelResourceFromJavaFacade();

		final var opIfc = new OperationInterface[1];
		final var opIfcSig = new OperationSignature[1];

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			var ifcSig = opIfcSigFac.get();
			var ifc = RepositoryFactory.eINSTANCE.createOperationInterface();
			ifc.getSignatures__OperationInterface().add(ifcSig);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(ifc);
			opIfc[0] = ifc;
			opIfcSig[0] = ifcSig;
		});

		var opIfcFragment = opIfc[0].eResource().getURIFragment(opIfc[0]);
		var opIfcSigFragment = opIfcSig[0].eResource().getURIFragment(opIfcSig[0]);

		PcmUserInteractionManager
				.addConflictResolutionStrategy(new FeatureInputConflictResolutionStrategy(List.of(opIfc[0]), null,
						List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), javaIfcNss));

		this.propagateChangesToResource(originalRepoRes, changes);

		var propagatedRepoResource = this.getResourceFromPcmFacade(repositoryFileName);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		var persistedJavaResource = this.getJavaModelResourceFromJavaFacade();
		var persistedOpIfc = propagatedRepoResource.getEObject(opIfcFragment);
		var persistedOpSig = (OperationSignature) propagatedRepoResource.getEObject(opIfcSigFragment);
		var persistedJavaClsCU = persistedJavaResource.getContents().stream()
				.filter((c) -> (c instanceof CompilationUnit)
						&& PcmCprAssertions.namespacesEqual(((CompilationUnit) c).getNamespaces(), javaIfcNss))
				.map((c) -> ((CompilationUnit) c)).findFirst();
		Assertions.assertTrue(persistedJavaClsCU.isPresent());
		Assertions.assertEquals(1, persistedJavaClsCU.get().getClassifiers().size());

		PcmCprAssertions.assertForAllEqualResources((r) -> PcmCprAssertions.assertJavaClassLayoutCorrect(r),
				javaResource, persistedJavaResource);

		// TODO Ensure that the corresponding Java interface method is generated

		// TODO Ensure that a method stub for the generated Java interface method is
		// present in each directly implementing class (abstract classes count for
		// convenience)

		// TODO Ensure that the generated Java interface method signature matches with
		// that of the operation signature in PCM (parameters and return type)

		// TODO Ensure that correspondences are persistent
	}

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
	}
}
