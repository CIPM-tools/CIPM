package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

public class ChangeDependencyAnalyser {
	public Set<PcmDependency> getCorrespondenceDependencies(EChange pcmChange, CorrespondenceModelView<?> corView) {
		var res = new HashSet<PcmDependency>();
		if (pcmChange instanceof CreateEObject)
			getCorrespondenceDependencies((CreateEObject<?>) pcmChange, corView, res);
		if (pcmChange instanceof DeleteEObject)
			getCorrespondenceDependencies((DeleteEObject<?>) pcmChange, corView, res);
		if (pcmChange instanceof InsertRootEObject)
			getCorrespondenceDependencies((InsertRootEObject<?>) pcmChange, corView, res);
		if (pcmChange instanceof RemoveRootEObject)
			getCorrespondenceDependencies((RemoveRootEObject<?>) pcmChange, corView, res);
		if (pcmChange instanceof UnsetFeature)
			getCorrespondenceDependencies((UnsetFeature<?, ?>) pcmChange, corView, res);
		if (pcmChange instanceof InsertEAttributeValue)
			getCorrespondenceDependencies((InsertEAttributeValue<?, ?>) pcmChange, corView, res);
		if (pcmChange instanceof RemoveEAttributeValue)
			getCorrespondenceDependencies((RemoveEAttributeValue<?, ?>) pcmChange, corView, res);
		if (pcmChange instanceof InsertEReference)
			getCorrespondenceDependencies((InsertEReference<?, ?>) pcmChange, corView, res);
		if (pcmChange instanceof RemoveEReference)
			getCorrespondenceDependencies((RemoveEReference<?, ?>) pcmChange, corView, res);
		return res;
	}

	private void getCorrespondenceDependencies(CreateEObject<?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		addCorrespondenceDependencies(pcmChange.getAffectedEObject(), depSet);
	}

	private void getCorrespondenceDependencies(DeleteEObject<?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		depSet.removeIf((dep) -> dep.hasElement(pcmChange.getAffectedEObject()));
		removeCorrespondenceDependencies(pcmChange.getAffectedEObject(), depSet);
	}

	private void getCorrespondenceDependencies(InsertRootEObject<?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		addCorrespondenceDependencies(pcmChange.getNewValue(), depSet);
	}

	private void getCorrespondenceDependencies(RemoveRootEObject<?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		depSet.removeIf((dep) -> dep.hasElement(pcmChange.getOldValue()));
		removeCorrespondenceDependencies(pcmChange.getOldValue(), depSet);
	}

	private void getCorrespondenceDependencies(UnsetFeature<?, ?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		depSet.removeIf((dep) -> dep.hasFeature(pcmChange.getAffectedEObject(), pcmChange.getAffectedFeature()));
	}

	private void getCorrespondenceDependencies(InsertEAttributeValue<?, ?> pcmChange,
			CorrespondenceModelView<?> corView, Set<PcmDependency> depSet) {

		// Assumption: If an EObject value of a feature shifts elements,
		// RemoveEAttributeValue is performed too

		var val = pcmChange.getNewValue();
		if (val instanceof EObject)
			depSet.add(
					new PcmDependency(pcmChange.getAffectedEObject(), (EObject) val, pcmChange.getAffectedFeature()));

	}

	private void getCorrespondenceDependencies(RemoveEAttributeValue<?, ?> pcmChange,
			CorrespondenceModelView<?> corView, Set<PcmDependency> depSet) {
		var val = pcmChange.getOldValue();
		if (val instanceof EObject)
			depSet.removeIf((dep) -> dep.hasFeature(pcmChange.getAffectedEObject(), pcmChange.getAffectedFeature()));
	}

	private void getCorrespondenceDependencies(InsertEReference<?, ?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		var val = pcmChange.getNewValue();
		if (val instanceof EObject)
			depSet.add(
					new PcmDependency(pcmChange.getAffectedEObject(), (EObject) val, pcmChange.getAffectedFeature()));
	}

	private void getCorrespondenceDependencies(RemoveEReference<?, ?> pcmChange, CorrespondenceModelView<?> corView,
			Set<PcmDependency> depSet) {
		var val = pcmChange.getOldValue();
		if (val instanceof EObject)
			depSet.removeIf((dep) -> dep.hasFeature(pcmChange.getAffectedEObject(), pcmChange.getAffectedFeature()));
	}

	private void addCorrespondenceDependencies(EObject pcmElem, Set<PcmDependency> depSet) {
		var cors = CorrespondenceAccess.getAllCorrespondencesFor(pcmElem);
		for (var c : cors) {
			var tag = c.getTag();
			for (var lhsO : c.getLeftEObjects()) {
				var isLhsOPcmElem = !(lhsO instanceof org.emftext.language.java.commons.Commentable);
				for (var rhsO : c.getRightEObjects()) {
					if (isLhsOPcmElem) {
						depSet.add(new PcmDependency(lhsO, rhsO, tag));
					} else {
						depSet.add(new PcmDependency(rhsO, lhsO, tag));
					}
				}
			}
		}
	}

	private void removeCorrespondenceDependencies(EObject pcmElem, Set<PcmDependency> depSet) {
		var cors = CorrespondenceAccess.getAllCorrespondencesFor(pcmElem);
		for (var c : cors) {
			var tag = c.getTag();
			for (var lhsO : c.getLeftEObjects()) {
				for (var rhsO : c.getRightEObjects()) {
					depSet.removeIf((dep) -> dep.hasElement(lhsO) && dep.hasElement(rhsO) && dep.tagEquals(tag));
				}
			}
		}
	}
}
