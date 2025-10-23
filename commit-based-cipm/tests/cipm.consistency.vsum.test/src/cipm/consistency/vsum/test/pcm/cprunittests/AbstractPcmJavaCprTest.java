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
	private EObject placeholder;
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
		if (model.getResource() == null) {
			model.parseSourceCodeDir(this.getPropagatedModelsRootPath());
		}
		Assertions.assertTrue(model.existsOnDisk());
		// FIXME Ensure that the Java resource is not empty
		// Otherwise it will be deleted (by Vitruvius)
		var modelRes = model.getResource();
		if (modelRes.getContents().isEmpty()) {
			placeholder = ClassifiersFactory.eINSTANCE.createClass();
			((org.emftext.language.java.classifiers.Class) placeholder).setName(placeholderName);
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
//		for (var c : changes) {
//			for (var feat : c.eClass().getEAllReferences()) {
//				var val = (EObject) c.eGet(feat);
//				if (!(val instanceof EModelElement)) {
//					var it = resourceInModelFacade.getAllContents();
//					while (it.hasNext()) {
//						var elem = it.next();
//						if (EcoreUtil.equals(c, elem)) {
//							c.eSet(feat, elem);
//							break;
//						}
//					}
//				}
//			}
//		}

//		for (var c : changes) {
//			if (c instanceof ReplaceSingleValuedEReference) {
//				var cc = (ReplaceSingleValuedEReference) c;
//				var repo = (Repository) resourceInModelFacade.getContents().get(0);
//				var deletedCmp = repo.getComponents__Repository().get(0);
//				cc.setAffectedEObject(deletedCmp);
//				if (resourceInModelFacade
//						.getEObject(resourceInModelFacade.getURIFragment(deletedCmp).toString()) == null)
//					throw new IllegalStateException("");
//
//				cc.setAffectedEObjectID(resourceInModelFacade.getURI()
//						.appendFragment(resourceInModelFacade.getURIFragment(deletedCmp)).toString());
//			}
//		}
		return changes;
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

	protected Resource getJavaModelResource() {
		return JavaModelAccess.getJavaModel();
	}

	protected Resource getJavaModelResourceFromJavaFacade() {
		return this.javaFacade.getResource();
	}

	protected JavaModelFacade getJavaFacade() {
		return this.javaFacade;
	}

	@Override
	protected void reloadVsumFacade() {
		javaFacade = this.setupJavaFacade();
		super.reloadVsumFacade();
	}
}
