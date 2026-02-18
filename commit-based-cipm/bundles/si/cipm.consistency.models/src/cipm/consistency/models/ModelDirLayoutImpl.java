package cipm.consistency.models;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Exceptions;

public class ModelDirLayoutImpl implements ModelDirLayout {
  protected Path rootDirPath;

  @Override
  public void initialize(final Path rootDirPath) {
    try {
      this.rootDirPath = rootDirPath.toAbsolutePath();
      if (!rootDirPath.toFile().exists()) {
        Files.createDirectories(rootDirPath);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  @Override
  public void delete() {
    try {
      final Consumer<Path> _function = (Path it) -> {
        it.toFile().delete();
      };
      Files.walk(this.rootDirPath).sorted(Comparator.<Path>reverseOrder()).forEach(_function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public void clean() {
    this.delete();
    this.initialize(this.rootDirPath);
  }

  @Override
  public Path getRootDirPath() {
    return this.rootDirPath;
  }
}
