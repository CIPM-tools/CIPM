package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;
import java.util.function.Predicate;

/**
 * A strategy for automating user interactions UI that ask for namespaces. If UI
 * asks for namespaces of a Java code model element matching certain conditions
 * (set in the constructor of this class), this strategy intercepts UI and makes
 * it return no namespaces instead.
 * <p>
 * The reason for this is, currently Java -> PCM CPRs create a PCM DataType for
 * each generic type parameter, as PCM currently does not support representing
 * them by other specialised means. These DataTypes, however, should not have
 * Java Classifier correspondents, because generic type parameters are no
 * concrete classifiers.
 * 
 * @author Alp Torac Genc
 */
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
