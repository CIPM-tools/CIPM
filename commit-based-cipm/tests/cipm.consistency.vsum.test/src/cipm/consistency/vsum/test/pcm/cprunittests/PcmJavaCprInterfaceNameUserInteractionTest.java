package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyNameConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;
import mir.reactions.dummyPCMJavaUserInteractionCPRs.DummyPCMJavaUserInteractionCPRsChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmJavaCprInterfaceNameUserInteractionTest extends AbstractPcmJavaCprTest {
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaUserInteractionCPRsChangePropagationSpecification());
		return list;
	}

	public void javaInterfaceNameTest(Function<EObject, ConflictResolutionStrategy[]> createdInterfaceToStratFunc,
			String expectedJavaInterfaceName) {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
		// Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.userInteractionTestPCMInterfaceName;
		final var createdInterface = new Interface[1];
		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			createdInterface[0] = RepositoryFactory.eINSTANCE.createOperationInterface();
			createdInterface[0].setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(createdInterface[0]);
		});

		if (createdInterfaceToStratFunc != null) {
			for (var s : createdInterfaceToStratFunc.apply(createdInterface[0])) {
				PcmUserInteractionManager.addConflictResolutionStrategy(s);
			}
		}

		this.propagateChangesToResource(originalRepoRes, changes);

		// Ensure that the Resource is saved after changes are applied
		// Ensure that the loaded Resource has the expected contents
		// Ensure that the change was actually applied to PCM
		var propagatedResource = this.getResourceFromPcmFacade(repositoryFileName);
		var loadedResource = this.loadNewResourceInstance(propagatedResource);
		PcmCprAssertions.assertForAllEqualResources((r) -> {
			Assertions.assertEquals(1, r.getContents().size());
			var rRepoEObj = r.getContents().get(0);
			Assertions.assertEquals(1, rRepoEObj.eContents().size());
			var rRepoInterface = rRepoEObj.eContents().get(0);
			PcmCprAssertions.assertFeatureValueEquals(rRepoInterface, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
					pcmInterfaceName);
		}, propagatedResource, loadedResource);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaInterface = javaResource.getContents().get(0);

		if (expectedJavaInterfaceName == null) {
			PcmCprAssertions.assertFeatureValueInPcmManagerConsistent(createdInterface[0], javaInterface,
					CommonsPackage.Literals.NAMED_ELEMENT__NAME);
		} else {
			PcmCprAssertions.assertFeatureValueSetViaPcmManager(createdInterface[0], javaInterface,
					CommonsPackage.Literals.NAMED_ELEMENT__NAME, expectedJavaInterfaceName);
		}

		// Ensure that correspondences are persistent
		var persistedPcmI = this.getResourceFromPcmFacade(repositoryFileName).getContents().get(0).eContents().get(0);
		var persistedJavaI = this.getJavaModelResourceFromJavaFacade().getContents().get(0);
		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedPcmI, persistedJavaI,
				"");
	}

	@Test
	public void testJavaPCMUserInteraction_InterfaceName_Intercepted() {
		this.javaInterfaceNameTest((te) -> {
			return new ConflictResolutionStrategy[] { new DummyNameConflictResolutionStrategy(te,
					PcmCPRTestConstants.userInteractionTestJavaInterfaceName) };
		}, PcmCPRTestConstants.userInteractionTestJavaInterfaceName);
	}

	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_InterfaceName_Manual() {
		this.javaInterfaceNameTest(null, null);
	}
}
