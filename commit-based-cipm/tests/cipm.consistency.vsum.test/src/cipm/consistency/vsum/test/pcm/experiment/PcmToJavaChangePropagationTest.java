package cipm.consistency.vsum.test.pcm.experiment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.IMUpdateEvaluator;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.PcmVsumFacadeImpl;
import cipm.consistency.vsum.test.pcm.cprunittests.UnnamedModuleComponentDetectionStrategy;
import cipm.consistency.vsum.test.pcm.newviews.ResourceCopier;
import mir.reactions.allPcm.AllPcmChangePropagationSpecification;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.resolve.EChangeResolverAndApplicator;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmToJavaChangePropagationTest {
	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;
	private ImFacade imFacade;
	private JavaModelFacade javaFacade;

	private PcmToJavaChangePropagationDirLayout dirLayout;

	private Resource oldJavaResourceCopy;
	private Resource oldPcmRepoResourceCopy;
	private Resource oldImResourceCopy;
	private Resource newJavaResourceCopy;
	private Resource newPcmRepoResourceCopy;
	private Resource newImResourceCopy;

	private JaccardCoefficientResult jcOfJavaInJavaToPcmProp;
	private JaccardCoefficientResult jcOfPcmInJavaToPcmProp;
	private ImUpdateEvalData fScoreOfImInJavaToPcmProp;

	private Resource pcmChangeRes;

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
			e.printStackTrace();
			Assertions.fail(e);
		}
		return resource;
	}

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
		return prop;
	}

	protected ImFacade getImFacade() {
		return this.imFacade;
	}

	protected JavaModelFacade getJavaFacade() {
		return this.javaFacade;
	}

	@BeforeEach
	public void setUp() {
		// TODO Determine the root path
		dirLayout = new PcmToJavaChangePropagationDirLayout(Path.of("rootPath"));

		this.copyTestResources();

		imFacade = this.setupImFacade();
		pcmFacade = this.setupPcmFacade();
		javaFacade = this.setupJavaFacade();
		vsumFacade = this.setupVsumFacade();

		computeEvaluationResultsForJavaToPcmPropagation();

		pcmChangeRes = getChangesResource(dirLayout.getPcmChangesPath());
	}

	@AfterEach
	public void tearDown() {
		// Closes all underlying models too
		vsumFacade.close();
		vsumFacade = null;

		dirLayout = null;

		removeResource(oldJavaResourceCopy);
		oldJavaResourceCopy = null;

		removeResource(oldPcmRepoResourceCopy);
		oldPcmRepoResourceCopy = null;

		removeResource(oldImResourceCopy);
		oldImResourceCopy = null;

		removeResource(newJavaResourceCopy);
		newJavaResourceCopy = null;

		removeResource(newPcmRepoResourceCopy);
		newPcmRepoResourceCopy = null;

		removeResource(newImResourceCopy);
		newImResourceCopy = null;

		removeResource(pcmChangeRes);
		pcmChangeRes = null;

		jcOfJavaInJavaToPcmProp = null;
		jcOfPcmInJavaToPcmProp = null;
		fScoreOfImInJavaToPcmProp = null;

		JavaModelAccess.removeJavaModel();
	}

	private void removeResource(Resource res) {
		if (res.getResourceSet() != null) {
			res.getResourceSet().getResources().remove(res);
		}
		res.unload();
		res.getContents().clear();
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
		pcmFacade.initialize(dirLayout.getPropagatedPcmModelPath());
		return pcmFacade;
	}

	private void computeEvaluationResultsForJavaToPcmPropagation() {
		jcOfJavaInJavaToPcmProp = computeJCForJava(newJavaResourceCopy, oldJavaResourceCopy);
		jcOfPcmInJavaToPcmProp = computeJCForPcm(newPcmRepoResourceCopy, oldPcmRepoResourceCopy);
		fScoreOfImInJavaToPcmProp = computeFScoreForIm((Repository) newPcmRepoResourceCopy.getContents().get(0),
				(InstrumentationModel) newImResourceCopy.getContents().get(0));
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
		return new PcmVsumFacadeImpl(dirLayout.getPropagatedRootPath(), List.of(pcmFacade, imFacade, javaFacade),
				this.getCPRs());
	}

	/**
	 * Copies all relevant Resource files from existing Teammates tests that
	 * propagate Java code changes to PCM.
	 * <p>
	 * Old model Resources are copied twice, since one copy will be used for
	 * evaluation purposes and will not be modified, while the other copy will be
	 * modified via propagation. Because it is not possible to directly set the
	 * Resource of ModelFacade instances, copy the Resource instances to their
	 * designated paths and let the ModelFacades load them.
	 */
	private void copyTestResources() {
		var oldJavaResource = loadResource(pathToURI(dirLayout.getOldJavaModelResourcePath()));
		oldJavaResourceCopy = copyAndSaveResource(oldJavaResource,
				pathToURI(dirLayout.getCopiedOldJavaModelResourcePath()));
		copyAndSaveResource(oldJavaResource, pathToURI(dirLayout.getPropagatedJavaModelPath()));

		var newJavaResource = loadResource(pathToURI(dirLayout.getNewJavaModelResourcePath()));
		newJavaResourceCopy = copyAndSaveResource(newJavaResource,
				pathToURI(dirLayout.getCopiedNewJavaModelResourcePath()));

		var oldPcmResource = loadResource(pathToURI(dirLayout.getOldPcmRepositoryResourcePath()));
		oldPcmRepoResourceCopy = copyAndSaveResource(oldPcmResource,
				pathToURI(dirLayout.getCopiedOldPcmRepositoryResourcePath()));
		copyAndSaveResource(oldPcmResource, pathToURI(dirLayout.getPropagatedPcmModelPath()));

		var newPcmResource = loadResource(pathToURI(dirLayout.getNewPcmRepositoryResourcePath()));
		newPcmRepoResourceCopy = copyAndSaveResource(newPcmResource,
				pathToURI(dirLayout.getCopiedNewPcmRepositoryResourcePath()));

		var oldImResource = loadResource(pathToURI(dirLayout.getOldImResourcePath()));
		oldImResourceCopy = copyAndSaveResource(oldImResource, pathToURI(dirLayout.getCopiedOldImResourcePath()));
		copyAndSaveResource(oldImResource, pathToURI(dirLayout.getPropagatedImModelPath()));

		var newImResource = loadResource(pathToURI(dirLayout.getNewImResourcePath()));
		newImResourceCopy = copyAndSaveResource(newImResource, pathToURI(dirLayout.getCopiedNewImResourcePath()));
	}

	@Test
	public void pcmToJavaChangePropagationTestTemplate() {
		var pcmChangeList = new ArrayList<EChange>();
		pcmChangeRes.getContents().stream().filter((c) -> c instanceof EChange).map((c) -> (EChange) c)
				.forEach(pcmChangeList::add);

		var newPcmRepoRes = this.getResourceFromPcmFacade(dirLayout.getRepositoryFileName());

		// Propagate PCM changes
		var pcmToJavaProp = this.propagateChangesToResource(newPcmRepoRes, pcmChangeList);
		assertResourcesNotModified();
		assertPropagationSuccessful(pcmToJavaProp, pcmChangeList);

		var jcOfJavaInPcmToJavaProp = computeJCForJava(JavaModelAccess.getJavaModel(), oldJavaResourceCopy);
		var jcOfPcmInPcmToJavaProp = computeJCForPcm(newPcmRepoRes, oldPcmRepoResourceCopy);
		var fScoreOfImInPcmToJavaProp = computeFScoreForIm((Repository) newPcmRepoRes.getContents().get(0),
				this.getImFacade().getModel());

		computeAndSaveExperimentResult(jcOfJavaInPcmToJavaProp, jcOfPcmInPcmToJavaProp, fScoreOfImInPcmToJavaProp,
				jcOfJavaInJavaToPcmProp, jcOfPcmInJavaToPcmProp, fScoreOfImInJavaToPcmProp);
	}

	private void computeAndSaveExperimentResult(Object... objs) {
		var result = new ExperimentResult(objs);
		result.interpretResults();
		result.save(dirLayout.getExperimentResultSavePath());
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

	private Resource copyAndSaveResource(Resource resToCopy, URI copyLocationURI) {
		var copy = ResourceCopier.copyViewResource(resToCopy, new ResourceSetImpl());
		copy.setURI(copyLocationURI);
		try {
			copy.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
		return copy;
	}

	private URI pathToURI(Path path) {
		return URI.createFileURI(path.toAbsolutePath().toString());
	}

	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(List.of(new UnnamedModuleComponentDetectionStrategy()));
		model.initialize(dirLayout.getOldJavaModelResourcePath());
		var modelRes = model.getResource();
		JavaModelAccess.setJavaModel(modelRes);
		return model;
	}

	protected ImFacade setupImFacade() {
		var imFacade = new ImFacade();
		imFacade.initialize(dirLayout.getOldImResourcePath());
		return imFacade;
	}

	private void assertPropagationSuccessful(Propagation pcmToJavaProp, List<EChange> propagatedPcmChanges) {
		var pcmChanges = pcmToJavaProp.getChanges().get(0).getOriginalChange().getEChanges();
		Assertions.assertEquals(propagatedPcmChanges.size(), pcmChanges.size());
		for (int i = 0; i < propagatedPcmChanges.size(); i++) {
			// Make sure to unresolve changes to content order related issues
			Assertions.assertTrue(EcoreUtil.equals(EChangeResolverAndApplicator.unresolve(pcmChanges.get(i)),
					EChangeResolverAndApplicator.unresolve(propagatedPcmChanges.get(i))));
		}
	}

	private void assertResourceNotModified(Resource oldResource) {
		// TODO Implement
	}

	private void assertResourcesNotModified() {
		assertResourceNotModified(oldJavaResourceCopy);
		assertResourceNotModified(oldPcmRepoResourceCopy);
		assertResourceNotModified(oldImResourceCopy);
		assertResourceNotModified(newJavaResourceCopy);
		assertResourceNotModified(newPcmRepoResourceCopy);
		assertResourceNotModified(newImResourceCopy);
	}

	private Resource getChangesResource(Path resourcePath) {
		var resSet = new ResourceSetImpl();
		var res = resSet.createResource(URI.createFileURI(resourcePath.toAbsolutePath().toString()));
		return res;
	}

	private JaccardCoefficientResult computeJCForJava(Resource newJavaModel, Resource oldJavaModel) {
		return ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(
				JavaModelComparator.compareJavaModels(newJavaModel, oldJavaModel, null, null, null));
	}

	private JaccardCoefficientResult computeJCForPcm(Resource newPcmRepo, Resource oldPcmRepo) {
		return ComparisonBasedJaccardCoefficientCalculator
				.calculateJaccardCoefficient(PCMModelComparator.compareRepositoryModels(newPcmRepo, oldPcmRepo));
	}

	private ImUpdateEvalData computeFScoreForIm(Repository repo, InstrumentationModel im) {
		var evalData = new ImUpdateEvalData();
		var eval = new IMUpdateEvaluator();
		eval.evaluateIMUpdate(repo, im, evalData, null);
		return evalData;
	}

	protected List<ChangePropagationSpecification> getCPRs() {
		List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
		changeSpecs.add(new PcmInitChangePropagationSpecification());
		changeSpecs.add(new AllPcmChangePropagationSpecification());
		changeSpecs.add(new ImInitChangePropagationSpecification());
		changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
		return changeSpecs;
	}
}
