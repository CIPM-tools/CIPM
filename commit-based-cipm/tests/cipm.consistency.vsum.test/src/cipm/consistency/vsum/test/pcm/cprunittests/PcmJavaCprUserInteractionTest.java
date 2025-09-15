package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.ContainersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceEntry;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyDistributionConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyNameConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyNamespaceConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;
import mir.reactions.dummyPCMJavaUserInteractionCPRs.DummyPCMJavaUserInteractionCPRsChangePropagationSpecification;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;
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
	public void testJavaPCMUserInteraction_InterfaceName_Manual() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.userInteractionTestPCMInterfaceName;
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
		Assertions.assertEquals(pcmInterfaceName,
				propagatedRepoInterface.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoInterface = resRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmInterfaceName,
				resRepoInterface.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoInterface, propagatedRepoInterface));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaInterface = javaResource.getContents().get(0);
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(pcmInterface[0],
						CommonsPackage.Literals.NAMED_ELEMENT__NAME, false),
				javaInterface.eGet(CommonsPackage.Literals.NAMED_ELEMENT__NAME));

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
	public void testJavaPCMUserInteraction_InterfaceName_Intercepted() {

		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var pcmInterfaceName = PcmCPRTestConstants.userInteractionTestPCMInterfaceName;

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		final var createdInterface = new OperationInterface[1];

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			createdInterface[0] = RepositoryFactory.eINSTANCE.createOperationInterface();
			createdInterface[0].setEntityName(pcmInterfaceName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getInterfaces__Repository().add(createdInterface[0]);
		});

		PcmUserInteractionManager.addConflictResolutionStrategy(new DummyNameConflictResolutionStrategy(
				createdInterface[0], PcmCPRTestConstants.userInteractionTestJavaInterfaceName));

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
		Assertions.assertEquals(pcmInterfaceName,
				propagatedRepoInterface.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoInterface = resRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmInterfaceName,
				resRepoInterface.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoInterface, propagatedRepoInterface));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaInterface = javaResource.getContents().get(0);
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(createdInterface[0],
						CommonsPackage.Literals.NAMED_ELEMENT__NAME, false),
				PcmCPRTestConstants.userInteractionTestJavaInterfaceName);

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

	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution_Manual() {
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

		//
		// Setup of correspondences
		//
		var corView = this.getPcmVsumFacade().getCorrespondenceView().getEditableView(ReactionsCorrespondence.class,
				() -> {
					return CorrespondenceFactory.eINSTANCE.createReactionsCorrespondence();
				});
		corView.addCorrespondenceBetween(cls1, cmpToBeDeleted, "");
		corView.addCorrespondenceBetween(cls2, cmpToBeDeleted, "");

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
		var tag = "";
		var postPropCorView = this.getPcmVsumFacade().getCorrespondenceView();
		var allObjs = List.of(cmpToPersistOne, cmpToPersistTwo, cls1, cls2);
		var corObjSet = new HashSet<EObject>();
		var correspondentList = new ArrayList<CorrespondenceEntry>();
		for (var obj : allObjs) {
			corObjSet.addAll(postPropCorView.getCorrespondingEObjects(obj));
			correspondentList.add(new CorrespondenceEntry(obj, postPropCorView.getCorrespondingEObjects(obj), tag));
		}

		/**
		 * There should be at least 3 correspondences in the correspondence view at the
		 * end of the interaction, because cls1 and cls2 each have to correspond to at
		 * least one component (same component is allowed). Hence there should be at
		 * least 3 corresponding EObjects.
		 * 
		 * This assertion ensures that these correspondences have been added to the
		 * correspondence view, so that the follow-up assertions work as intended.
		 */
		Assertions.assertTrue(corObjSet.size() >= 3);
		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, correspondentList);
	}

	/**
	 * Ensures that there is only one EObject equal to seekEqualFor in the given
	 * col.
	 */
	private void assertContainsOnlyOneEqualEObject(EObject seekEqualFor, Collection<EObject> col) {
		var matches = col.stream().filter((o) -> EcoreUtil.equals(seekEqualFor, o)).toArray(EObject[]::new);
		if (matches.length == 0) {
			Assertions.fail("Given col contains no equal EObject");
		} else if (matches.length > 1) {
			Assertions.fail("Given col contains multiple equal EObjects");
		}
	}

	private void assertContainsEqualEObjects(Collection<EObject> col1, Collection<EObject> col2) {
		Assertions.assertEquals(col1.size(), col2.size());
		for (var obj1 : col1) {
			assertContainsOnlyOneEqualEObject(obj1, col2);
		}
		for (var obj2 : col2) {
			assertContainsOnlyOneEqualEObject(obj2, col1);
		}
	}

	private void assertCorrespondenceViewAndPcmManagerConsistent(CorrespondenceModelView<?> postPropCorView,
			EObject knownSide, String tag) {
		var knownSidePcmManagerCorEntry = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, tag, false);
		var knownSideCorView = this.getPcmVsumFacade().getCorrespondenceView();
		if (knownSidePcmManagerCorEntry == null) {
			Assertions.assertTrue(knownSideCorView.getCorrespondingEObjects(knownSide, tag).isEmpty());
			return;
		}

		var knownSidePcmManagerCors = knownSidePcmManagerCorEntry.getCorrespondentsForKnownElement();
		var knownSideCorViewCors = knownSideCorView.getCorrespondingEObjects(knownSide);
		assertContainsEqualEObjects(knownSideCorViewCors, knownSidePcmManagerCors);
	}

	private void assertNoCorrespondencesSaved() {
		var correspondencesURI = this.getPcmVsumFacade().getDirLayout().getVsumCorrespondenceModelUri();
		var correspondencesPath = this.getPcmVsumFacade().getDirLayout().getVsumCorrespondenceModelPath();

		// No correspondence file => No correspondences saved
		if (!correspondencesPath.toFile().exists())
			return;

		var corRes = new ResourceSetImpl().createResource(correspondencesURI);
		try {
			corRes.load(null);
		} catch (IOException e) {
			Assertions.fail(e);
		}

		Assertions.assertEquals(0, corRes.getContents().size());
	}

	private void assertNoCorrespondencesInPcmManager() {
		Assertions.assertEquals(0, PcmUserInteractionManager.getAllCompleteCorrespondences());
	}

	private void assertCorrespondenceViewAndPcmManagerConsistent(CorrespondenceModelView<?> postPropCorView,
			Collection<CorrespondenceEntry> cors) {
		if (cors.isEmpty()) {
			assertNoCorrespondencesSaved();
			assertNoCorrespondencesInPcmManager();
			return;
		}

		for (var corEntry : cors) {
			var knownSide = corEntry.getKnownElement();
			var correspondents = corEntry.getCorrespondentsForKnownElement();
			var tag = corEntry.getTag();

			var knownSidePcmManagerCorEntry = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, tag, false);
			var knownSideCorViewCors = this.getPcmVsumFacade().getCorrespondenceView()
					.getCorrespondingEObjects(knownSide);

			if (knownSidePcmManagerCorEntry == null) {
				Assertions.assertTrue(knownSideCorViewCors.isEmpty());
				Assertions.assertTrue(correspondents.isEmpty());
				continue;
			}

			var knownSidePcmManagerCors = knownSidePcmManagerCorEntry.getCorrespondentsForKnownElement();
			Assertions.assertEquals(correspondents.size(), knownSidePcmManagerCors.size());
			Assertions.assertEquals(correspondents.size(), knownSideCorViewCors.size());
			assertContainsEqualEObjects(knownSideCorViewCors, knownSidePcmManagerCors);

			assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, knownSide, tag);
		}
	}

	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution_Intercepted() {
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
//		for (var cor : correspondences) {
//			var cmp = cor.getKnownElement();
//			var tag = cor.getTag();
//
//			var cmpCorrespondents = postPropCorView.getCorrespondingEObjects(cmp, tag);
//			Assertions.assertEquals(1, cmpCorrespondents.size());
//			var cls = cor.getCorrespondentsForKnownElement().iterator().next();
//			Assertions.assertTrue(EcoreUtil.equals(cls, cmpCorrespondents.iterator().next()));
//
//			var clsCorrespondents = postPropCorView.getCorrespondingEObjects(cls, tag);
//			Assertions.assertEquals(1, clsCorrespondents.size());
//			Assertions.assertTrue(EcoreUtil.equals(cmp, clsCorrespondents.iterator().next()));
//		}

		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, correspondences);

//		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, cmpToBeDeleted, tag);
//		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, cmpToPersistOne, tag);
//		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, cmpToPersistTwo, tag);
//		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, cls1, tag);
//		assertCorrespondenceViewAndPcmManagerConsistent(postPropCorView, cls2, tag);
	}

	@Disabled("Enable if manual user interaction is to be tested")
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

//		Assertions.assertEquals(1, prop.getOriginalChangeCount());
//		var change = prop.getChanges().get(0).getOriginalChange().getEChanges().get(0);
//		Assertions.assertTrue(ReplaceSingleValuedEAttribute.class.isAssignableFrom(change.getClass()));

		// Ensure that the change was actually applied to PCM
		var propagatedResource = this.getResourceFromPcmFacade(repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(1, propagatedRepoEObj.eContents().size());
		var propagatedRepoCmp = propagatedRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmCmpName, propagatedRepoCmp.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoCmp = resRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmCmpName, resRepoCmp.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoCmp, propagatedRepoCmp));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaMod = javaResource.getContents().get(0);
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(createdCmp[0],
						CommonsPackage.Literals.NAMED_ELEMENT__NAME, false),
				javaMod.eGet(CommonsPackage.Literals.NAMED_ELEMENT__NAME));

		var inputNss = (List) PcmUserInteractionManager.getDesiredFeatureValue(createdCmp[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, false);
		var javaModNss = (List) javaMod.eGet(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
		Assertions.assertTrue(inputNss.size() == javaModNss.size() && inputNss.containsAll(javaModNss));

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
		Assertions.assertEquals(pcmCmpName, propagatedRepoCmp.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertEquals(1, resRepoEObj.eContents().size());
		var resRepoCmp = resRepoEObj.eContents().get(0);
		Assertions.assertEquals(pcmCmpName, resRepoCmp.eGet(EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME));
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertTrue(EcoreUtil.equals(resRepoCmp, propagatedRepoCmp));

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		Assertions.assertEquals(1, javaResource.getContents().size());
		var javaMod = javaResource.getContents().get(0);
		Assertions.assertEquals(
				PcmUserInteractionManager.getDesiredFeatureValue(createdCmp[0],
						CommonsPackage.Literals.NAMED_ELEMENT__NAME, false),
				PcmCPRTestConstants.namespaceTestComponentModuleName);

		var inputNss = (List) PcmUserInteractionManager.getDesiredFeatureValue(createdCmp[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, false);
		var javaModNss = PcmCPRTestConstants.namespaceTestComponentModuleNamespaces;
		Assertions.assertTrue(inputNss.size() == javaModNss.size() && inputNss.containsAll(javaModNss));

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
