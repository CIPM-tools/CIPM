package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.nio.file.Path;

/**
 * An interface for classes that encapsulate the means to persist
 * {@link ITimeMeasurementDataStructure} instances. <br>
 * <br>
 * There is no obligation for the concrete implementors to support all data
 * structures. Therefore, it is advised to check the documentation of the
 * concrete implementation before use.
 * 
 * @author Alp Torac Genc
 */
public interface ITimeMeasurementPersistingStrategy {
	/**
	 * Saves the given dataStructure according to the concrete implementation.
	 * 
	 * @param dataStructure        The data structure to be persisted
	 * @param measurementsSavePath The absolute path, at which the given data
	 *                             structure should be persisted
	 */
	public void save(ITimeMeasurementDataStructure dataStructure, Path measurementsSavePath);
}
