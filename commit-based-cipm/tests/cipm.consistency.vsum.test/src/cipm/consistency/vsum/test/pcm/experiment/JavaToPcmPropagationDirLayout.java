package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

import cipm.consistency.commitintegration.CommitIntegrationDirLayout;

/**
 * Extends {@link CommitIntegrationDirLayout} with layout information on model
 * changes, as well as utility methods for accessing paths of individual model
 * resource files.
 * 
 * @author Alp Torac Genc
 */
public class JavaToPcmPropagationDirLayout extends CommitIntegrationDirLayout {
	private Path changesSavePath;
	private Path javaChangesSaveFilePath;
	private Path pcmChangesSaveFilePath;
	private Path imChangesSaveFilePath;

	public JavaToPcmPropagationDirLayout(CommitIntegrationDirLayout dirLayout) {
		this(dirLayout.getRootDirPath());
	}

	public JavaToPcmPropagationDirLayout(Path rootDirPath) {
		super();
		this.initialize(rootDirPath);
	}

	@Override
	public void initialize(Path rootDirPath) {
		super.initialize(rootDirPath);

		this.changesSavePath = this.rootDirPath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getChangessavedirname());
		this.javaChangesSaveFilePath = this.changesSavePath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getJavachangessavefilename());
		this.pcmChangesSaveFilePath = this.changesSavePath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmchangessavefilename());
		this.imChangesSaveFilePath = this.changesSavePath
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getImchangessavefilename());
	}

	public Path getChangesSavePath() {
		return changesSavePath;
	}

	public Path getJavaChangesSaveFilePath() {
		return javaChangesSaveFilePath;
	}

	public Path getPcmChangesSaveFilePath() {
		return pcmChangesSaveFilePath;
	}

	public Path getImChangesSaveFilePath() {
		return imChangesSaveFilePath;
	}

	public Path getJavaModelSavePath() {
		return this.getCodeDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getJavafilename());
	}

	public Path getIMSavePath() {
		return this.getImDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getImfilename());
	}

	public Path getPcmRepositoryPath() {
		return this.getPcmDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmrepositoryfilename());
	}

	public Path getPcmSystemPath() {
		return this.getPcmDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmsystemfilename());
	}

	public Path getPcmAllocationPath() {
		return this.getPcmDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmallocationfilename());
	}

	public Path getPcmResourceEnvironmentPath() {
		return this.getPcmDirPath()
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmresourceenvironmentfilename());
	}

	public Path getPcmUsagePath() {
		return this.getPcmDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getPcmusagemodelfilename());
	}

	public Path getVSUMCorrespondencesPath() {
		return this.getVsumDirPath().resolve(PcmToJavaChangePropagationDirLayoutConstants.getVsumdirname())
				.resolve(PcmToJavaChangePropagationDirLayoutConstants.getVsumcorrespondencemodelname());
	}
}
