package cipm.consistency.fitests.similarity.eobject;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An abstract class meant to be extended by classes that envelop the means to
 * parse Resource instances from (original) model files. <br>
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
	}

	/**
	 * Clears all exclusion patterns from this instance.
	 */
	public void clearExclusionPatterns() {
		this.exclusionPatterns.clear();
	}

	/**
	 * Parses a ResourceSet for the model at given path.
	 * 
	 * @param modelDir The path to a given model. Refer to the concrete
	 *                 implementation for more information on where this path is
	 *                 supposed to point at.
	 * @return A ResourceSet that contains all parsed Resource instances for the
	 *         given model path.
	 */
	public abstract ResourceSet parseModelResource(Path modelDir);
}
