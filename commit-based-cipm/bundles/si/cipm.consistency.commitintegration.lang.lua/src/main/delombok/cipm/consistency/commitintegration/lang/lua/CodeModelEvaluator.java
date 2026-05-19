package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.tools.evaluation.data.CodeModelCorrectnessEval;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.xtext.lua.LuaStandaloneSetup;

/* @InjectWith(/* name is null */) */@SuppressWarnings("all")
public class CodeModelEvaluator {
  @Inject
  @Extension
  private Provider<XtextResourceSet> _provider;

  @Inject
  @Extension
  private ValidationTestHelper _validationTestHelper;

  /**
   * This brings a string into a form where we can compare it to another of the same form
   * This prevents false positives from whitespace, comment which would otherwise create
   * differences between the two strings.
   */
  public static String bringIntoCanonicalForm(final String luaCode) {
    String _xblockexpression = null;
    {
      String stripped = luaCode;
      final Pattern pattern = Pattern.compile("\\[\\[.*\\]\\]", Pattern.DOTALL);
      final Matcher matcher = pattern.matcher(stripped);
      stripped = matcher.replaceAll("");
      stripped = stripped.replaceAll("(?m)--[^\n]*\n?", "");
      stripped = stripped.replaceAll("(?m)^[\t ]*", "");
      stripped = stripped.replaceAll("(?m)[\t ]*$", "");
      stripped = stripped.replaceAll("[\r\n]+", "\n");
      _xblockexpression = stripped.trim();
    }
    return _xblockexpression;
  }

  public static String bringIntoExtremelyCanonicalForm(final String canonicalForm) {
    String _xblockexpression = null;
    {
      String canonical = canonicalForm.replaceAll("[\t ]+", "");
      canonical = canonical.replaceAll(",", "");
      _xblockexpression = canonical.replaceAll("\n", "");
    }
    return _xblockexpression;
  }

  public boolean checkCaseStudyFile(final XtextResourceSet rs, final Path srcFile, final Path appPath) {
    try {
      boolean _exists = srcFile.toFile().exists();
      boolean _not = (!_exists);
      if (_not) {
        return false;
      }
      final URI uri = URI.createFileURI(srcFile.toAbsolutePath().toString());
      Resource res = null;
      try {
        res = rs.getResource(uri, true);
      } catch (final Throwable _t) {
        if (_t instanceof WrappedException || _t instanceof IOException) {
          final Exception e = (Exception)_t;
          e.printStackTrace();
          return false;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      final String origString = Files.readString(srcFile);
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      res.save(outputStream, Collections.<Object, Object>unmodifiableMap(CollectionLiterals.<Object, Object>newHashMap()));
      final String parsedAndPrinted = outputStream.toString();
      boolean _equals = origString.equals(parsedAndPrinted);
      if (_equals) {
        CodeModelCorrectnessEval _codeModelCorrectness = EvaluationDataContainer.get().getCodeModelCorrectness();
        int _identicalFiles = EvaluationDataContainer.get().getCodeModelCorrectness().getIdenticalFiles();
        int _plus = (_identicalFiles + 1);
        _codeModelCorrectness.setIdenticalFiles(_plus);
      }
      final String origCanonical = CodeModelEvaluator.bringIntoCanonicalForm(origString);
      final String parsedAndPrintedCanonical = CodeModelEvaluator.bringIntoCanonicalForm(parsedAndPrinted);
      final String origExtremelyCanonical = CodeModelEvaluator.bringIntoExtremelyCanonicalForm(origCanonical);
      final String parsedAndPrintedExtremelyCanonical = CodeModelEvaluator.bringIntoExtremelyCanonicalForm(parsedAndPrintedCanonical);
      this._validationTestHelper.assertNoIssues(res);
      final boolean equivalence = origExtremelyCanonical.equals(parsedAndPrintedExtremelyCanonical);
      if (equivalence) {
        CodeModelCorrectnessEval _codeModelCorrectness_1 = EvaluationDataContainer.get().getCodeModelCorrectness();
        int _similarFiles = EvaluationDataContainer.get().getCodeModelCorrectness().getSimilarFiles();
        int _plus_1 = (_similarFiles + 1);
        _codeModelCorrectness_1.setSimilarFiles(_plus_1);
      } else {
        InputOutput.<String>println(("dissimilar file: " + srcFile));
        final Path targetDir = Paths.get("./caseStudyEvaluation/").resolve(appPath.relativize(srcFile));
        final Path plainDir = targetDir.resolve("plain");
        final Path canonicalDir = targetDir.resolve("canonical");
        final Path extremelyCanonicalDir = targetDir.resolve("extremely-canonical");
        Files.createDirectories(plainDir);
        Files.createDirectories(canonicalDir);
        Files.createDirectories(extremelyCanonicalDir);
        Files.writeString(plainDir.resolve("orig.lua"), origString);
        Files.writeString(plainDir.resolve("parsedAndPrinted.lua"), parsedAndPrinted);
        Files.writeString(canonicalDir.resolve("orig.lua"), origCanonical);
        Files.writeString(canonicalDir.resolve("parsedAndPrinted.lua"), parsedAndPrintedCanonical);
        Files.writeString(extremelyCanonicalDir.resolve("orig.lua"), origExtremelyCanonical);
        Files.writeString(extremelyCanonicalDir.resolve("parsedAndPrinted.lua"), parsedAndPrintedExtremelyCanonical);
      }
      return equivalence;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public static CodeModelCorrectnessEval resetEvalData() {
    CodeModelCorrectnessEval _xblockexpression = null;
    {
      final CodeModelCorrectnessEval cmEval = EvaluationDataContainer.get().getCodeModelCorrectness();
      cmEval.setDissimilarFiles(0);
      cmEval.setSimilarFiles(0);
      cmEval.setIdenticalFiles(0);
      _xblockexpression = cmEval;
    }
    return _xblockexpression;
  }

  private void evaluateSourceCodeDir(final Path appPath) {
    try {
      final XtextResourceSet rs = this._provider.get();
      final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.lua");
      final ArrayList<Path> equalPaths = new ArrayList<Path>();
      final ArrayList<Path> unequalPaths = new ArrayList<Path>();
      final CodeModelCorrectnessEval cmEval = CodeModelEvaluator.resetEvalData();
      final Path targetDir = Paths.get("./caseStudyEvaluation/");
      FileUtils.deleteDirectory(targetDir.toFile());
      try (final Stream<Path> paths = new Function0<Stream<Path>>() {
        @Override
        public Stream<Path> apply() {
          try {
            return Files.walk(appPath);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      }.apply()) {
        final Predicate<Path> _function = (Path p) -> {
          return matcher.matches(p);
        };
        final Consumer<Path> _function_1 = (Path path) -> {
          final Path relPath = appPath.relativize(path);
          boolean _checkCaseStudyFile = this.checkCaseStudyFile(rs, path, appPath);
          if (_checkCaseStudyFile) {
            equalPaths.add(relPath);
          } else {
            unequalPaths.add(relPath);
            int _dissimilarFiles = cmEval.getDissimilarFiles();
            int _plus = (_dissimilarFiles + 1);
            cmEval.setDissimilarFiles(_plus);
          }
        };
        paths.filter(_function).forEach(_function_1);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public static void evaluateCodeModelCorrectness(final Path worktree) {
    final CodeModelEvaluator evaluator = new CodeModelEvaluator();
    final Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
    injector.injectMembers(evaluator);
    evaluator.evaluateSourceCodeDir(worktree);
  }
}
