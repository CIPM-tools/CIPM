package cipm.consistency.cpr.pcmjava.logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.net4j.util.collection.Pair;

public class PcmUserInteractionTimeStatistics {
	private static PcmUserInteractionTimeStatistics instance;

	private ChronoUnit timeUnit = ChronoUnit.MILLIS;

	private List<Pair<LocalDateTime, LocalDateTime>> timeEntries = new ArrayList<>();

	private LocalDateTime timeMeasurementStartTime;
	private LocalDateTime timeMeasurementEndTime;

	private long propagationTimeInMillis = 0;

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
			propagationTimeInMillis += timeUnit.between(entry.getElement1(), entry.getElement2());
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
		}
	}

	public void reset() {
		timeEntries.clear();
		timeMeasurementStartTime = null;
		timeMeasurementEndTime = null;
		propagationTimeInMillis = 0;
	}
}
