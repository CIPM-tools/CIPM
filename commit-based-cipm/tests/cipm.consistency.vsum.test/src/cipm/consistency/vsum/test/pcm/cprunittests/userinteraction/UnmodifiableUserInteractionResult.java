package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;

public class UnmodifiableUserInteractionResult extends AbstractUserInteractionResult {
	public UnmodifiableUserInteractionResult(Collection<IUserInteractionWrapper> relevantUserInteractions,
			Map<EStructuralFeature, Object> results) {
		super(relevantUserInteractions, results);
	}
}
