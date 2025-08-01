package cipm.consistency.vsum.test.pcm.newviews;

import java.util.List;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.ViewSelection;

public interface IChangeAcceptingView extends CommittableView {
	public List<EChange> getAllChanges();

	public void addChange(EChange change);

	public default void addChanges(List<EChange> changesToAdd) {
		if (changesToAdd == null)
			return;

		changesToAdd.forEach((c) -> this.addChange(c));
	}

	public boolean removeChange(EChange change);

	public void cleanChanges();
	
	public void setSelection(ViewSelection selection);
}
