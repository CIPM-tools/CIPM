package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractUserInteractionWrapper
		implements IUserInteractionWrapper, IUserInteractionResultObserver {
	private final Collection<IUserInteractionWrapper> observedUserInteractions = new ArrayList<>();
	private final Collection<IUserInteractionResult> observedUserInteractionResults = new ArrayList<>();

	private final AbstractModifiableUserInteractionResult userInteractionResult;

	protected AbstractUserInteractionWrapper(AbstractModifiableUserInteractionResult userInteractionResult) {
		this.userInteractionResult = userInteractionResult;
	}

	protected AbstractModifiableUserInteractionResult getModifiableUserInteractionResult() {
		return this.userInteractionResult;
	}

	@Override
	public void addObservedUserInteractionResult(IUserInteractionResult wrapper) {
		this.observedUserInteractionResults.add(wrapper);
		this.addedObservedUserInteractionResult(wrapper);
	}

	@Override
	public void addedObservedUserInteractionResult(IUserInteractionResult wrapper) {
	}

	@Override
	public void removeObservedUserInteractionResult(IUserInteractionResult wrapper) {
		this.observedUserInteractionResults.remove(wrapper);
		this.removedObservedUserInteractionResult(wrapper);
	}

	@Override
	public void removedObservedUserInteractionResult(IUserInteractionResult wrapper) {
	}

	@Override
	public void cleanObservedUserInteractionResults() {
		this.observedUserInteractionResults.clear();
	}

	@Override
	public Collection<IUserInteractionResult> getObservedUserInteractionResults() {
		return new ArrayList<>(this.observedUserInteractionResults);
	}

	@Override
	public void resultsChanged(IUserInteractionResult wrapper) {
	}

	@Override
	public void addObservedUserInteraction(IUserInteractionWrapper wrapper) {
		this.observedUserInteractions.add(wrapper);
	}

	@Override
	public void addedObservedUserInteraction(IUserInteractionWrapper wrapper) {
	}

	@Override
	public void removeObservedUserInteraction(IUserInteractionWrapper wrapper) {
		this.observedUserInteractions.remove(wrapper);
	}

	@Override
	public void removedObservedUserInteraction(IUserInteractionWrapper wrapper) {
	}

	@Override
	public void cleanObservedUserInteractions() {
		this.observedUserInteractions.clear();
	}

	@Override
	public Collection<IUserInteractionWrapper> getObservedUserInteractions() {
		return new ArrayList<>(this.observedUserInteractions);
	}

	@Override
	public void userInteractionCompleted(IUserInteractionWrapper wrapper) {
	}

	@Override
	public boolean isResultComplete() {
		return this.userInteractionResult.isComplete();
	}

	@Override
	public IUserInteractionResult getResult() {
		return this.userInteractionResult.getPresentResults();
	}

	@Override
	public IUserInteractionID getID() {
		// TODO Auto-generated method stub
		return null;
	}
}
