package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.util.RepositoryResourceFactoryImpl;
import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeSequenceProcessingRule;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.SplitReplaceChangesRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

public class SplitReplaceChangesRuleTest {
	/**
	 * Sets up the necessary resource factory registries and loggers
	 */
	@BeforeAll
	public static void setupBeforeAll() {
		// Added for PCM extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("repository",
				new RepositoryResourceFactoryImpl());

		// Added for change extensions and others
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	}

	private static final ChangeComputer cc = new ChangeComputer();
	private static final ChangeSequenceProcessingRule rule = new SplitReplaceChangesRule();

	private List<EChange> getBaselineChanges() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addObjToResourceAction(repo),
				ChangePreprocessingTestModifications.removeObjFromResourceAction(repo)));

		Assertions.assertEquals(1,
				changes.stream().filter((c) -> c instanceof ReplaceSingleValuedFeatureEChange).count());
		Assertions.assertEquals(5, changes.size());
		return changes;
	}

	private void assertNonReplaceChangesUntouched(List<EChange> changes, List<EChange> postRuleChanges) {
		// SplitReplaceChangesRule should never reduce original change sequence length
		Assertions.assertTrue(changes.size() <= postRuleChanges.size());

		for (int changesIdx = 0, postRuleChangesIdx = 0; changesIdx < changes.size(); changesIdx++) {
			var change = changes.get(changesIdx);
			var postRuleChange = postRuleChanges.get(postRuleChangesIdx);

			if (!(change instanceof ReplaceSingleValuedFeatureEChange)) {
				Assertions.assertTrue(ChangeUtil.eObjectsEqual(change, postRuleChange));
				postRuleChangesIdx += 1;
			} else {
				if (postRuleChange instanceof RemoveFromListEChange) {
					Assertions.assertTrue(ChangeUtil.newAndOldValuesPresentAndEqual(change)
							|| ChangeUtil.getOldValue(change) == null);
					postRuleChangesIdx += 1;
				}
				postRuleChange = postRuleChanges.get(postRuleChangesIdx);
				Assertions.assertTrue(postRuleChange instanceof InsertInListEChange);
				postRuleChangesIdx += 1;
			}
		}
	}

	@Test
	public void baselineTest() {
		Assertions.assertFalse(getBaselineChanges().isEmpty());
	}

	@Test
	public void transformReplaceChange_OneFeature_FirstValueAssignment() {
		var changes = getBaselineChanges();

		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(5, postRuleChanges.size());
		Assertions
				.assertTrue(postRuleChanges.stream().noneMatch((c) -> c instanceof ReplaceSingleValuedFeatureEChange));
		assertNonReplaceChangesUntouched(changes, postRuleChanges);
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	@Test
	public void transformReplaceChange_TwoFeatures_FirstValueAssignment() {
		final var repoName = "repoName";
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addObjToResourceAction(repo),
				ChangePreprocessingTestModifications.setObjEntityNameAction(repo, repoName),
				ChangePreprocessingTestModifications.removeObjFromResourceAction(repo)));

		Assertions.assertEquals(6, changes.size());

		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(6, postRuleChanges.size());
		assertNonReplaceChangesUntouched(changes, postRuleChanges);
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}
}
