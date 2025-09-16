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

		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(originalRepoRes);
		Assertions.assertNull(prop.getException());
		this.logPropagatedChanges(prop);

		// Ensure that the change was actually applied to PCM
		var propagatedResource = this.getResourceFromPcmFacade(repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(1, propagatedRepoEObj.eContents().size());
		var propagatedRepoInterface = propagatedRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(propagatedRepoInterface,
				EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME, pcmInterfaceName);

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoInterface = resRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(resRepoInterface, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
				pcmInterfaceName);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoInterface, propagatedRepoInterface));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaInterface = javaResource.getContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(javaInterface, CommonsPackage.Literals.NAMED_ELEMENT__NAME,
				pcmInterfaceName);

		// Ensure that correspondences are persistent
		var persistedPcmI = this.getResourceFromPcmFacade(repositoryFileName).getContents().get(0).eContents().get(0);
		var persistedJavaI = this.getJavaModelResourceFromJavaFacade().getContents().get(0);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedPcmI).size());
		var javaCorrespondent = this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedPcmI)
				.iterator().next();
		Assertions.assertEquals(persistedJavaI, javaCorrespondent);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedJavaI).size());
		var pcmCorrespondent = this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedJavaI)
				.iterator().next();
		Assertions.assertEquals(persistedPcmI, pcmCorrespondent);
	}
}
