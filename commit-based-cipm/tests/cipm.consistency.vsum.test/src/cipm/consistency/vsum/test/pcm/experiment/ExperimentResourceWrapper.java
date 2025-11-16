package cipm.consistency.vsum.test.pcm.experiment;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import tools.vitruv.change.correspondence.Correspondences;
import tools.vitruv.dsls.reactions.runtime.correspondence.CorrespondenceFactory;

public class ExperimentResourceWrapper {
	private ResourceSet resSet;

	// Initial models in original vsum test
	private Resource initialJavaModel;
	private Resource initialPcmRepository;
	private Resource initialPcmSystem;
	private Resource initialPcmAllocation;
	private Resource initialPcmResEnv;
	private Resource initialPcmUsage;
	private Resource initialIm;
	private Resource initialCorrespondences;

	// Post Java -> PCM propagation models in original vsum test
	private Resource targetJavaModel;
	private Resource targetPcmRepository;
	private Resource targetPcmSystem;
	private Resource targetPcmAllocation;
	private Resource targetPcmResEnv;
	private Resource targetPcmUsage;
	private Resource targetIm;
	private Resource targetCorrespondences;

	// Changes propagated in original vsum test (Java -> PCM propagation)
	private Resource originalJavaChanges;
	private Resource originalPcmChanges;
	private Resource originalImChanges;

	// Models propagated during experiment (PCM -> Java propagation)
	private Resource propagatedJavaModel;
	private Resource propagatedPcmRepository;
	private Resource propagatedPcmSystem;
	private Resource propagatedPcmAllocation;
	private Resource propagatedPcmResEnv;
	private Resource propagatedPcmUsage;
	private Resource propagatedIm;
	private Resource propagatedCorrespondences;

	// Changes propagated during experiment (PCM -> Java propagation)
	private Resource propagatedJavaChanges;
	private Resource propagatedPcmChanges;
	private Resource propagatedImChanges;

	private JavaToPcmPropagationDirLayout initialLayout;
	private JavaToPcmPropagationDirLayout targetLayout;
	private PcmToJavaChangePropagationDirLayout experimentLayout;

	public ExperimentResourceWrapper(ResourceSet resSet, PcmToJavaChangePropagationDirLayout experimentLayout) {
		this.resSet = resSet;
		this.experimentLayout = experimentLayout;
		this.initialLayout = experimentLayout.getOldJavaToPcmPropagationDirLayout();
		this.targetLayout = experimentLayout.getNewJavaToPcmPropagationDirLayout();
	}

	public void initialise() {
		loadTargetModels();
		loadInitialModels();
		loadOriginalChanges();
		initialiseExperimentTestResources();
		adaptURIsInCorrespondences();
		adaptURIsInChanges();
	}

	private void loadTargetModels() {
		targetJavaModel = ResourceOperationsUtil.loadResource(resSet, targetLayout.getJavaModelSavePath());
		targetPcmRepository = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmRepositoryPath());
		targetPcmSystem = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmSystemPath());
		targetPcmAllocation = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmAllocationPath());
		targetPcmResEnv = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmResourceEnvironmentPath());
		targetPcmUsage = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmUsagePath());
		targetIm = ResourceOperationsUtil.loadResource(resSet, targetLayout.getIMSavePath());
		targetCorrespondences = ResourceOperationsUtil.loadResource(resSet, targetLayout.getVSUMCorrespondencesPath());
	}

	private void loadOriginalChanges() {
		originalJavaChanges = ResourceOperationsUtil.loadResource(resSet, targetLayout.getJavaChangesSaveFilePath());
		originalPcmChanges = ResourceOperationsUtil.loadResource(resSet, targetLayout.getPcmChangesSaveFilePath());
		originalImChanges = ResourceOperationsUtil.loadResource(resSet, targetLayout.getImChangesSaveFilePath());
	}

	private void loadInitialModels() {
		if (initialLayout != null) {
			initialJavaModel = ResourceOperationsUtil.loadResource(resSet, initialLayout.getJavaModelSavePath());
			initialPcmRepository = ResourceOperationsUtil.loadResource(resSet, initialLayout.getPcmRepositoryPath());
			initialPcmSystem = ResourceOperationsUtil.loadResource(resSet, initialLayout.getPcmSystemPath());
			initialPcmAllocation = ResourceOperationsUtil.loadResource(resSet, initialLayout.getPcmAllocationPath());
			initialPcmResEnv = ResourceOperationsUtil.loadResource(resSet,
					initialLayout.getPcmResourceEnvironmentPath());
			initialPcmUsage = ResourceOperationsUtil.loadResource(resSet, initialLayout.getPcmUsagePath());
			initialIm = ResourceOperationsUtil.loadResource(resSet, initialLayout.getIMSavePath());
			initialCorrespondences = ResourceOperationsUtil.loadResource(resSet,
					initialLayout.getVSUMCorrespondencesPath());
		} else {
			initialJavaModel = ResourceOperationsUtil.createEmptyResource(resSet,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getJavaModelSavePath());
			ResourceOperationsUtil.saveResource(initialJavaModel);

			initialPcmRepository = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmRepository,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmRepositoryPath());
			var initPcmRepo = (Repository) initialPcmRepository.getContents().get(0);
			EcoreUtil.removeAll(initPcmRepo.eContents());
			ResourceOperationsUtil.saveResource(initialPcmRepository);

			initialPcmSystem = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmSystem,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmSystemPath());
			EcoreUtil.removeAll(initialPcmSystem.getContents().get(0).eContents());
			ResourceOperationsUtil.saveResource(initialPcmSystem);

			initialPcmAllocation = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmAllocation,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmAllocationPath());
			EcoreUtil.removeAll(initialPcmAllocation.getContents().get(0).eContents());
			ResourceOperationsUtil.saveResource(initialPcmAllocation);

			initialPcmResEnv = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmResEnv,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmResourceEnvironmentPath());
			EcoreUtil.removeAll(initialPcmResEnv.getContents().get(0).eContents());
			ResourceOperationsUtil.saveResource(initialPcmResEnv);

			initialPcmUsage = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmUsage,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmUsagePath());
			EcoreUtil.removeAll(initialPcmUsage.getContents().get(0).eContents());
			ResourceOperationsUtil.saveResource(initialPcmUsage);

			initialIm = ResourceOperationsUtil.copyAndSaveResource(resSet, targetIm,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getIMSavePath());
			var initInsMod = (InstrumentationModel) initialIm.getContents().get(0);
			EcoreUtil.removeAll(initInsMod.eContents());
			ResourceOperationsUtil.saveResource(initialIm);

			initialCorrespondences = ResourceOperationsUtil.copyAndSaveResource(resSet, targetCorrespondences,
					experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getVSUMCorrespondencesPath());
			var initCors = (Correspondences) initialCorrespondences.getContents().get(0);
			EcoreUtil.removeAll(initCors.eContents());
			ResourceOperationsUtil.saveResource(initialCorrespondences);

			addAndSaveInitialCorrespondences();
		}
	}

	private void addAndSaveInitialCorrespondences() {
		var initPcmRepo = (Repository) initialPcmRepository.getContents().get(0);
		var initInsMod = (InstrumentationModel) initialIm.getContents().get(0);
		var initCors = (Correspondences) initialCorrespondences.getContents().get(0);
		/*
		 * Re-add correspondences for PCM Repository and IM InstrumentationModel, which
		 * are the only root elements for their respective model. Since changes that
		 * capture them being added as root elements are not recorded in tests, PCM and
		 * IM initialisation CPRs will not trigger to add the correspondences they need
		 * (see PcmInitChangePropagationSpecification and
		 * ImInitChangePropagationSpecification)
		 */
		var repoCor = CorrespondenceFactory.eINSTANCE.createReactionsCorrespondence();
		repoCor.getLeftEObjects().add(initPcmRepo);
		repoCor.getRightEObjects().add(RepositoryPackage.Literals.REPOSITORY);
		repoCor.setTag("");
		initCors.getCorrespondences().add(repoCor);
		var imCor = CorrespondenceFactory.eINSTANCE.createReactionsCorrespondence();
		imCor.getLeftEObjects().add(initInsMod);
		imCor.getRightEObjects().add(InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL);
		imCor.setTag("");
		initCors.getCorrespondences().add(imCor);
		ResourceOperationsUtil.saveResource(initialCorrespondences);
	}

	private void initialiseExperimentTestResources() {
		initialJavaModel = ResourceOperationsUtil.copyAndSaveResource(resSet, initialJavaModel,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getJavaModelSavePath());
		initialPcmRepository = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmRepository,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmRepositoryPath());
		initialPcmSystem = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmSystem,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmSystemPath());
		initialPcmAllocation = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmAllocation,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmAllocationPath());
		initialPcmResEnv = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmResEnv,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmResourceEnvironmentPath());
		initialPcmUsage = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmUsage,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmUsagePath());
		initialIm = ResourceOperationsUtil.copyAndSaveResource(resSet, initialIm,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getIMSavePath());
		initialCorrespondences = ResourceOperationsUtil.copyAndSaveResource(resSet, initialCorrespondences,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getVSUMCorrespondencesPath());

		targetJavaModel = ResourceOperationsUtil.copyAndSaveResource(resSet, targetJavaModel,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getJavaModelSavePath());
		targetPcmRepository = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmRepository,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getPcmRepositoryPath());
		targetPcmSystem = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmSystem,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getPcmSystemPath());
		targetPcmAllocation = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmAllocation,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getPcmAllocationPath());
		targetPcmResEnv = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmResEnv,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getPcmResourceEnvironmentPath());
		targetPcmUsage = ResourceOperationsUtil.copyAndSaveResource(resSet, targetPcmUsage,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getPcmUsagePath());
		targetIm = ResourceOperationsUtil.copyAndSaveResource(resSet, targetIm,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getIMSavePath());
		targetCorrespondences = ResourceOperationsUtil.copyAndSaveResource(resSet, targetCorrespondences,
				experimentLayout.getCopiedNewJavaToPcmPropagationDirLayout().getVSUMCorrespondencesPath());

		originalJavaChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalJavaChanges,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getJavaChangesSaveFilePath());
		originalPcmChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalPcmChanges,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getPcmChangesSaveFilePath());
		originalImChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalImChanges,
				experimentLayout.getCopiedOldJavaToPcmPropagationDirLayout().getImChangesSaveFilePath());

		propagatedJavaModel = ResourceOperationsUtil.copyAndSaveResource(resSet, initialJavaModel,
				experimentLayout.getPropagatedDirLayout().getJavaModelSavePath());
		propagatedPcmRepository = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmRepository,
				experimentLayout.getPropagatedDirLayout().getPcmRepositoryPath());
		propagatedPcmSystem = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmSystem,
				experimentLayout.getPropagatedDirLayout().getPcmSystemPath());
		propagatedPcmAllocation = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmAllocation,
				experimentLayout.getPropagatedDirLayout().getPcmAllocationPath());
		propagatedPcmResEnv = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmResEnv,
				experimentLayout.getPropagatedDirLayout().getPcmResourceEnvironmentPath());
		propagatedPcmUsage = ResourceOperationsUtil.copyAndSaveResource(resSet, initialPcmUsage,
				experimentLayout.getPropagatedDirLayout().getPcmUsagePath());
		propagatedIm = ResourceOperationsUtil.copyAndSaveResource(resSet, initialIm,
				experimentLayout.getPropagatedDirLayout().getIMSavePath());
		propagatedCorrespondences = ResourceOperationsUtil.copyAndSaveResource(resSet, initialCorrespondences,
				experimentLayout.getPropagatedDirLayout().getVSUMCorrespondencesPath());

		propagatedJavaChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalJavaChanges,
				experimentLayout.getPropagatedDirLayout().getJavaChangesSaveFilePath());
		propagatedPcmChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalPcmChanges,
				experimentLayout.getPropagatedDirLayout().getPcmChangesSaveFilePath());
		propagatedImChanges = ResourceOperationsUtil.copyAndSaveResource(resSet, originalImChanges,
				experimentLayout.getPropagatedDirLayout().getImChangesSaveFilePath());
	}

	private void adaptURIsInChanges() {
		ChangeUtil.adaptChangeURIs(propagatedJavaChanges, propagatedJavaModel);
		ResourceOperationsUtil.saveResource(propagatedJavaChanges);
		ChangeUtil.adaptChangeURIs(propagatedPcmChanges, propagatedPcmRepository);
		ResourceOperationsUtil.saveResource(propagatedPcmChanges);
		ChangeUtil.adaptChangeURIs(propagatedImChanges, propagatedIm);
		ResourceOperationsUtil.saveResource(propagatedImChanges);

	}

	private void adaptURIsInCorrespondences() {
		var propRess = List.of(propagatedJavaModel, propagatedPcmRepository, propagatedIm);

		var cors = (Correspondences) propagatedCorrespondences.getContents().get(0);
		cors.getCorrespondences().forEach((c) -> {
			// Replace all EObjects in correspondences with their correspondents from
			// the propagatedX Resources. This fixes their URIs.
			for (var originalList : List.of(c.getLeftEObjects(), c.getRightEObjects())) {
				var iterationList = List.copyOf(originalList);
				for (int i = 0; i < iterationList.size(); i++) {
					final var idx = i;
					var original = iterationList.get(idx);

					// Since there may be correspondences to Ecore Literals too,
					// only replace EObjects, if they actually have a replacement
					// in propagatedX Resources
					if (original.eResource() == null || !original.eResource().getURI().isFile())
						continue;

					var replacement = propRess.stream()
							.map((r) -> r.getEObject(original.eResource().getURIFragment(original)))
							.filter((r) -> r != null).findFirst().get();

					originalList.add(i, replacement);
					originalList.remove(original);
				}
			}
		});

		ResourceOperationsUtil.saveResource(propagatedCorrespondences);
	}

	public void reloadPropagatedResources() {
		ResourceOperationsUtil.reload(propagatedJavaModel);
		ResourceOperationsUtil.reload(propagatedPcmRepository);
		ResourceOperationsUtil.reload(propagatedPcmSystem);
		ResourceOperationsUtil.reload(propagatedPcmAllocation);
		ResourceOperationsUtil.reload(propagatedPcmResEnv);
		ResourceOperationsUtil.reload(propagatedPcmUsage);
		ResourceOperationsUtil.reload(propagatedIm);
		ResourceOperationsUtil.reload(propagatedCorrespondences);
	}

	public PcmToJavaChangePropagationDirLayout getExperimentLayout() {
		return experimentLayout;
	}

	public ResourceSet getResSet() {
		return resSet;
	}

	public Resource getInitialJavaModel() {
		return initialJavaModel;
	}

	public Resource getInitialPcmRepository() {
		return initialPcmRepository;
	}

	public Resource getInitialPcmSystem() {
		return initialPcmSystem;
	}

	public Resource getInitialPcmAllocation() {
		return initialPcmAllocation;
	}

	public Resource getInitialPcmResEnv() {
		return initialPcmResEnv;
	}

	public Resource getInitialPcmUsage() {
		return initialPcmUsage;
	}

	public Resource getInitialIm() {
		return initialIm;
	}

	public Resource getInitialCorrespondences() {
		return initialCorrespondences;
	}

	public Resource getTargetJavaModel() {
		return targetJavaModel;
	}

	public Resource getTargetPcmRepository() {
		return targetPcmRepository;
	}

	public Resource getTargetPcmSystem() {
		return targetPcmSystem;
	}

	public Resource getTargetPcmAllocation() {
		return targetPcmAllocation;
	}

	public Resource getTargetPcmResEnv() {
		return targetPcmResEnv;
	}

	public Resource getTargetPcmUsage() {
		return targetPcmUsage;
	}

	public Resource getTargetIm() {
		return targetIm;
	}

	public Resource getTargetCorrespondences() {
		return targetCorrespondences;
	}

	public Resource getOriginalJavaChanges() {
		return originalJavaChanges;
	}

	public Resource getOriginalPcmChanges() {
		return originalPcmChanges;
	}

	public Resource getOriginalImChanges() {
		return originalImChanges;
	}

	public Resource getPropagatedJavaModel() {
		return propagatedJavaModel;
	}

	public Resource getPropagatedPcmRepository() {
		return propagatedPcmRepository;
	}

	public Resource getPropagatedPcmSystem() {
		return propagatedPcmSystem;
	}

	public Resource getPropagatedPcmAllocation() {
		return propagatedPcmAllocation;
	}

	public Resource getPropagatedPcmResEnv() {
		return propagatedPcmResEnv;
	}

	public Resource getPropagatedPcmUsage() {
		return propagatedPcmUsage;
	}

	public Resource getPropagatedIm() {
		return propagatedIm;
	}

	public Resource getPropagatedCorrespondences() {
		return propagatedCorrespondences;
	}

	public Resource getPropagatedJavaChanges() {
		return propagatedJavaChanges;
	}

	public Resource getPropagatedPcmChanges() {
		return propagatedPcmChanges;
	}

	public Resource getPropagatedImChanges() {
		return propagatedImChanges;
	}

	public JavaToPcmPropagationDirLayout getPreviousPropagationLayout() {
		return initialLayout;
	}

	public JavaToPcmPropagationDirLayout getOriginalLayout() {
		return targetLayout;
	}

}
