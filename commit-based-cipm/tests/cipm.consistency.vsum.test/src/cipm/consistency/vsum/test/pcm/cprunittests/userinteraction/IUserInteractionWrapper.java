package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface IUserInteractionWrapper extends IUserInteractionObserver {
	public boolean performUserInteraction();

	public boolean deriveResult();

	public boolean isResultComplete();

	public IUserInteractionResult getResult();

	public boolean isUserInteractionComplete();

	public List<EStructuralFeature> getInvolvedFeatures();

	/**
	 * @return A modifiable list of EObjects involved in the underlying user
	 *         interaction
	 */
	public List<EObject> getInvolvedEObjects();

	public IUserInteractionID getID();

	public default List<EStructuralFeature> getOverlappingFeatures(IUserInteractionWrapper wrapper) {
		var overlap = new ArrayList<EStructuralFeature>();
		final var wrapperFeatures = wrapper.getInvolvedFeatures();
		this.getInvolvedFeatures().forEach((f) -> {
			if (wrapperFeatures.contains(f))
				overlap.add(f);
		});
		return overlap;
	}

	public default List<EObject> getOverlappingEObjects(IUserInteractionWrapper wrapper) {
		var overlap = new ArrayList<EObject>();
		final var wrapperObjs = wrapper.getInvolvedEObjects();
		this.getInvolvedEObjects().forEach((o) -> {
			if (wrapperObjs.contains(o))
				overlap.add(o);
		});
		return overlap;
	}

	public default boolean overlapsWith(IUserInteractionWrapper wrapper) {
		return this.getInvolvedEObjects().stream().anyMatch((thisO) -> wrapper.getInvolvedEObjects().contains(thisO))
				|| this.getInvolvedFeatures().stream()
						.anyMatch((thisF) -> wrapper.getInvolvedFeatures().contains(thisF));
	}

	public default boolean wrappersEqual(IUserInteractionWrapper otherWrapper) {
		if (otherWrapper == null)
			return false;
		if (!this.getID().equals(otherWrapper.getID()))
			return false;

		return this.getInvolvedEObjects().size() == otherWrapper.getInvolvedEObjects().size()
				&& this.getInvolvedEObjects().containsAll(otherWrapper.getInvolvedEObjects());
	}

	@Override
	public boolean equals(Object obj);
}
