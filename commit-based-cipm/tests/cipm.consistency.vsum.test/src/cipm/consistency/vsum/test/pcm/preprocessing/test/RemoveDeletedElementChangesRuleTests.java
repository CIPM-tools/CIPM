package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
import cipm.consistency.vsum.test.pcm.preprocessing.AtomicChangeTransformer;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeSequenceProcessingRule;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.RemoveDeletedElementChangesRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;

public class RemoveDeletedElementChangesRuleTests {
	private static final AtomicChangeTransformer act = new AtomicChangeTransformer();
	private static final ChangeComputer cc = new ChangeComputer();
	private static final ChangeSequenceProcessingRule rule = new RemoveDeletedElementChangesRule();

	@Test
	public void addAndRemoveRepository_WithoutTransformation() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(
				// Add a repository:
				// 1) Create
				// 2) Insert
				// 3) Replace ID
				(r) -> r.getContents().add(repo),
				// Remove the repository:
				// 4) Remove
				// 5) Delete
				(r) -> r.getContents().remove(repo)));

		Assertions.assertEquals(5, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(0, postRuleChanges.size());
		assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	@Test
	public void addAndRemoveRepository_WithTransformation() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(
				// Add a repository:
				// 1) Create repo
				// 2) Insert repo
				// 3) InsertEAttributeValue (repo.ID)
				(r) -> r.getContents().add(repo),
				// Remove the repository:
				// 4) RemoveEAttributeValue (repo.ID)
				// 5) Remove repo
				// 6) Delete repo
				(r) -> r.getContents().remove(repo)));
		var transformedChanges = act.transform(changes);
		assertChangeSequencesHaveSameEffect(changes, transformedChanges);

		// FIXME Adapt assertion once transformation is fixed
		Assertions.assertEquals(6, transformedChanges.size());

		var postRuleChanges = rule.apply(transformedChanges);
		Assertions.assertEquals(0, postRuleChanges.size());
		assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}

	private void assertChangeSequencesHaveSameEffect(Collection<EChange> changeSeq1, Collection<EChange> changeSeq2) {
		var res1 = cc.getEmptyResourceInstance();
		var res2 = cc.getEmptyResourceInstance();

		var compChange1 = VitruviusChangeFactory.getInstance().createTransactionalChange(changeSeq1);
		var compChange2 = VitruviusChangeFactory.getInstance().createTransactionalChange(changeSeq2);

		compChange1 = (TransactionalChange) compChange1.unresolve();
		compChange2 = (TransactionalChange) compChange2.unresolve();

		compChange1.resolveAndApply(res1.getResourceSet());
		compChange2.resolveAndApply(res2.getResourceSet());

		var res1Elems = new ArrayList<EObject>();
		res1.getAllContents().forEachRemaining((c) -> res1Elems.add(c));
		var res2Elems = new ArrayList<EObject>();
		res2.getAllContents().forEachRemaining((c) -> res2Elems.add(c));

		Assertions.assertEquals(res1Elems.size(), res2Elems.size());

		for (int i = 0; i < res1Elems.size(); i++) {
			Assertions.assertTrue(ChangeUtil.eObjectsEqual(res1Elems.get(i), res2Elems.get(i)));
		}
	}
}
