package cipm.consistency.vsum.test.evaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.xtext.EcoreUtil2;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaStateBasedChangeResolutionStrategy;
//import cipm.consistency.commitintegration.lang.lua.changeresolution.HierarchicalStateBasedChangeResolutionStrategy;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.PcmUpdateEvalData;
import cipm.consistency.tools.evaluation.data.PcmUpdateEvalData.ComparisonType;
import cipm.consistency.tools.evaluation.data.PcmUpdateEvalData.PcmEvalType;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.IMUpdateEvaluator;

/**
 * This class can be used to evaluate one propagation step, including the state based change
 * resolution.
 * 
 * This is achieved by comparing the model resulting from the propagation with the parsed model of
 * the target version.
 *
 * 
 * @param <CM>
 *            The used code model
 * @author Lukas Burgey
 */
public class PropagationEvaluator<CM extends CodeModelFacade> {
    private static final Logger LOGGER = Logger.getLogger(PropagationEvaluator.class);

    private Propagation propagation;
    private CommitIntegrationState<CM> state;
    private CommitIntegration<CM> commitIntegration;
    private Path manualModelDirPath;

    private boolean loaded = false;

    public PropagationEvaluator(Propagation propagation, CommitIntegration<CM> commitIntegration,
            Path manualModelPath) {
        this.propagation = propagation;
        this.commitIntegration = commitIntegration;
        this.manualModelDirPath = manualModelPath;

        state = new CommitIntegrationState<CM>();

        try {
            // load the CIS without overwriting and loading the vsum
            // (Loading a moved VSUM is currently not possible, because the paths in its
            // models.models file
            // are absolute and not relative)
            state.initialize(commitIntegration, propagation.getCommitIntegrationStateCopyPath(), false, false);
            loaded = true;
        } catch (Exception e) {
//            e.printStackTrace();
            var msg = "Unable to load commit integration state located at "
                    + propagation.getCommitIntegrationStateCopyPath() + ": " + e.getMessage();
            LOGGER.error(msg);

            var errorEval = new EvaluationDataContainer();
            errorEval.setErrorMessage(msg);
            errorEval.setEvaluationRan(false);
            errorEval.setSuccessful(false);
            EvaluationDataContainer.set(errorEval);
        }
    }

    /*
     * Validate a given EObject and all its children
     */
    private static boolean validateEObject(EObject rootEObject) {
        var diagnostics = new BasicDiagnostic();
        var eObjValidator = new EObjectValidator();
        var allContents = EcoreUtil2.getAllContentsOfType(rootEObject, EObject.class);
        var contentsValid = true;
        for (var eObj : allContents) {
            var valid = eObjValidator.validate(eObj, diagnostics, null);
            contentsValid &= valid;
        }
        if (!contentsValid) {
            for (var diag : diagnostics.getChildren()) {
                LOGGER.warn(diag.getMessage());
            }
        }
        return contentsValid;
    }

    private static boolean validateResource(Resource res) {
        var invalidEobjects = 0;
        for (var eObj : res.getContents()) {
            if (!validateEObject(eObj)) {
                invalidEobjects++;
            }
        }
        return invalidEobjects == 0;
    }

    private static boolean evaluateModel(Path modelPath, Class<? extends EObject> clazz) {
        try {
            var modelRootElement = ModelUtil.readFromFile(modelPath.toFile(), clazz);
            if (modelRootElement == null) {
                LOGGER.warn("Could read model from evaluate model");
                return false;
            }
            EcoreUtil2.resolveAll(modelRootElement);
            return validateEObject(modelRootElement);
        } catch (WrappedException e) {
            LOGGER.warn(e.getMessage());
//            if (e.getCause() instanceof UnresolvedReferenceException unresExp) {
//                LOGGER.warn("Could not resolve " + unresExp.getFeature()
//                    .getName() + " / " + unresExp.getReference() + " on " + unresExp.getObject());
//            }
            return false;
        }
    }

    private boolean evaluateChangeResolutionTextFileSimilarity() {
        var targetModelPath = state.getCodeModelFacade()
            .getDirLayout()
            .getParsedCodePath();
        var actualModelPath = state.getDirLayout()
            .getVsumCodeModelPath();
        if (targetModelPath == null || actualModelPath == null) {
            // no evaluation if we don't have the respective model files to evaluate
            return true;
        }

        var modelsSimilar = diffModelFiles(targetModelPath, actualModelPath);
        if (!modelsSimilar) {
            LOGGER.warn("Code model update: parsed.code.xmi != vsum.code.xmi");
            var printDiff = false;
            if (printDiff) {
                printDiffBetween(targetModelPath, actualModelPath);
            }
        }

        return modelsSimilar;
    }

    private Resource getCodeModel(Path path) {
        var file = path.toFile();
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI filePathUri = org.eclipse.emf.common.util.URI.createFileURI(file.getAbsolutePath());

        Resource resource = resourceSet.getResource(filePathUri, true);
        return resource;
    }

    private Resource getVsumCodeModel() {
        return getCodeModel(state.getDirLayout()
            .getVsumCodeModelPath());
    }

    private Comparison compareCodeModels(Resource parsedCodeModel, Resource vsumCodeModel) {
        return JavaModelComparator.compareJavaModels(parsedCodeModel, vsumCodeModel, null, null, null);
    }

    private void evaluateCodeModelUpdate(EvaluationDataContainer eval) {
        var vsumCodeModel = getVsumCodeModel();
        Resource parsedCodeModel = state.getCodeModelFacade()
            .getResource();

        var comparison = compareCodeModels(parsedCodeModel, vsumCodeModel);
        var jaccardResult = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comparison);

        if (jaccardResult.getJC() < 1) {
            LOGGER.warn("Code model update has suboptimal JC: " + jaccardResult.getJC());
        }
        eval.getCodeModelUpdateEvalData()
            .setValuesUsingJaccardCoefficientResult(jaccardResult);
    }

    private Path locateStateCopyForCommit(Path directory, int commitCount, String commitId) {
        var currentName = propagation.getCommitIntegrationStateOriginalPath()
            .getFileName();
        var lookupName = currentName + "-" + commitCount + "-" + commitId;
        var pathOfPossibleCommitCopy = directory.resolve(lookupName);
        if (pathOfPossibleCommitCopy.toFile()
            .exists()) {
            return pathOfPossibleCommitCopy;
        }
        return null;
    }

    private Path locateStateCopyForCommit(Path directory, Propagation propagation) {
//        var currentName = propagation.getCommitIntegrationStateOriginalPath()
//            .getFileName();
        var lookupName = propagation.getCommitIntegrationStateCopyPath()
            .getFileName();
        var pathOfPossibleCommitCopy = directory.resolve(lookupName);
        if (pathOfPossibleCommitCopy.toFile()
            .exists()) {
            return pathOfPossibleCommitCopy;
        }
        return null;
    }

    private CommitIntegrationState<CM> getCommitIntegrationStateForCommit(Path directory, int commitCount,
            String commitId) {
        var stateCopyPath = locateStateCopyForCommit(directory, commitCount, commitId);
        if (stateCopyPath == null) {
            return null;
        }
        var stateCopy = new CommitIntegrationState<CM>();
        try {
            stateCopy.initialize(commitIntegration, stateCopyPath, false, false);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return stateCopy;
    }

    private CommitIntegrationState<CM> getCommitIntegrationStateForCommit(Path directory, Propagation propagation) {
        var stateCopyPath = locateStateCopyForCommit(directory, propagation);
        if (stateCopyPath == null) {
            return null;
        }
        var stateCopy = new CommitIntegrationState<CM>();
        try {
            stateCopy.initialize(commitIntegration, stateCopyPath, false, false);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return stateCopy;
    }

    private void evaluatePcmUpdateJaccardManual(EvaluationDataContainer eval) {
        // locate the state in which the manual copy was created
        var stateCopyAutomatic = getCommitIntegrationStateForCommit(this.manualModelDirPath, propagation);
        if (stateCopyAutomatic == null) {
            return;
        }

        var referenceRepository = ModelUtil.readFromFile(stateCopyAutomatic.getPcmFacade()
            .getDirLayout()
            .getPcmRepositoryPath()
            .toFile(), Repository.class);

        var newRepository = ModelUtil.readFromFile(state.getPcmFacade()
            .getDirLayout()
            .getPcmRepositoryPath()
            .toFile(), Repository.class);

        var comparison = PCMModelComparator.compareRepositoryModels(newRepository.eResource(),
                referenceRepository.eResource());

        var jaccardResult = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comparison);

        var pcmEvalData = new PcmUpdateEvalData();
        pcmEvalData.setValuesUsingJaccardCoefficientResult(jaccardResult);
        pcmEvalData.setEvalType(PcmEvalType.ComparisonWithManuallyCreated);
        pcmEvalData.setComparisonType(ComparisonType.PcmDiffUtil);

        eval.getPcmUpdateEvals()
            .add(pcmEvalData);
    }

    private void evaluatePcmUpdateJaccardAutomatic(EvaluationDataContainer eval) {
        // locate the state copy that was created from scratch for this commit
        var stateCopyAutomatic = getCommitIntegrationStateForCommit(propagation.getCommitIntegrationStateCopyPath()
            .getParent(), 1, propagation.getCommitId());

        if (stateCopyAutomatic == null) {
            return;
        }

        var referenceRepository = ModelUtil.readFromFile(stateCopyAutomatic.getPcmFacade()
            .getDirLayout()
            .getPcmRepositoryPath()
            .toFile(), Repository.class);

        var newRepository = ModelUtil.readFromFile(state.getPcmFacade()
            .getDirLayout()
            .getPcmRepositoryPath()
            .toFile(), Repository.class);

        var comparison = PCMModelComparator.compareRepositoryModels(newRepository.eResource(),
                referenceRepository.eResource());
//        var diffs = comparison.getDifferences();
//        var noIdDiffs = diffs.stream()
//            .filter(i -> !(i instanceof AttributeChange))
//            .toList();

        var jaccardResult = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comparison);

        if (jaccardResult.getJC() < 1) {
            LOGGER.warn("PCM repo model update has suboptimal JC: " + jaccardResult.getJC());
        }

        var pcmEvalData = new PcmUpdateEvalData();
        pcmEvalData.setValuesUsingJaccardCoefficientResult(jaccardResult);
        pcmEvalData.setEvalType(PcmEvalType.ComparisonWithAutomatic);
        pcmEvalData.setComparisonType(ComparisonType.PcmDiffUtil);

        eval.getPcmUpdateEvals()
            .add(pcmEvalData);
    }

    /**
     * Evaluates if the change resolution that was used during the propagation is correct.
     * 
     * During the change resolution we generate a change sequence between the previously parsed code
     * model and the parsed code model of the new ("target") version. If applying this change
     * sequence to the code model of the vsum does not result in a model similar to the parsed code
     * model of the target version something went wrong.
     * 
     * @param propagation
     * @return True if the vsum code model was updated correctly during the propagation
     */
    private boolean evaluateChangeResolution() {
        return evaluateChangeResolutionTextFileSimilarity();
    }

    private boolean evaluateVsumCodeModel() {
        var valid = validateResource(getVsumCodeModel());
        if (!valid) {
            LOGGER.warn("VSUM Code model is invalid");
        }
        return valid;
    }

    /**
     * Runs the Repository validator against the repository model that was the result of this
     * propagation.
     * 
     * @param propagation
     * @return True if valid
     */
    private static boolean evaluateVsumRepositoryModel(Path path) {
        var valid = evaluateModel(path, Repository.class);
        if (!valid) {
            LOGGER.error("VSUM Repository model is invalid");
        }
        return valid;
    }

    /**
     * Check if the IMM passes validation
     * 
     * @param propagation
     * @return True if valid
     */
    private static boolean evaluateResultingImm(Path path) {
        var valid = evaluateModel(path, InstrumentationModel.class);
        if (!valid) {
            LOGGER.debug("IMM is invalid");
        }
        return valid;
    }

    private void evaluateImUpdate() {
        var evaluator = new IMUpdateEvaluator();
        var repo = state.getPcmFacade()
            .getInMemoryPCM()
            .getRepository();
        var im = state.getImFacade()
            .getModel();
        var imEvalData = EvaluationDataContainer.get()
            .getImUpdateEvalData();

        var repoFile = propagation.getPreviousPcmRepositoryPath()
            .toFile();
        if (!repoFile.exists()) {
            return;
        }

        try {
            var previousRepo = ModelUtil.readFromFile(repoFile, Repository.class);
//            evaluator.evaluateIMUpdate(repo, im, imEvalData, previousRepo);
        } catch (Exception e) {
            LOGGER.error("Error loading previous repository: " + e.getMessage());
        }
    }

    public boolean evaluate() {
        if (!loaded) {
            return false;
        }

        // 1. Evaluations that only write to the evaluation data:
        var eval = EvaluationDataContainer.get();
        if (propagation.getException() != null) {
            eval.setErrorMessage(propagation.getException()
                .getMessage());
        }
        evaluateCodeModelUpdate(eval);

        // these two evaluations accidentally overwrite parts of the data container
        // so we manage the container manually during these evals and reset the
        // global container afterwards
        evaluatePcmUpdateJaccardAutomatic(eval);
        evaluatePcmUpdateJaccardManual(eval);
        EvaluationDataContainer.set(eval);

        evaluateImUpdate();

        // 2. Evaluations with binary result:
        var valid = true;
        valid &= evaluateChangeResolution();
        valid &= evaluateVsumCodeModel();
        valid &= evaluateVsumRepositoryModel(state.getPcmFacade()
            .getDirLayout()
            .getPcmRepositoryPath());
        valid &= evaluateResultingImm(state.getImFacade()
            .getDirLayout()
            .getImFilePath());

        if (valid) {
            LOGGER.info("Propagation passed evaluation\n");
        }
        EvaluationDataContainer.get()
            .setEvaluationRan(true);

        // save the evaluation data to the directory of the integration state copy
        state.persistEvaluationData();

        return valid;
    }

    private static boolean diffModelFiles(Path target, Path actual) {
        try {
            return FileUtils.contentEquals(target.toFile(), actual.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * These next two things from: https://www.baeldung.com/run-shell-command-in-java
     */
    private static void printDiffBetween(Path target, Path actual) {
        try {
            var process = Runtime.getRuntime()
                .exec(String.format("/usr/bin/icdiff --cols=200 %s %s", target.toString(), actual.toString()));

            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor()
                .submit(streamGobbler);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                future.get();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }
}
