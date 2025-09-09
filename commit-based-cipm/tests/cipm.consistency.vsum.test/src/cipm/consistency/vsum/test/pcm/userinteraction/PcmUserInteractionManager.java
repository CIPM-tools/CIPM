package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.collection.Triplet;

public final class PcmUserInteractionManager {
	private static final Object unsetKey = new Object();

	private static final List<AbstractUserInteraction> wrappers = new ArrayList<AbstractUserInteraction>();

	private static final List<ConflictResolutionStrategy> resolutionStrats = new ArrayList<ConflictResolutionStrategy>();
	/**
	 * Assumption: All features only have to be changed once at most after change
	 * pre-processing
	 */
	private static final Map<EStructuralFeature, Object> desiredFeatureValues = new HashMap<EStructuralFeature, Object>();
	private static final Set<Triplet<EObject, EObject, String>> desiredCorrespondences = new HashSet<Triplet<EObject, EObject, String>>();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())) {
			resolutionStrats.forEach((s) -> s.applyFor(userInteraction));
		}

		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())) {
			userInteraction.getDesiredFeatures().forEach((f) -> {
				if (!desiredFeatureValues.containsKey(f)) {
					desiredFeatureValues.put(f, unsetKey);
				} else if (desiredFeatureValues.get(f) != unsetKey) {
					userInteraction.getDesiredFeatureChangedValue(f, desiredFeatureValues.get(f));
				}
			});
			wrappers.add(userInteraction);
		}
	}

	public static void removeUserInteraction(AbstractUserInteraction userInteraction) {
		wrappers.remove(userInteraction);
	}

	public static Object getDesiredFeatureValue(EStructuralFeature feat, boolean computeIfAbsent) {
		var val = desiredFeatureValues.containsKey(feat) ? desiredFeatureValues.get(feat) : unsetKey;
		if (computeIfAbsent && val == unsetKey) {
			var it = new ArrayList<>(wrappers).iterator();

			while (it.hasNext() && val == unsetKey) {
				var currentW = it.next();
				if (!currentW.hasDesiredFeature(feat)) {
					continue;
				} else {
					currentW.performManualUserInteraction();
					val = desiredFeatureValues.get(feat);
				}
			}
		}
		return val != unsetKey ? val : null;
	}

	public static boolean hasDesiredFeatureValue(EStructuralFeature feat) {
		if (desiredFeatureValues.containsKey(feat)) {
			return desiredFeatureValues.get(feat) != unsetKey;
		}
		return false;
	}

	public static Object removeDesiredFeatureValue(EStructuralFeature feat) {
		return desiredFeatureValues.containsKey(feat) ? desiredFeatureValues.remove(feat) : unsetKey;
	}

	public static void setDesiredFeatureValue(AbstractUserInteraction userInteraction, EStructuralFeature feat,
			Object value) {
		desiredFeatureValues.put(feat, value);
		if (value != unsetKey) {
			wrappers.forEach((w) -> {
				if (w != userInteraction) {
					w.getDesiredFeatureChangedValue(feat, value);
				}
			});
		}
	}

	public static Triplet<EObject, EObject, String> getDesiredCorrespondence(EObject knownSide,
			String correspondenceTag, boolean computeIfAbsent) {
		var optCor = desiredCorrespondences.stream()
				.filter((t) -> (t.getElement1() == knownSide || t.getElement2() == knownSide)
						&& t.getElement3().equals(correspondenceTag))
				.findFirst();

		Triplet<EObject, EObject, String> correspondence = null;

		if (optCor.isPresent())
			correspondence = optCor.get();

		if (correspondence == null && computeIfAbsent) {
			correspondence = new Triplet<>(knownSide, null, correspondenceTag);
			desiredCorrespondences.add(correspondence);
		}
		EObject os = knownSide == correspondence.getElement1() ? correspondence.getElement2()
				: correspondence.getElement1();

		if (knownSide != null && os != null)
			return correspondence;
		if (!computeIfAbsent)
			return null;

		var it = new ArrayList<>(wrappers).iterator();

		while (it.hasNext() && os == null) {
			var currentW = it.next();
			if (!currentW.hasDesiredCorrespondence(knownSide, correspondenceTag)) {
				continue;
			} else {
				currentW.performManualUserInteraction();
				// Manual interaction is supposed to update the correspondence
				os = knownSide == correspondence.getElement1() ? correspondence.getElement2()
						: correspondence.getElement1();
			}
		}

		return correspondence;
	}

	public static boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return desiredCorrespondences.stream()
				.filter((t) -> (t.getElement1() == knownSide && t.getElement2() != null
						|| t.getElement2() == knownSide && t.getElement1() != null)
						&& t.getElement3().equals(correspondenceTag))
				.findFirst().isPresent();
	}

	public static Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		var cor = getDesiredCorrespondence(knownSide, correspondenceTag, true);
		return cor != null ? desiredCorrespondences.remove(cor) : null;
	}

	public static void setDesiredCorrespondence(AbstractUserInteraction userInteraction, EObject knownSide,
			EObject otherSide, String correspondenceTag) {
		var cor = getDesiredCorrespondence(knownSide, correspondenceTag, true);
		if (cor != null) {
			if (cor.getElement1() == knownSide)
				cor.setElement2(otherSide);
			if (cor.getElement2() == knownSide)
				cor.setElement1(otherSide);
		} else {
			desiredCorrespondences.add(new Triplet<EObject, EObject, String>(knownSide, otherSide, correspondenceTag));
		}
	}

	public static void addConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		resolutionStrats.add(strat);
	}

	public static void reset() {
		wrappers.clear();
		desiredFeatureValues.clear();
		resolutionStrats.clear();
		desiredCorrespondences.clear();
	}
}
