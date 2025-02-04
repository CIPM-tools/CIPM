package cipm.consistency.fitests.similarity.jamopp.utiltests;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.jamopp.IJaMoPPUtilityTest;

/**
 * A test class, whose tests can be used to make sure no initialiser interfaces,
 * concrete initialisers or initialiser tests are missing.
 * 
 * @author Alp Torac Genc
 */
public class UtilityTests extends AbstractJaMoPPSimilarityTest implements IJaMoPPUtilityTest {
	/**
	 * Points at the parent package. Used by discovering methods in this class.
	 */
	private static final File root = new File(Paths
			.get(new File("").toPath().toString(), "src", UtilityTests.class.getPackageName()
					.substring(0, UtilityTests.class.getPackageName().lastIndexOf(".")).replace(".", File.separator))
			.toAbsolutePath().toUri());

	/**
	 * The suffix used in tests.
	 */
	private static final String testSuffix = "Test";

	/**
	 * @return The name of the test corresponding to cls.
	 */
	private String getTestName(Class<?> cls) {
		return cls.getSimpleName() + testSuffix;
	}

	/**
	 * @return A list of all files under {@link #root}.
	 */
	private Collection<File> getAllFiles() {
		return this.getAllFiles(root);
	}

	/**
	 * Checks if all necessary concrete initialisers can be accessed under the used
	 * initialiser package, which is used in initialiser tests. <br>
	 * <br>
	 * Prints the amount of accessible/registered initialiser types. The missing
	 * types can be found in the assertion message.
	 */
	@Test
	public void testAllConcreteInitialisersRegistered() {
		var clss = this.getAllConcreteInitialiserCandidates();
		var registeredInits = this.getUsedInitialiserPackage().getAllInitialiserInstances();

		var matches = List.of(
				clss.stream().filter((cls) -> registeredInits.stream().anyMatch((init) -> init.isInitialiserFor(cls)))
						.toArray(Class<?>[]::new));

		this.getLogger().info(matches.size() + " out of " + clss.size() + " concrete initialisers are registered");

		if (matches.size() != clss.size()) {
			Assertions.fail("Concrete initialisers not registered: "
					+ this.clsStreamToString(clss.stream().filter((cls) -> !matches.contains(cls))));
		}
	}

	/**
	 * Checks if all necessary initialiser interface types can be accessed under the
	 * used initialiser package, which is used in initialiser tests. <br>
	 * <br>
	 * Prints the amount of accessible/registered initialiser interface types. The
	 * missing types can be found in the assertion message.
	 */
	@Test
	public void testAllInitialiserInterfacesRegistered() {
		var clss = this.getAllInitialiserCandidates();
		var registeredInits = this.getUsedInitialiserPackage().getAllInitialiserInterfaceTypes();

		var matches = List.of(clss.stream()
				.filter((cls) -> registeredInits.stream()
						.anyMatch((init) -> this.getInitialiserInterfaceName(cls).equals(init.getSimpleName())))
				.toArray(Class<?>[]::new));

		this.getLogger().info(matches.size() + " out of " + clss.size() + " initialiser interfaces are registered");

		if (matches.size() != clss.size()) {
			Assertions.fail("Initialiser interfaces not registered: "
					+ this.clsStreamToString(clss.stream().filter((cls) -> !matches.contains(cls))));
		}
	}

	/**
	 * Checks if all classes in the need of an initialiser, which have their own
	 * methods that modify them (the methods they do not get from inheritance), have
	 * their corresponding test file. <br>
	 * <br>
	 * In short, checks if all necessary initialiser test files are present. <br>
	 * <br>
	 * Prints the number of required test files that are actually present.
	 * Information on the missing test files can be found in the assertion message.
	 * <br>
	 * <br>
	 * <b> Only checks whether there are corresponding test files. Does not check
	 * the unit tests they implement. </b>
	 */
	@Test
	public void testAllInterfaceTestsPresent() {
		var intfcs = this.getAllInitialiserCandidates();
		var allFiles = this.getAllFiles();

		var matches = List.of(intfcs.stream()
				.filter((c) -> !IInitialiser.declaresModificationMethods(this.getInitialiserInterfaceFor(c))
						|| allFiles.stream().anyMatch((tf) -> fileNameEquals(tf, getTestName(c))))
				.toArray(Class<?>[]::new));

		this.getLogger().info(matches.size() + " out of " + intfcs.size() + " interfaces are covered by tests");

		if (matches.size() != intfcs.size()) {
			Assertions.fail("Tests missing for: "
					+ this.clsStreamToString(intfcs.stream().filter((e) -> !matches.contains(e))));
		}
	}
}
