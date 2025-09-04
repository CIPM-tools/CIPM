package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.Collection;

public interface IUserInteractionResultObserver {
	public void addObservedUserInteractionResult(IUserInteractionResult wrapper);

	public void addedObservedUserInteractionResult(IUserInteractionResult wrapper);

	public void removeObservedUserInteractionResult(IUserInteractionResult wrapper);

	public void removedObservedUserInteractionResult(IUserInteractionResult wrapper);

	public void cleanObservedUserInteractionResults();

	public default boolean isUserInteractionResultObserved(IUserInteractionResult wrapper) {
		return this.getObservedUserInteractionResults().contains(wrapper);
	}

	public Collection<IUserInteractionResult> getObservedUserInteractionResults();

	public void resultsChanged(IUserInteractionResult wrapper);
}
