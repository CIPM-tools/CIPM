package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.PcmVsumFacadeImpl;
import jamopp.resource.JavaResource2Factory;
import mir.reactions.dummyPCMCPRs.DummyPCMCPRsChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;

public abstract class AbstractPcmCprTest {
	private static final Logger LOGGER = Logger.getLogger(AbstractPcmCprTest.class);

	/**
	 * {@link AbstractPcmCprTest#getRootPath()}
	 */
	private static final Path rootPath = Paths.get("target", "PcmCprTest").toAbsolutePath();
	/**
	 * {@link AbstractPcmCprTest#getOldModelsRootPath()}
	 */
	private static final Path oldModelsRootPath = rootPath.resolve("oldModels");
	/**
	 * {@link AbstractPcmCprTest#getPropagatedModelsRootPath()}
	 */
	private static final Path propagatedModelsRootPath = rootPath.resolve("propagatedModels");

	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;

	@BeforeEach
	public void setup() {
		LoggingSetup.setMinLogLevel(Level.DEBUG);
		this.setupModelResources();
		this.copyOldModelFilesToPropagatedModelFiles();

		pcmFacade = this.setupPcmFacade();
		vsumFacade = this.setupVsumFacade(pcmFacade);
	}

	@AfterEach
	public void tearDown() {
		var root = rootPath.toFile();
		if (root.exists()) {
			try {
				Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
						if (e == null) {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						} else {
							// directory iteration failed
							throw e;
						}
					}
				});
			} catch (IOException e) {
				this.failTest(e);
			}
		}
	}

	/**
	 * Sets up the necessary resource factory registries
	 */
	@BeforeAll
	public static void setupBeforeAll() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new JavaResource2Factory());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("javaxmi", new JavaResource2Factory());

		// Added for .repository and .changes extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	}

	/**
	 * Use {@link #getPropagatedModelsRootPath()} as the root directory, so that the
	 * initial states of the models are not modified, allowing them to be used in
	 * assertions later on. <br>
	 * <br>
	 * It is not recommended to call the super method from the concrete classes
	 * while overriding this method, in order to keep the construction clear and to
	 * avoid possible side effects. If only a minimal PCM is desired, the super
	 * method can be used.
	 * 
	 * @return The PCM facade that will be used within this test.
	 */
	protected PcmFacade setupPcmFacade() {
		return new PcmFacade();
	}

	/**
	 * Use {@link #getRootPath()} as the root directory of the PcmVsumFacade.<br>
	 * <br>
	 * It is not recommended to call the super method from the concrete classes
	 * while overriding this method, in order to keep the construction clear and to
	 * avoid possible side effects. If only a minimal PCM without correspondences is
	 * desired, the super method can be used.
	 * 
	 * @param pcmFacade The underlying PcmFacade created by
	 *                  {@link #setupPcmFacade()} or by other ways.
	 * @return The VSUM facade for the PCM that will be used in this test.
	 */
	protected PcmVsumFacade setupVsumFacade(PcmFacade pcmFacade) {
		return new PcmVsumFacadeImpl(this.getRootPath(), List.of(pcmFacade),
				List.of(new DummyPCMCPRsChangePropagationSpecification()));
	}

	/**
	 * Encapsulates the setup of all model resources (PCM, Java and/or IM). <br>
	 * <br>
	 * All created files should be saved under {@link #getOldModelsRootPath()}, so
	 * that the created model facades can find and use them. Their names and
	 * sub-paths should conform the layout of their respective models. <br>
	 * <br>
	 * Does nothing unless overridden.
	 */
	protected void setupModelResources() {

	}

	/**
	 * Copies all old model files {@value #oldModelsRootPath} to propagated model
	 * files {@value #propagatedModelsRootPath} directory, so that the initial
	 * states of the models are available for assertions later on
	 */
	protected void copyOldModelFilesToPropagatedModelFiles() {
		if (this.getOldModelsRootPath().toFile().exists()) {
			for (var f : this.getOldModelsRootPath().toFile().listFiles()) {
				try {
					var targetFile = this.getPropagatedModelsRootPath().resolve(f.getName()).toFile();
					if (targetFile.exists()) {
						targetFile.delete();
					} else {
						targetFile.getParentFile().mkdirs();
					}
					Files.copy(f.toPath(), targetFile.toPath());
				} catch (IOException e) {
					e.printStackTrace();
					Assertions.fail(e);
				}
			}
		}
	}

	protected void failTest(Exception e) {
		LOGGER.error(String.format("Exception thrown during test: ", e.getClass().getSimpleName()), e);
		Assertions.fail(e);
	}

	protected void failTest(String msg) {
		LOGGER.error(msg);
		Assert.fail(msg);
	}

	protected Propagation propagatePcmChanges(Resource pcmResourceToPropagate, Collection<EChange> changes) {
		LOGGER.info(String.format("Propagating"));

		this.getPcmVsumFacade().addChanges(changes);
		// the actual propagation is done here
		var propagation = this.getPcmVsumFacade().propagateResource(pcmResourceToPropagate);

		return propagation;
	}

	/**
	 * PcmVsumFacade saves the propagated resource internally, so no need to save
	 * model resources post propagation
	 */
	public void testPcmChangePropagation(Resource pcmResourceToPropagate, Collection<EChange> pcmChangesToPropagate) {
		var props = propagatePcmChanges(pcmResourceToPropagate, pcmChangesToPropagate);
		this.logPropagatedChanges(props);
	}

	protected void logPropagatedChanges(Propagation props) {
		if (props != null && props.getChanges() != null) {
			for (var originalChange : props.getChanges()) {
				LOGGER.info("Original change: " + originalChange.getOriginalChange());
				LOGGER.info("Consequential change: " + originalChange.getConsequentialChanges());
			}
		}
	}

	/**
	 * @return The path, under which all files regarding this test reside.
	 */
	public Path getRootPath() {
		return rootPath;
	}

	/**
	 * @return The path, under which all initial model files reside.
	 */
	public Path getOldModelsRootPath() {
		return oldModelsRootPath;
	}

	/**
	 * @return The path, under which all propagated (or to be propagated) files of
	 *         models reside. Whether those files are propagated or not depends on
	 *         the state of the test.
	 */
	public Path getPropagatedModelsRootPath() {
		return propagatedModelsRootPath;
	};

	public PcmFacade getPcmFacade() {
		return this.pcmFacade;
	}

	public PcmVsumFacade getPcmVsumFacade() {
		return this.vsumFacade;
	}
}
