package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

import cipm.consistency.commitintegration.CommitIntegrationDirLayout;

public class ChangeGeneratingCommitIntegrationDirLayout extends CommitIntegrationDirLayout {
	private static final String changesSaveDirName = "changes";
	private static final String javaChangesSaveFileName = "javaChanges.changes";
	private static final String pcmChangesSaveFileName = "pcmChanges.changes";
	private static final String imChangesSaveFileName = "imChanges.changes";

	private Path rootPath;
	private Path changesSavePath;
	private Path javaChangesSaveFilePath;
	private Path pcmChangesSaveFilePath;
	private Path imChangesSaveFilePath;

	public ChangeGeneratingCommitIntegrationDirLayout(CommitIntegrationDirLayout dirLayout) {
		this(dirLayout.getRootDirPath());
	}

	public ChangeGeneratingCommitIntegrationDirLayout(Path rootDirPath) {
		super();
		this.initialize(rootDirPath);
	}

	@Override
	public void initialize(Path rootDirPath) {
		super.initialize(rootDirPath);

		this.rootPath = rootDirPath;
		this.changesSavePath = this.rootPath.resolve(changesSaveDirName);
		this.javaChangesSaveFilePath = this.changesSavePath.resolve(javaChangesSaveFileName);
		this.pcmChangesSaveFilePath = this.changesSavePath.resolve(pcmChangesSaveFileName);
		this.imChangesSaveFilePath = this.changesSavePath.resolve(imChangesSaveFileName);
	}

	public static String getChangessavedirname() {
		return changesSaveDirName;
	}

	public static String getJavachangessavefilename() {
		return javaChangesSaveFileName;
	}

	public static String getPcmchangessavefilename() {
		return pcmChangesSaveFileName;
	}

	public static String getImchangessavefilename() {
		return imChangesSaveFileName;
	}

	public Path getRootPath() {
		return rootPath;
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

}
