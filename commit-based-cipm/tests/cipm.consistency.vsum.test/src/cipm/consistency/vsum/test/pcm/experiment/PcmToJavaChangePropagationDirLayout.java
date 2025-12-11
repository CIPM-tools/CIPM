package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

/**
 * Aggregates all file layout information for the experiment.
 * 
 * @author Alp Torac Genc
 */
public class PcmToJavaChangePropagationDirLayout {
	// Root path

	private Path rootPath;

	// Paths to existing test results / resources

	private JavaToPcmPropagationDirLayout oldJavaToPcmPropagationDirLayout;
	private JavaToPcmPropagationDirLayout newJavaToPcmPropagationDirLayout;

	// Paths to copied resources

	private JavaToPcmPropagationDirLayout copiedOldJavaToPcmPropagationDirLayout;
	private JavaToPcmPropagationDirLayout copiedNewJavaToPcmPropagationDirLayout;
	private JavaToPcmPropagationDirLayout propagatedDirLayout;

	private Path experimentResultSavePath;

	public PcmToJavaChangePropagationDirLayout(JavaToPcmPropagationDirLayout oldJavaToPcmPropagationDirLayout,
			JavaToPcmPropagationDirLayout newJavaToPcmPropagationDirLayout, Path rootPath) {
		this.rootPath = rootPath;

		var copiedRootPath = this.rootPath.resolve(PcmToJavaChangePropagationDirLayoutConstants.getCopiedrootdirname());
		var copiedOldRootPath = copiedRootPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getCopiedoldrootdirname());
		var copiedNewRootPath = copiedRootPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getCopiednewrootdirname());
		var propPath = this.rootPath.resolve(PcmToJavaChangePropagationDirLayoutConstants.getPropagatedrootdirname());

		this.oldJavaToPcmPropagationDirLayout = oldJavaToPcmPropagationDirLayout;
		this.copiedOldJavaToPcmPropagationDirLayout = new JavaToPcmPropagationDirLayout(copiedOldRootPath);

		this.newJavaToPcmPropagationDirLayout = newJavaToPcmPropagationDirLayout;
		this.copiedNewJavaToPcmPropagationDirLayout = new JavaToPcmPropagationDirLayout(copiedNewRootPath);

		this.propagatedDirLayout = new JavaToPcmPropagationDirLayout(propPath);

		this.experimentResultSavePath = this.rootPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getExperimentresultsfilename());
	}

	public JavaToPcmPropagationDirLayout getOldJavaToPcmPropagationDirLayout() {
		return oldJavaToPcmPropagationDirLayout;
	}

	public JavaToPcmPropagationDirLayout getNewJavaToPcmPropagationDirLayout() {
		return newJavaToPcmPropagationDirLayout;
	}

	public Path getRootPath() {
		return rootPath;
	}

	public JavaToPcmPropagationDirLayout getCopiedOldJavaToPcmPropagationDirLayout() {
		return copiedOldJavaToPcmPropagationDirLayout;
	}

	public JavaToPcmPropagationDirLayout getCopiedNewJavaToPcmPropagationDirLayout() {
		return copiedNewJavaToPcmPropagationDirLayout;
	}

	public JavaToPcmPropagationDirLayout getPropagatedDirLayout() {
		return propagatedDirLayout;
	}

	public Path getExperimentResultSavePath() {
		return experimentResultSavePath;
	}
}
