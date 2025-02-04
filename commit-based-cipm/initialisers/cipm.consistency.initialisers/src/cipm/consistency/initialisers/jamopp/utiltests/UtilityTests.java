package cipm.consistency.initialisers.jamopp.utiltests;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.initialisers.IInitialiserPackage;
import cipm.consistency.initialisers.jamopp.IJaMoPPUtilityTest;
import cipm.consistency.initialisers.jamopp.JaMoPPInitialiserPackage;

/**
 * A test class, whose tests can be used to make sure no initialiser interfaces
 * or concrete initialisers are missing.
 * 
 * @author Alp Torac Genc
 */
public class UtilityTests implements IJaMoPPUtilityTest {
	private static final Logger LOGGER = Logger.getLogger(UtilityTests.class);
	/**
	 * Points at the parent package. Used by discovering methods in this class.
	 */
	private static final File root = new File(Paths
			.get(new File("").toPath().toString(), "src", UtilityTests.class.getPackageName()
					.substring(0, UtilityTests.class.getPackageName().lastIndexOf(".")).replace(".", File.separator))
			.toAbsolutePath().toUri());

	@BeforeEach
	public void setUp() {
		Logger logger = this.getLogger();
		logger.setLevel(Level.ALL);

		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
	}

	public Logger getLogger() {
		return LOGGER;
	}

	@Override
	public IInitialiserPackage getUsedInitialiserPackage() {
		return new JaMoPPInitialiserPackage();
	}

	@Override
	public Collection<File> getAllFiles() {
		return this.getAllFiles(root);
	}

	/**
	 * The expected amount of concrete initialiser types. <br>
	 * <br>
	 * Note: Must be adapted, if new initialisers are added, which have concrete
	 * implementations.
	 */
	private int getExpectedConcreteInitialiserCount() {
		return 181;
	}

	/**
	 * The expected amount of initialiser types (abstract or concrete).<br>
	 * <br>
	 * Note: Must be adapted, if new initialisers are added.
	 */
	private int getExpectedInitialiserCount() {
		return 274;
	}

	/**
	 * Prints all interface types from {@link #getAllPossibleJaMoPPEObjectTypes()}.
	 * <br>
	 * <br>
	 * Does not test anything, just prints the said types.
	 */
	@Test
	public void printFullHierarchy() {
		var hSet = this.getAllPossibleJaMoPPEObjectTypes();
		this.getLogger().info(this.clsStreamToString(hSet.stream()));
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
		Assertions.assertEquals(this.getExpectedConcreteInitialiserCount(), clss.size());
		var registeredInits = this.getAllInitialiserInstances();
		Assertions.assertEquals(this.getExpectedConcreteInitialiserCount(), registeredInits.size());

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
		Assertions.assertEquals(this.getExpectedInitialiserCount(), clss.size());
		var registeredInits = this.getAllInitialiserInterfaceTypes();
		Assertions.assertEquals(this.getExpectedInitialiserCount(), registeredInits.size());

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
	 * Checks if all concrete initialisers (SomethingInitialiser.java) files are
	 * implemented and present under {@link #root}. <br>
	 * <br>
	 * Prints the number of present concrete initialiser files. Information on the
	 * missing files can be found in the assertion message. <br>
	 * <br>
	 * <b>Only checks whether the said files are present, does not inspect their
	 * content at all.</b>
	 */
	@Test
	public void testAllConcreteInitialisersPresent() {
		var intfcs = this.getAllConcreteInitialiserCandidates();
		Assertions.assertEquals(this.getExpectedConcreteInitialiserCount(), intfcs.size());
		var files = this.getAllFiles();

		var matches = List.of(intfcs.stream().filter(
				(ifc) -> files.stream().anyMatch((f) -> this.fileNameEquals(f, this.getConcreteInitialiserName(ifc))))
				.toArray(Class<?>[]::new));
		var count = matches.size();

		this.getLogger().info(count + " out of " + intfcs.size() + " concrete initialiser files are present");

		if (count != intfcs.size()) {
			Assertions.fail("Concrete initialiser files missing for: "
					+ this.clsStreamToString(intfcs.stream().filter((ifc) -> !matches.contains(ifc))));
		}
	}

	/**
	 * Checks if all interface initialisers (ISomethingInitialiser.java) files are
	 * implemented and present under {@link #root}. <br>
	 * <br>
	 * Prints the number of present initialiser files. Information on the missing
	 * files can be found in the assertion message. <br>
	 * <br>
	 * <b>Only checks whether the said files are present, does not inspect their
	 * content at all.</b>
	 */
	@Test
	public void testAllInitialiserInterfacesPresent() {
		var intfcs = this.getAllInitialiserCandidates();
		Assertions.assertEquals(this.getExpectedInitialiserCount(), intfcs.size());
		var files = this.getAllFiles();

		var matches = List.of(intfcs.stream().filter(
				(ifc) -> files.stream().anyMatch((f) -> this.fileNameEquals(f, this.getInitialiserInterfaceName(ifc))))
				.toArray(Class<?>[]::new));
		var count = matches.size();

		this.getLogger().info(count + " out of " + intfcs.size() + " initialiser files are present");

		if (count != intfcs.size()) {
			Assertions.fail("Initialiser files missing for: "
					+ this.clsStreamToString(intfcs.stream().filter((ifc) -> !matches.contains(ifc))));
		}
	}
}
