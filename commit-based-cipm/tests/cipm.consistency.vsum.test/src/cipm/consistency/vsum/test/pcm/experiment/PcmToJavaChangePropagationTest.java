package cipm.consistency.vsum.test.pcm.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;
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
import mir.reactions.allPcm.AllPcmChangePropagationSpecification;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.resolve.EChangeResolverAndApplicator;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmToJavaChangePropagationTest {
	private static final Logger LOGGER = Logger.getLogger(PcmToJavaChangePropagationTest.class);

	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;
	private ImFacade imFacade;
	private JavaModelFacade javaFacade;

	private PcmToJavaChangePropagationDirLayout dirLayout;

	private JavaToPcmPropagationWrapper oldWrapper;
	private JavaToPcmPropagationWrapper newWrapper;

	private JavaToPcmPropagationWrapper oldCopyWrapper;
	private JavaToPcmPropagationWrapper newCopyWrapper;

	private JavaToPcmPropagationWrapper propWrapper;

	private ResourceSet resSet;

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

	public void setUp(PcmToJavaChangePropagationDirLayout dirLayout) {
		this.dirLayout = dirLayout;

		this.copyTestResources();

		imFacade = this.setupImFacade();
		pcmFacade = this.setupPcmFacade();
		javaFacade = this.setupJavaFacade();
		vsumFacade = this.setupVsumFacade();

		computeEvaluationResultsForJavaToPcmPropagation();
	}

	public void tearDown() {
		// Closes all underlying models too
//		vsumFacade.close();
//		vsumFacade = null;
//
//		dirLayout = null;
//
//		oldWrapper.close();
//		newWrapper.close();
//		oldCopyWrapper.close();
//		newCopyWrapper.close();
//		propWrapper.close();
//
//		oldWrapper = null;
//		newWrapper = null;
//		oldCopyWrapper = null;
//		newCopyWrapper = null;
//		propWrapper = null;
//
//		jcOfJavaInJavaToPcmProp = null;
//		jcOfPcmInJavaToPcmProp = null;
//		fScoreOfImInJavaToPcmProp = null;
//
//		JavaModelAccess.removeJavaModel();
	}

	protected PcmFacade setupPcmFacade() {
		var pcmFacade = new PcmFacade();
		pcmFacade.initialize(dirLayout.getPropagatedDirLayout().getPcmDirPath());

		// FIXME Remove once initial change propagation is successful
		// Must ensure that correct PCM starting models are used for propagation
		pcmFacade.getResources().stream().forEach((r) -> EcoreUtil.removeAll(r.getContents().get(0).eContents()));
		pcmFacade.saveToDisk();
		propWrapper.adaptURIsInPCMChangeResource(pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get());
		pcmChangeRes = propWrapper.getPcmChanges();

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
		return new PcmVsumFacadeImpl(dirLayout.getPropagatedDirLayout().getRootPath(),
				List.of(pcmFacade, imFacade, javaFacade), this.getCPRs());
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
		this.resSet = new ResourceSetImpl();

		oldWrapper = new JavaToPcmPropagationWrapper(this.resSet);
		oldWrapper.initialise(this.dirLayout.getOldJavaToPcmPropagationDirLayout());
		newWrapper = new JavaToPcmPropagationWrapper(this.resSet);
		newWrapper.initialise(this.dirLayout.getNewJavaToPcmPropagationDirLayout());

		oldCopyWrapper = oldWrapper.copyTo(this.dirLayout.getCopiedOldJavaToPcmPropagationDirLayout().getRootPath());
		newCopyWrapper = newWrapper.copyTo(this.dirLayout.getCopiedNewJavaToPcmPropagationDirLayout().getRootPath());

		// All Resource instances that will be used for propagation are to be found
		// under this layout
		propWrapper = oldWrapper.copyTo(this.dirLayout.getPropagatedDirLayout().getRootPath());

		oldJavaResourceCopy = oldCopyWrapper.getJavaModel();
		oldPcmRepoResourceCopy = oldCopyWrapper.getPcmRepository();
		oldImResourceCopy = oldCopyWrapper.getIm();

		newJavaResourceCopy = newCopyWrapper.getJavaModel();
		newPcmRepoResourceCopy = newCopyWrapper.getPcmRepository();
		newImResourceCopy = newCopyWrapper.getIm();
	}

	public void pcmToJavaChangePropagationTestTemplate(PcmToJavaChangePropagationDirLayout dirLayout) {
		this.setUp(dirLayout);

		var pcmChangeList = new ArrayList<EChange>();
		for (var c : pcmChangeRes.getContents()) {
			pcmChangeList.add((EChange) c);
		}
//		for (var c : pcmChangeRes.getContents().subList(0, 15)) {
//			pcmChangeList.add((EChange) c);
//		}

		var newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();

		// Propagate PCM changes
		var pcmToJavaProp = this.propagateChangesToResource(newPcmRepoRes, pcmChangeList);
		LOGGER.info("Pcm to Java propagation over");
//		assertResourcesNotModified();
//		assertPropagationSuccessful(pcmToJavaProp, pcmChangeList);

		LOGGER.info("Computing JC for Java");
		var jcOfJavaInPcmToJavaProp = computeJCForJava(JavaModelAccess.getJavaModel(), oldJavaResourceCopy);
		LOGGER.info("Computing JC for Pcm");
		var jcOfPcmInPcmToJavaProp = computeJCForPcm(newPcmRepoRes, oldPcmRepoResourceCopy);
		LOGGER.info("Computing F1-Score for Im");
		var fScoreOfImInPcmToJavaProp = computeFScoreForIm((Repository) newPcmRepoRes.getContents().get(0),
				this.getImFacade().getModel());

		LOGGER.info("Computing and saving experiment result");
		computeAndSaveExperimentResult(jcOfJavaInPcmToJavaProp, jcOfPcmInPcmToJavaProp, fScoreOfImInPcmToJavaProp,
				jcOfJavaInJavaToPcmProp, jcOfPcmInJavaToPcmProp, fScoreOfImInJavaToPcmProp);

		LOGGER.info("Tearing down");
		this.tearDown();
	}

	private void computeAndSaveExperimentResult(Object... objs) {
		var result = new ExperimentResult(objs);
		result.interpretResults();
		result.save(dirLayout.getExperimentResultSavePath());
	}

	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(List.of(new UnnamedModuleComponentDetectionStrategy()));
		model.initialize(dirLayout.getPropagatedDirLayout().getCodeDirPath());
		var modelRes = model.getResource();
		JavaModelAccess.setJavaModel(modelRes);
		return model;
	}

	protected ImFacade setupImFacade() {
		var imFacade = new ImFacade();
		imFacade.initialize(dirLayout.getPropagatedDirLayout().getImDirPath());
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
		changeSpecs.add(new ImInitChangePropagationSpecification());
		changeSpecs.add(new AllPcmChangePropagationSpecification());
		changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
		return changeSpecs;
	}
}
