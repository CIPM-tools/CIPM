package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.List;

import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.types.TypesFactory;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import cipm.consistency.cpr.pcmjava.preprocessing.rules.atomic.RemoveRedundantChangesRule;
import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
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
	private static final ChangePreprocessingRule rule = new RemoveRedundantChangesRule();

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
		cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				ChangePreprocessingTestModifications.addRootToResourceAction(repo));
		var baseRes = cc.getResourceCopy(res);

		// Set repo's name, then delete it
		var changes = cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
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
	public void deletedElementIsReferenced_SingleReference() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		final var cmp = RepositoryFactory.eINSTANCE.createBasicComponent();
		var res = cc.getEmptyResourceInstance();
		cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				List.of(ChangePreprocessingTestModifications.addRootToResourceAction(repo),
						ChangePreprocessingTestModifications.addToManyValuedFeatAction(repo,
								RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY, cmp)));
		var baseRes = cc.getResourceCopy(res);

		var changes = cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				List.of(ChangePreprocessingTestModifications.removeFromManyValuedFeatAction(repo,
						RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY, cmp)));

		Assertions.assertEquals(3, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(3, changes.size());
		Assertions.assertEquals(3, postRuleChanges.size());
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(baseRes, changes, postRuleChanges);
	}

	/**
	 * Pre-setup: Create Cls -> Set Cls as tr1.target
	 * <p>
	 * Change sequence: Remove C from R.components -> Delete C
	 */
	@Test
	public void deletedElementIsReferenced_MultipleReference() {
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var tr1 = TypesFactory.eINSTANCE.createClassifierReference();
		var tr2 = TypesFactory.eINSTANCE.createClassifierReference();
		var res = cc.getEmptyResourceInstance();

		res.getContents().add(cls);
		res.getContents().add(tr1);
		res.getContents().add(tr2);

		tr1.setTarget(cls);
		tr2.setTarget(cls);

		var baseRes = cc.getResourceCopy(res);

		var changes = cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				List.of(ChangePreprocessingTestModifications.unsetSingleValuedFeatAction(tr1,
						TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET),
						ChangePreprocessingTestModifications.unsetSingleValuedFeatAction(tr2,
								TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET),
						ChangePreprocessingTestModifications.removeRootFromResourceAction(cls)));

//		Assertions.assertEquals(3, changes.size());
		var postRuleChanges = rule.apply(changes);
//		Assertions.assertEquals(3, changes.size());
//		Assertions.assertEquals(3, postRuleChanges.size());

		// FIXME Fix the test

		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(baseRes, changes, postRuleChanges);
	}

	@Disabled
	@Test
	public void test() {

		// TODO Implement a proper test, where 2 root EObjects have a reference and you
		// remove the referenced one. Currently, using a type from param moves that type
		// into param and it is no longer a root element.

		final var type1 = RepositoryFactory.eINSTANCE.createPrimitiveDataType();
		final var type2 = RepositoryFactory.eINSTANCE.createPrimitiveDataType();
		final var param = RepositoryFactory.eINSTANCE.createParameter();
		var res = cc.getEmptyResourceInstance();
		cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				List.of(ChangePreprocessingTestModifications.addRootToResourceAction(type1),
						ChangePreprocessingTestModifications.addRootToResourceAction(type2),
						ChangePreprocessingTestModifications.addRootToResourceAction(param)
//						ChangePreprocessingTestModifications.setSingleValuedFeatAction(param,
//								RepositoryPackage.Literals.PARAMETER__DATA_TYPE_PARAMETER, type1),
//						ChangePreprocessingTestModifications.setSingleValuedFeatAction(param,
//								RepositoryPackage.Literals.PARAMETER__DATA_TYPE_PARAMETER, type2)
				));

		var baseRes = cc.getResourceCopy(res);
		Assertions.assertEquals(3, baseRes.getContents().size());

		var changes = cc.getEChangesFor(cc.getEmptyResourceInstance(), res,
				List.of(ChangePreprocessingTestModifications.setSingleValuedFeatAction(param,
						RepositoryPackage.Literals.PARAMETER__DATA_TYPE_PARAMETER, type1),
						ChangePreprocessingTestModifications.removeRootFromResourceAction(type1)));

		var postRuleChanges = rule.apply(changes);
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(baseRes, changes, postRuleChanges);
	}
}
