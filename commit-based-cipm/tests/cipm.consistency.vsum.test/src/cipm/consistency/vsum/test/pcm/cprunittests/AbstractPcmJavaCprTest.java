package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public abstract class AbstractPcmJavaCprTest extends AbstractPcmCprTest {
	private static final Path javaCommitIntegrationSettingsContainer = Path.of("javaSettings.txt");
	private JavaModelFacade javaFacade;

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
		// Ensure that the Java resource is not empty
		// Otherwise it will be deleted (by Vitruvius)
		JavaModelAccess.setJavaModel(model.getResource());
		var placeholder = ClassifiersFactory.eINSTANCE.createClass();
		placeholder.setName("abc");
		JavaModelAccess.getJavaModel().getContents().add(placeholder);
		try {
			JavaModelAccess.getJavaModel().save(null);
		} catch (IOException e) {
			this.failTest(e);
		}
		model.reload();
		return model;
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
}
