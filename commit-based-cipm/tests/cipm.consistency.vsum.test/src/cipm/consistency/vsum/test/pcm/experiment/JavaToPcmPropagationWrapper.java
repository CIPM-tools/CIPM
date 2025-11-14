package cipm.consistency.vsum.test.pcm.experiment;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class JavaToPcmPropagationWrapper implements AutoCloseable {
	private JavaToPcmPropagationDirLayout layout;
	private ResourceSet resSet;

	private Resource initialJavaModel;
	private Resource initialPCMRepository;
	private Resource initialIM;
	private Resource initialCorrespondences;

	private Resource propagatedJavaModel;

	private Resource propagatedPcmRepository;
	private Resource propagatedPcmSystem;
	private Resource propagatedPcmAllocation;
	private Resource propagatedPcmResEnv;
	private Resource propagatedPcmUsage;

	private Resource propagatedIm;

	private Resource propagatedCorrespondences;

	private Resource javaChanges;
	private Resource pcmChanges;
	private Resource imChanges;

	public JavaToPcmPropagationWrapper(ResourceSet resSet, JavaToPcmPropagationDirLayout layout) {
		this.resSet = resSet;
		this.layout = layout;
	}

	public void initialise() {
		initialJavaModel = ExperimentResourceUtil.loadResource(resSet, layout.getInitialJavaModelPath());
		propagatedJavaModel = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedJavaModelSavePath());

		initialPCMRepository = ExperimentResourceUtil.loadResource(resSet, layout.getInitialRepositoryPath());
		propagatedPcmRepository = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedPcmRepositoryPath());

		propagatedPcmSystem = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedPcmSystemPath());
		propagatedPcmAllocation = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedPcmAllocationPath());
		propagatedPcmResEnv = ExperimentResourceUtil.loadResource(resSet,
				layout.getPropagatedPcmResourceEnvironmentPath());
		propagatedPcmUsage = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedPcmUsagePath());

		initialIM = ExperimentResourceUtil.loadResource(resSet, layout.getInitialIMPath());
		propagatedIm = ExperimentResourceUtil.loadResource(resSet, layout.getPropagatedIMSavePath());

		javaChanges = ExperimentResourceUtil.loadResource(resSet, layout.getJavaChangesSaveFilePath());
		pcmChanges = ExperimentResourceUtil.loadResource(resSet, layout.getPcmChangesSaveFilePath());
		imChanges = ExperimentResourceUtil.loadResource(resSet, layout.getImChangesSaveFilePath());

		initialCorrespondences = ExperimentResourceUtil.loadResource(resSet, layout.getInitialCorrespondencesPath());
		propagatedCorrespondences = ExperimentResourceUtil.loadResource(resSet,
				layout.getPropagatedCorrespondencesPath());
	}

	public void copyInitialModelsTo(JavaToPcmPropagationWrapper wrapperToCopyTo, ResourceSet resSetToCopyTo) {
		wrapperToCopyTo.initialJavaModel = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, initialJavaModel,
				wrapperToCopyTo.layout.getInitialJavaModelPath());
		wrapperToCopyTo.initialPCMRepository = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				initialPCMRepository, wrapperToCopyTo.layout.getInitialRepositoryPath());
		wrapperToCopyTo.initialIM = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, initialIM,
				wrapperToCopyTo.layout.getInitialIMPath());
		wrapperToCopyTo.initialCorrespondences = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				initialCorrespondences, wrapperToCopyTo.layout.getInitialCorrespondencesPath());
	}

	public void copyPropagatedModelsTo(JavaToPcmPropagationWrapper wrapperToCopyTo, ResourceSet resSetToCopyTo) {
		wrapperToCopyTo.propagatedJavaModel = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedJavaModel, wrapperToCopyTo.layout.getPropagatedJavaModelSavePath());

		wrapperToCopyTo.propagatedPcmRepository = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedPcmRepository, wrapperToCopyTo.layout.getPropagatedPcmRepositoryPath());
		wrapperToCopyTo.propagatedPcmSystem = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedPcmSystem, wrapperToCopyTo.layout.getPropagatedPcmSystemPath());
		wrapperToCopyTo.propagatedPcmAllocation = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedPcmAllocation, wrapperToCopyTo.layout.getPropagatedPcmAllocationPath());
		wrapperToCopyTo.propagatedPcmResEnv = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedPcmResEnv, wrapperToCopyTo.layout.getPropagatedPcmResourceEnvironmentPath());
		wrapperToCopyTo.propagatedPcmUsage = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedPcmUsage, wrapperToCopyTo.layout.getPropagatedPcmUsagePath());

		wrapperToCopyTo.propagatedIm = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, propagatedIm,
				wrapperToCopyTo.layout.getPropagatedIMSavePath());
		wrapperToCopyTo.propagatedCorrespondences = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo,
				propagatedCorrespondences, wrapperToCopyTo.layout.getPropagatedCorrespondencesPath());
	}

	public void copyChangesTo(JavaToPcmPropagationWrapper wrapperToCopyTo, ResourceSet resSetToCopyTo) {
		wrapperToCopyTo.javaChanges = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, javaChanges,
				wrapperToCopyTo.layout.getJavaChangesSaveFilePath());
		wrapperToCopyTo.pcmChanges = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, pcmChanges,
				wrapperToCopyTo.layout.getPcmChangesSaveFilePath());
		wrapperToCopyTo.imChanges = ExperimentResourceUtil.copyAndSaveResource(resSetToCopyTo, imChanges,
				wrapperToCopyTo.layout.getImChangesSaveFilePath());
	}

	@Override
	public void close() {
		ExperimentResourceUtil.removeResource(initialJavaModel);
		ExperimentResourceUtil.removeResource(initialPCMRepository);
		ExperimentResourceUtil.removeResource(initialIM);
		ExperimentResourceUtil.removeResource(initialCorrespondences);

		ExperimentResourceUtil.removeResource(propagatedJavaModel);

		ExperimentResourceUtil.removeResource(propagatedPcmRepository);
		ExperimentResourceUtil.removeResource(propagatedPcmSystem);
		ExperimentResourceUtil.removeResource(propagatedPcmAllocation);
		ExperimentResourceUtil.removeResource(propagatedPcmResEnv);
		ExperimentResourceUtil.removeResource(propagatedPcmUsage);

		ExperimentResourceUtil.removeResource(propagatedIm);

		ExperimentResourceUtil.removeResource(javaChanges);
		ExperimentResourceUtil.removeResource(pcmChanges);
		ExperimentResourceUtil.removeResource(imChanges);

		ExperimentResourceUtil.removeResource(propagatedCorrespondences);

	}

	public ResourceSet getResSet() {
		return resSet;
	}

	public JavaToPcmPropagationDirLayout getLayout() {
		return layout;
	}

	public Resource getInitialJavaModel() {
		return initialJavaModel;
	}

	public Resource getInitialPCMRepository() {
		return initialPCMRepository;
	}

	public Resource getInitialIM() {
		return initialIM;
	}

	public Resource getInitialCorrespondences() {
		return initialCorrespondences;
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

	public Resource getJavaChanges() {
		return javaChanges;
	}

	public Resource getPcmChanges() {
		return pcmChanges;
	}

	public Resource getImChanges() {
		return imChanges;
	}

}
