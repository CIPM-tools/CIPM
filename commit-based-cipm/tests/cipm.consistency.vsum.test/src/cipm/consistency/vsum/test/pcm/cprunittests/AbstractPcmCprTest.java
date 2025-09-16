package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.palladiosimulator.pcm.allocation.util.AllocationResourceFactoryImpl;
import org.palladiosimulator.pcm.repository.util.RepositoryResourceFactoryImpl;
import org.palladiosimulator.pcm.resourceenvironment.util.ResourceenvironmentResourceFactoryImpl;
import org.palladiosimulator.pcm.system.util.SystemResourceFactoryImpl;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelResourceFactoryImpl;

import cipm.consistency.models.ModelFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.PcmVsumFacadeImpl;
import jamopp.resource.JavaResource2Factory;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

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

	protected static final String repositoryFileName = "Repository.repository";

	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;

	@BeforeEach
	public void setup() {
		this.setupModelResources();
		this.copyOldModelFilesToPropagatedModelFiles();

		pcmFacade = this.setupPcmFacade();
		vsumFacade = this.setupVsumFacade();
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
	 * Sets up the necessary resource factory registries and loggers
	 */
	@BeforeAll
	public static void setupBeforeAll() {
		LoggingSetup.setMinLogLevel(Level.DEBUG);

		LOGGER.debug("Setting up resource extension to factory map");
		// Added for Java extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new JavaResource2Factory());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("javaxmi", new JavaResource2Factory());

		// Added for PCM extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("repository",
				new RepositoryResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("allocation",
				new AllocationResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("system", new SystemResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("usagemodel",
				new UsagemodelResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("resourceenvironment",
				new ResourceenvironmentResourceFactoryImpl());

		// Added for change extensions and others
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

		LOGGER.debug("Set up resource extension to factory map");
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
	 * @implSpec AbstractPcmCprTest: Creates a minimal PCM without any
	 *           correspondences by default
	 * 
	 * @return The PCM facade that will be used within this test.
	 */
	protected PcmFacade setupPcmFacade() {
		var pcmFacade = new PcmFacade();
		pcmFacade.initialize(this.getPropagatedModelsRootPath());
		return pcmFacade;
	}

	/**
	 * Use {@link #getRootPath()} as the root directory of the PcmVsumFacade.<br>
	 * <br>
	 * It is not recommended to call the super method from the concrete classes
	 * while overriding this method, in order to keep the construction clear and to
	 * avoid possible side effects. If only a minimal PCM without correspondences is
	 * desired, the super method can be used.
	 * 
	 * @return The VSUM facade for the PCM that will be used in this test.
	 */
	protected PcmVsumFacade setupVsumFacade() {
		return new PcmVsumFacadeImpl(this.getRootPath(), this.getVsumFacadeModels(), this.getCPRs());
	}

	protected List<ModelFacade> getVsumFacadeModels() {
		var list = new ArrayList<ModelFacade>();
		list.add(pcmFacade);
		return list;
	}

	protected abstract List<ChangePropagationSpecification> getCPRs();

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

	protected void logPropagatedChanges(Propagation props) {
		if (props != null && props.getChanges() != null) {
			for (var originalChange : props.getChanges()) {
				LOGGER.info("Original change: " + originalChange.getOriginalChange());
				LOGGER.info("Consequential change: " + originalChange.getConsequentialChanges());
			}
		}
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	protected Resource loadResource(URI uri) {
		var resSet = new ResourceSetImpl();
		return this.loadResource(resSet.createResource(uri));
	}

	/**
	 * Creates, loads and returns a new Resource instance for the same URI. Can be
	 * used to create a separate Resource instance for the given resource.
	 */
	protected Resource loadNewResourceInstance(Resource resource) {
		return this.loadResource(resource.getURI());
	}

	/**
	 * Loads and returns the given resource. Does not create a new resource
	 * instance.
	 */
	protected Resource loadResource(Resource resource) {
		try {
			resource.load(null);
		} catch (IOException e) {
			this.failTest(e);
		}
		return resource;
	}

	/**
	 * Retrieves a resource instance from the {@link #getPcmFacade()}, whose file's
	 * name matches the given parameter. <br>
	 * <br>
	 * The file name of a resource instance is the last segment of its URI.
	 * 
	 * @return The Resource with the given file name that is directly inside the
	 *         PcmFacade
	 */
	protected Resource getResourceFromPcmFacade(String resourceFileName) {
		return this.getPcmFacade().getResources().stream()
				.filter((r) -> r.getURI().lastSegment().equals(resourceFileName)).findFirst().get();
	}

	/**
	 * The return value is the result of loading
	 * {@link #getResourceFromPcmFacade(String)} into a separate Resource instance.
	 * This should be the propagation target, as propagating and modifying the same
	 * Resource instance results in issues (due to concurrent changes (?)).
	 * 
	 * TODO Clarify whether this is true
	 * 
	 * @return A loaded "copy" of the Resource with the given file name inside the
	 *         PcmFacade
	 */
	protected Resource getNewInstanceForResourceFromPcmFacade(String resourceFileName) {
		return this.loadNewResourceInstance(this.getResourceFromPcmFacade(resourceFileName));
	}

	/**
	 * The return value is the result of loading
	 * {@link #getResourceFromPcmFacade(String)} into a separate Resource instance.
	 * This should be the propagation target, as propagating and modifying the same
	 * Resource instance results in issues (due to concurrent changes (?)).
	 * 
	 * TODO Clarify whether this is true
	 * 
	 * @return A loaded "copy" of the Resource with the given file name inside the
	 *         PcmFacade
	 */
	protected Resource getNewInstanceForResourceFromPcmFacade(Resource resourceInPCMModelFacade) {
		return this.getNewInstanceForResourceFromPcmFacade(resourceInPCMModelFacade.getURI().lastSegment());
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

	protected Propagation propagateChangesToResource(Resource res, Collection<EChange> changes) {
		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(res);
		Assertions.assertNull(prop.getException());
		this.logPropagatedChanges(prop);
		return prop;
	}

	protected void reloadVsumFacade() {
		pcmFacade = this.setupPcmFacade();
		vsumFacade = this.setupVsumFacade();
	}
}
