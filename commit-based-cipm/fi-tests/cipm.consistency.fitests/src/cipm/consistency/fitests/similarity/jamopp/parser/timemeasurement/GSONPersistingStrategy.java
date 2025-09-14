package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Contains the means to save the time measurements to JSON files using
 * {@link Gson}. While saving the time measurements, all (non-static) attributes
 * of {@link ITimeMeasurementDataStructure} containing time measurements will be
 * translated to JSON objects and then written to a file. <br>
 * <br>
 * It can be used in conjunction with concrete sub-types of
 * {@link ITimeMeasurementDataStructure}, as long as {@link Gson} can parse them
 * without further assistance.
 * 
 * @author Alp Torac Genc
 */
public class GSONPersistingStrategy implements ITimeMeasurementPersistingStrategy {
	/**
	 * @see {@link #GSONPersistingStrategy(String, DateTimeFormatter)}
	 */
	private DateTimeFormatter fileContentTimePattern;

	/**
	 * @param fileContentTimePattern The pattern that will be used to transform a
	 *                               date to a string, which will be written into
	 *                               the saved measurements file.
	 */
	public GSONPersistingStrategy(DateTimeFormatter fileContentTimePattern) {
		this.fileContentTimePattern = fileContentTimePattern;
	}

	public void save(ITimeMeasurementDataStructure dataStructure, Path measurementsSavePath) {
		// Ensure that all necessary parent directories exist prior to saving
		var measurementsFile = measurementsSavePath.toFile();
		var measurementsFileParent = measurementsFile.getParentFile();
		if (measurementsFileParent != null) {
			measurementsFileParent.mkdirs();
		}

		var gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeHierarchyAdapter(LocalDateTime.class, this.getDateAdapter()).create();
		try (BufferedWriter writer = Files.newBufferedWriter(measurementsSavePath);
				var gsonWriter = gson.newJsonWriter(writer)) {
			gson.toJson(dataStructure, dataStructure.getClass(), gsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					String.format("Could not save the expected similarity results at %s", measurementsSavePath), e);
		}
	}

	private JsonSerializer<LocalDateTime> getDateAdapter() {
		return new JsonSerializer<LocalDateTime>() {
			@Override
			public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(fileContentTimePattern.format(src));
			}
		};
	}
}
