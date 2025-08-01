package cipm.consistency.vsum.test.pcm.unused;

import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Supplier;

import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.models.pcm.PcmFacade;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public interface IPcmCommitIntegration {
	/**
	 * 
	 * @return The root path of this commit integration. All other paths should be
	 *         resolved to this path or subpaths
	 */
	public Path getRootPath();

	/**
	 * 
	 * @return All the change propagation specs that are used by this commit
	 *         integration
	 */
	public List<ChangePropagationSpecification> getChangeSpecs();

	/**
	 * 
	 * @return A supplier to instantiate the generic code model
	 */
	public Supplier<PcmFacade> getPcmModelFacadeSupplier();

	public Supplier<CodeModelFacade> getCodeModelFacadeSupplier();
}
