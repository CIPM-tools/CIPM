package cipm.consistency.fitests.similarity.eobject;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * An abstract class meant to be extended by classes that envelop the means to
 * parse Resource instances from model source files. <br>
 * <br>
 * Implementors are expected to:
 * <ul>
 * <li>Have their ResourceSet set via {@link #setResourceSet(ResourceSet)} prior
 * to any {@link #parseModelResource(Path)} calls
 * <li>Parse models in form of {@link Resource} instances and place all those
 * Resource instances into the same {@link ResourceSet}
 * <li>Support exclusion patterns that can be used to exclude certain model
 * files from being parsed
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractResourceParsingStrategy {
	/**
	 * @see {@link AbstractResourceParsingStrategy}
	 */
	private ResourceSet resourceSet;
	/**
	 * @see {@link AbstractResourceParsingStrategy}
	 */
	private final Set<String> exclusionPatterns = new HashSet<>();

	/**
	 * Performs any preparation necessary prior to the construction of an instance.
	 * Then constructs an instance.
	 * 
	 * @see {@link #preConstructionSetup()}
	 */
	public AbstractResourceParsingStrategy() {
		this.preConstructionSetup();
	}

	/**
	 * Sets the ResourceSet used from within to the given one.
	 * 
	 * @see {@link #getResourceSet()}
	 */
	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	/**
	 * @return The ResourceSet inside this instance, which should contain all parsed
	 *         model Resources.
	 */
	public ResourceSet getResourceSet() {
		return this.resourceSet;
	}

	/**
	 * Adds the given exclusion pattern to the set of exclusion patterns. Refer to
	 * the concrete implementation for more details on what kind of patterns are
	 * supported.
	 * 
	 * @param pattern An exclusion pattern to add to the set
	 */
	public void addExclusionPattern(String pattern) {
		this.exclusionPatterns.add(pattern);
		this.exclusionPatternsChanged();
	}

	/**
	 * Removes the given exclusion pattern from the set of exclusion patterns. Refer
	 * to the concrete implementation for more details on what kind of patterns are
	 * supported.
	 * 
	 * @param pattern An exclusion pattern to remove from the set
	 */
	public void removeExclusionPattern(String pattern) {
		this.exclusionPatterns.remove(pattern);
		this.exclusionPatternsChanged();
	}

	/**
	 * Clears all exclusion patterns from this instance.
	 */
	public void clearExclusionPatterns() {
		this.exclusionPatterns.clear();
		this.exclusionPatternsChanged();
	}

	/**
	 * Modifications on the return value will not affect this class.
	 * 
	 * @return All exclusion patterns of this instance.
	 */
	public Set<String> getExclusionPatterns() {
		return new HashSet<String>(this.exclusionPatterns);
	}

	/**
	 * Performs any implementation-specific operations needed to adapt the
	 * underlying model parsing mechanisms. <br>
	 * <br>
	 * This method should be called by each method, which modifies the exclusion
	 * patterns stored in this instance.
	 */
	protected abstract void exclusionPatternsChanged();

	/**
	 * Performs any necessary preparation prior to the construction of an instance
	 * of this class. This includes setting up the necessary
	 * {@link Resource.Factory.Registry} and other preparation steps, which do not
	 * involve the members of this class. <br>
	 * <br>
	 * If this method is to be overridden in the concrete implementations, it is
	 * recommended to check the implementation of the super method, as there could
	 * be conflicts.<br>
	 * <br>
	 * Note: This is the first step in the constructor. Therefore, there should be
	 * no references to class members that are initialised within the constructor,
	 * as that might cause NullPointerExceptions.
	 */
	protected void preConstructionSetup() {
		ResourceHelper.setResourceRegistry("*", new XMIResourceFactoryImpl());
	}

	/**
	 * Parses a ResourceSet for the model at given path.
	 * 
	 * TODO Rename parameter
	 * 
	 * @param modelDir The path to a given model source file directory. Refer to the
	 *                 concrete implementation for more information on where this
	 *                 path is supposed to point at.
	 * @return A ResourceSet that contains all parsed Resource instances for the
	 *         given model source file directory path.
	 */
	public abstract ResourceSet parseModelResource(Path modelDir);

	/**
	 * @return The extension of the {@link Resource} files, if they are saved.
	 */
	public abstract String getResourceFileExtension();
}
