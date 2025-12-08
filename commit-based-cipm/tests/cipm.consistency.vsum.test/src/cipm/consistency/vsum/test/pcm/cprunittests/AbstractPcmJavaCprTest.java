package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.models.ModelFacade;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;

public abstract class AbstractPcmJavaCprTest extends AbstractPcmCprTest {
	private static final Path javaCommitIntegrationSettingsContainer = Path.of("javaSettings.txt");
	private JavaModelFacade javaFacade;

	/**
	 * TODO Remove after fixing the issue with empty Java model Resources getting
	 * deleted by Vitruvius
	 */
	private EObject placeholder;
	/**
	 * TODO Remove after fixing the issue with empty Java model Resources getting
	 * deleted by Vitruvius
	 */
	private static final String placeholderName = "placeholder";

	@BeforeEach
	@Override
	public void setup() {
//		var settingsFile = this.createJavaSettingsFile();
//		CommitIntegrationSettingsContainer.initialize(settingsFile.toPath());
		CommitIntegrationSettingsContainer
				.initialize(this.getPropagatedModelsRootPath().resolve(javaCommitIntegrationSettingsContainer));
		javaFacade = this.setupJavaFacade();
		super.setup();
	}

	@AfterEach
	@Override
	public void tearDown() {
		PcmUserInteractionManager.reset();
		super.tearDown();
	}

	/**
	 * Sets up and returns a {@link JavaModelFacade} to be used in tests. <br>
	 * <br>
	 * Note: Always keep the returned model facade's Java model resource consistent
	 * with the current Java model resource in JavaModelAccess.
	 */
	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(getComponentDetectionStrategies());
		model.initialize(this.getPropagatedModelsRootPath());
		if (model.getResource() == null) {
			model.parseSourceCodeDir(this.getPropagatedModelsRootPath());
		}
		Assertions.assertTrue(model.existsOnDisk());
		// FIXME Ensure that the Java resource is not empty
		// Otherwise it will be deleted (by Vitruvius)
		var modelRes = model.getResource();
		if (modelRes.getContents().isEmpty()) {
			this.createPlaceholderForJavaModelResource();
			modelRes.getContents().add(placeholder);
			try {
				modelRes.save(null);
			} catch (IOException e) {
				this.failTest(e);
			}
			model.reload();
			placeholder = model.getResource().getContents().get(0);
		}
		JavaModelAccess.setJavaModel(modelRes);
		return model;
	}

	/**
	 * TODO Remove after fixing the issue with empty Java model Resources getting
	 * deleted by Vitruvius
	 */
	protected void createPlaceholderForJavaModelResource() {
		placeholder = ClassifiersFactory.eINSTANCE.createClass();
		((org.emftext.language.java.classifiers.Class) placeholder).setName(placeholderName);
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
		var unmodifiedResDupl = this.getNewInstanceForResourceFromPcmFacade(resourceInModelFacade);
		var modifiedResDupl = this.getNewInstanceForResourceFromPcmFacade(resourceInModelFacade);
		modifications.accept(modifiedResDupl);
		var d = new DefaultStateBasedChangeResolutionStrategy(UseIdentifiers.NEVER);
		var changes = d.getChangeSequenceBetween(modifiedResDupl, unmodifiedResDupl).getEChanges();
		return changes;
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

	protected Resource getJavaModelResource() {
		return JavaModelAccess.getJavaModel();
	}

	protected Resource getJavaModelResourceFromJavaFacade() {
		return this.javaFacade.getResource();
	}

	protected JavaModelFacade getJavaFacade() {
		return this.javaFacade;
	}
}
