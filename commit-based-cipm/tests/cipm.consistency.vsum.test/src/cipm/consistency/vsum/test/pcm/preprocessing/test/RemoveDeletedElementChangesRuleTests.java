package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeSequenceProcessingRule;
import cipm.consistency.vsum.test.pcm.preprocessing.RemoveDeletedElementChangesRule;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * <p>
 * Pre-setup: The changes that are performed on the Resource instance. These
 * changes are ignored by pre-processing
 * <p>
 * Change sequence: The changes that are performed on the Resource instance
 * after Pre-setup. These changes are the subject of pre-processing.
 */
public class RemoveDeletedElementChangesRuleTests {
	private static final ChangeComputer cc = new ChangeComputer();
	private static final ChangeSequenceProcessingRule rule = new RemoveDeletedElementChangesRule();

	/**
	 * Change sequence: Create R -> Delete R
	 */
	@Test
	public void isolatedCreateDelete() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addRootToResourceAction(repo),
				ChangePreprocessingTestModifications.removeRootFromResourceAction(repo)));

		Assertions.assertEquals(5, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(5, changes.size());
		Assertions.assertEquals(0, postRuleChanges.size());
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	/**
	 * Change sequence: Create R1 -> Create R2 -> Delete R2 -> Delete R1
	 */
	@Test
	public void nestedCreateDelete() {
		var repo1 = RepositoryFactory.eINSTANCE.createRepository();
		var repo2 = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addRootToResourceAction(repo1),
				ChangePreprocessingTestModifications.addRootToResourceAction(repo2),
				ChangePreprocessingTestModifications.removeRootFromResourceAction(repo2),
				ChangePreprocessingTestModifications.removeRootFromResourceAction(repo1)));

		Assertions.assertEquals(10, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(10, changes.size());
		Assertions.assertEquals(0, postRuleChanges.size());
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	/**
	 * Change sequence: Create R1 -> Create R2 -> Delete R1 -> Delete R2
	 */
	@Test
	public void mixedCreateDelete() {
		var repo1 = RepositoryFactory.eINSTANCE.createRepository();
		var repo2 = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addRootToResourceAction(repo1),
				ChangePreprocessingTestModifications.addRootToResourceAction(repo2),
				ChangePreprocessingTestModifications.removeRootFromResourceAction(repo1),
				ChangePreprocessingTestModifications.removeRootFromResourceAction(repo2)));

		Assertions.assertEquals(10, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(10, changes.size());
		Assertions.assertEquals(0, postRuleChanges.size());
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	/**
	 * Pre-setup: Create R
	 * <p>
	 * Change sequence: Set R.entityName -> Delete R
	 */
	@Test
	public void changeBetweenCreateDelete() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var repoName = "repoName";
		var res = cc.getEmptyResourceInstance();
		// Add repository to res, discard changes
		cc.getEChangesFor(res, ChangePreprocessingTestModifications.addRootToResourceAction(repo));
		var baseRes = cc.getResourceCopy(res);

		// Set repo's name, then delete it
		var changes = cc.getEChangesFor(res,
				List.of(ChangePreprocessingTestModifications.setEntityNameAction(repo, repoName),
						ChangePreprocessingTestModifications.removeRootFromResourceAction(repo)));

		Assertions.assertEquals(3, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(3, changes.size());
		Assertions.assertEquals(2, postRuleChanges.size());
		Assertions.assertTrue(postRuleChanges.get(0) instanceof RemoveRootEObject);
		Assertions.assertTrue(postRuleChanges.get(1) instanceof DeleteEObject);
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(baseRes, changes, postRuleChanges);
	}

	/**
	 * Pre-setup: Create R -> Add C to R.components
	 * <p>
	 * Change sequence: Remove C from R.components -> Delete C
	 */
	@Test
	public void brokenFeatureChange() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		final var cmp = RepositoryFactory.eINSTANCE.createBasicComponent();
		var res = cc.getEmptyResourceInstance();
		cc.getEChangesFor(res,
				List.of(ChangePreprocessingTestModifications.addRootToResourceAction(repo),
						ChangePreprocessingTestModifications.addToManyValuedFeatAction(repo,
								RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY, cmp)));
		var baseRes = cc.getResourceCopy(res);

		var changes = cc.getEChangesFor(res,
				List.of(ChangePreprocessingTestModifications.removeFromManyValuedFeatAction(repo,
						RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY, cmp)));

		Assertions.assertEquals(3, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(3, changes.size());
		Assertions.assertEquals(3, postRuleChanges.size());
		postRuleChanges.remove(postRuleChanges.size() - 1);
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(baseRes, changes, postRuleChanges);
	}
}
