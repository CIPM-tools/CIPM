package cipm.consistency.vsum.test.pcm.cprunittests;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

public class PcmJavaCprCorrespondenceTest extends AbstractPcmJavaCprTest {
	@Test
	public void testJavaPCMCorrespondence() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = "pcmifc";

		var priginalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(priginalRepoRes, (r) -> {
			final var pcmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			pcmInterface.setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(pcmInterface);
		});

		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(priginalRepoRes);
		Assertions.assertNull(prop.getException());
		this.logPropagatedChanges(prop);

//		Assertions.assertEquals(1, prop.getOriginalChangeCount());
//		var change = prop.getChanges().get(0).getOriginalChange().getEChanges().get(0);
//		Assertions.assertTrue(ReplaceSingleValuedEAttribute.class.isAssignableFrom(change.getClass()));

		// Ensure that the change was actually applied to PCM
		var propagatedResource = this.getResourceFromPcmFacade(repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(1, propagatedRepoEObj.eContents().size());
		var propagatedRepoInterface = propagatedRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmInterfaceName, propagatedRepoInterface.eGet(
				propagatedRepoInterface.eClass().getEStructuralFeature(RepositoryPackage.INTERFACE__ENTITY_NAME)));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoInterface = resRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmInterfaceName, resRepoInterface
				.eGet(resRepoInterface.eClass().getEStructuralFeature(RepositoryPackage.INTERFACE__ENTITY_NAME)));
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoInterface, propagatedRepoInterface));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaInterface = javaResource.getContents().get(0);
		Assertions.assertEquals(pcmInterfaceName,
				javaInterface.eGet(javaInterface.eClass().getEStructuralFeature(ClassifiersPackage.INTERFACE__NAME)));

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
