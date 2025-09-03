package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.HashMap;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;

public class PcmJavaCprCorrespondenceTest extends AbstractPcmJavaCprTest {
	@Test
	public void testJavaPCMCorrespondence() {
		var javaResource = this.getJavaModelResource();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = "pcmifc";
		final var pcmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
		pcmInterface.setEntityName(pcmInterfaceName);
		var mods = new HashMap<URI, Consumer<Resource>>();
		var repoRes = this.getResourceFromPcmFacade(repositoryFileName);
		mods.put(repoRes.getURI(), (r) -> {
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(pcmInterface);
		});
//		var propList = this.getPcmVsumFacade().modifyEObjects(getPcmFacade(), mods);

		var newRes = this.getNewInstanceForResourceFromPcmFacade(repositoryFileName);
		mods.get(newRes.getURI()).accept(newRes);
		var d = new DefaultStateBasedChangeResolutionStrategy();
		var changes = d.getChangeSequenceBetween(newRes, repoRes);
		this.getPcmVsumFacade().addChanges(changes.getEChanges());
		var prop = this.getPcmVsumFacade().propagateResource(repoRes);

//		Assertions.assertEquals(1, propList.size());
//		var prop = propList.iterator().next();
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
	}
}
