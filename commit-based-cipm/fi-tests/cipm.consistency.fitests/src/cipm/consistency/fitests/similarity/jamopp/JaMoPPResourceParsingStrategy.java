package cipm.consistency.fitests.similarity.jamopp;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.ResourceSet;

import cipm.consistency.fitests.similarity.eobject.AbstractResourceParsingStrategy;
import cipm.consistency.fitests.similarity.eobject.ResourceHelper;
import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import jamopp.recovery.trivial.TrivialRecovery;
import jamopp.resource.JavaResource2Factory;

/**
 * A class that uses {@link JaMoPPJDTSingleFileParser} to parse Java model
 * Resources. Provides methods for performing {@link TrivialRecovery} in cases,
 * where bindings are used. <br>
 * <br>
 * <ul>
 * <li>Given model paths should point at the top-most directory of the Java
 * project
 * <li>Supports Regex expressions for model paths as exclusion patterns
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPResourceParsingStrategy extends AbstractResourceParsingStrategy {
	/**
	 * @see {@link #preConstructionSetup()}
	 */
	private static final String javaSrcExt = "java";
	/**
	 * @see {@link #preConstructionSetup()}
	 * @see {@link #getResourceFileExtension()}
	 */
	private static final String resFileExt = "javaxmi";

	/**
	 * @see {@link #getParser()}
	 */
	private final JaMoPPJDTSingleFileParser parser;

	/**
	 * Performs any preparation necessary prior to the construction of an instance.
	 * Then constructs an instance, as well as the underlying parser it uses.
	 * 
	 * @see {@link #preConstructionSetup()}
	 */
	public JaMoPPResourceParsingStrategy() {
		super();
		this.parser = new JaMoPPJDTSingleFileParser();
		this.setUpModelParser();
	}

	/**
	 * Declared as protected to allow sub-types to access the underlying parser. Not
	 * meant to be used in non-sub-types.
	 * 
	 * @return The parser that is used for parsing Java model Resources.
	 */
	protected JaMoPPJDTSingleFileParser getParser() {
		return parser;
	}

	/**
	 * Prepares the parser for parsing model resources. <br>
	 * <br>
	 * Can be overridden in sub-types to modify if needed.
	 */
	protected void setUpModelParser() {
		/*
		 * Default values of ParserOptions are:
		 * 
		 * RESOLVE_ALL_BINDINGS = true
		 * 
		 * RESOLVE_BINDINGS = true
		 * 
		 * RESOLVE_BINDINGS_OF_INFERABLE_TYPES = true
		 * 
		 * CREATE_LAYOUT_INFORMATION = true
		 * 
		 * PREFER_BINDING_CONVERSION = true
		 */
		this.parser.setResourceSet(this.getResourceSet());

		ParserOptions.CREATE_LAYOUT_INFORMATION.setValue(Boolean.FALSE);
		ParserOptions.REGISTER_LOCAL.setValue(Boolean.TRUE);
		ParserOptions.RESOLVE_EVERYTHING.setValue(Boolean.FALSE);
		ParserOptions.RESOLVE_ALL_BINDINGS.setValue(Boolean.FALSE);
	}

	/**
	 * @implSpec Sets the resource factory registry for the file extension of
	 *           JaMoPP-related Resource instances ({@value #resFileExt}).
	 *           Additionally sets the resource factory registry for Resource
	 *           instances with the file extension {@value #javaSrcExt}.
	 */
	@Override
	protected void preConstructionSetup() {
		ResourceHelper.setResourceRegistry(javaSrcExt, new JavaResource2Factory());
		ResourceHelper.setResourceRegistry(resFileExt, new JavaResource2Factory());
	}

	@Override
	public ResourceSet parseModelResource(Path modelDir) {
		return parser.parseDirectory(modelDir);
	}

	/**
	 * Performs {@link TrivialRecovery} on the current ResourceSet of this instance.
	 */
	public void performTrivialRecovery() {
		this.performTrivialRecovery(this.getResourceSet());
	}

	/**
	 * Performs {@link TrivialRecovery} on the given ResourceSet.
	 */
	public void performTrivialRecovery(ResourceSet resourceSet) {
		new TrivialRecovery(resourceSet).recover();
	}

	@Override
	protected void exclusionPatternsChanged() {
		var exclusionPatternSet = this.getExclusionPatterns();
		String[] exclusionPatterns = null;

		if (exclusionPatternSet == null || exclusionPatternSet.isEmpty()) {
			exclusionPatterns = new String[] {};
		} else {
			exclusionPatterns = exclusionPatternSet.toArray(String[]::new);
		}

		this.getParser().setExclusionPatterns(exclusionPatterns);
	}

	/**
	 * @return The extension of the resource files created within tests, should they
	 *         be saved.
	 */
	@Override
	public String getResourceFileExtension() {
		return resFileExt;
	}
}
