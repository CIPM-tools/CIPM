package cipm.consistency.vsum.test.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.google.common.base.Supplier;

import cipm.consistency.commitintegration.CommitIntegrationFailureMode;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
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
	 * @return The git repository wrapper instance that is used for the commit
	 *         integration
	 * @throws IOException
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	public GitRepositoryWrapper getGitRepositoryWrapper()
			throws InvalidRemoteException, TransportException, GitAPIException, IOException;

	/**
	 * 
	 * @return A supplier to instantiate the generic code model
	 */
	public Supplier<PcmFacade> getPcmModelFacadeSupplier();

	/**
	 * Returns the failure mode for failing propagations.
	 * 
	 * @return
	 */
	public CommitIntegrationFailureMode getFailureMode();

	/**
	 * Set the failure mode for failing propagations.
	 * 
	 * @return
	 */
	public void setFailureMode(CommitIntegrationFailureMode failureMode);

	public Supplier<CodeModelFacade> getCodeModelFacadeSupplier();
}
