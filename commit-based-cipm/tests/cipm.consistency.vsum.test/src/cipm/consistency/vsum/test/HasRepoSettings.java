package cipm.consistency.vsum.test;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.detection.ComponentDetectionStrategy;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

/**
 * An interface to be implemented by classes (settings classes), whose purpose
 * is to encapsulate test-specific details, in order to make them re-usable and
 * extendable. It can also be implemented by the containers of the settings
 * class instances to avoid unnecessary method duplications. <br>
 * <br>
 * Getters below return things that are required by {@link AbstractCITest},
 * although some of them are slightly re-named to circumvent visibility issues
 * in implementors, which would have the same method signatures (except for the
 * visibility modifier) otherwise. <br>
 * <br>
 * If this interface is implemented by a class that contains a settings class
 * instance, {@link #getRepoSettings()} must return the underlying concrete
 * settings class instance. If this interface is implemented by a settings
 * class, {@link #getRepoSettings()} must return the settings class instance
 * itself {@code settingsClassInstance.this}. Otherwise, methods of this
 * interface will not work as intended. <br>
 * <br>
 * 
 * <b>Make sure to override each required getter in concrete settings classes
 * (and/or their containers that also implement this interface)</b>
 * 
 * @author atora
 */
public interface HasRepoSettings {
	/**
	 * Checks the reference equality of the given parameters (via the plain
	 * {@code "=="} operator). Meant to be used by the methods below to avoid
	 * endless recursive calls.
	 * 
	 * @return Whether the given parameters are equal by reference.
	 */
	private static boolean isSameAsThis(HasRepoSettings settings1, HasRepoSettings settings2) {
		return settings1 == settings2;
	}

	/**
	 * A method that allows retrieving a commit hash from the implementor of this
	 * interface. The parameter format and requirements may change depending on the
	 * concrete implementor.
	 * 
	 * @param commitHashIdentifier A given parameter, with which the concrete
	 *                             implementor can identify the desired commit hash
	 *                             and return it back
	 * @return The desired commit hash.
	 */
	public default String getCommitHash(Object commitHashIdentifier) {
		return isSameAsThis(this, this.getRepoSettings()) ? null
				: this.getRepoSettings().getCommitHash(commitHashIdentifier);
	}

	/**
	 * @return The name of the test group (or test type) needed by
	 *         {@link AbstractCITest#getTestType()}
	 */
	public default String getTestGroup() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getTestGroup();
	}

	/**
	 * @return The path to the repository needed by
	 *         {@link AbstractCITest#getRepositoryPath()}
	 */
	public default String getRepositoryAddress() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getRepositoryAddress();
	}

	/**
	 * @return The path to the settings needed by
	 *         {@link AbstractCITest#getSettingsPath()}
	 */
	public default String getSettingsAddress() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getSettingsAddress();
	}

	/**
	 * The underlying implementation of
	 * {@link AbstractCITest#getJavaPCMSpecification()}
	 */
	public default ChangePropagationSpecification getJavaPCMSpec() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getJavaPCMSpec();
	}

	/**
	 * @return The component detection strategies, which may be required by
	 *         {@link JavaParserAndPropagatorUtils.Configuration} in some cases
	 */
	public default ComponentDetectionStrategy[] getComponentDetectionStrategy() {
		return isSameAsThis(this, this.getRepoSettings()) ? null
				: this.getRepoSettings().getComponentDetectionStrategy();
	}

	/**
	 * @return Parameters to be used in
	 *         {@link #performTestSpecificTearDown(Object...)}
	 */
	public default Object[] getTestSpecificTearDownParams() {
		return isSameAsThis(this, this.getRepoSettings()) ? null
				: this.getRepoSettings().getTestSpecificTearDownParams();
	}

	/**
	 * @return The path to the test specific "[...]-exec-files" directory.
	 */
	public default String getExecFilesAddress() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getExecFilesAddress();
	}

	/**
	 * Implemented to access the underlying {@link HasRepoSettings} members to avoid
	 * having to unnecessarily duplicate all getter methods above in implementors,
	 * such as in {@link AbstractRepoTest}.
	 */
	public HasRepoSettings getRepoSettings();

	/**
	 * Does preparations before unit test run.
	 * 
	 * @param params Required parameters. Check concrete implementors for what is
	 *               needed and in what order.
	 */
	public default void performTestSpecificSetUp(Object[] params) throws Exception {
		if (!isSameAsThis(this, this.getRepoSettings()))
			this.getRepoSettings().performTestSpecificSetUp(params);
	}

	/**
	 * Does preparations before unit test run.
	 * {@link #performTestSpecificSetUp(Object[])} variant can be used to pass
	 * parameters without {@link #getTestSpecificSetUpParams()}, or to modify the
	 * passed parameters.
	 */
	public default void performTestSpecificSetUp() throws Exception {
		this.performTestSpecificSetUp(this.getTestSpecificSetUpParams());
	}

	/**
	 * @return Parameters to be used in {@link #performTestSpecificSetUp(Object...)}
	 */
	public default Object[] getTestSpecificSetUpParams() {
		return isSameAsThis(this, this.getRepoSettings()) ? null : this.getRepoSettings().getTestSpecificSetUpParams();
	}

	/**
	 * Does clean up after unit test run.
	 * 
	 * @param params Required parameters. Check concrete implementors for what is
	 *               needed and in what order.
	 */
	public default void performTestSpecificTearDown(Object[] params) throws Exception {
		if (!isSameAsThis(this, this.getRepoSettings()))
			this.getRepoSettings().performTestSpecificTearDown(params);
	}

	/**
	 * Does clean up after unit test run.
	 * {@link #performTestSpecificTearDown(Object[])} variant can be used to pass
	 * parameters other than {@link #getTestSpecificTearDownParams()}, or to modify
	 * the passed parameters.
	 */
	public default void performTestSpecificTearDown() throws Exception {
		this.performTestSpecificTearDown(this.getTestSpecificTearDownParams());
	}
}
