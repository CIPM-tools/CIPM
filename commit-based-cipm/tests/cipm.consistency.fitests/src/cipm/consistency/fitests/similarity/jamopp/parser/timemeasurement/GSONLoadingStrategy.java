package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Contains the means to parse JSON files using {@link Gson}. The used Gson
 * instance is able to parse {@link DefaultTimeMeasurementDataStructure}
 * instances. Whether it can parse further data structures depends on their
 * structure. <br>
 * <br>
 * Note: {@link Gson} may require type adapters (especially
 * {@link JsonDeserializer}) for cases, where the data structure to be parsed
 * internally declares attributes with non-constructible types (abstract classes
 * or interfaces). In such cases, this class has to be extended. Without the
 * necessary type adapters, parse attempt may result in exceptions.
 * 
 * @author Alp Torac Genc
 */
public class GSONLoadingStrategy implements ITimeMeasurementLoadingStrategy {
	/**
	 * @see {@link #GSONLoadingStrategy(DateTimeFormatter, Class[])}
	 */
	private final DateTimeFormatter fileContentDateFormatter;
	/**
	 * @see {@link #GSONLoadingStrategy(DateTimeFormatter, Class[])}
	 */
	private final Set<ITimeMeasurementTag> possibleTags = new HashSet<ITimeMeasurementTag>();
	/**
	 * @see {@link #getDataStructureClassToParse()}
	 */
	private final Class<? extends ITimeMeasurementDataStructure> dataStructureClassToParse;

	public GSONLoadingStrategy(DateTimeFormatter fileContentDateFormatter,
			Class<? extends ITimeMeasurementDataStructure> dataStructureClassToParse) {
		this(fileContentDateFormatter, dataStructureClassToParse, null);
	}

	/**
	 * @param fileContentDateFormatter  {@link #getFileContentDateFormatter()}
	 * @param dataStructureClassToParse {@link #getDataStructureClassToParse()}
	 * @param possibleTagSubclasses     {@link #addTagSubclass(Class)}
	 */
	public GSONLoadingStrategy(DateTimeFormatter fileContentDateFormatter,
			Class<? extends ITimeMeasurementDataStructure> dataStructureClassToParse,
			Class<ITimeMeasurementTag>[] possibleTagSubclasses) {
		this.fileContentDateFormatter = fileContentDateFormatter;
		this.dataStructureClassToParse = dataStructureClassToParse;

		if (possibleTagSubclasses != null) {
			for (var ts : possibleTagSubclasses) {
				this.addTagSubclass(ts);
			}
		}
	}

	/**
	 * @return The {@link Gson} instance to use while parsing the data structure
	 */
	protected Gson buildGSON() {
		return new GsonBuilder().registerTypeHierarchyAdapter(LocalDateTime.class, this.getDateDeserializer())
				.registerTypeHierarchyAdapter(ITimeMeasurementTag.class, this.getTagDeserializer()).create();
	}

	/**
	 * 
	 * @param possibleTagSubclass A concrete (enum) sub-class of
	 *                            {@link ITimeMeasurementTag} that should be
	 *                            considered, while parsing the data structure.
	 *                            These sub-classes have to be provided manually, as
	 *                            there is no clean way to access all sub-types of a
	 *                            given type programmatically
	 */
	public void addTagSubclass(Class<ITimeMeasurementTag> possibleTagSubclass) {
		if (possibleTagSubclass != null) {
			for (var ec : possibleTagSubclass.getEnumConstants()) {
				this.possibleTags.add(ec);
			}
		}
	}

	/**
	 * @implSpec Attempts to parse an instance of
	 *           {@link #getDataStructureClassToParse()} from the file at the given
	 *           absolute path. Throws {@link IllegalArgumentException} if an
	 *           {@link IOException} occurs in the process.
	 */
	@Override
	public ITimeMeasurementDataStructure load(Path pathToDataStructureFile) {
		var gson = this.buildGSON();
		ITimeMeasurementDataStructure result = null;
		try (var r = new FileReader(pathToDataStructureFile.toFile())) {
			result = gson.fromJson(gson.newJsonReader(r), this.getDataStructureClassToParse());
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not read the file at: " + pathToDataStructureFile.toString(), e);
		}
		return result;
	}

	/**
	 * @return The date format, which will be used while parsing dates
	 */
	public DateTimeFormatter getFileContentDateFormatter() {
		return this.fileContentDateFormatter;
	}

	/**
	 * {@link Gson} must know the type of the instance it is attempting to parse,
	 * hence the need for the underlying attribute.
	 * 
	 * @return A concrete sub-type of {@link ITimeMeasurementDataStructure} that
	 *         will be attempted to be parsed from its file.
	 */
	public Class<? extends ITimeMeasurementDataStructure> getDataStructureClassToParse() {
		return this.dataStructureClassToParse;
	}

	private JsonDeserializer<LocalDateTime> getDateDeserializer() {
		return new JsonDeserializer<LocalDateTime>() {
			@Override
			public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				var date = json.getAsString();
				return LocalDateTime.from(getFileContentDateFormatter().parse(date));
			}
		};
	}

	private JsonDeserializer<ITimeMeasurementTag> getTagDeserializer() {
		return new JsonDeserializer<ITimeMeasurementTag>() {
			@Override
			public ITimeMeasurementTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				var tagString = json.getAsString();
				ITimeMeasurementTag tag = null;
				for (var tagEnum : possibleTags) {
					if (tagString.equals(tagEnum.toString())) {
						tag = tagEnum;
						break;
					}
				}
				return tag;
			}
		};
	}
}
