package cipm.consistency.fitests.similarity.eobject;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.fitests.similarity.AbstractSimilarityTest;

/**
 * An abstract class that extends {@link AbstractSimilarityTest} with additional
 * methods regarding {@link Resource} instance creation.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractResourceSimilarityTest extends AbstractSimilarityTest {
	/**
	 * @see {@link #getResourceHelper()}
	 */
	private ResourceHelper resHelper;

	@BeforeEach
	@Override
	public void setUp(TestInfo info) {
		super.setUp(info);

		this.setResourceHelper(new ResourceHelper());

		this.getResourceHelper().setResourceSaveRootPath(this.getAbsoluteResourceRootPath());
		this.getResourceHelper().setResourceFileExtension(this.getResourceFileExtension());
	}

	@AfterEach
	@Override
	public void tearDown() {
		this.getResourceHelper().clean();
		this.cleanUpResourceHelper();

		super.tearDown();
	}

	/**
	 * Sets the used {@link ResourceHelper} to null, in order to ensure that each
	 * test method has a fresh instance.
	 */
	protected void cleanUpResourceHelper() {
		this.resHelper = null;
	}

	/**
	 * Sets up the {@link ResourceHelper} instance that will be used with the given
	 * one.
	 */
	protected void setResourceHelper(ResourceHelper resHelper) {
		this.resHelper = resHelper;
	}

	/**
	 * The {@link ResourceHelper} instance that can be used for creating
	 * {@link Resource} instances.
	 */
	protected ResourceHelper getResourceHelper() {
		return this.resHelper;
	}

	/**
	 * Delegates the creation of a {@link Resource} instance to the underlying
	 * {@link ResourceHelper}. <br>
	 * <br>
	 * The name of the {@link Resource} instance will be the return value of
	 * {@link #getResourceFileName()}.
	 * 
	 * @return A {@link Resource} instance with the given contents
	 */
	protected Resource createResource(Collection<? extends EObject> eos) {
		return this.getResourceHelper().createResource(eos, this.getResourceFileName());
	}

	/**
	 * Uses the currently run test class and method to compute a name for the file
	 * of the {@link Resource} instance, should it be saved.
	 * 
	 * @return A name for the file of the {@link Resource} instance.
	 */
	public String getResourceFileName() {
		return this.getCurrentTestClassName() + "_" + this.getCurrentTestMethodName();
	}

	/**
	 * @return The absolute path, under which the {@link Resource} files will be
	 *         saved.
	 */
	public abstract String getAbsoluteResourceRootPath();

	/**
	 * @return The extension of the {@link Resource} files, if they are saved.
	 */
	public abstract String getResourceFileExtension();
}
