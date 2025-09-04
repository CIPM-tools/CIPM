package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.HashMap;
import java.util.Map;

public final class PcmUserInteractionManager {
	private static final PcmUserInteractionManager instance = new PcmUserInteractionManager();
	private static final Map<UserInteractionKey, IUserInteractionWrapper> userInteractionWrappers = new HashMap<UserInteractionKey, IUserInteractionWrapper>();

	private PcmUserInteractionManager() {
	}

	public static final class UserInteractionKey {
		private UserInteractionKey() {

		}

		private static UserInteractionKey getInstance() {
			return new UserInteractionKey();
		}
	}

	public static UserInteractionKey registerUserInteraction(IUserInteractionWrapper wrapper) {
		var key = UserInteractionKey.getInstance();
		userInteractionWrappers.values().stream().filter((w) -> w.overlapsWith(wrapper)).forEach((w) -> {
			w.addObservedUserInteraction(wrapper);
			wrapper.addObservedUserInteraction(w);
		});
		userInteractionWrappers.put(key, wrapper);
		return key;
	}

	public static IUserInteractionWrapper deregisterUserInteraction(UserInteractionKey key) {
		var removedWrapper = userInteractionWrappers.remove(key);
		userInteractionWrappers.values().stream()
				.filter((w) -> w.getObservedUserInteractions().contains(removedWrapper)).forEach((w) -> {
					w.removeObservedUserInteraction(removedWrapper);
					removedWrapper.removeObservedUserInteraction(w);
				});
		return removedWrapper;
	}

	public static boolean userInteractionRegistered(UserInteractionKey key) {
		return userInteractionWrappers.containsKey(key);
	}

	public static IUserInteractionWrapper getUserInteraction(UserInteractionKey key) {
		return userInteractionWrappers.get(key);
	}

	public static IUserInteractionResult getResultFor(UserInteractionKey userInteractionKey) {
		var wrapper = userInteractionWrappers.get(userInteractionKey);
		if (wrapper == null) {
			return null;
		} else {
			if (wrapper.isResultComplete() || wrapper.deriveResult() || wrapper.performUserInteraction()) {
				return wrapper.getResult();
			}
			return null;
		}
	}
}
