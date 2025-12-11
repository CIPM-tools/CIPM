package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.logger.PcmToJavaChangePropagationLogger;

/**
 * Represents a user interaction during PCM to Java change propagation. This
 * abstract class also integrates {@link PcmUserInteractionManager} and
 * {@link PcmToJavaChangePropagationLogger}, since most concrete user
 * interactions use them in similar ways.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractUserInteraction implements CanModifyEntries {
	/**
	 * A list of {@link ConflictResolutionStrategy} instances that affected this
	 * user interaction
	 */
	private final List<ConflictResolutionStrategy> appliedCRSs = new ArrayList<>();
	/**
	 * A unique identifier for this instance
	 */
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
	 * Adds the given crs to the list of ConflictResolutionStrategy instances that
	 * affected this user interaction
	 */
	public void conflictResolutionStrategyApplied(ConflictResolutionStrategy crs) {
		this.appliedCRSs.add(crs);
	}

	/**
	 * @return A copy of the list of ConflictResolutionStrategy instances that
	 *         affected this user interaction
	 */
	public List<ConflictResolutionStrategy> getAppliedConflictResolutionStrategies() {
		return List.copyOf(appliedCRSs);
	}

	/**
	 * Refer to the implementation in concrete classes for more information.
	 * 
	 * @return A list of PCM elements that are the direct reason why this user
	 *         interaction is present.
	 */
	public abstract List<EObject> getTriggeringPCMelements();

	/**
	 * Defaults to {@link #getTriggeringPCMelements()}. Can be overridden in
	 * concrete classes, if other PCM elements are affected too.
	 * 
	 * @return A list of PCM elements that are involved in this user interaction.
	 *         Should logically include {@link #getTriggeringPCMelements()}.
	 */
	public List<EObject> getAffectedPCMElements() {
		return getTriggeringPCMelements();
	}

	/**
	 * Refer to the implementation in concrete classes for more information.
	 * 
	 * @return A list of Java code model elements that are involved in this user
	 *         interaction.
	 */
	public abstract List<EObject> getAffectedJavaElements();

	/**
	 * Fires the actual manual user interaction of this instance. This method is
	 * intended to be called by {@link PcmUserInteractionManager}, upon trying to
	 * retrieve a feature value or correspondence that does not exist yet should be
	 * computed.
	 */
	public abstract void performManualUserInteraction();

	/**
	 * Signals to this instance that a feature entry has been changed. Concrete
	 * classes should decide what to do with the changed feature entry.
	 * 
	 * @param featEntry The changed feature entry
	 */
	public abstract void getDesiredFeatureChangedValue(FeatureEntry featEntry);

	/**
	 * Signals to this instance that a correspondence entry has been changed.
	 * Concrete classes should decide what to do with the changed correspondence
	 * entry.
	 * 
	 * @param corEntry The changed correspondence entry
	 */
	public abstract void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry);

	/**
	 * A variant of {@link #hasDesiredFeature(EObject, EObject, EStructuralFeature)}
	 * with no Java code model element.
	 */
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		return this.hasDesiredFeature(triggeringPCMElement, null, feat);
	}

	/**
	 * @param triggeringPCMElement The PCM element that is the direct reason why
	 *                             this user interaction exists. Important for
	 *                             context purposes.
	 * 
	 * @return Whether this instance currently has the feature value for
	 *         {@code affectedJavaElement.feat}
	 */
	public abstract boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat);

	/**
	 * @param knownSide         The known correspondent
	 * @param correspondenceTag The tag of the correspondence
	 * 
	 * @return Whether this instance currently has information on the
	 *         correspondent(s) of knownSide with the given correspondenceTag
	 */
	public abstract boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag);

	/**
	 * @return The list of feature values that this user interaction requires to be
	 *         automated
	 */
	public abstract Set<FeatureEntry> getDesiredFeatures();

	/**
	 * @return The list of correspondences that this user interaction requires to be
	 *         automated
	 */
	public abstract Set<CorrespondenceEntry> getDesiredCorrespondences();

	/**
	 * A variant of
	 * {@link #isDesiredFeatureValuePresent(EObject, EObject, EStructuralFeature)}
	 * without a Java code model element.
	 */
	protected boolean isDesiredFeatureValuePresent(EObject triggeringPCMElement, EStructuralFeature feat) {
		return isDesiredFeatureValuePresent(triggeringPCMElement, null, feat);
	}

	/**
	 * @param triggeringPCMElement The PCM element that is the direct reason why
	 *                             this user interaction exists. Important for
	 *                             context purposes.
	 * 
	 * @return Whether {@link PcmUserInteractionManager} has a feature value for
	 *         {@code affectedJavaElement.feat}
	 */
	protected boolean isDesiredFeatureValuePresent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat);
	}

	/**
	 * Reports the given feature entry to {@link PcmUserInteractionManager}
	 */
	protected void reportDesiredFeatureValue(FeatureEntry featEntry) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, featEntry);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionReportedFeature(this, featEntry);
	}

	/**
	 * A variant of
	 * {@link #retrieveDesiredFeatureValueIfPresent(EObject, EObject, EStructuralFeature)}
	 * without Java code model element.
	 */
	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EStructuralFeature feat) {
		return retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, null, feat);
	}

	/**
	 * @param triggeringPCMElement The PCM element that is the direct reason why
	 *                             this user interaction exists. Important for
	 *                             context purposes.
	 * 
	 * @return The feature value for {@code affectedJavaElement.feat}, if it exists
	 *         in {@link PcmUserInteractionManager}. Otherwise null.
	 */
	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		PcmToJavaChangePropagationLogger.getInstance().userInteractionAskedForFeature(this, triggeringPCMElement,
				affectedJavaElement, feat, false);
		var result = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat,
				false);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionGotFeatureFor(this, result, triggeringPCMElement,
				affectedJavaElement, feat, false);
		return result;
	}

	/**
	 * @return Whether the correspondent(s) of knownSide with the given
	 *         correspondence tag are present in {@link PcmUserInteractionManager}.
	 */
	protected boolean isDesiredCorrespondencePresent(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.hasDesiredCorrespondence(knownSide, correspondenceTag);
	}

	/**
	 * Reports the given correspondence entry to {@link PcmUserInteractionManager}.
	 */
	protected void reportDesiredCorrespondence(CorrespondenceEntry corEntry) {
		PcmUserInteractionManager.setDesiredCorrespondence(this, corEntry);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionReportedCorrespondence(this, corEntry);
	}

	/**
	 * Cleans up and finalises this instance
	 */
	protected void finaliseUserInteraction() {
		PcmUserInteractionManager.removeUserInteraction(this);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionFinalised(this);
	}

	/**
	 * @return The correspondent(s) of knownSide with the given correspondence tag,
	 *         if they are present in {@link PcmUserInteractionManager}. Otherwise
	 *         null.
	 */
	protected CorrespondenceEntry retrieveDesiredCorrespondenceIfPresent(EObject knownSide, String correspondenceTag) {
		PcmToJavaChangePropagationLogger.getInstance().userInteractionAskedForCorrespondence(this, knownSide,
				correspondenceTag, false);
		var result = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, false);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionGotCorrespondenceFor(this, result, knownSide,
				correspondenceTag, true);
		return result;
	}

	/**
	 * @return Whether this user interaction has all the feature values and
	 *         correspondences it needs, in order for the manual user interaction to
	 *         be spared.
	 */
	public abstract boolean isResolved();

	/**
	 * Attempts to retrieve all necessary feature values and correspondences from
	 * {@link PcmUserInteractionManager}. Those that the
	 * {@link PcmUserInteractionManager} has will be automatically retrieved. The
	 * rest of them will be acquired through manual user interaction.
	 */
	public abstract void resolveAll();

	/**
	 * A variant of {@link #resolveForFeature(EObject, EObject, EStructuralFeature)}
	 * without a Java code model element.
	 */
	public Object resolveForFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		return resolveForFeature(triggeringPCMElement, null, feat);
	}

	/**
	 * @param triggeringPCMElement The PCM element that is the direct reason why
	 *                             this user interaction exists. Important for
	 *                             context purposes.
	 * 
	 * @return Attempts to retrieve the feature value for
	 *         {@code affectedJavaElement.feat} from
	 *         {@link PcmUserInteractionManager}. If it is present, automatically
	 *         retrieves it. Otherwise, acquires it through manual user interaction.
	 */
	public Object resolveForFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		PcmToJavaChangePropagationLogger.getInstance().userInteractionAskedForFeature(this, triggeringPCMElement,
				affectedJavaElement, feat, true);
		var result = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat,
				true);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionGotFeatureFor(this, result, triggeringPCMElement,
				affectedJavaElement, feat, true);
		return result;
	}

	/**
	 * @return Attempts to retrieve the correspondent(s) of knownSide with the given
	 *         correspondence tag from {@link PcmUserInteractionManager}. If they
	 *         are present, automatically retrieves them. Otherwise, acquires them
	 *         through manual user interaction.
	 */
	public CorrespondenceEntry resolveForCorrespondence(EObject knownSide, String correspondenceTag) {
		PcmToJavaChangePropagationLogger.getInstance().userInteractionAskedForCorrespondence(this, knownSide,
				correspondenceTag, true);
		var result = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, true);
		PcmToJavaChangePropagationLogger.getInstance().userInteractionGotCorrespondenceFor(this, result, knownSide,
				correspondenceTag, true);
		return result;
	}
}
