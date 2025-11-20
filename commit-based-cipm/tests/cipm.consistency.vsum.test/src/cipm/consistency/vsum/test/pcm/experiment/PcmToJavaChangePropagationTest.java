package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.statements.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractLoopAction;
import org.palladiosimulator.pcm.seff.BranchAction;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.logger.PcmCprLogger;
import cipm.consistency.cpr.pcmjava.logger.PcmUserInteractionStatistics;
import cipm.consistency.cpr.pcmjava.userinteraction.GenericParameterConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.NamespaceConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
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

	private ExperimentResult result;

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

	public void initialiseResources(PcmToJavaChangePropagationDirLayout dirLayout) {
		result = new ExperimentResult();
		if (dirLayout.getOldJavaToPcmPropagationDirLayout() != null) {
			result.setVsumTestPath(dirLayout.getOldJavaToPcmPropagationDirLayout().getRootDirPath());
		} else {
			result.setVsumTestPath(dirLayout.getNewJavaToPcmPropagationDirLayout().getRootDirPath());
		}
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

	@AfterEach
	public void tearDown() {
		// TODO Close all resources and models
		result = null;
		PcmCprLogger.getInstance().clearEntries();
	}

	protected PcmFacade setupPcmFacade() {
		var pcmFacade = new PcmFacade();
		pcmFacade.initialize(getDirLayout().getPropagatedDirLayout().getPcmDirPath());
		return pcmFacade;
	}

	/**
	 * Finds the model that was parsed from source files or was copied from the
	 * previous propagation. Assumes that the parsed models will have the naming
	 * scheme: "X-NUMBER-Y", where NUMBER is the propagation number in the vsum
	 * test, X and Y are arbitrary Strings.
	 * 
	 * @param modelDirPath The path to the directory, under which models directly
	 *                     reside (ex: If model is "testFolder/model.modelext",
	 *                     modelDirPath is "testFolder")
	 * @return Returns the parsed model in the modelDirPath. Meant for parsed Java
	 *         code models and PCM repositories in Teammates vsum tests.
	 */
	private Resource getParsedModelCounterpart(Path modelDirPath) {
		var codePath = resWrapper.getExperimentLayout().getNewJavaToPcmPropagationDirLayout().getCodeDirPath();
		var parsedFilesList = List.of(codePath.toFile().listFiles()).stream()
				.filter((f) -> f.getName().split("-").length == 3).collect(Collectors.toList());
		var parsedModelFile = parsedFilesList.stream()
				.filter((f) -> Integer.valueOf(f.getName().split("-")[1]).intValue() == parsedFilesList.size())
				.findFirst().get();
		return ResourceOperationsUtil.loadResource(parsedModelFile.toPath().toAbsolutePath());
	}

	private void computeEvaluationResultsForJavaToPcmPropagation() {
		result.setJaccardCoefficientForJavaModelInJavaToPcmPropagation(
				computeJCForJava(resWrapper.getTargetJavaModel(), getParsedModelCounterpart(
						resWrapper.getExperimentLayout().getNewJavaToPcmPropagationDirLayout().getCodeDirPath())));
		result.setJaccardCoefficientForPcmRepositoryInJavaToPcmPropagation(
				computeJCForPcm(resWrapper.getTargetPcmRepository(), getParsedModelCounterpart(
						resWrapper.getExperimentLayout().getNewJavaToPcmPropagationDirLayout().getPcmDirPath())));
		result.setfOneScoreForImInJavaToPcmPropagation(
				computeFScoreForIm((Repository) resWrapper.getTargetPcmRepository().getContents().get(0),
						(InstrumentationModel) resWrapper.getTargetIm().getContents().get(0)));
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
		return new PcmVsumFacadeImpl(getDirLayout().getPropagatedDirLayout().getVsumDirPath(),
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

	private void setJCForStatementlessJavaModels() {
		LOGGER.info("Computing JC for statement-less Java model (Pcm -> Java propagation)");

		// TODO Leave out ARCHIVE and BINDING compilation units too, since PCM
		// propagation does not generate them

		// TODO Explicitly remove all statements from propagated Java Model too
		// to ensure that it does not have them neither

		var res = ResourceOperationsUtil.loadNewResourceInstance(resWrapper.getTargetJavaModel());
		var statements = new ArrayList<EObject>();
		res.getAllContents().forEachRemaining((st) -> {
			if (st instanceof Statement && !(st instanceof ConcreteClassifier))
				statements.add(st);
		});
		EcoreUtil.removeAll(statements);

		result.setJaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation(
				computeJCForJava(resWrapper.getPropagatedJavaModel(), res));
		res.unload();
		res.getResourceSet().getResources().remove(res);
	}

	private void setJCForSEFFlessPCMs() {
		LOGGER.info("Computing JC for SEFF-less PCM (Pcm -> Java propagation)");

		// TODO Explicitly remove all SEFF actions below from propagated PCM Repository
		// too to ensure that it does not have them neither

		var res = ResourceOperationsUtil.loadNewResourceInstance(resWrapper.getTargetPcmRepository());
		var statements = new ArrayList<EObject>();
		res.getAllContents().forEachRemaining((st) -> {
			if (st instanceof AbstractLoopAction || st instanceof BranchAction)
				statements.add(st);
		});
		EcoreUtil.removeAll(statements);

		result.setJaccardCoefficientForSEFFlessPcmRepositoryInPcmToJavaPropagation(
				computeJCForPcm(resWrapper.getPropagatedPcmRepository(), res));
		res.unload();
		res.getResourceSet().getResources().remove(res);
	}

	public void pcmToJavaChangePropagationTestTemplate(PcmToJavaChangePropagationDirLayout dirLayout) {
		this.initialiseResources(dirLayout);

		var changeList = preprocessPCMchanges();

		var newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();

		// TODO For all DataTypes that have no correspondents at all, match them with
		// the corresponding synthetic element (via CRS because synthetic elements do
		// not exist during PCM -> Java propagation)

		// TODO For remaining manual user interactions, automate them by implementing
		// CRSs specifically for the propagation

		// TODO Derive automatability metric and save it

		// TODO Measure run-time of propagation and pre-processing (without user
		// interactions)

		var genericCRS = new GenericParameterConflictResolutionStrategy((s) -> s.length() < 2);
		PcmUserInteractionManager.addConflictResolutionStrategy(genericCRS);
		PcmUserInteractionStatistics.getInstance().addTestIndependentConflictResolutionStrategy(genericCRS);

		var namespaceCRS = new NamespaceConflictResolutionStrategy(resWrapper.getTargetJavaModel());
		PcmUserInteractionManager.addConflictResolutionStrategy(namespaceCRS);
		PcmUserInteractionStatistics.getInstance().addTestSpecificConflictResolutionStrategy(namespaceCRS);

		// Propagate PCM changes
		var pcmToJavaProp = this.propagateChangesToResource(newPcmRepoRes, changeList);
		LOGGER.info("Pcm to Java propagation over");

		LOGGER.info("Reloading propagated models for evaluation");
		resWrapper.reloadPropagatedResources();

		LOGGER.info("Computing JC for Java model (Pcm -> Java propagation)");
		result.setJaccardCoefficientForJavaModelInPcmToJavaPropagation(
				computeJCForJava(resWrapper.getPropagatedJavaModel(), resWrapper.getTargetJavaModel()));

		setJCForStatementlessJavaModels();

		LOGGER.info("Computing JC for Pcm repository (Pcm -> Java propagation)");
		result.setJaccardCoefficientForPcmRepositoryInPcmToJavaPropagation(
				computeJCForPcm(resWrapper.getPropagatedPcmRepository(), resWrapper.getTargetPcmRepository()));

		setJCForSEFFlessPCMs();

		LOGGER.info("Computing F1-Score for Im (Pcm -> Java propagation)");
		result.setfOneScoreForImInPcmToJavaPropagation(
				computeFScoreForIm((Repository) resWrapper.getPropagatedPcmRepository().getContents().get(0),
						(InstrumentationModel) resWrapper.getPropagatedIm().getContents().get(0)));

		LOGGER.info("Serialising user interaction manager entries");
		PcmCprLogger.getInstance().prepareForSerialisation();
		LOGGER.info("Saving experiment result");
		result.save(getDirLayout().getExperimentResultSavePath());
		LOGGER.info("Saved experiment result");

		LOGGER.info("Evaluating Pcm -> Java propagation");
		var evaluator = new PcmToJavaPropagationEvaluator(pcmToJavaProp, resWrapper);
		var evaluatorResult = evaluator.evaluate();
		var evaluationDataContainer = EvaluationDataContainer.get();
		evaluationDataContainer.setSuccessful(evaluatorResult);
		var evaluationFileName = "pcmToJavaPropagationEvaluationData.json";
		var evaluationPath = resWrapper.getExperimentLayout().getPropagatedDirLayout().getRootDirPath()
				.resolve(evaluationFileName);
		LOGGER.info("Saving Pcm -> Java propagation evaluation");
		EvaluationDataContainerReaderWriter.write(evaluationDataContainer, evaluationPath);

		LOGGER.info("Tearing down");
		this.tearDown();
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
				.calculateJaccardCoefficient(PCMModelComparator.compareRepositoryModelsIDBased(newPcmRepo, oldPcmRepo));
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
