package cipm.consistency.vsum.test;

/**
 * An abstract class for test classes, which are to be used to generate test
 * resources required by FirstInstance tests. It encapsulates the logic needed
 * to generate test resources for FirstInstance tests (fitests) and save them at
 * the corresponding path in the target directory of FirstInstance tests
 * (fitests). <br>
 * <br>
 * Use the {@link #generateResourcesFor(Object)} method from
 * {@link AbstractFITestResourceGenerator} to generate the said test resources.
 * Pass the index of the last commit hash to the aforementioned method, as the
 * resource will only be generated with that commit in mind. <br>
 * <br>
 * Make sure to call {@link #initResGen(Object)} at the start of each test to
 * initialise the generator responsible for creating Java-Model files required
 * by fitests. <br>
 * <br>
 * <b>Note that the size of generated test resources may slightly vary, which is
 * to be expected</b>
 * 
 * @author atora
 */
public abstract class AbstractFITestResourceGenerator extends AbstractRepoTest {
	/**
	 * The object responsible for generating Java-Models.
	 * 
	 * Generate an instance using
	 * {@link FITestResourceGenerator#init(String, String)}
	 */
	private FITestResourceGenerator resGen = null;

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		this.resGen.cleanUp();
	}

	/**
	 * Instantiates a {@link FITestResourceGenerator} but does not initialise it.
	 * Meant to be used in {@link #initResGen(Object)}.
	 */
	protected FITestResourceGenerator createResGen() {
		return new FITestResourceGenerator();
	}

	/**
	 * Computes a unique suffix for the model to be generated based on the given
	 * parameter, so that generating multiple models is allowed.
	 * 
	 * @param commitHashIdentifier see {@link HasRepoSettings#getCommitHash(Object)}
	 * 
	 * @return The suffix of the file name (excluding extension)
	 */
	protected abstract String getTestModelFileIdentifier(Object commitHashIdentifier);

	/**
	 * @param commitHashIdentifier see {@link HasRepoSettings#getCommitHash(Object)}
	 * 
	 * @return The full file name (excluding extension)
	 */
	protected String getTestModelFileNameWOExtension(Object commitHashIdentifier) {
		return this.getTestModelFileNamePrefix() + this.getTestModelFileIdentifier(commitHashIdentifier);
	}

	/**
	 * Prepares {@code this} for resource generation.
	 * 
	 * @param commitHashIdentifier see {@link HasRepoSettings#getCommitHash(Object)}
	 */
	protected void initResGen(Object commitHashIdentifier) {
		if (this.resGen != null) {
			this.resGen.cleanUp();
		}

		this.resGen = this.createResGen();
		this.resGen.init(this.getTestType(), this.getTestModelFileExtension());
		this.resGen.addModelGenerationHook(this.getTestModelFileNameWOExtension(commitHashIdentifier));
	}

	/**
	 * @return The extension of the model file to be generated
	 */
	protected String getTestModelFileExtension() {
		return ".javaxmi";
	}

	/**
	 * @return The common prefix of the name of the model file to be generated
	 */
	protected String getTestModelFileNamePrefix() {
		return "JavaModel-test";
	}

	/**
	 * Attempts to generate a Java-Model file for the commit hash with the given
	 * identifier
	 * 
	 * @param commitHashIdentifier see {@link HasRepoSettings#getCommitHash(Object)}
	 */
	protected void generateResourcesFor(Object commitHashIdentifier) throws Exception {
		// Only the last commit is relevant here
		this.resGen.generateResourceWhile(
				() -> executePropagationAndEvaluation(null, this.getCommitHash(commitHashIdentifier), 0));
	}
}
