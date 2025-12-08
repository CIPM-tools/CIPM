package cipm.consistency.vsum.test.pcm.newviews;

import java.util.Collection;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.framework.views.CommittableView;

/**
 * A {@link View} interface, which accepts model changes (as {@link EChange}
 * instances) from outside.
 * 
 * <p>
 * TODO Move to tools.vitruv.framework.views
 * 
 * @author Alp Torac Genc
 */
public interface IChangeAcceptingView extends CommittableView {
	/**
	 * @return Changes in this view, which are not propagated yet. The next call to
	 *         {@link #commitChanges()} will propagate these changes.
	 */
	public Collection<EChange> getAllChanges();

	/**
	 * Adds the given model change to this instance to be propagated.
	 * 
	 * @see {@link #getAllChanges()}
	 */
	public void addChange(EChange change);

	/**
	 * A variant of {@link #addChanges(Collection)} for change collections.
	 */
	public default void addChanges(Collection<EChange> changesToAdd) {
		if (changesToAdd == null)
			return;

		changesToAdd.forEach((c) -> this.addChange(c));
	}

	/**
	 * Removes the given model change from this instance. That change will no longer
	 * be propagated with the next call to {@link #commitChanges()}.
	 */
	public boolean removeChange(EChange change);

	/**
	 * Removes all model changes from this instance. Those changes will no longer be
	 * propagated with the next call to {@link #commitChanges()}.
	 */
	public void cleanChanges();
}
