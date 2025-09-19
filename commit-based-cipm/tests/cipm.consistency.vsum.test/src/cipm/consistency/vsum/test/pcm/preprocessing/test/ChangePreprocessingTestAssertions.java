package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.vsum.test.pcm.cprunittests.ChangeComputer;
import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;

public final class ChangePreprocessingTestAssertions {
	private static final ChangeComputer defaultChangeComputer = new ChangeComputer();

	public static void assertChangeSequencesHaveSameEffect(Collection<EChange> changeSeq1,
			Collection<EChange> changeSeq2) {
		assertChangeSequencesHaveSameEffect(defaultChangeComputer.getEmptyResourceInstance(), changeSeq1, changeSeq2);
	}

	public static void assertChangeSequencesHaveSameEffect(Resource startRes, Collection<EChange> changeSeq1,
			Collection<EChange> changeSeq2) {
		var compChange1 = VitruviusChangeFactory.getInstance().createTransactionalChange(changeSeq1);
		var compChange2 = VitruviusChangeFactory.getInstance().createTransactionalChange(changeSeq2);

		compChange1 = (TransactionalChange) compChange1.unresolve();
		compChange2 = (TransactionalChange) compChange2.unresolve();

		var res1 = defaultChangeComputer.getResourceCopy(startRes);
		var res2 = defaultChangeComputer.getResourceCopy(startRes);

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
