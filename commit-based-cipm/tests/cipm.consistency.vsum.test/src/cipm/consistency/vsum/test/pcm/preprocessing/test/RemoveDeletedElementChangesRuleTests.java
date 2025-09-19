package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeSequenceProcessingRule;
import cipm.consistency.vsum.test.pcm.preprocessing.RemoveDeletedElementChangesRule;

public class RemoveDeletedElementChangesRuleTests {
	private static final ChangeComputer cc = new ChangeComputer();
	private static final ChangeSequenceProcessingRule rule = new RemoveDeletedElementChangesRule();

	@Test
	public void removeChanges_CreateSetDelete() {
		final var repo = RepositoryFactory.eINSTANCE.createRepository();
		var changes = cc.getEChangesFor(List.of(ChangePreprocessingTestModifications.addObjToResourceAction(repo),
				ChangePreprocessingTestModifications.removeObjFromResourceAction(repo)));

		Assertions.assertEquals(5, changes.size());
		var postRuleChanges = rule.apply(changes);
		Assertions.assertEquals(0, postRuleChanges.size());
		ChangePreprocessingTestAssertions.assertChangeSequencesHaveSameEffect(changes, postRuleChanges);
	}
}
