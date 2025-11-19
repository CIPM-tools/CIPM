package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;
import java.util.function.Predicate;

public class GenericParameterConflictResolutionStrategy extends ConflictResolutionStrategy {
	private Predicate<String> genericParameterNamePredicate;
	private List<String> genericParameterNames;

	public GenericParameterConflictResolutionStrategy(List<String> genericParameterNames) {
		this.genericParameterNames = genericParameterNames;
	}

	public GenericParameterConflictResolutionStrategy(Predicate<String> genericParameterNamePredicate) {
		this.genericParameterNamePredicate = genericParameterNamePredicate;
	}

	private boolean isGenericParameterName(String s) {
		if (genericParameterNamePredicate != null) {
			return genericParameterNamePredicate.test(s);
		} else {
			return genericParameterNames.contains(s);
		}
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		((NamespaceUserInteraction) userInteraction).useNoNamespace();
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof NamespaceUserInteraction
				&& ((NamespaceUserInteraction) userInteraction).allowsNoNamespaces()
				&& userInteraction.getTriggeringPCMelements().size() == 1
				&& isGenericParameterName(((org.palladiosimulator.pcm.core.entity.NamedElement) userInteraction
						.getTriggeringPCMelements().get(0)).getEntityName());
	}
}
