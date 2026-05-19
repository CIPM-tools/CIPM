package cipm.consistency.commitintegration.lang.lua

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer
import com.google.inject.Provider
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.common.util.WrappedException
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.xtext.lua.LuaStandaloneSetup
import org.xtext.lua.tests.LuaInjectorProvider
import org.apache.commons.io.FileUtils

//@ExtendWith(InjectionExtension)
@InjectWith(LuaInjectorProvider)
class CodeModelEvaluator {
	@Inject extension Provider<XtextResourceSet>
	@Inject extension ValidationTestHelper

	/**
	 * This brings a string into a form where we can compare it to another of the same form
	 * This prevents false positives from whitespace, comment which would otherwise create
	 * differences between the two strings.
	 */
	def static String bringIntoCanonicalForm(String luaCode) {
		var stripped = luaCode;

		// strip multiline comments
		val Pattern pattern = Pattern.compile("\\[\\[.*\\]\\]", Pattern.DOTALL);
		val Matcher matcher = pattern.matcher(stripped);
		stripped = matcher.replaceAll("");

		// strip comments
		stripped = stripped.replaceAll("(?m)--[^\n]*\n?", "")

		// to leading and trailingwhite space characters
		stripped = stripped.replaceAll("(?m)^[\t ]*", "")
		stripped = stripped.replaceAll("(?m)[\t ]*$", "")

		// only one newline between lines
		stripped = stripped.replaceAll("[\r\n]+", "\n")

		// no newlines in first and last line 
		stripped.trim()
	}

	def static String bringIntoExtremelyCanonicalForm(String canonicalForm) {
		// spaces my occur in the original file
		// this breaks strings, but that doesn't matter here
		var canonical = canonicalForm.replaceAll("[\t ]+", "")

		// printing of some table elements introduces unwanted commas
		canonical = canonical.replaceAll(",", "")

		// no newlines in first and last line 
		canonical.replaceAll("\n", "")
	}

	def boolean checkCaseStudyFile(XtextResourceSet rs, Path srcFile, Path appPath) {
		if (!srcFile.toFile.exists) {
			return false;
		}

		val uri = URI.createFileURI(srcFile.toAbsolutePath.toString)
		var Resource res = null
		try {
			res = rs.getResource(uri, true)
		} catch (WrappedException | IOException e) {
			e.printStackTrace;
			return false;
		}
		val origString = Files.readString(srcFile)

		val outputStream = new ByteArrayOutputStream()
		res.save(outputStream, #{})
		val parsedAndPrinted = outputStream.toString()

		if (origString.equals(parsedAndPrinted)) {
			EvaluationDataContainer.get.codeModelCorrectness.identicalFiles = EvaluationDataContainer.get.
				codeModelCorrectness.identicalFiles + 1
		}

		// strip things like unimportant whitespace, duplicate newlines, etc.
		val origCanonical = bringIntoCanonicalForm(origString)
		val parsedAndPrintedCanonical = bringIntoCanonicalForm(parsedAndPrinted)

		// further also strip _all_ newlines and whitespace
		// this definitely breaks the file, but that does not matter for the string comparision
		val origExtremelyCanonical = bringIntoExtremelyCanonicalForm(origCanonical)
		val parsedAndPrintedExtremelyCanonical = bringIntoExtremelyCanonicalForm(parsedAndPrintedCanonical)

		assertNoIssues(res)
		val equivalence = origExtremelyCanonical.equals(parsedAndPrintedExtremelyCanonical)
		if (equivalence) {
			EvaluationDataContainer.get.codeModelCorrectness.similarFiles = EvaluationDataContainer.get.
				codeModelCorrectness.similarFiles + 1
		} else {
			println("dissimilar file: " + srcFile)
			// write strings to files if there are differences
			val targetDir = Paths.get("./caseStudyEvaluation/").resolve(appPath.relativize(srcFile))
			val plainDir = targetDir.resolve("plain")
			val canonicalDir = targetDir.resolve("canonical")
			val extremelyCanonicalDir = targetDir.resolve("extremely-canonical")
			Files.createDirectories(plainDir)
			Files.createDirectories(canonicalDir)
			Files.createDirectories(extremelyCanonicalDir)
			Files.writeString(plainDir.resolve("orig.lua"), origString)
			Files.writeString(plainDir.resolve("parsedAndPrinted.lua"), parsedAndPrinted)
			Files.writeString(canonicalDir.resolve("orig.lua"), origCanonical)
			Files.writeString(canonicalDir.resolve("parsedAndPrinted.lua"), parsedAndPrintedCanonical)
			Files.writeString(extremelyCanonicalDir.resolve("orig.lua"), origExtremelyCanonical)
			Files.writeString(extremelyCanonicalDir.resolve("parsedAndPrinted.lua"), parsedAndPrintedExtremelyCanonical)
		}
		return equivalence
	}

	def static resetEvalData() {
		val cmEval = EvaluationDataContainer.get.codeModelCorrectness;
		cmEval.dissimilarFiles = 0
		cmEval.similarFiles = 0
		cmEval.identicalFiles = 0
		cmEval
	}

	private def void evaluateSourceCodeDir(Path appPath) {
		val rs = get()
		val matcher = FileSystems.^default.getPathMatcher("glob:**.lua")

		val equalPaths = new ArrayList<Path>();
		val unequalPaths = new ArrayList<Path>();

		val cmEval = resetEvalData()
		val targetDir = Paths.get("./caseStudyEvaluation/")
		FileUtils.deleteDirectory(targetDir.toFile)

		try (val paths = Files.walk(appPath))
			paths.filter[p|matcher.matches(p)].forEach [ path |
				val relPath = appPath.relativize(path)
				if (checkCaseStudyFile(rs, path, appPath)) {
					equalPaths.add(relPath)
				} else {
					unequalPaths.add(relPath)
					cmEval.dissimilarFiles = cmEval.dissimilarFiles + 1
				}
			]
	}

	def static void evaluateCodeModelCorrectness(Path worktree) {
		val evaluator = new CodeModelEvaluator()
		val injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
		injector.injectMembers(evaluator);
		evaluator.evaluateSourceCodeDir(worktree);
	}
}
