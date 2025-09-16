package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import mir.reactions.dummyPCMJavaCorrespondenceCPRs.DummyPCMJavaCorrespondenceCPRsChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmJavaCprCorrespondenceTest extends AbstractPcmJavaCprTest {
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaCorrespondenceCPRsChangePropagationSpecification());
		return list;
	}

	@Test
	public void testJavaPCMCorrespondence() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.correspondenceTestPCMInterfaceName;

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			final var pcmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			pcmInterface.setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(pcmInterface);
		});

		this.propagateChangesToResource(originalRepoRes, changes);

		var propagatedRepoResource = this.getResourceFromPcmFacade(repositoryFileName);
		var loadedRepoResource = this.loadNewResourceInstance(propagatedRepoResource);

		// Ensure that the change was actually applied to PCM
		// Ensure that the Resource is saved after changes are applied
		// Ensure that the loaded Resource has the expected contents
		PcmCprAssertions.assertForAllEqualResources((r) -> {
			Assertions.assertEquals(1, r.getContents().size());
			var rRepoEObj = r.getContents().get(0);
			Assertions.assertEquals(1, rRepoEObj.eContents().size());
			var rRepoInterface = rRepoEObj.eContents().get(0);
			PcmCprAssertions.assertFeatureValueEquals(rRepoInterface, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
					pcmInterfaceName);
		}, originalRepoRes, propagatedRepoResource, loadedRepoResource);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		var persistedJavaResource = this.getJavaModelResourceFromJavaFacade();

		PcmCprAssertions.assertForAllEqualResources((r) -> {
			Assertions.assertEquals(1, r.getContents().size());
			var rInterface = r.getContents().get(0);
			PcmCprAssertions.assertFeatureValueEquals(rInterface, CommonsPackage.Literals.NAMED_ELEMENT__NAME,
					pcmInterfaceName);
		}, javaResource, persistedJavaResource);

		// Ensure that correspondences are persistent
		var persistedPcmI = propagatedRepoResource.getContents().get(0).eContents().get(0);
		var persistedJavaI = persistedJavaResource.getContents().get(0);

		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedPcmI, persistedJavaI,
				"");
	}
}
