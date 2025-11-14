package cipm.consistency.vsum.test.pcm.experiment;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class ExperimentResourceWrapper implements AutoCloseable {
	private ResourceSet resSet;
	private ExperimentDirLayout layout;
	private JavaToPcmPropagationWrapper originalJavaPropagationModels;

	private JavaToPcmPropagationWrapper oldJavaPropagationModelsCopyWrapper;
	private JavaToPcmPropagationWrapper newJavaPropagationModelsCopyWrapper;

	private JavaToPcmPropagationWrapper pcmPropagationModelsWrapper;

	public ExperimentResourceWrapper(ResourceSet resSet, ExperimentDirLayout layout) {
		this.resSet = resSet;
		this.layout = layout;
	}

	public void initialise() {
		this.originalJavaPropagationModels = new JavaToPcmPropagationWrapper(resSet,
				layout.getJavaToPcmPropagationDirLayout());
		this.originalJavaPropagationModels.initialise();
		this.copyModels();
	}

	public void copyModels() {
		this.oldJavaPropagationModelsCopyWrapper = new JavaToPcmPropagationWrapper(resSet,
				layout.getCopiedOldJavaToPcmPropagationDirLayout());
		this.originalJavaPropagationModels.copyInitialModelsTo(oldJavaPropagationModelsCopyWrapper, resSet);
		this.originalJavaPropagationModels.copyChangesTo(oldJavaPropagationModelsCopyWrapper, resSet);

		this.newJavaPropagationModelsCopyWrapper = new JavaToPcmPropagationWrapper(resSet,
				layout.getCopiedNewJavaToPcmPropagationDirLayout());
		this.originalJavaPropagationModels.copyPropagatedModelsTo(newJavaPropagationModelsCopyWrapper, resSet);

		this.pcmPropagationModelsWrapper = new JavaToPcmPropagationWrapper(resSet, layout.getPropagatedDirLayout());
		this.originalJavaPropagationModels.copyInitialModelsTo(pcmPropagationModelsWrapper, resSet);
	}

	public ResourceSet getResSet() {
		return resSet;
	}

	public ExperimentDirLayout getLayout() {
		return layout;
	}

//	public JavaToPcmPropagationWrapper getOriginalJavaPropagationModels() {
//		return originalJavaPropagationModels;
//	}
//
//	public JavaToPcmPropagationWrapper getOldJavaPropagationModelsCopyWrapper() {
//		return oldJavaPropagationModelsCopyWrapper;
//	}
//
//	public JavaToPcmPropagationWrapper getNewJavaPropagationModelsCopyWrapper() {
//		return newJavaPropagationModelsCopyWrapper;
//	}
//
//	public JavaToPcmPropagationWrapper getPcmPropagationModelsWrapper() {
//		return pcmPropagationModelsWrapper;
//	}

	@Override
	public void close() {
		originalJavaPropagationModels.close();
		oldJavaPropagationModelsCopyWrapper.close();
		newJavaPropagationModelsCopyWrapper.close();
		pcmPropagationModelsWrapper.close();
	}

	public Resource getPcmChangeResource() {
		return this.originalJavaPropagationModels.getPcmChanges();
	}

	public Resource getPcmRepositoryToPropagate() {
		return this.pcmPropagationModelsWrapper.getPropagatedPcmRepository();
	}

	public Resource getJavaPropagationPropagatedJavaModel() {
		return this.newJavaPropagationModelsCopyWrapper.getPropagatedJavaModel();
	}

	public Resource getJavaPropagationInitialJavaModel() {
		return this.oldJavaPropagationModelsCopyWrapper.getInitialJavaModel();
	}

	public Resource getJavaPropagationPropagatedPcmRepository() {
		return this.newJavaPropagationModelsCopyWrapper.getPropagatedPcmRepository();
	}

	public Resource getJavaPropagationInitialPcmRepository() {
		return this.oldJavaPropagationModelsCopyWrapper.getInitialPCMRepository();
	}

	public Resource getJavaPropagationPropagatedIm() {
		return this.newJavaPropagationModelsCopyWrapper.getPropagatedIm();
	}

	public Resource getJavaPropagationInitialIm() {
		return this.oldJavaPropagationModelsCopyWrapper.getInitialIM();
	}
}
