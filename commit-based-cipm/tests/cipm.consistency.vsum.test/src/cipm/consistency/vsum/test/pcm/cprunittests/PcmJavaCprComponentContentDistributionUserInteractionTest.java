package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceEntry;
import cipm.consistency.vsum.test.pcm.userinteraction.DummyDistributionConflictResolutionStrategy;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;
import mir.reactions.dummyPCMJavaUserInteractionCPRs.DummyPCMJavaUserInteractionCPRsChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.correspondence.CorrespondenceFactory;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;

public class PcmJavaCprComponentContentDistributionUserInteractionTest extends AbstractPcmJavaCprTest {
	// PCM element locators
	private static final Function<Resource, Repository> repoObjLocator = (r) -> (Repository) r.getContents().get(0);
	private static final Function<Resource, BasicComponent> cmpToBeDeletedLocator = (
			r) -> (BasicComponent) repoObjLocator.apply(r).getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName))
					.findFirst().get();
	private static final Function<Resource, BasicComponent> cmpToPersistOneLocator = (
			r) -> (BasicComponent) repoObjLocator.apply(r).getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentOneName))
					.findFirst().get();
	private static final Function<Resource, BasicComponent> cmpToPersistTwoLocator = (
			r) -> (BasicComponent) repoObjLocator.apply(r).getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentTwoName))
					.findFirst().get();

	// Java model element locators
	private static final Function<Resource, org.emftext.language.java.classifiers.Class> cls1Locator = (
			r) -> (org.emftext.language.java.classifiers.Class) r.getContents().stream()
					.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
							&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
									PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassOneName))
					.findFirst().get();
	private static final Function<Resource, org.emftext.language.java.classifiers.Class> cls2Locator = (
			r) -> (org.emftext.language.java.classifiers.Class) r.getContents().stream()
					.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
							&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
									PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassTwoName))
					.findFirst().get();

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaUserInteractionCPRsChangePropagationSpecification());
		return list;
	}

	private void setUpPCM() {
		// Setup via propagation to ensure that the PcmFacade gets these changes
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
	}

	private void setUpJavaModel() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
		var cls1 = ClassifiersFactory.eINSTANCE.createClass();
		cls1.setName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassOneName);
		javaResource.getContents().add(cls1);

		var cls2 = ClassifiersFactory.eINSTANCE.createClass();
		cls2.setName(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassTwoName);
		javaResource.getContents().add(cls2);
		this.getJavaFacade().saveToDisk();
	}

	private void setUpCorrespondences() {
		var pcmRes = this.getResourceFromPcmFacade(repositoryFileName);
		var cmpToBeDeleted = cmpToBeDeletedLocator.apply(pcmRes);

		var javaResource = this.getJavaModelResourceFromJavaFacade();
		var cls1 = cls1Locator.apply(javaResource);
		var cls2 = cls2Locator.apply(javaResource);

		var corView = this.getPcmVsumFacade().getCorrespondenceView().getEditableView(ReactionsCorrespondence.class,
				() -> {
					return CorrespondenceFactory.eINSTANCE.createReactionsCorrespondence();
				});
		corView.addCorrespondenceBetween(cls1, cmpToBeDeleted, "");
		corView.addCorrespondenceBetween(cls2, cmpToBeDeleted, "");
	}

	public void pcmCmpContentDistTest(BiFunction<Resource, Resource, List<CorrespondenceEntry>> pcmAndJavaResToCorsFunc,
			BiFunction<Resource, Resource, ConflictResolutionStrategy[]> pcmAndJavaResToStratsFunc) {
		setUpPCM();
		setUpJavaModel();
		setUpCorrespondences();

		var pcmRes = this.getResourceFromPcmFacade(repositoryFileName);
		var cmpToPersistOne = cmpToPersistOneLocator.apply(pcmRes);
		var cmpToPersistTwo = cmpToPersistTwoLocator.apply(pcmRes);

		var javaResource = this.getJavaModelResourceFromJavaFacade();
		var cls1 = cls1Locator.apply(javaResource);
		var cls2 = cls2Locator.apply(javaResource);

		// Setup of conflict resolution strategies
		if (pcmAndJavaResToStratsFunc != null) {
			for (var s : pcmAndJavaResToStratsFunc.apply(pcmRes, javaResource)) {
				PcmUserInteractionManager.addConflictResolutionStrategy(s);
			}
		}

		// Propagate actual changes
		var changes = this.getEChangesFor(pcmRes, (r) -> {
			var rRepoObj = repoObjLocator.apply(r);
			var rCmpToDel = cmpToBeDeletedLocator.apply(r);
			rRepoObj.getComponents__Repository().remove(rCmpToDel);
		});

		this.propagateChangesToResource(pcmRes, changes);

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

		if (pcmAndJavaResToCorsFunc == null) {
			/**
			 * There should be at least 3 EObjects present in the correspondence view at the
			 * end of the interaction, because cls1 and cls2 each have to correspond to at
			 * least one component (same component is allowed).
			 * 
			 * This assertion ensures that these correspondences have been added to the
			 * correspondence view, so that the follow-up assertions work as intended.
			 */
			Assertions.assertTrue(corObjSet.size() >= 3);
			PcmCprAssertions.assertCorrespondenceViewAndPcmManagerConsistent(this.getPcmVsumFacade(),
					correspondentList);
		} else {
			PcmCprAssertions.assertCorrespondenceViewAndPcmManagerConsistent(this.getPcmVsumFacade(),
					pcmAndJavaResToCorsFunc.apply(pcmRes, javaResource));
		}
	}

	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution_Intercepted() {
		final BiFunction<Resource, Resource, List<CorrespondenceEntry>> corFunc = (pcmRes, javaRes) -> {
			var cmpToPersistOne = cmpToPersistOneLocator.apply(pcmRes);
			var cmpToPersistTwo = cmpToPersistTwoLocator.apply(pcmRes);

			var cls1 = cls1Locator.apply(javaRes);
			var cls2 = cls2Locator.apply(javaRes);

			return List.of(new CorrespondenceEntry(cmpToPersistOne, cls1, ""),
					new CorrespondenceEntry(cmpToPersistTwo, cls2, ""));
		};
		pcmCmpContentDistTest(corFunc, (pcmRes, javaRes) -> {
			var cmpToBeDeleted = cmpToBeDeletedLocator.apply(pcmRes);
			return new ConflictResolutionStrategy[] {
					new DummyDistributionConflictResolutionStrategy(cmpToBeDeleted, corFunc.apply(pcmRes, javaRes)) };
		});
	}

	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution_Manual() {
		pcmCmpContentDistTest(null, null);
	}
}
