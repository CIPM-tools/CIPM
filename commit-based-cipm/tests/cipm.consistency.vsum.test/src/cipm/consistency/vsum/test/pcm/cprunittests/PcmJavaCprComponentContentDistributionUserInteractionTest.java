package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

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
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaUserInteractionCPRsChangePropagationSpecification());
		return list;
	}

	public void pcmCmpContentDistTest(BiFunction<Resource, Resource, List<CorrespondenceEntry>> pcmAndJavaResToCorsFunc,
			BiFunction<Resource, Resource, ConflictResolutionStrategy[]> pcmAndJavaResToStratsFunc) {
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
		// Setup of conflict resolution strategies
		//
		if (pcmAndJavaResToStratsFunc != null) {
			for (var s : pcmAndJavaResToStratsFunc.apply(pcmRes, javaResource)) {
				PcmUserInteractionManager.addConflictResolutionStrategy(s);
			}
		}

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
			 * There should be at least 3 correspondences in the correspondence view at the
			 * end of the interaction, because cls1 and cls2 each have to correspond to at
			 * least one component (same component is allowed). Hence there should be at
			 * least 3 corresponding EObjects.
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
			var repoObj = (Repository) pcmRes.getContents().get(0);
			var cmpToPersistOne = (BasicComponent) repoObj.getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentOneName))
					.findFirst().get();
			var cmpToPersistTwo = (BasicComponent) repoObj.getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestPersistingComponentTwoName))
					.findFirst().get();

			var cls1 = (org.emftext.language.java.classifiers.Class) javaRes.getContents().stream()
					.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
							&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
									PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassOneName))
					.findFirst().get();
			var cls2 = (org.emftext.language.java.classifiers.Class) javaRes.getContents().stream()
					.filter((c) -> c instanceof org.emftext.language.java.classifiers.Class
							&& ((org.emftext.language.java.classifiers.Class) c).getName().equals(
									PcmCPRTestConstants.componentContentDistributionTestDeletedComponentClassTwoName))
					.findFirst().get();

			return List.of(new CorrespondenceEntry(cmpToPersistOne, cls1, ""),
					new CorrespondenceEntry(cmpToPersistTwo, cls2, ""));
		};
		pcmCmpContentDistTest(corFunc, (pcmRes, javaRes) -> {
			var repoObj = (Repository) pcmRes.getContents().get(0);
			var cmpToBeDeleted = (BasicComponent) repoObj.getComponents__Repository().stream()
					.filter((c) -> c instanceof BasicComponent && ((BasicComponent) c).getEntityName()
							.equals(PcmCPRTestConstants.componentContentDistributionTestDeletedComponentName))
					.findFirst().get();
			return new ConflictResolutionStrategy[] {
					new DummyDistributionConflictResolutionStrategy(cmpToBeDeleted, corFunc.apply(pcmRes, javaRes)) };
		});
	}

//	@Disabled("Enable if manual user interaction is to be tested")
	@Test
	public void testJavaPCMUserInteraction_CmpContentDistribution_Manual() {
		pcmCmpContentDistTest(null, null);
	}
}
