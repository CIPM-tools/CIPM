package cipm.consistency.fitests.repositorytests;

import java.time.format.DateTimeFormatter;

import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.DefaultTimeMeasurementDataStructure;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GSONLoadingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;

public class DefaultGSONLoadingStrategy extends GSONLoadingStrategy {
	@SuppressWarnings("unchecked")
	public DefaultGSONLoadingStrategy() {
		super(DateTimeFormatter.ISO_DATE_TIME, DefaultTimeMeasurementDataStructure.class,
				new Class[] { GeneralTimeMeasurementTag.class, RepoTimeMeasurementTag.class });
	}
}
