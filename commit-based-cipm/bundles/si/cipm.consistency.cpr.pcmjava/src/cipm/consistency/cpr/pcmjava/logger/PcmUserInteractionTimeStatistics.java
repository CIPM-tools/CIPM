package cipm.consistency.cpr.pcmjava.logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.net4j.util.collection.Pair;

/**
 * A class that aggregates information on the time aspect of PCM to Java change
 * propagation.
 * 
 * <p>
 * Attributes of this class can be serialised via the {@link GSON} library.
 * 
 * @author Alp Torac Genc
 */
public class PcmUserInteractionTimeStatistics {
	private static PcmUserInteractionTimeStatistics instance;

	private ChronoUnit timeUnit = ChronoUnit.MILLIS;

	/**
	 * Time entries here consist of 2 {@link LocalDateTime} instances that stand for
	 * the start and end time points of individual measurements. Summing up the time
	 * differences of all these entries gives the total execution time of the PCM to
	 * Java change propagation.
	 */
	private List<Pair<LocalDateTime, LocalDateTime>> timeEntries = new ArrayList<>();

	private LocalDateTime timeMeasurementStartTime;
	private LocalDateTime timeMeasurementEndTime;

	/**
	 * Execution time of the PCM to Java change propagation (exclusively the
	 * automatic part of the change propagation)
	 */
	private long propagationTimeWithoutUserInteractionsInMillis = 0;
	/**
	 * Execution time of the PCM to Java change propagation (including the manual
	 * parts of the change propagation)
	 */
	private long propagationTimeWithUserInteractionsInMillis = 0;

	private PcmUserInteractionTimeStatistics() {
	}

	public static PcmUserInteractionTimeStatistics getInstance() {
		if (instance == null)
			instance = new PcmUserInteractionTimeStatistics();
		return instance;
	}

	private Pair<LocalDateTime, LocalDateTime> getMostRecentEntry() {
		return !timeEntries.isEmpty() ? timeEntries.get(timeEntries.size() - 1) : null;
	}

	/**
	 * @return Complete the most recent time entry by setting the end time point for
	 *         that entry.
	 */
	private Pair<LocalDateTime, LocalDateTime> completeMostRecentTimeEntry() {
		var mostRecentEntry = getMostRecentEntry();
		if (mostRecentEntry != null && mostRecentEntry.getElement2() == null) {
			var now = LocalDateTime.now();
			mostRecentEntry.setElement2(now);
		}
		return mostRecentEntry;
	}

	private void startNewTimeEntry() {
		var now = LocalDateTime.now();
		timeEntries.add(new Pair<>(now, null));
	}

	private void addMostRecentTimeEntryToPropagationTime() {
		var entry = completeMostRecentTimeEntry();
		if (entry != null) {
			propagationTimeWithoutUserInteractionsInMillis += timeUnit.between(entry.getElement1(),
					entry.getElement2());
		}
	}

	public void startPropagationTimeMeasurement() {
		startNewTimeEntry();
	}

	public void endPropagationTimeMeasurement() {
		addMostRecentTimeEntryToPropagationTime();
	}

	public void finaliseTimeMeasurement() {
		if (!timeEntries.isEmpty()) {
			timeMeasurementStartTime = timeEntries.get(0).getElement1();
			timeMeasurementEndTime = completeMostRecentTimeEntry().getElement2();
			propagationTimeWithUserInteractionsInMillis = timeUnit.between(timeMeasurementStartTime,
					timeMeasurementEndTime);
		}
	}

	public long getPropagationTimeWithoutUserInteractionsInMillis() {
		return propagationTimeWithoutUserInteractionsInMillis;
	}

	public long getPropagationTimeWithUserInteractionsInMillis() {
		return propagationTimeWithUserInteractionsInMillis;
	}

	public void reset() {
		timeEntries.clear();
		timeMeasurementStartTime = null;
		timeMeasurementEndTime = null;
		propagationTimeWithoutUserInteractionsInMillis = 0;
	}
}
