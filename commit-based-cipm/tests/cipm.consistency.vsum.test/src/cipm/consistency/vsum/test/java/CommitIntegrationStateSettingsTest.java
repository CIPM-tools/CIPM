package cipm.consistency.vsum.test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;

public class CommitIntegrationStateSettingsTest {

    @Test
    void testRecreateDirectoryPreservesSettings(
            @TempDir Path tempDir) throws Exception {

        // 1. Create a State and initialize its directory layout
        var state = new CommitIntegrationState<JavaModelFacade>();
        state.getDirLayout().initialize(tempDir);

        // 2. Create a settings file
        Path settingsFile =
            state.getDirLayout().getSettingsFilePath();

        String settingsContent =
            "cipm.consistency.settings.path.compile=test_compile.bat\n";

        Files.writeString(settingsFile, settingsContent);

        // 3. Create another file that should be deleted
        Path oldFile = tempDir.resolve("old-model.txt");
        Files.writeString(oldFile, "old model data");

        // 4. Call the directory recreation method
        Method method = CommitIntegrationState.class
            .getDeclaredMethod(
                "recreateDirectoryPreservingSettings",
                Path.class
            );

        method.setAccessible(true);
        method.invoke(state, tempDir);

        // 5. Check the results
        assertTrue(Files.exists(settingsFile));

        assertEquals(
            settingsContent,
            Files.readString(settingsFile)
        );

        assertFalse(Files.exists(oldFile));
    }
    
    
    @Test
    void testRecreateDirectoryWithoutSettings(
            @TempDir Path tempDir) throws Exception {

        // 1. Initialize the directory layout
        var state = new CommitIntegrationState<JavaModelFacade>();
        state.getDirLayout().initialize(tempDir);

        // 2. Create an old file, but no settings file
        Path oldFile = tempDir.resolve("old-model.txt");
        Files.writeString(oldFile, "old model data");

        // 3. Call the directory recreation method
        Method method = CommitIntegrationState.class
            .getDeclaredMethod(
                "recreateDirectoryPreservingSettings",
                Path.class
            );

        method.setAccessible(true);
        method.invoke(state, tempDir);

        // 4. Verify the result
        assertTrue(Files.exists(tempDir));

        assertFalse(Files.exists(oldFile));

        assertFalse(Files.exists(
            state.getDirLayout().getSettingsFilePath()
        ));
    }
}