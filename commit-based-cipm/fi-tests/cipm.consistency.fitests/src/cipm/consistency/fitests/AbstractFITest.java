package cipm.consistency.fitests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.commitintegration.diff.util.JavaModelComparator;

/**
 * The class to be used by test classes, whose purpose is to test FirstInstance.
 * Offers utility and assertion methods to help implement unit tests cleaner, as
 * well as logger integration that help keep up with the program flow.
 * 
 * @author atora
 */
public abstract class AbstractFITest {
	private static final Logger LOGGER = Logger.getLogger("cipm." + AbstractFITest.class.getSimpleName());

	/**
	 * The path to the directory, under which copies of resources to be used during
	 * run time can be found.
	 */
	private final String workingResourcesPath = new File(this.getTargetPath()).getAbsoluteFile().getParentFile()
			.getAbsolutePath() + File.separator + "tmp";

	/**
	 * Prepares loggers. Enabling too many loggers can cause Java memory issues.
	 */
	protected void setUpLogger() {
		Logger logger = Logger.getLogger("cipm");
		logger.setLevel(Level.ALL);
		logger = Logger.getLogger("jamopp");
		logger.setLevel(Level.ALL);
//		logger = Logger.getLogger("fi.SimilaritySwitch");
//		logger.setLevel(Level.ALL);
//		logger = Logger.getLogger("fi.JaMoPPDiffBuilder");
//		logger.setLevel(Level.ALL);
//		logger = Logger.getLogger("fi.HierarchicalMatchEngine");
//		logger.setLevel(Level.ALL);
//		logger = Logger.getLogger("fi.JaMoPPSoftwareModelExtractor");
//		logger.setLevel(Level.ALL);
//		logger = Logger.getLogger("fi.NormalizationUtil");
//		logger.setLevel(Level.ALL);
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
	}

	/**
	 * Recursively cleans models, which have been used in tests.
	 * 
	 * @param path The path to the directory of the said models
	 */
	protected void cleanModels(String path) throws IOException {
		var file = new File(path);

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
				return;
			}

			if (file.isDirectory()) {
				var children = file.listFiles();

				if (children != null) {
					for (File cf : children) {
						this.cleanModels(cf.getAbsolutePath());
					}
				}

				file.delete();
			}
		}
	}

	/**
	 * Recursively copies Java-Model files from the given parent parameter to the
	 * path given via copyPath. Replaces model files, which already exist. <br>
	 * <br>
	 * This spares having to generate a separate model after each test case, which
	 * modifies them.
	 * 
	 * @param parent   The directory, under which the original Java-Models can be
	 *                 found
	 * @param copyPath The path to the directory, where the content from parent will
	 *                 be copied to
	 */
	protected void copyModels(String parentPath, String copyPath) throws IOException {
		File parent = new File(parentPath).getAbsoluteFile();
		LOGGER.debug("Copying the contents of " + parent.getAbsolutePath() + " into " + copyPath);
		for (File f : parent.listFiles()) {
			var fileName = f.getName();

			if (f.isDirectory()) {
				LOGGER.debug("Directory found: " + fileName);
				String newCopyAddress = copyPath + File.separator + fileName;
				LOGGER.debug("Copy address changed to " + newCopyAddress);
				File tmpDir = new File(newCopyAddress);

				this.copyModels(f.getAbsolutePath(), tmpDir.getAbsolutePath());
			}
			if (f.isFile()) {
				LOGGER.debug("File found: " + fileName);
				File tmpFile = new File(copyPath + File.separator + fileName);

				if (tmpFile.exists()) {
					LOGGER.debug("Existing file will be replaced");
				} else {
					LOGGER.debug("Creating file");
					tmpFile.mkdirs();
					LOGGER.debug("Created file");
				}

				LOGGER.debug("Copying original file into new file");
				Files.copy(f.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				LOGGER.debug("Copied original file into new file: " + fileName);

				LOGGER.debug("Verifying equality of file content");
				Assertions.assertTrue(Files.readString(f.toPath()).equals(Files.readString(tmpFile.toPath())));
				LOGGER.debug("Verified equality of file content");
			}
		}
		LOGGER.debug("Parent directory " + parent.getName() + " has been copied");
	}

	/**
	 * @return {@link #workingResourcesPath}
	 */
	protected String getWorkingResourcesPath() {
		return workingResourcesPath;
	}

	/**
	 * @return Relative path to the target folder (without the "./" prefix)
	 */
	protected String getTargetPath() {
		return "target";
	}

	/**
	 * Creates a Resource instance using the given ResourceSet and URI and then
	 * attempts to load the resource.
	 */
	protected Resource makeAndLoadResource(ResourceSet resSet, URI uri) throws IOException {
		var res = resSet.createResource(uri);
		LOGGER.debug("Loading resource at: " + uri.toFileString());
		res.load(null);
		LOGGER.debug("Loaded resource");
		return res;
	}

	/**
	 * Attempts to validate the given {@link Resource} instance. <br>
	 * <br>
	 * The assertion part can be commented out to generate the list of error
	 * messages via {@link #LOGGER}
	 */
	protected void validateEObjects(Resource res) {
		LOGGER.debug("Validating the given resource");

		var diagnostician = new Diagnostician();

		res.getAllContents().forEachRemaining((obj) -> {
			var diagnostic = diagnostician.validate(obj);
			var objClass = obj.eClass();
			var objName = diagnostician.getObjectLabel(obj);

			if (diagnostic.getSeverity() == Diagnostic.ERROR) {
				LOGGER.debug("Validation for " + objName + " of type " + objClass.getName() + " resulted in error");
			}
			// Enable the line below to have the unit tests fail, if an error is found
//			Assertions.assertNotEquals(diagnostic.getSeverity(), Diagnostic.ERROR, diagnostic.getMessage());
		});

		LOGGER.debug("Validated the given resource");
	}

	/**
	 * Compares models via
	 * {@link JavaModelComparator#compareJavaModels(Notifier, Notifier, List, List, IPostProcessor)}
	 */
	protected Comparison compareModels(Notifier newState, Notifier currentState, List<Resource> newResources,
			List<Resource> currentResources, IPostProcessor postProcessor) {
		LOGGER.debug("Comparing models");

		var cmp = JavaModelComparator.compareJavaModels(newState, currentState, newResources, currentResources,
				postProcessor);

		int matchCount = cmp.getMatches().size();
		int diffCount = cmp.getDifferences().size();

		LOGGER.debug("Compared compared. Match count: " + matchCount + ", Difference count: " + diffCount);

		return cmp;
	}

	/**
	 * Asserts that a given resource is loaded successfully
	 */
	protected void assertResourceLoaded(Resource res) {
		Assertions.assertNotNull(res);
		Assertions.assertTrue(res.isLoaded());
	}

	/**
	 * Asserts that a meaningful (described by parameters) comparison has been done.
	 * 
	 * @param cmp               A given comparison
	 * @param expectMatches     Whether the given comparison is expected to contain
	 *                          matches
	 * @param expectDifferences Whether the given comparison is expected to contain
	 *                          differences
	 */
	protected void assertComparisonDone(Comparison cmp, boolean expectMatches, boolean expectDifferences) {
		Assertions.assertNotNull(cmp);
		Assertions.assertTrue(!expectMatches || cmp.getMatches().size() > 0);
		Assertions.assertTrue(!expectDifferences || cmp.getDifferences().size() > 0);
	}

	/**
	 * Asserts that all found matches during the given comparison are correctly
	 * referenced by their corresponding differences. Asserts further that all of
	 * the said differences are also contained within the list of differences found
	 * during comparison ({@code cmp.getDifferences()})
	 */
	protected void assertAllMatchDiffsHaveDiffs(Comparison cmp) {
		var matches = cmp.getMatches();
		var diffs = cmp.getDifferences();

		Assertions.assertTrue(matches.stream().allMatch(((m) -> {
			// Make sure all differences the matches reference are
			// contained in list of differences found during comparison.
			for (var md : m.getAllDifferences()) {
				// Fail as early as possible
				if (!diffs.contains(md)) {
					LOGGER.debug("A difference in matches is not contained in comparison differences");
					return false;
				}
			}

			// Make sure that cross-referencing between matches and
			// differences is correct
			return this.matchToDiffReferencesCorrect(m);
		})));
	}

	/**
	 * @return Whether differences contained in the given match and its sub-matches
	 *         reference their corresponding matches correctly
	 */
	protected boolean matchToDiffReferencesCorrect(Match m) {
		var directDiffsCorrect = m.getDifferences().stream().allMatch((md) -> md.getMatch() == m);

		var indirectDiffsCorrect = m.getSubmatches().stream().allMatch((sm) -> this.matchToDiffReferencesCorrect(sm));

		return directDiffsCorrect && indirectDiffsCorrect;
	}

	/**
	 * Asserts that all found differences have a match, which is contained in the
	 * list of all found matches. <br>
	 * <br>
	 * Can take a long time to compute.
	 */
	protected void assertAllDiffsHaveMatches(Comparison cmp) {
		var matches = cmp.getMatches();
		var diffs = cmp.getDifferences();

		Assertions.assertTrue(diffs.stream().allMatch(((d) -> {
			var diffMatch = d.getMatch();

			/*
			 * Make sure that the match of the difference has a direct reference to it as
			 * well
			 */
			boolean result = diffMatch.getDifferences().contains(d);

			/*
			 * Match referenced in difference should either be in cmp.getMatches() or be a
			 * sub-match of a match in cmp.getMatches()
			 */
			result = result && (matches.contains(diffMatch) || matches.stream().anyMatch((m) -> {
				for (var sm : m.getAllSubmatches()) {
					if (sm == diffMatch) {
						return true;
					}
				}
				return false;
			}));

			if (!result) {
				LOGGER.debug("A difference has no corresponding match in comparison matches");
			}

			return result;
		})));
	}

	/**
	 * Asserts that the given resource contains no errors <br>
	 * <br>
	 * {@code res.getErrors() == 0}
	 */
	protected void assertNoResourceErrors(Resource res) {
		var errors = res.getErrors();
		errors.forEach((e) -> LOGGER.debug("Error while loading: " + e.getMessage()));
		Assertions.assertEquals(errors.size(), 0);
	}

	/**
	 * Asserts that the given resource contains no warnings <br>
	 * <br>
	 * {@code res.getWarnings() == 0}
	 */
	protected void assertNoResourceWarnings(Resource res) {
		var warnings = res.getWarnings();
		warnings.forEach((e) -> LOGGER.debug("Warning while loading: " + e.getMessage()));
		Assertions.assertEquals(warnings.size(), 0);
	}

	/**
	 * Asserts that the cross-referencing in the comparison is done correctly
	 * 
	 * @param cmp               A given comparison
	 * @param expectMatches     Whether the given comparison is expected to contain
	 *                          matches
	 * @param expectDifferences Whether the given comparison is expected to contain
	 *                          differences
	 */
	protected void assertComparisonSuccessful(Comparison cmp, boolean expectMatches, boolean expectDifferences) {
		LOGGER.debug("Verifying comparison cross-referencing");

		this.assertComparisonDone(cmp, expectMatches, expectDifferences);
		this.assertAllMatchDiffsHaveDiffs(cmp);
		this.assertAllDiffsHaveMatches(cmp);

		LOGGER.debug("Verified comparison cross-referencing");
	}
}
