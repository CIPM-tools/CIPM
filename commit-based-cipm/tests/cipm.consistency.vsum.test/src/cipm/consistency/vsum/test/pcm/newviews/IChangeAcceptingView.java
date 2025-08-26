package cipm.consistency.vsum.test.pcm.newviews;

import java.util.Collection;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.framework.views.CommittableView;

public interface IChangeAcceptingView extends CommittableView {
	public Collection<EChange> getAllChanges();

	public void addChange(EChange change);

	public default void addChanges(Collection<EChange> changesToAdd) {
		if (changesToAdd == null)
			return;

		changesToAdd.forEach((c) -> this.addChange(c));
	}

	public boolean removeChange(EChange change);

	public void cleanChanges();
}
