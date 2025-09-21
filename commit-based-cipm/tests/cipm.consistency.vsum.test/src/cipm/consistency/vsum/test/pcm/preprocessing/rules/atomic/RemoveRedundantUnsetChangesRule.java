package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EReference;

import com.google.common.collect.Lists;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.UnsetFeature;

/**
 * Unset R.feat (EAttribute or EReference without EOpposite) -> Delete R => NOP
 * -> Delete R
 * <p>
 * Assumption: Create and delete changes have been handled beforehand
 */
public class RemoveRedundantUnsetChangesRule extends ChangePreprocessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangesList = new ArrayList<>(changeSequence);

		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			List<UnsetFeature<?, ?>> affectedUnsetChanges = newChangesList
					// Only changes prior to deletingChange are relevant
					.subList(0, newChangesList.indexOf(deletingChange)).stream()
					.filter((c) -> c instanceof UnsetFeature).map((c) -> (UnsetFeature<?, ?>) c)
					.filter((c) -> ChangeUtil.eObjectsNonNullAndEqual(ChangeUtil.getAffectedEObject(c), deletedElement))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * Remove all unset changes on deletedElement, which do not target an EReference
			 * feature, since deletedElement will be deleted.
			 * 
			 * Leave unset changes on deletedElement targeting an EReference feature, which
			 * has an EOpposite, because they will affect the EOpposite's holder EObject.
			 * This is crucial for consistency, as that EObject will otherwise still
			 * reference deletedElement, which will cause change resolution issues.
			 */
			for (var unsetChange : affectedUnsetChanges) {
				var unsetChangeFeat = unsetChange.getAffectedFeature();
				if (!(unsetChangeFeat instanceof EReference) || ((EReference) unsetChangeFeat).getEOpposite() == null) {
					newChangesList.remove(unsetChange);
				}
			}
		}

		return newChangesList;
	}

}
