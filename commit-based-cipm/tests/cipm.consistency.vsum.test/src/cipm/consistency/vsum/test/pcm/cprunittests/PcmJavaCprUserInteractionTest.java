package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceEntry;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyDistributionConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyNameConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;
import mir.reactions.dummyPCMJavaUserInteractionCPRs.DummyPCMJavaUserInteractionCPRsChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.correspondence.CorrespondenceFactory;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;

public class PcmJavaCprUserInteractionTest extends AbstractPcmJavaCprTest {
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaUserInteractionCPRsChangePropagationSpecification());
		return list;
	}

	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_Manual() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.userInteractionTestInterfaceName;
		final var pcmInterface = new Interface[1];
		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			pcmInterface[0] = RepositoryFactory.eINSTANCE.createOperationInterface();
			pcmInterface[0].setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(pcmInterface[0]);
		});

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
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(pcmInterface[0],
						javaInterface.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME), true),
				javaInterface.eGet(javaInterface.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME)));

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

	@Test
	public void testJavaPCMUserInteraction_Intercepted() {

		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.userInteractionTestInterfaceName;

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		final var createdInterface = new OperationInterface[1];

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			createdInterface[0] = RepositoryFactory.eINSTANCE.createOperationInterface();
			createdInterface[0].setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(createdInterface[0]);
		});

		var mockedUserInput = "abc";
		PcmUserInteractionManager.addConflictResolutionStrategy(
				new DummyNameConflictResolutionStrategy(createdInterface[0], mockedUserInput));

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
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(createdInterface[0],
						javaInterface.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME), true),
				javaInterface.eGet(javaInterface.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME)));

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

	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution() {
		//
		// Setup of PCM
		//

		this.getPcmVsumFacade().propagateResource(this.getResourceFromPcmFacade(repositoryFileName).getURI(), (r) -> {
			var repoObj = (Repository) r.getContents().get(0);

			var cmpToBeDeleted = RepositoryFactory.eINSTANCE.createBasicComponent();
			cmpToBeDeleted.setEntityName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName);
			repoObj.getComponents__Repository().add(cmpToBeDeleted);

			var cmpToPersistOne = RepositoryFactory.eINSTANCE.createBasicComponent();
			cmpToPersistOne
					.setEntityName(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentOneName);
			repoObj.getComponents__Repository().add(cmpToPersistOne);

			var cmpToPersistTwo = RepositoryFactory.eINSTANCE.createBasicComponent();
			cmpToPersistTwo
					.setEntityName(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentTwoName);
			repoObj.getComponents__Repository().add(cmpToPersistTwo);
		});

//		var pcmRes = this.getResourceFromPcmFacade(repositoryFileName);
//		var repoObj = (Repository) pcmRes.getContents().get(0);
//
//		var cmpToBeDeleted = RepositoryFactory.eINSTANCE.createBasicComponent();
//		cmpToBeDeleted.setEntityName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName);
//		repoObj.getComponents__Repository().add(cmpToBeDeleted);
//
//		var cmpToPersistOne = RepositoryFactory.eINSTANCE.createBasicComponent();
//		cmpToPersistOne.setEntityName(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentOneName);
//		repoObj.getComponents__Repository().add(cmpToPersistOne);
//
//		var cmpToPersistTwo = RepositoryFactory.eINSTANCE.createBasicComponent();
//		cmpToPersistTwo.setEntityName(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentTwoName);
//		repoObj.getComponents__Repository().add(cmpToPersistTwo);
//		this.getPcmFacade().saveToDisk();

		//
		// Setup of Java
		//
		var javaResource = this.getJavaModelResourceFromJavaFacade();
		var cls1 = ClassifiersFactory.eINSTANCE.createClass();
		cls1.setName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassOneName);
		javaResource.getContents().add(cls1);

		var cls2 = ClassifiersFactory.eINSTANCE.createClass();
		cls2.setName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassTwoName);
		javaResource.getContents().add(cls2);
		this.getJavaFacade().saveToDisk();

		//
		// Reload VSUM and re-locate resources and their content
		//

		var pcmRes = this.getResourceFromPcmFacade(repositoryFileName);
		var repoObj = (Repository) pcmRes.getContents().get(0);
		var cmpToBeDeleted = (BasicComponent) repoObj.getComponents__Repository().stream()
				.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
						.equals(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName))
				.findFirst().get();
		var cmpToPersistOne = (BasicComponent) repoObj.getComponents__Repository().stream()
				.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
						.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentOneName))
				.findFirst().get();
		var cmpToPersistTwo = (BasicComponent) repoObj.getComponents__Repository().stream()
				.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
						.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentTwoName))
				.findFirst().get();

//		javaResource = this.getJavaModelResourceFromJavaFacade();
//		cls1 = (org.emftext.language.java.classifiers.Class) javaResource.getContents().stream()
//				.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
//						&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
//								PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassOneName))
//				.findFirst().get();
//		cls2 = (org.emftext.language.java.classifiers.Class) javaResource.getContents().stream()
//				.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
//						&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
//								PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassTwoName))
//				.findFirst().get();

		//
		// Setup of correspondences
		//
		var corView = this.getPcmVsumFacade().getCorrespondenceView().getEditableView(ReactionsCorrespondence.class,
				() -> {
					return CorrespondenceFactory.eINSTANCE.createReactionsCorrespondence();
				});
		corView.addCorrespondenceBetween(cls1, cmpToBeDeleted, "");
		corView.addCorrespondenceBetween(cls2, cmpToBeDeleted, "");
		this.getPcmVsumFacade().saveCorrespondences();

		//
		// Conflict resolution strategy setup
		//
		List<CorrespondenceEntry> correspondences = List.of(new CorrespondenceEntry(cmpToPersistOne, cls1, ""),
				new CorrespondenceEntry(cmpToPersistTwo, cls2, ""));

		PcmUserInteractionManager.addConflictResolutionStrategy(
				new DummyDistributionConflictResolutionStrategy(cmpToBeDeleted, correspondences));

		//
		// Actual test
		//
		pcmRes = this.getResourceFromPcmFacade(repositoryFileName);
		var changes = this.getEChangesFor(pcmRes, (r) -> {
			var rRepoObj = (Repository) r.getContents().get(0);
			var cmpToDel = rRepoObj.getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName))
					.findFirst().get();
			rRepoObj.getComponents__Repository().remove(cmpToDel);
		});

		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(pcmRes);
		Assertions.assertNull(prop.getException());
		this.logPropagatedChanges(prop);

		// Correspondence tests
		var postPropCorView = this.getPcmVsumFacade().getCorrespondenceView();
		for (var cor : correspondences) {
			var cmp = cor.getKnownElement();
			var tag = cor.getTag();

			var cmpCorrespondents = postPropCorView.getCorrespondingEObjects(cmp, tag);
			Assertions.assertEquals(1, cmpCorrespondents.size());
			var cls = cor.getCorrespondentsForKnownElement().iterator().next();
			Assertions.assertTrue(EcoreUtil.equals(cls, cmpCorrespondents.iterator().next()));

			var clsCorrespondents = postPropCorView.getCorrespondingEObjects(cls, tag);
			Assertions.assertEquals(1, clsCorrespondents.size());
			Assertions.assertTrue(EcoreUtil.equals(cmp, clsCorrespondents.iterator().next()));
		}
	}
}
