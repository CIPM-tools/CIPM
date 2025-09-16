package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.userinteraction.DummyNameConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyNamespaceConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;
import mir.reactions.dummyPCMJavaUserInteractionCPRs.DummyPCMJavaUserInteractionCPRsChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmJavaCprUserInteractionTest extends AbstractPcmJavaCprTest {
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaUserInteractionCPRsChangePropagationSpecification());
		return list;
	}

//	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_ModuleQualifiedName_Manual() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();

		final var pcmCmpName = PcmCPRTestConstants.namespaceTestComponentName;

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		final var createdCmp = new BasicComponent[1];

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			createdCmp[0] = RepositoryFactory.eINSTANCE.createBasicComponent();
			createdCmp[0].setEntityName(pcmCmpName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getComponents__Repository().add(createdCmp[0]);
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
		var propagatedRepoCmp = propagatedRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(propagatedRepoCmp, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
				pcmCmpName);

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoCmp = resRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(resRepoCmp, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
				pcmCmpName);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoCmp, propagatedRepoCmp));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaMod = javaResource.getContents().get(0);
		PcmCprAssertions.assertActualFeatureValueAndPcmManagerConsistent(createdCmp[0], javaMod,
				CommonsPackage.Literals.NAMED_ELEMENT__NAME);
		PcmCprAssertions.assertActualFeatureValueAndPcmManagerConsistent(createdCmp[0], javaMod,
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);

		// Ensure that correspondences are persistent
		var persistedPcmCmp = this.getResourceFromPcmFacade(repositoryFileName).getContents().get(0).eContents().get(0);
		var persistedJavaMod = this.getJavaModelResourceFromJavaFacade().getContents().get(0);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedPcmCmp).size());
		var javaCorrespondent = this.getPcmVsumFacade().getCorrespondenceView()
				.getCorrespondingEObjects(persistedPcmCmp).iterator().next();
		Assertions.assertEquals(persistedJavaMod, javaCorrespondent);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedJavaMod).size());
		var pcmCorrespondent = this.getPcmVsumFacade().getCorrespondenceView()
				.getCorrespondingEObjects(persistedJavaMod).iterator().next();
		Assertions.assertEquals(persistedPcmCmp, pcmCorrespondent);
	}

	@Test
	public void testJavaPCMUserInteraction_ModuleQualifiedName_Intercepted() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();

		final var pcmCmpName = PcmCPRTestConstants.namespaceTestComponentName;

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		final var createdCmp = new BasicComponent[1];

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			createdCmp[0] = RepositoryFactory.eINSTANCE.createBasicComponent();
			createdCmp[0].setEntityName(pcmCmpName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getComponents__Repository().add(createdCmp[0]);
		});

		PcmUserInteractionManager.addConflictResolutionStrategy(new DummyNameConflictResolutionStrategy(createdCmp[0],
				PcmCPRTestConstants.namespaceTestComponentModuleName));

		PcmUserInteractionManager.addConflictResolutionStrategy(new DummyNamespaceConflictResolutionStrategy(
				createdCmp[0], PcmCPRTestConstants.namespaceTestComponentModuleNamespaces));

		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(originalRepoRes);
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
		var propagatedRepoCmp = propagatedRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(propagatedRepoCmp, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
				pcmCmpName);

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoCmp = resRepoEObj.eContents().get(0);
		PcmCprAssertions.assertFeatureValueEquals(resRepoCmp, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
				pcmCmpName);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoCmp, propagatedRepoCmp));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaMod = javaResource.getContents().get(0);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(createdCmp[0],
				CommonsPackage.Literals.NAMED_ELEMENT__NAME, PcmCPRTestConstants.namespaceTestComponentModuleName);
		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(createdCmp[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES,
				PcmCPRTestConstants.namespaceTestComponentModuleNamespaces);
		PcmCprAssertions.assertActualFeatureValueAndPcmManagerConsistent(createdCmp[0], javaMod,
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);

		// Ensure that correspondences are persistent
		var persistedPcmCmp = this.getResourceFromPcmFacade(repositoryFileName).getContents().get(0).eContents().get(0);
		var persistedJavaMod = this.getJavaModelResourceFromJavaFacade().getContents().get(0);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedPcmCmp).size());
		var javaCorrespondent = this.getPcmVsumFacade().getCorrespondenceView()
				.getCorrespondingEObjects(persistedPcmCmp).iterator().next();
		Assertions.assertEquals(persistedJavaMod, javaCorrespondent);
		Assertions.assertEquals(1,
				this.getPcmVsumFacade().getCorrespondenceView().getCorrespondingEObjects(persistedJavaMod).size());
		var pcmCorrespondent = this.getPcmVsumFacade().getCorrespondenceView()
				.getCorrespondingEObjects(persistedJavaMod).iterator().next();
		Assertions.assertEquals(persistedPcmCmp, pcmCorrespondent);
	}
}
