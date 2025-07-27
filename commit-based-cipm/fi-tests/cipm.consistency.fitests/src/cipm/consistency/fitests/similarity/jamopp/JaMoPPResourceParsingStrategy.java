package cipm.consistency.fitests.similarity.jamopp;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.ResourceSet;

import cipm.consistency.fitests.similarity.eobject.AbstractResourceParsingStrategy;
import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import jamopp.recovery.trivial.TrivialRecovery;

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
	 * @see {@link #getParser()}
	 */
	private final JaMoPPJDTSingleFileParser parser;

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
}
