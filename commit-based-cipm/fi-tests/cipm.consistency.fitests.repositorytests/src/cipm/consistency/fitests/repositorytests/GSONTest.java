package cipm.consistency.fitests.repositorytests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.parser.FileUtil;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.DefaultTimeMeasurementDataStructure;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GSONLoadingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GSONPersistingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementDataStructure;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementLoadingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementPersistingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurerKeyType;

/**
 * Contains tests for GSON-based, time measurement related classes that ensure
 * they work as intended.
 * <p>
 * Note: Make sure to keep the time measurement file at
 * {@link #gsonTestResourceRootPath} up to date, if there are format changes, as
 * this test otherwise does not account for new format changes within time
 * sample files.
 * 
 * @author Alp Torac Genc
 */
public class GSONTest {
	private static final DateTimeFormatter fileContentTimePattern = DateTimeFormatter.ISO_DATE_TIME;

	@SuppressWarnings("unchecked")
	private static final ITimeMeasurementLoadingStrategy loadingStrat = new GSONLoadingStrategy(fileContentTimePattern,
			DefaultTimeMeasurementDataStructure.class,
			new Class[] { GeneralTimeMeasurementTag.class, RepoTimeMeasurementTag.class });
	private static final ITimeMeasurementPersistingStrategy persistingStrat = new GSONPersistingStrategy(
			fileContentTimePattern);

	private static final Path gsonTestResourceRootPath = Path.of("gsonTestResource").toAbsolutePath();
	private static final Path formerTimeMeasurementPath = gsonTestResourceRootPath
			.resolve("timeMeasurementSample.json");
	private static final Path newTimeMeasurementPath = gsonTestResourceRootPath
			.resolve("gsonTestNewTimeMeasurement.json");

	@BeforeEach
	public void setUp() {
		var timeMeasurementsRootDir = gsonTestResourceRootPath.toFile();
		if (!timeMeasurementsRootDir.exists() || timeMeasurementsRootDir.listFiles().length == 0) {
			Assertions.fail("Test file missing at " + formerTimeMeasurementPath.toString());
		}
	}

	@AfterEach
	public void tearDown() {
		if (newTimeMeasurementPath != null && newTimeMeasurementPath.toFile().exists()) {
			try {
				Files.delete(newTimeMeasurementPath);
			} catch (IOException e) {
				e.printStackTrace();
				Assertions.fail(e);
			}
		}
	}

	/**
	 * Asserts that the content of the file under the given path is not blank
	 * 
	 * @return Contents of the given path as a string instance
	 */
	private String readTimeMeasurement(Path pathToTimeMeasurementToRead) {
		String content = null;
		try {
			content = Files.readString(pathToTimeMeasurementToRead);
			Assertions.assertFalse(content.isBlank());
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}

		return content;
	}

	/**
	 * @return The data structure parsed from the file at the given path
	 */
	private ITimeMeasurementDataStructure loadTimeMeasurement(Path pathToTimeMeasurementToLoad) {
		return loadingStrat.load(pathToTimeMeasurementToLoad);
	}

	/**
	 * Persists the given data structure under the given path
	 */
	private void persistTimeMeasurement(ITimeMeasurementDataStructure dataStructure, Path savePath) {
		persistingStrat.save(dataStructure, savePath);
	}

	/**
	 * Asserts that the given data structure is parsed correctly and does not have
	 * any unexpected empty fields.
	 */
	private void assertDataStructureIntact(ITimeMeasurementDataStructure timeMeasurements) {
		Assertions.assertNotNull(timeMeasurements.getTimeMeasurerDescription());

		Assertions.assertNotNull(timeMeasurements.getEndTime());

		Assertions.assertNotNull(timeMeasurements.getStartTime());

		var entries = timeMeasurements.getTimeMeasurementEntries();
		Assertions.assertNotNull(entries);
		Assertions.assertFalse(entries.isEmpty());

		timeMeasurements.getTimeMeasurementEntries().forEach((e) -> {
			var key = e.getKey();
			Assertions.assertNotNull(key);
			key.getKeys().entrySet().forEach((ke) -> {
				Assertions.assertNotNull(ke.getKey());
				Assertions.assertTrue(ParserTestTimeMeasurerKeyType.class.isAssignableFrom(ke.getKey().getClass()));
				Assertions.assertNotNull(ke.getValue());
			});
			Assertions.assertNotNull(e.getTag());
			Assertions.assertTrue(ITimeMeasurementTag.class.isAssignableFrom(e.getTag().getClass()));
		});

		var tu = timeMeasurements.getTimeUnit();
		Assertions.assertNotNull(tu);
		Assertions.assertTrue(TimeUnit.class.isAssignableFrom(tu.getClass()));
	}

	/**
	 * Ensures that loading previously saved time measurements works as intended
	 */
	@Test
	public void testDataStructureLoading() {
		var timeMeasurements = this.loadTimeMeasurement(formerTimeMeasurementPath);
		this.assertDataStructureIntact(timeMeasurements);
	}

	/**
	 * Ensures that loaded and re-saved time measurements can be parsed as intended
	 */
	@Test
	public void testSavedDataStructureLoading() {
		var timeMeasurements = this.loadTimeMeasurement(formerTimeMeasurementPath);
		this.persistTimeMeasurement(timeMeasurements, newTimeMeasurementPath);
		var persistedTimeMeasurements = this.loadTimeMeasurement(newTimeMeasurementPath);
		this.assertDataStructureIntact(persistedTimeMeasurements);
	}

	/**
	 * Ensures that loaded and re-saved time measurements can be parsed as intended
	 * and the content of their files are equal
	 */
	@Test
	public void testSavedDataStructureLoading_ContentEquality() {
		var formerFileContent = this.readTimeMeasurement(formerTimeMeasurementPath);
		var formerTimeMeasurements = this.loadTimeMeasurement(formerTimeMeasurementPath);
		this.persistTimeMeasurement(formerTimeMeasurements, newTimeMeasurementPath);

		var newFileContent = this.readTimeMeasurement(newTimeMeasurementPath);

		/*
		 * Remove redundant training zeroes comparing to address cases, where GSON
		 * serialises Long instances as Double instances, i.e. with trailing zeroes
		 * (".0").
		 * 
		 * Make sure to match the comma after the trailing zero too, so that start and
		 * end times are not affected, as their formatting may include decimal numbers.
		 */
		newFileContent = newFileContent.replaceAll("\\.0,", ",");

		/*
		 * Ensure that serialising produces the same file
		 * 
		 * Remove all white-spaces from the read file content to avoid cross-platform
		 * issues, especially due to the differing line separators.
		 * 
		 * This is particularly important, if the time measurement sample file at
		 * gsonTestResourceRootPath is included into the GIT repository, as GIT may
		 * replace existing line separators with the one for UNIX systems or add a line
		 * break to the end of the file.
		 */
		Assertions.assertEquals(FileUtil.getEffectiveText(formerFileContent),
				FileUtil.getEffectiveText(newFileContent));

		var newTimeMeasurements = this.loadTimeMeasurement(newTimeMeasurementPath);

		// Ensure that deserialising freshly serialised instance produces an equal
		// instance
		Assertions.assertTrue(formerTimeMeasurements.equals(newTimeMeasurements));
	}
}
