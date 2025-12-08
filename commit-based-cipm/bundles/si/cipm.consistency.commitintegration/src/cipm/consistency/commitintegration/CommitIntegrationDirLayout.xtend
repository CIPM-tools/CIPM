package cipm.consistency.commitintegration

import cipm.consistency.models.ModelDirLayoutImpl
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class CommitIntegrationDirLayout extends ModelDirLayoutImpl {
	
	static String vsumDirName = "vsum"
	static String imDirName = "im"
	static String pcmDirName = "pcm"
	static String measurementsDirName = "measurements"
	static String codeDirName = "code"
	static String instrumentedCodeDirName = "instrumented-code"

	static String vsumCodeModelName = "vsum.code.xmi"
	
	static String commitsFileName = "commits"
	static String settingsFileName = "settings.settings"
	static String evaluationDataFileName = "evaluationData.json"
	
	Path vsumDirPath
	Path pcmDirPath
	Path imDirPath
	Path measurementsDirPath
	Path codeDirPath
	Path instrumentedCodeDirPath

	Path vsumCodeModelPath
	URI vsumCodeModelURI
	
	Path commitsFilePath
	Path settingsFilePath
	Path evaluationDataFilePath
	
	override initialize(Path rootDirPath) {
		super.initialize(rootDirPath)
		
		vsumDirPath = rootDirPath.resolve(vsumDirName)
		pcmDirPath = rootDirPath.resolve(pcmDirName)
		imDirPath = rootDirPath.resolve(imDirName)
		measurementsDirPath = rootDirPath.resolve(measurementsDirName)
		codeDirPath = rootDirPath.resolve(codeDirName)
		instrumentedCodeDirPath = rootDirPath.resolve(instrumentedCodeDirName)
		
		vsumCodeModelPath = codeDirPath.resolve(vsumCodeModelName).toAbsolutePath()
		vsumCodeModelURI = URI.createFileURI(vsumCodeModelPath.toString())
		
		commitsFilePath = rootDirPath.resolve(commitsFileName)
		settingsFilePath = rootDirPath.resolve(settingsFileName)
		evaluationDataFilePath = rootDirPath.resolve(evaluationDataFileName).toAbsolutePath()
	}
	
}
