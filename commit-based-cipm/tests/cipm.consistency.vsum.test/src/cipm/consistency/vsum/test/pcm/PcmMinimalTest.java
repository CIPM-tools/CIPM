package cipm.consistency.vsum.test.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.cpr.pcmjava.CommitIntegrationPCMJavaChangePropagationSpecification;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import jamopp.resource.JavaResource2Factory;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;

public class PcmMinimalTest {
	private static final Logger LOGGER = Logger.getLogger(PcmMinimalTest.class);

	private PcmVsumFacadeImpl vsumFacade;
	private PcmFacade pcmFacade;
	private ImFacade imFacade;

	private Path rootPath = Paths.get("target", "PcmMinimalTest");
	private Path oldCommitRootPath = rootPath.resolve("oldCommit");
	private Path newCommitRootPath = rootPath.resolve("newCommit");
	private Path propagatedModelsRootPath = rootPath.resolve("propagatedModels");

//	private Path oldJavaModelPath = rootPath.resolve("oldCommit.javaxmi");
//	private Path newJavaModelPath = rootPath.resolve("newCommit.javaxmi");

//	private Path oldRepoModelPath = rootPath.resolve("oldCommit.repository");
//	private Path newRepoModelPath = rootPath.resolve("newCommit.repository");

	private Path oldToNewPcmChangesPath = rootPath.resolve("oldToNewCommitPcmChanges.changes");

	protected void setup(boolean overwrite) {
		pcmFacade = new PcmFacade();
		pcmFacade.initialize(oldCommitRootPath);
		imFacade = new ImFacade();
		vsumFacade = new PcmVsumFacadeImpl(this.rootPath, List.of(pcmFacade, imFacade),
				List.of(new PcmInitChangePropagationSpecification(), new ImInitChangePropagationSpecification(),
						new PcmImUpdateChangePropagationSpecification(),
						new CommitIntegrationPCMJavaChangePropagationSpecification()));
	}

	@BeforeEach
	public void setup() {
		LoggingSetup.setMinLogLevel(Level.DEBUG);
		setup(false);
	}

	@BeforeAll
	public static void deleteDataBeforeRunningTests() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new JavaResource2Factory());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("javaxmi", new JavaResource2Factory());

		// Added for .repository and .changes extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	}

	protected void failTest(String msg) {
		LOGGER.error(msg);
		Assert.fail(msg);
	}

	private Propagation propagatePcmChanges(Resource repoModel, Resource changeRes, PcmVsumFacadeImpl pcmVsum) {
		LOGGER.info(String.format("Propagating"));

		var changes = new ArrayList<EChange>();
		changeRes.getContents().stream().filter((c) -> c instanceof EChange).forEach((c) -> changes.add((EChange) c));

		pcmVsum.addChanges(changes);
		// the actual propagation is done here
		var propagation = pcmVsum.propagateResource(repoModel, null);

		// add some information needed for the evaluation to the propagation object
//		propagation.setCommitIntegrationStateSnapshotPath(snapshotPath);
//		propagation.setCommitIntegrationStateOriginalPath(state.getDirLayout().getRootDirPath());
//		propagation.setPreviousParsedCodeModelPath(previousParsedModelPath);
//		propagation.setParsedCodeModelPath(parsedModelPath);
//		propagation.setPreviousPcmRepositoryPath(previousRepositoryPath);

		return propagation;
	}

	@Test
	public void testPcmChangePropagation() {
		var resSet = new ResourceSetImpl();
//		var oldJavaModelRes = resSet
//				.createResource(URI.createFileURI(this.oldJavaModelPath.toFile().getAbsolutePath()));
//		var newJavaModelRes = resSet
//				.createResource(URI.createFileURI(this.newJavaModelPath.toFile().getAbsolutePath()));
//		var oldRepoModelRes = resSet
//				.createResource(URI.createFileURI(this.oldRepoModelPath.toFile().getAbsolutePath()));
//		var newRepoModelRes = resSet
//				.createResource(URI.createFileURI(this.newRepoModelPath.toFile().getAbsolutePath()));

		var changeRes = resSet
				.createResource(URI.createFileURI(this.oldToNewPcmChangesPath.toFile().getAbsolutePath()));
		try {
			changeRes.load(null);
		} catch (IOException e) {
			this.failTest(e.getMessage());
		}
		var propTargetPath = propagatedModelsRootPath.resolve("Repository.repository");
		try {
			FileUtils.copyFile(pcmFacade.getDirLayout().getPcmRepositoryPath().toFile(), propTargetPath.toFile());
		} catch (IOException e) {
			this.failTest(e.getMessage());
		}
		var propagationTarget = resSet.createResource(URI.createFileURI(propTargetPath.toFile().getAbsolutePath()));
		try {
			propagationTarget.load(null);
		} catch (IOException e) {
			this.failTest(e.getMessage());
		}
		var props = propagatePcmChanges(propagationTarget, changeRes, vsumFacade);
		if (props != null && props.getChanges() != null) {
			for (var originalChange : props.getChanges()) {
				LOGGER.debug("Original change: " + originalChange.getOriginalChange());
				LOGGER.debug("Consequential change: " + originalChange.getConsequentialChanges());
			}
		}
		try {
			propagationTarget.save(null);
		} catch (IOException e) {
			this.failTest(e.getMessage());
		}
	}
}
