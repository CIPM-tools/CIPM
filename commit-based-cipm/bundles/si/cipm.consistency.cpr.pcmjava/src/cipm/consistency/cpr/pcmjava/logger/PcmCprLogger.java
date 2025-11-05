package cipm.consistency.cpr.pcmjava.logger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;
import tools.vitruv.change.interaction.builder.InteractionBuilder;

public class PcmCprLogger {
	// TODO Implement
	private static PcmCprLogger instance;
	private static final List<PcmCprEntry> entries = new ArrayList<>();

	private PcmCprLogger() {
	}

	public static PcmCprLogger getInstance() {
		if (instance == null) {
			instance = new PcmCprLogger();
		}
		return instance;
	}

	public void manualUserInteractionTriggered(AbstractUserInteraction abstractUserInteraction) {
		// TODO Auto-generated method stub

	}

	public void manualUserInteractionPerformed(AbstractUserInteraction abstractUserInteraction) {
		// TODO Auto-generated method stub

	}

	public void userInteractionAskedForCorrespondence(AbstractUserInteraction abstractUserInteraction,
			EObject knownSide, String correspondenceTag, boolean computeIfAbsent) {
		// TODO Auto-generated method stub

	}

	public void userInteractionGotCorrespondenceFor(AbstractUserInteraction abstractUserInteraction,
			CorrespondenceEntry result, EObject knownSide, String correspondenceTag, boolean computeIfAbsent) {
		// TODO Auto-generated method stub

	}

	public void userInteractionAskedForFeature(AbstractUserInteraction abstractUserInteraction,
			EObject triggeringPCMElement, EObject affectedJavaElement, EStructuralFeature feat,
			boolean computeIfAbsent) {
		// TODO Auto-generated method stub

	}

	public void userInteractionGotFeatureFor(AbstractUserInteraction abstractUserInteraction, Object result,
			EObject triggeringPCMElement, EObject affectedJavaElement, EStructuralFeature feat,
			boolean computeIfAbsent) {
		// TODO Auto-generated method stub

	}

	public void userInteractionFinalised(AbstractUserInteraction abstractUserInteraction) {
		// TODO Auto-generated method stub

	}

	public void userInteractionFinalising(AbstractUserInteraction abstractUserInteraction) {
		// TODO Auto-generated method stub

	}

	public void userInteractionReportingCorrespondence(AbstractUserInteraction abstractUserInteraction,
			CorrespondenceEntry corEntry) {
		// TODO Auto-generated method stub

	}

	public void userInteractionReportedCorrespondence(AbstractUserInteraction abstractUserInteraction,
			CorrespondenceEntry corEntry) {
		// TODO Auto-generated method stub

	}

	public void userInteractionReportingFeature(AbstractUserInteraction abstractUserInteraction,
			FeatureEntry featEntry) {
		// TODO Auto-generated method stub

	}

	public void userInteractionReportedFeature(AbstractUserInteraction abstractUserInteraction,
			FeatureEntry featEntry) {
		// TODO Auto-generated method stub

	}

	public <T> void manualUserInteractionHappening(AbstractUserInteraction abstractUserInteraction,
			InteractionBuilder<T, ?> vitruvUserInteraction) {
		// TODO Auto-generated method stub

	}

	public <T> void manualUserInteractionHappened(AbstractUserInteraction abstractUserInteraction, T result,
			InteractionBuilder<T, ?> vitruvUserInteraction) {
		// TODO Auto-generated method stub

	}

	public void conflictResolutionStrategyApplyingFor(ConflictResolutionStrategy conflictResolutionStrategy,
			AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub

	}

	public void conflictResolutionStrategyAppliedFor(ConflictResolutionStrategy conflictResolutionStrategy,
			AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub

	}

	public void conflictResolutionStrategyRegistered(ConflictResolutionStrategy strat) {
		// TODO Auto-generated method stub

	}

	public void conflictResolutionStrategyRemoved(ConflictResolutionStrategy strat) {
		// TODO Auto-generated method stub

	}

	public void userInteractionRegistered(AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub

	}

	public void userInteractionRemoved(AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub

	}

}
