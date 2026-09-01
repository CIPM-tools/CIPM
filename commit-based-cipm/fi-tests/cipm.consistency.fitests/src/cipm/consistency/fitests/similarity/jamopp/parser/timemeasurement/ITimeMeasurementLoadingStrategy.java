package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.nio.file.Path;

/**
 * An interface for classes encapsulating the means to load
 * {@link ITimeMeasurementDataStructure} from files.<br>
 * <br>
 * There is no obligation for the concrete implementors to support all data
 * structures. Therefore, it is advised to check the documentation of the
 * concrete implementation before use.
 * 
 * @author Alp Torac Genc
 */
public interface ITimeMeasurementLoadingStrategy {
	/**
	 * Arbitrary concrete implementors may not be able to parse all types of
	 * {@link ITimeMeasurementDataStructure} from their files. Refer to the concrete
	 * implementor for more details about the return type.
	 * 
	 * @param pathToDataStructureFile The absolute path to the file, which contains
	 *                                the serialised contents of a
	 *                                {@link ITimeMeasurementDataStructure} instance
	 * @return The data structure stored inside the file at the given path
	 */
	public ITimeMeasurementDataStructure load(Path pathToDataStructureFile);
}
