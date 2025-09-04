package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.Collection;

public interface IUserInteractionObserver {
	public void addObservedUserInteraction(IUserInteractionWrapper wrapper);

	public void addedObservedUserInteraction(IUserInteractionWrapper wrapper);

	public void removeObservedUserInteraction(IUserInteractionWrapper wrapper);

	public void removedObservedUserInteraction(IUserInteractionWrapper wrapper);

	public void cleanObservedUserInteractions();

	public default boolean isUserInteractionObserved(IUserInteractionWrapper wrapper) {
		return this.getObservedUserInteractions().contains(wrapper);
	}

	public Collection<IUserInteractionWrapper> getObservedUserInteractions();

	public void userInteractionCompleted(IUserInteractionWrapper wrapper);
}
