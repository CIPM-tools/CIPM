package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

/**
 * <p>
 * Set containment obj.feat to R -> Delete R ==> NOP -> Delete R
 * <p>
 * Set containing obj.feat to R -> Delete R ==> Unset obj.feat -> Delete R
 */
public class RemoveDeletedElementFeatureChangesRule extends ChangeSequenceProcessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		/*
		 * Reverse the list for easier iteration
		 */
		List<EChange> newChangesList = new ArrayList<>(changeSequence);

		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			List<FeatureEChange<?, ?>> affectedSingleValuedFeatureChanges = newChangesList
					// Only changes prior to deletingChange are relevant
					.subList(0, newChangesList.indexOf(deletingChange)).stream()
					// Only feature changes are relevant
					.filter((c) -> c instanceof FeatureEChange).map((c) -> (FeatureEChange<?, ?>) c)
					// Many-valued feature changes have to be handled elsewhere, since they are
					// needed by change propagation to place affected EObjects to staged area.
					// Including these changes will result in change resolution errors, due to
					// EObject IDs being invalid
					.filter((c) -> !(c instanceof UpdateMultiValuedFeatureEChange))
					// Only changes involving deletedElement are relevant
					.filter((c) -> ChangeUtil.isEObjectInvolvedIn(c, deletedElement))
					.collect(Collectors.toCollection(ArrayList::new));

			for (var affectedChange : affectedSingleValuedFeatureChanges) {

				/*
				 * Use unset changes instead of ReplaceSingleValuedEReferences that involve
				 * deletedElement as new or old value, since EObject ID fields are mandatory and
				 * change propagation will not be able to resolve deletedElement once it is
				 * deleted.
				 */
				if (affectedChange instanceof ReplaceSingleValuedEReference) {
					var idx = newChangesList.indexOf(affectedChange);
					newChangesList.add(idx, this.getUnsetChangeFor(affectedChange));
				}

				/*
				 * All feature changes that involve deletedElement should be removed
				 */
				newChangesList.remove(affectedChange);
			}
		}
		return newChangesList;
	}

	private UnsetFeature<?, ?> getUnsetChangeFor(FeatureEChange<?, ?> change) {
		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
		unsetChange.setAffectedEObject(change.getAffectedEObject());
		unsetChange.setAffectedFeature(change.getAffectedFeature());
		unsetChange.setAffectedEObjectID(change.getAffectedEObjectID());
		return unsetChange;
	}
}
