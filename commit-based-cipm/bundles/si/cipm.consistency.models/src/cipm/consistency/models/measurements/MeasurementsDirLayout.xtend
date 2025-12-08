package cipm.consistency.models.measurements;

import cipm.consistency.models.ModelDirLayoutImpl
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class MeasurementsDirLayout extends ModelDirLayoutImpl {
	static final String measurementsFileName = "measurements.measurements"

	Path measurementsFilePath
	URI measurementsFileUri


	override void initialize(Path rootDirPath) {
		super.initialize(rootDirPath)

		measurementsFilePath = rootDirPath.resolve(measurementsFileName)
		measurementsFileUri = URI.createFileURI(measurementsFilePath.toAbsolutePath().toString())
	}
}
