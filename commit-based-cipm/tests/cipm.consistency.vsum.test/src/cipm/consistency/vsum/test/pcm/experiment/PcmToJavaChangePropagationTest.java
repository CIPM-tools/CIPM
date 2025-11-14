package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.userinteraction.NamespaceConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.IMUpdateEvaluator;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.PcmVsumFacadeImpl;
import cipm.consistency.vsum.test.pcm.cprunittests.UnnamedModuleComponentDetectionStrategy;
import mir.reactions.allPcm.AllPcmChangePropagationSpecification;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmToJavaChangePropagationTest {
	private static final Logger LOGGER = Logger.getLogger(PcmToJavaChangePropagationTest.class);

	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;
	private ImFacade imFacade;
	private JavaModelFacade javaFacade;

	private ExperimentResourceWrapper resWrapper;

	private JaccardCoefficientResult jcOfJavaInJavaToPcmProp;
	private JaccardCoefficientResult jcOfPcmInJavaToPcmProp;
	private ImUpdateEvalData fScoreOfImInJavaToPcmProp;

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
		this.resWrapper = new ExperimentResourceWrapper(new ResourceSetImpl(), dirLayout);
		this.resWrapper.initialise();

		imFacade = this.setupImFacade();
		pcmFacade = this.setupPcmFacade();
		javaFacade = this.setupJavaFacade();
		vsumFacade = this.setupVsumFacade();

		computeEvaluationResultsForJavaToPcmPropagation();
	}

	private PcmToJavaChangePropagationDirLayout getDirLayout() {
		return this.resWrapper.getExperimentLayout();
	}

	public void tearDown() {
		// TODO Close all resources and models
	}

	protected PcmFacade setupPcmFacade() {
		var pcmFacade = new PcmFacade();
		pcmFacade.initialize(getDirLayout().getPropagatedDirLayout().getPcmDirPath());
		return pcmFacade;
	}

	private void computeEvaluationResultsForJavaToPcmPropagation() {
		jcOfJavaInJavaToPcmProp = computeJCForJava(resWrapper.getTargetJavaModel(), resWrapper.getInitialJavaModel());
		jcOfPcmInJavaToPcmProp = computeJCForPcm(resWrapper.getTargetPcmRepository(),
				resWrapper.getInitialPcmRepository());
		fScoreOfImInJavaToPcmProp = computeFScoreForIm(
				(Repository) resWrapper.getTargetPcmRepository().getContents().get(0),
				(InstrumentationModel) resWrapper.getTargetIm().getContents().get(0));
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
		return new PcmVsumFacadeImpl(getDirLayout().getPropagatedDirLayout().getRootDirPath(),
				List.of(pcmFacade, imFacade, javaFacade), this.getCPRs());
	}

	private List<EChange> preprocessPCMchanges() {
		var pcmChangeRes = resWrapper.getPropagatedPcmChanges();
		var pcmChangeList = new ArrayList<EChange>();
		for (var c : pcmChangeRes.getContents()) {
			pcmChangeList.add((EChange) c);
		}
		var orderedPCMChangeList = new ExperimentPcmChangePreprocessor().orderPCMchanges(pcmChangeList);
		for (var o : pcmChangeList) {
			pcmChangeRes.getContents().remove(o);
		}
		pcmChangeRes.getContents().addAll(orderedPCMChangeList);
		return orderedPCMChangeList;
	}

	public void pcmToJavaChangePropagationTestTemplate(PcmToJavaChangePropagationDirLayout dirLayout) {
		this.setUp(dirLayout);

		var pcmChangeList = preprocessPCMchanges();

		var newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();

		// TODO Ignore DataTypes generated for TypeParameters (such as "T")
		// Filter by name, if name length is 1, ignore

		PcmUserInteractionManager.addConflictResolutionStrategy(
				new NamespaceConflictResolutionStrategy(resWrapper.getTargetJavaModel()));

		// Propagate PCM changes
		var pcmToJavaProp = this.propagateChangesToResource(newPcmRepoRes, pcmChangeList);
		LOGGER.info("Pcm to Java propagation over");
//		assertResourcesNotModified();
//		assertPropagationSuccessful(pcmToJavaProp, pcmChangeList);

		LOGGER.info("Reloading models");
		getJavaFacade().reload();
		JavaModelAccess.setJavaModel(getJavaFacade().getResource());
		getPcmFacade().reload();
		newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();
		getImFacade().reload();

		LOGGER.info("Computing JC for Java");
		var jcOfJavaInPcmToJavaProp = computeJCForJava(JavaModelAccess.getJavaModel(),
				resWrapper.getInitialJavaModel());
		LOGGER.info("Computing JC for Pcm");
		var jcOfPcmInPcmToJavaProp = computeJCForPcm(newPcmRepoRes, resWrapper.getInitialPcmRepository());
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
		result.save(getDirLayout().getExperimentResultSavePath());
	}

	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(List.of(new UnnamedModuleComponentDetectionStrategy()));
		model.initialize(getDirLayout().getPropagatedDirLayout().getCodeDirPath());
		var modelRes = model.getResource();
		JavaModelAccess.setJavaModel(modelRes);
		return model;
	}

	protected ImFacade setupImFacade() {
		var imFacade = new ImFacade();
		imFacade.initialize(getDirLayout().getPropagatedDirLayout().getImDirPath());
		return imFacade;
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

	private static final String experimentRootDirNamePrefix = "Teammates-Experiment-";

	private static final List<JavaToPcmPropagationDirLayout> dirLayouts = new ArrayList<>();

	@Test
	public void testPcmPropagation() {
		LoggingSetup.setMinLogLevel(Level.DEBUG);

		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-1-6484257")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-2-48b67ba")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-3-83f518e")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-4-f33d0bc")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-5-ce4463a")));

		var pcmToJavaPropTest = new PcmToJavaChangePropagationTest();

		pcmToJavaPropTest.pcmToJavaChangePropagationTestTemplate(new PcmToJavaChangePropagationDirLayout(null,
				dirLayouts.get(0), Path.of("target", experimentRootDirNamePrefix + 1).toAbsolutePath()));
	}
}
