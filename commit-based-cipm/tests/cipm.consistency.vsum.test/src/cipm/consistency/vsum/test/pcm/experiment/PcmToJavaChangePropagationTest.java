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
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.arrays.ArrayDimension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.generics.TypeArgument;
import org.emftext.language.java.modifiers.Modifier;
import org.emftext.language.java.modifiers.Private;
import org.emftext.language.java.modifiers.Public;
import org.emftext.language.java.statements.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.AbstractLoopAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.logger.PcmCprLogger;
import cipm.consistency.cpr.pcmjava.logger.PcmUserInteractionAutomaticityStatistics;
import cipm.consistency.cpr.pcmjava.logger.PcmUserInteractionTimeStatistics;
import cipm.consistency.cpr.pcmjava.userinteraction.AutomatingConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.GenericParameterConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.NamespaceConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.cpr.pcmjava.userinteraction.SyntheticElementConflictResolutionStrategy;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.IMUpdateEvaluator;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.pcm.ChangeSaver;
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

		result.setOriginalJavaChangeCount(resWrapper.getOriginalJavaChanges().getContents().size());
		result.setOriginalPcmChangeCount(resWrapper.getOriginalPcmChanges().getContents().size());
		result.setOriginalImChangeCount(resWrapper.getOriginalImChanges().getContents().size());

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

	// TODO Rename statementless Java model and SEFFless PCM model

	private void setJCForAdaptedJavaModels() {
		LOGGER.info("Computing JC for adapted Java model (Pcm -> Java propagation)");

		var adaptedRes = ResourceOperationsUtil.loadNewResourceInstance(resWrapper.getTargetJavaModel());
		var adaptedResPath = resWrapper.getExperimentLayout().getPropagatedDirLayout().getRootDirPath()
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getExperimentresultadaptedmodelsdir())
				.resolve(adaptedRes.getURI().lastSegment());
		adaptedRes.setURI(ResourceOperationsUtil.pathToURI(adaptedResPath));

		// Remove the synthetic compilation unit, since it does not initially exist
		// during PCM propagation and PCM propagation cannot generate it nor account for
		// it
		//
		// Cannot remove it without turning their occurrences into proxies
		// => Leave it as is
		//
//		var syntheticCU = JavaModelAccess.getSyntheticCompilationUnit(adaptedRes);
//		if (syntheticCU != null) {
//			EcoreUtil.remove(syntheticCU);
//		}

		var allContents = new ArrayList<EObject>();
		adaptedRes.getAllContents().forEachRemaining(allContents::add);
		for (var o : allContents) {
			if (o.eResource() != adaptedRes)
				continue;

			// Remove JavaRoots with ARCHIVE and BINDING, since PCM
			// propagation cannot generate them and they do not initially exist
			//
			// Cannot remove them without turning their occurrences into proxies
			// => Leave them as is
			//
//			if (o instanceof JavaRoot && ((JavaRoot) o).getOrigin() != null
//					&& (((JavaRoot) o).getOrigin().equals(Origin.ARCHIVE)
//							|| ((JavaRoot) o).getOrigin().equals(Origin.BINDING))) {
//				EcoreUtil.remove(o);
//			}

			// Remove all Java Statements, since PCM propagation currently cannot account
			// for them. Make sure to exclude ConcreteClassifiers, as they are
			// Statement instances.
			else if (o instanceof Statement && !(o instanceof ConcreteClassifier)) {
				EcoreUtil.remove(o);
			}
			// Remove all Expressions, since they are not considered in PCM
			// propagation
			else if (o instanceof Expression) {
				EcoreUtil.remove(o);
			}
			// Remove all TypeArguments, since they are not considered in PCM
			// propagation
			else if (o instanceof TypeArgument) {
				EcoreUtil.remove(o);
			}
			// Remove all TypeArguments, since they are not considered in PCM
			// propagation
			else if (o instanceof ArrayDimension) {
				EcoreUtil.remove(o);
			}
			// Remove all non Public / Private modifiers, since they are not considered in
			// PCM propagation
			else if (o instanceof Modifier && !(o instanceof Public || o instanceof Private)) {
				EcoreUtil.remove(o);
			}
		}

		result.setJaccardCoefficientForAdaptedJavaModelInPcmToJavaPropagation(
				computeJCForJava(resWrapper.getPropagatedJavaModel(), adaptedRes));

		ResourceOperationsUtil.saveResource(adaptedRes);
		adaptedRes.unload();
		adaptedRes.getResourceSet().getResources().remove(adaptedRes);
	}

	private void setJCForAdaptedPCMs() {
		LOGGER.info("Computing JC for adapted PCM (Pcm -> Java propagation)");

		var adaptedRes = ResourceOperationsUtil.loadNewResourceInstance(resWrapper.getTargetPcmRepository());
		var adaptedResPath = resWrapper.getExperimentLayout().getPropagatedDirLayout().getRootDirPath()
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getExperimentresultadaptedmodelsdir())
				.resolve(adaptedRes.getURI().lastSegment());
		adaptedRes.setURI(ResourceOperationsUtil.pathToURI(adaptedResPath));

		var allContents = new ArrayList<EObject>();
		adaptedRes.getAllContents().forEachRemaining(allContents::add);
		for (var o : allContents) {
			if (o.eResource() != adaptedRes)
				continue;
			// Remove all loop / branch bodies and branch transitions, since PCM propagation
			// does not consider them
			if ((o instanceof ResourceDemandingBehaviour
					&& (((ResourceDemandingBehaviour) o).getAbstractLoopAction_ResourceDemandingBehaviour() != null
							|| ((ResourceDemandingBehaviour) o)
									.getAbstractBranchTransition_ResourceDemandingBehaviour() != null))
					|| o instanceof AbstractBranchTransition)
				EcoreUtil.remove(o);
		}
		;

		result.setJaccardCoefficientForAdaptedPcmRepositoryInPcmToJavaPropagation(
				computeJCForPcm(resWrapper.getPropagatedPcmRepository(), adaptedRes));

		ResourceOperationsUtil.saveResource(adaptedRes);
		adaptedRes.unload();
		adaptedRes.getResourceSet().getResources().remove(adaptedRes);
	}

	private void addCRSs() {
		// Order of adding CRSs matters here

		// Realistic CRS that prevents creation of Java ConcreteClassifiers for generic
		// parameters
		var genericCRS = new GenericParameterConflictResolutionStrategy((s) -> s.length() < 2);
		PcmUserInteractionManager.addConflictResolutionStrategy(genericCRS);
		PcmUserInteractionAutomaticityStatistics.getInstance().addTestIndependentConflictResolutionStrategy(genericCRS);

		// Oracle CRS that looks up namespaces from target Java code model, in order to
		// automate experiment with valid input
		var namespaceCRS = new NamespaceConflictResolutionStrategy(resWrapper.getTargetJavaModel());
		PcmUserInteractionManager.addConflictResolutionStrategy(namespaceCRS);
		PcmUserInteractionAutomaticityStatistics.getInstance().addTestSpecificConflictResolutionStrategy(namespaceCRS);

		// Oracle CRS that addresses Java code model elements that are synthetic in
		// target model during the propagation
		var syntheticCRS = new SyntheticElementConflictResolutionStrategy(resWrapper.getTargetJavaModel(),
				List.of("synthetic"));
		PcmUserInteractionManager.addConflictResolutionStrategy(syntheticCRS);
		PcmUserInteractionAutomaticityStatistics.getInstance().addTestSpecificConflictResolutionStrategy(syntheticCRS);

		// Oracle CRS that automates all other non-addressed user interactions, in order
		// to fully automate the experiment
		var bruteForceAutomationCRS = new AutomatingConflictResolutionStrategy(List.of("automated"));
		PcmUserInteractionManager.addConflictResolutionStrategy(bruteForceAutomationCRS);
		PcmUserInteractionAutomaticityStatistics.getInstance()
				.addTestSpecificConflictResolutionStrategy(bruteForceAutomationCRS);
	}

	private void savePropagatedChanges(Propagation prop) {
		var propLayout = resWrapper.getExperimentLayout().getPropagatedDirLayout();
		var propagatedChangesPath = propLayout.getRootDirPath()
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getPropagatedchangesdir());
		var propJavaChangesPath = propagatedChangesPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getJavachangessavefilename());
		var propPcmChangesPath = propagatedChangesPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmchangessavefilename());
		var propImChangesPath = propagatedChangesPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getImchangessavefilename());

		ChangeSaver.saveUnresolvedChanges(prop, propJavaChangesPath, propPcmChangesPath, propImChangesPath);

		var propagatedJavaChanges = ResourceOperationsUtil.loadResource(propJavaChangesPath);
		var propagatedPcmChanges = ResourceOperationsUtil.loadResource(propPcmChangesPath);
		var propagatedIMChanges = ResourceOperationsUtil.loadResource(propImChangesPath);

		result.setPropagatedJavaChangeCount(propagatedJavaChanges.getContents().size());
		result.setPropagatedPcmChangeCount(propagatedPcmChanges.getContents().size());
		result.setPropagatedImChangeCount(propagatedIMChanges.getContents().size());
	}

	private void logPropagationTime() {
		LOGGER.info("Pcm to Java propagation over in "
				+ PcmUserInteractionTimeStatistics.getInstance().getPropagationTimeWithUserInteractionsInMillis()
				+ " millis with user interactions, and "
				+ PcmUserInteractionTimeStatistics.getInstance().getPropagationTimeWithoutUserInteractionsInMillis()
				+ " millis without user interactions (difference in millis: "
				+ (PcmUserInteractionTimeStatistics.getInstance().getPropagationTimeWithUserInteractionsInMillis()
						- PcmUserInteractionTimeStatistics.getInstance()
								.getPropagationTimeWithoutUserInteractionsInMillis())
				+ ")");
	}

	private void logAutomaticityDegree() {
		LOGGER.info(PcmUserInteractionAutomaticityStatistics.getInstance().getNumberOfTriggeredUserInteractions()
				+ " user interactions triggered");
		LOGGER.info(PcmUserInteractionAutomaticityStatistics.getInstance()
				.getNumberOfTriggeredFullyAutomaticUserInteractions() + " would realistically be fully automatic");
		LOGGER.info(PcmUserInteractionAutomaticityStatistics.getInstance()
				.getNumberOfTriggeredSemiAutomaticPartiallyInterceptedUserInteractions()
				+ " would realistically be semi-automatic partially intercepted");
		LOGGER.info(PcmUserInteractionAutomaticityStatistics.getInstance()
				.getNumberOfTriggeredSemiAutomaticNonInterceptedUserInteractions()
				+ " would realistically be semi-automatic non-intercepted");

		LOGGER.info("Automaticity degree: "
				+ PcmUserInteractionAutomaticityStatistics.getInstance().getAutomaticityDegree());
	}

	public void pcmToJavaChangePropagationTestTemplate(PcmToJavaChangePropagationDirLayout dirLayout) {
		this.initialiseResources(dirLayout);

		var changeList = preprocessPCMchanges();

		var newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment()
						.equals(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();

		addCRSs();

		// Propagate PCM changes
		PcmUserInteractionTimeStatistics.getInstance().startPropagationTimeMeasurement();
		var pcmToJavaProp = this.propagateChangesToResource(newPcmRepoRes, changeList);
		PcmUserInteractionTimeStatistics.getInstance().endPropagationTimeMeasurement();
		PcmUserInteractionTimeStatistics.getInstance().finaliseTimeMeasurement();

		PcmUserInteractionAutomaticityStatistics.getInstance().computeAutomaticityDegree();

		logPropagationTime();
		logAutomaticityDegree();

		LOGGER.info("Saving propagated changes");

		savePropagatedChanges(pcmToJavaProp);
		// Propagated change Resources must be reloaded anew, if they are to be used

		LOGGER.info("Reloading propagated models for evaluation");
		resWrapper.reloadPropagatedResources();

		LOGGER.info("Computing JC for Java model (Pcm -> Java propagation)");
		result.setJaccardCoefficientForJavaModelInPcmToJavaPropagation(
				computeJCForJava(resWrapper.getPropagatedJavaModel(), resWrapper.getTargetJavaModel()));

		setJCForAdaptedJavaModels();

		LOGGER.info("Computing JC for Pcm repository (Pcm -> Java propagation)");
		result.setJaccardCoefficientForPcmRepositoryInPcmToJavaPropagation(
				computeJCForPcm(resWrapper.getPropagatedPcmRepository(), resWrapper.getTargetPcmRepository()));

		setJCForAdaptedPCMs();

		LOGGER.info("Computing F1-Score for Im (Pcm -> Java propagation)");
		result.setfOneScoreForImInPcmToJavaPropagation(
				computeFScoreForIm((Repository) resWrapper.getPropagatedPcmRepository().getContents().get(0),
						(InstrumentationModel) resWrapper.getPropagatedIm().getContents().get(0)));

		LOGGER.info("Serialising user interaction manager entries");
		PcmCprLogger.getInstance().prepareForSerialisation();
		LOGGER.info("Saving experiment result");
		result.save(getDirLayout().getExperimentResultSavePath());
		LOGGER.info("Saved experiment result");

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
