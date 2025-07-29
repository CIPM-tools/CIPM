package cipm.consistency.fitests.similarity.jamopp;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.fitests.similarity.eobject.AbstractResourceHelper;

import jamopp.resource.JavaResource2Factory;

/**
 * A class that can be used for operations on Resource instances in JaMoPP
 * context.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPResourceHelper extends AbstractResourceHelper {
	/**
	 * The extension of Java source code files.
	 */
	private static final String javaSrcExt = "java";
	/**
	 * The extension of the resource files created within tests, should they be
	 * saved.
	 */
	private static final String resFileExt = "javaxmi";

	public JaMoPPResourceHelper() {
		super();
		this.setResourceFileExtension(resFileExt);
		this.setInitialResourceRegistries();
	}

	/**
	 * Adds the mapping into {@link Resource.Factory.Registry} for saving resources
	 * with the extension {@link #getResourceFileExtension()} using XMI format.
	 * 
	 * @see {@link #setResourceRegistry(String, Object)}
	 */
	public void setInitialResourceRegistries() {
		this.setResourceRegistry(javaSrcExt, new JavaResource2Factory());
		this.setResourceRegistry(resFileExt, new JavaResource2Factory());
	}
}
