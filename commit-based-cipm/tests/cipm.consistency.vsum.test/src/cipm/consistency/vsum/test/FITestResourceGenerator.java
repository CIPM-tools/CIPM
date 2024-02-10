package cipm.consistency.vsum.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.commitintegration.IJavaModelParserListener;
import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * A class that helps initialise the hook needed to generate test resources
 * required for FirstInstance tests (fitests). <br>
 * <br>
 * It has to be initialised via {@link #init(String, String)} and finalised via
 * {@link #cleanUp()} to add and remove the hook respectively. The generation of
 * the test resource will result in a {@link ResourceGeneratedException}, which
 * has to be caught by passing the action that generates the test resource as a
 * {@link Callable} instance to {@link #generateResourceWhile(Callable)}. <br>
 * <br>
 * <b>Make sure to use {@link #generateResourceWhile(Callable)} method to avoid
 * getting the {@link ResourceGeneratedException}</b>
 * 
 * @author atora
 * @see {@link ResourceGeneratedException},
 *      {@link #init(String, String, boolean)}
 */
class FITestResourceGenerator {
	private static final Logger LOGGER = Logger.getLogger("cipm." + FITestResourceGenerator.class.getSimpleName());

	/**
	 * The hook object that keeps triggers
	 * {@link #generateResource(Path, Path, VirtualModel, Path, Resource)} upon
	 * detecting the generation of a Java-Model in
	 * {@link JavaParserAndPropagatorUtils}
	 */
	private IJavaModelParserListener parseListener;

	/**
	 * The name of the test group ({@link AbstractCITest#getTestType()})
	 */
	private String testType;
	/**
	 * The extension of the Java-Model file to be generated
	 */
	private String extension;

	/**
	 * Initialises {@code this} by setting {@link #testType} and {@link #extension}.
	 * <br>
	 * <br>
	 * <b>The hook {@link #parseListener} has to be initialised separately by
	 * calling {@link #addModelGenerationHook(String)}</b>
	 * 
	 * @param testType  The name of the test group
	 *                  ({@link AbstractCITest#getTestType()})
	 * @param extension The extension of the file, to which the generated Java model
	 *                  will be saved
	 */
	public void init(String testType, String extension) {
		this.testType = testType;
		this.extension = extension;
	}

	/**
	 * Initialises the hook {@link #parseListener}. <br>
	 * <br>
	 * <b> {@link #testType} and {@link #extension} have to be initialised via
	 * {@link #init(String, String)} </b>
	 * 
	 * @param modelFileName The name of the file, to which the generated Java model
	 *                      will be saved
	 * 
	 * @see {@link #generateResourceWhile(Callable)}
	 * @see {@link #generateResource(Path, Path, VirtualModel, Path, Resource, boolean)}
	 */
	protected void addModelGenerationHook(String modelFileName) {
		this.removeModelGenerationHook();

		this.parseListener = this.createModelGenerationHook();

		var newPathString = this.getFITestsTargetResourcePath(this.getTestType(), modelFileName);
		var newURI = URI.createFileURI(newPathString);
		var newPath = Path.of(newPathString);

		LOGGER.debug("Setting listener path to: " + newPath.toString() + " made of: " + newURI.toString() + " made of: "
				+ newPath);
		this.parseListener.setVariables(newPath);
		JavaParserAndPropagatorUtils.addParseListener(this.parseListener);
	}

	/**
	 * Creates a hook that can be used to trigger an action each time
	 * {@link JavaParserAndPropagatorUtils} parses a Java-Model
	 * 
	 * @return The said hook
	 */
	protected IJavaModelParserListener createModelGenerationHook() {
		return new IJavaModelParserListener() {
			/**
			 * The path, at which the Java-Model file will be created
			 */
			private Path fiTestResourcePath;

			@Override
			public void javaModelParsed(Path dir, Path target, VirtualModel vsum, Path configPath, Resource all) {
				generateResource(dir, this.fiTestResourcePath, vsum, configPath, all);
			}

			/**
			 * {@inheritDoc}
			 * 
			 * vars[0] = {@link #fiTestResourcePath}
			 */
			@Override
			public void setVariables(Object... vars) {
				this.fiTestResourcePath = (Path) vars[0];
			}
		};
	}

	/**
	 * @see {@link #testType}
	 */
	public String getTestType() {
		return this.testType;
	}

	/**
	 * @see {@link #extension}
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * Method to be called to clean up
	 */
	public void cleanUp() {
		this.removeModelGenerationHook();
	}

	/**
	 * Removes the hook from {@link JavaParserAndPropagatorUtils}, if there is such
	 * a hook.
	 */
	protected void removeModelGenerationHook() {
		if (this.parseListener != null) {
			JavaParserAndPropagatorUtils.removeParseListener(this.parseListener);
		}
	}

	/**
	 * @return The absolute path of the target folder of FirstInstance tests
	 */
	protected String getFITestsAbsoluteTargetPath() {
		var currentPath = new File(".").getAbsoluteFile();

		var pathToTarget = currentPath.getParentFile().getParentFile().getParentFile().getAbsolutePath();

		pathToTarget += File.separator + "fi-tests" + File.separator + "cipm.consistency.fitests" + File.separator
				+ "target";

		return pathToTarget;
	}

	/**
	 * @param testType      The name of the test group
	 * @param modelFileName The name of the file, to which the newly generated
	 *                      Java-Model will be saved (without extension)
	 * @return The absolute path of the file (described via parameters) in the
	 *         target folder of FirstInstance
	 */
	protected String getFITestsTargetResourcePath(String testType, String modelFileName) {
		return this.getFITestsAbsoluteTargetPath() + File.separator + testType + File.separator + modelFileName
				+ this.getExtension();
	}

	/**
	 * Use this method to make the call chain terminate as soon as the needed test
	 * resource is generated without any {@link ResourceGeneratedException}
	 * terminating the program.
	 */
	public void generateResourceWhile(Callable<Boolean> c) throws Exception {
		try {
			c.call();
		} catch (ResourceGeneratedException e) {
			LOGGER.debug("FITest resource generated");
		}
	}

	/**
	 * Generates the FirstInstance test resource. Meant to be used only by the
	 * listener defined in {@link #createModelGenerationHook()}
	 * 
	 * Throws a {@link ResourceGeneratedException} return as soon as the said test
	 * resource is generated
	 */
	protected void generateResource(Path dir, Path target, VirtualModel vsum, Path configPath, Resource all)
			throws ResourceGeneratedException {
		try {
			JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(dir, target, configPath).save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new ResourceGeneratedException();
	}

	/**
	 * An Exception that is only meant to be thrown/caught by the internals of
	 * {@link FITestResourceGenerator}. This is a temporary solution for generating
	 * test resources for FirstInstance tests without having to wait till the end of
	 * the propagation process, which can take a very long time.
	 * 
	 * @author atora
	 */
	private class ResourceGeneratedException extends RuntimeException {
		private static final long serialVersionUID = 810010594491292049L;
	}
}
