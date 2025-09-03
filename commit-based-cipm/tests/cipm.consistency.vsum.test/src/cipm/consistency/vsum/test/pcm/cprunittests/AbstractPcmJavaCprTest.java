package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.models.ModelFacade;
import mir.reactions.dummyPCMJavaCorrespondenceCPRs.DummyPCMJavaCorrespondenceCPRsChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;

public abstract class AbstractPcmJavaCprTest extends AbstractPcmCprTest {
	private static final Path javaCommitIntegrationSettingsContainer = Path.of("javaSettings.txt");
	private JavaModelFacade javaFacade;
	private EObject placeholder;

	@BeforeEach
	public void setup() {
//		var settingsFile = this.createJavaSettingsFile();
//		CommitIntegrationSettingsContainer.initialize(settingsFile.toPath());
		CommitIntegrationSettingsContainer
				.initialize(this.getPropagatedModelsRootPath().resolve(javaCommitIntegrationSettingsContainer));
		javaFacade = this.setupJavaFacade();
		super.setup();
	}

//	protected File createJavaSettingsFile() {
//		var filePath = this.getPropagatedModelsRootPath().resolve(javaCommitIntegrationSettingsContainer);
//		var file = filePath.toFile();
//		file.getParentFile().mkdirs();
//		try {
//			file.createNewFile();
//			var writer = new FileWriter(file);
//			writer.write("cipm.consistency.settings.cpr.pcmim=true\r\n"
//					+ "cipm.consistency.settings.reconstruction.finegrained=false\r\n"
//					+ "cipm.consistency.settings.instrumentation.full=false\r\n"
//					+ "cipm.consistency.settings.path.preprocess=\r\n" + "cipm.consistency.settings.path.compile=\r\n"
//					+ "cipm.consistency.settings.parser.excludes=\r\n" + "cipm.consistency.settings.rest.packages=\r\n"
//					+ "");
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			this.failTest(e);
//		}
//		return file;
//	}

	/**
	 * Sets up and returns a {@link JavaModelFacade} to be used in tests. <br>
	 * <br>
	 * Note: Always keep the returned model facade's Java model resource consistent
	 * with the current Java model resource in JavaModelAccess. <br>
	 * <br>
	 * TODO: Maybe use listener-observer pattern between the 2 classes to automate
	 * this
	 */
	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(getComponentDetectionStrategies());
		model.initialize(this.getPropagatedModelsRootPath());
		model.parseSourceCodeDir(this.getPropagatedModelsRootPath());
		Assertions.assertTrue(model.existsOnDisk());
		// FIXME Ensure that the Java resource is not empty
		// Otherwise it will be deleted (by Vitruvius)
		JavaModelAccess.setJavaModel(model.getResource());
		placeholder = ClassifiersFactory.eINSTANCE.createClass();
//		placeholder.setName("abc");
		JavaModelAccess.getJavaModel().getContents().add(placeholder);
		try {
			JavaModelAccess.getJavaModel().save(null);
		} catch (IOException e) {
			this.failTest(e);
		}
		model.reload();
		placeholder = model.getResource().getContents().get(0);
		return model;
	}

	/**
	 * TODO Remove after fixing the issue with empty Java model Resources getting
	 * deleted by Vitruvius
	 */
	protected void removePlaceholderInJavaModelResource() {
		if (this.placeholder != null && this.placeholder.eResource() != null) {
			this.placeholder.eResource().getContents().remove(placeholder);
		}
	}

	protected List<EChange> getEChangesFor(Resource resourceInModelFacade, Consumer<Resource> modifications) {
		var newRes = this.getNewInstanceForResourceFromPcmFacade(resourceInModelFacade);
		modifications.accept(newRes);
		var d = new DefaultStateBasedChangeResolutionStrategy();
		return d.getChangeSequenceBetween(newRes, resourceInModelFacade).getEChanges();
	}

	protected List<EChange> getEChangesFor(String resourceInModelFacade, Consumer<Resource> modifications) {
		return this.getEChangesFor(this.getResourceFromPcmFacade(resourceInModelFacade), modifications);
	}

	@Override
	protected List<ModelFacade> getVsumFacadeModels() {
		var list = super.getVsumFacadeModels();
		list.add(javaFacade);
		return list;
	}

	protected List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
		var list = new ArrayList<ComponentDetectionStrategy>();
		list.add(new UnnamedModuleComponentDetectionStrategy());
		return list;
	}

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMJavaCorrespondenceCPRsChangePropagationSpecification());
		return list;
	}

	protected Resource getJavaModelResource() {
		return JavaModelAccess.getJavaModel();
	}

	protected Resource getJavaModelResourceFromJavaFacade() {
		return this.javaFacade.getResource();
	}
}
