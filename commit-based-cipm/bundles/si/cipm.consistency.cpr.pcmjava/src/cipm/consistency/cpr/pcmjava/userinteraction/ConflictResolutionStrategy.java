package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.cpr.pcmjava.logger.PcmToJavaChangePropagationLogger;

/**
 * Encapsulates strategies for automating user interactions under certain
 * conditions. Integrates {@link PcmUserInteractionManager} and
 * {@link PcmToJavaChangePropagationLogger}, since concrete classes'
 * interactions with them are mostly similar.
 * <p>
 * Use {@link #applyIfPossible(AbstractUserInteraction)} to attempt to apply
 * this strategy to user interactions.
 * 
 * @author Alp Torac Genc
 */
public abstract class ConflictResolutionStrategy implements CanModifyEntries {
	private String id;

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setID(String id, boolean forceSet) {
		if (forceSet || this.id == null) {
			this.id = id;
		}
	}

	@Override
	public String toString() {
		if (getID() != null)
			return getID();
		return super.toString();
	}

	/**
	 * Attempts to apply this strategy for the given user interaction
	 * 
	 * @return Whether this strategy was applied to the given user interaction
	 */
	public boolean applyIfPossible(AbstractUserInteraction userInteraction) {
		var applicable = checkInternalApplicationConditions(userInteraction);
		if (applicable) {
			var uiWasResolved = userInteraction.isResolved();
			applyStrategy(userInteraction);
			userInteraction.conflictResolutionStrategyApplied(this);
			PcmToJavaChangePropagationLogger.getInstance().conflictResolutionStrategyAppliedFor(this, userInteraction);
			var uiIsResolved = userInteraction.isResolved();
			if (!uiWasResolved && uiIsResolved) {
				PcmToJavaChangePropagationLogger.getInstance()
						.conflictResolutionStrategyInterceptedUserInteraction(this, userInteraction);
			} else if (!uiWasResolved && !uiIsResolved) {
				PcmToJavaChangePropagationLogger.getInstance()
						.conflictResolutionStrategyPartiallyInterceptedUserInteraction(this, userInteraction);
			}
		}
		return applicable;
	}

	/**
	 * Reports the given feature entry to {@link PcmUserInteractionManager}, in
	 * efforts to automate the given user interaction.
	 */
	protected void reportDesiredFeatureValue(AbstractUserInteraction userInteraction, FeatureEntry featEntry) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, featEntry);
		PcmToJavaChangePropagationLogger.getInstance().conflictResolutionStrategyReportedFeature(userInteraction, this,
				featEntry);
	}

	/**
	 * Reports the given correspondence entry to {@link PcmUserInteractionManager},
	 * in efforts to automate the given user interaction.
	 */
	protected void reportDesiredCorrespondence(AbstractUserInteraction userInteraction, CorrespondenceEntry corEntry) {
		PcmUserInteractionManager.setDesiredCorrespondence(this, corEntry);
		PcmToJavaChangePropagationLogger.getInstance().conflictResolutionStrategyReportedCorrespondence(userInteraction,
				this, corEntry);
	}

	/**
	 * Applies the actual logic of this strategy to the given user interaction.
	 * Concrete classes should implement their user interaction automation logic
	 * here.
	 */
	protected abstract void applyStrategy(AbstractUserInteraction userInteraction);

	/**
	 * @return Whether this strategy is applicable to the given user interaction.
	 */
	protected abstract boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction);

	/**
	 * A variant of
	 * {@link #isRelevantFor(AbstractUserInteraction, List, boolean, List, boolean, List, boolean)}
	 * that does not consider any sub-list matches.
	 */
	public boolean isRelevantFor(AbstractUserInteraction userInteraction, List<EObject> pcmContext,
			List<EObject> javaContext, List<EStructuralFeature> featList) {
		return isRelevantFor(userInteraction, pcmContext, false, javaContext, false, featList, false);
	}

	/**
	 * @param userInteraction           A given user interaction
	 * @param pcmContext                A list of PCM elements that this strategy is
	 *                                  applicable for
	 * @param considerPcmContextSubset  Whether this strategy still applies, if a
	 *                                  only sub-list of pcmContext is involved in
	 *                                  userInteraction
	 * @param javaContext               A list of Java code model elements that this
	 *                                  strategy is applicable for
	 * @param considerJavaContextSubset Whether this strategy still applies, if a
	 *                                  only sub-list of javaContext is involved in
	 *                                  userInteraction
	 * @param featList                  A list of EStructuralFeatures that this
	 *                                  strategy is applicable for
	 * @param considerFeatListSubset    Whether this strategy still applies, if a
	 *                                  only sub-list of featList is involved in
	 *                                  userInteraction
	 * 
	 * @return Whether this strategy is relevant, given the parameters
	 */
	public boolean isRelevantFor(AbstractUserInteraction userInteraction, List<EObject> pcmContext,
			boolean considerPcmContextSubset, List<EObject> javaContext, boolean considerJavaContextSubset,
			List<EStructuralFeature> featList, boolean considerFeatListSubset) {
		return (isApplicableForTriggeringPCMElements(userInteraction, pcmContext, considerPcmContextSubset)
				|| isApplicableForJavaContext(userInteraction, javaContext, considerJavaContextSubset))
				&& isApplicableForFeatList(userInteraction, featList, considerFeatListSubset);
	}

	/**
	 * @param userInteraction          A given user interaction
	 * @param pcmContext               A list of PCM elements that this strategy is
	 *                                 applicable for
	 * @param considerPcmContextSubset Whether this strategy still applies, if a
	 *                                 only sub-list of pcmContext is involved in
	 *                                 userInteraction
	 * @return Whether this strategy is relevant, given the parameters
	 */
	public boolean isApplicableForTriggeringPCMElements(AbstractUserInteraction userInteraction,
			List<EObject> pcmContext, boolean considerPcmContextSubset) {
		var uiTriggeringPCMElems = userInteraction.getTriggeringPCMelements();
		if (uiTriggeringPCMElems.isEmpty())
			return true;

		if (pcmContext == null || pcmContext.isEmpty())
			return false;

		return (considerPcmContextSubset && uiTriggeringPCMElems.stream()
				.anyMatch((uipcm) -> pcmContext.stream().anyMatch((pcm) -> EcoreUtil.equals(uipcm, pcm))))

				||

				(!considerPcmContextSubset && uiTriggeringPCMElems.size() == pcmContext.size()
						&& uiTriggeringPCMElems.stream().allMatch(
								(uipcm) -> pcmContext.stream().anyMatch((pcm) -> EcoreUtil.equals(uipcm, pcm))));
	}

	/**
	 * @param userInteraction           A given user interaction
	 * @param javaContext               A list of Java code model elements that this
	 *                                  strategy is applicable for
	 * @param considerJavaContextSubset Whether this strategy still applies, if a
	 *                                  only sub-list of javaContext is involved in
	 *                                  userInteraction
	 * 
	 * @return Whether this strategy is relevant, given the parameters
	 */
	public boolean isApplicableForJavaContext(AbstractUserInteraction userInteraction, List<EObject> javaContext,
			boolean considerJavaContextSubset) {
		var uiJavaContext = userInteraction.getAffectedJavaElements();
		if (uiJavaContext.isEmpty())
			return true;

		if (javaContext == null || javaContext.isEmpty())
			return false;

		return (considerJavaContextSubset && uiJavaContext.stream()
				.anyMatch((uijc) -> javaContext.stream().anyMatch((jc) -> EcoreUtil.equals(uijc, jc))))

				||

				(!considerJavaContextSubset && uiJavaContext.size() == javaContext.size() && uiJavaContext.stream()
						.allMatch((uijc) -> javaContext.stream().anyMatch((jc) -> EcoreUtil.equals(uijc, jc))));
	}

	/**
	 * @param userInteraction        A given user interaction
	 * @param featList               A list of EStructuralFeatures that this
	 *                               strategy is applicable for
	 * @param considerFeatListSubset Whether this strategy still applies, if a only
	 *                               sub-list of featList is involved in
	 *                               userInteraction
	 * 
	 * @return Whether this strategy is relevant, given the parameters
	 */
	public boolean isApplicableForFeatList(AbstractUserInteraction userInteraction, List<EStructuralFeature> featList,
			boolean considerFeatListSubset) {
		var uiFeatList = userInteraction.getDesiredFeatures();
		if (uiFeatList.isEmpty())
			return true;

		if (featList == null || featList.isEmpty())
			return false;

		return (considerFeatListSubset
				&& uiFeatList.stream().anyMatch((fe) -> featList.stream().anyMatch((fle) -> fe.featureEquals(fle))))

				||

				(!considerFeatListSubset && uiFeatList.stream()
						.allMatch((fe) -> featList.stream().anyMatch((fle) -> fe.featureEquals(fle))));
	}
}
