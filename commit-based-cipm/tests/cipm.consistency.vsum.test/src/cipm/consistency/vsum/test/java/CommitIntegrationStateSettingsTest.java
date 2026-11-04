package cipm.consistency.vsum.test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.commons.io.FileUtils;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;

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
    
    
    @Test
    void testStateCopyPreservesSpecificSettings(
            @TempDir Path tempDir) throws Exception {

        // 1. Create a main State directory
        Path mainDir = tempDir.resolve("main");
        Files.createDirectories(mainDir);

        Path mainSettings = mainDir.resolve("settings.settings");

        String settingsContent =
            "cipm.consistency.settings.parser.excludes=TEAMMATES_TEST_RULE\n";

        Files.writeString(mainSettings, settingsContent);

        // 2. Load the main State settings
        CommitIntegrationSettingsContainer.initialize(mainSettings);

        String originalValue =
            CommitIntegrationSettingsContainer
                .getSettingsContainer()
                .getProperty(SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS);

        assertEquals("TEAMMATES_TEST_RULE", originalValue);

        // 3. Copy the directory, as the production code does
        Path copyDir = tempDir.resolve("state-copy");

        FileUtils.copyDirectory(
            mainDir.toFile(),
            copyDir.toFile()
        );

        // 4. Initialize settings from the copied directory
        Path copiedSettings = copyDir.resolve("settings.settings");

        CommitIntegrationSettingsContainer.initialize(copiedSettings);

        // 5. Verify the copied settings
        String copiedValue =
            CommitIntegrationSettingsContainer
                .getSettingsContainer()
                .getProperty(SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS);

        assertTrue(Files.exists(copiedSettings));
        assertEquals(originalValue, copiedValue);
    }
    
    
    @Test
    void testStateCopyWithoutSettingsChangesGlobalSettings(
            @TempDir Path tempDir) throws Exception {

        // 1. Create settings for the main State
        Path mainSettings = tempDir.resolve("main-settings.properties");

        Files.writeString(
            mainSettings,
            "cipm.consistency.settings.parser.excludes=TEAMMATES_TEST_RULE\n"
        );

        // 2. Initialize the main settings
        CommitIntegrationSettingsContainer.initialize(mainSettings);

        String mainValue = CommitIntegrationSettingsContainer
            .getSettingsContainer()
            .getProperty(SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS);

        assertEquals("TEAMMATES_TEST_RULE", mainValue);

        // 3. Simulate an old State Copy without a settings file
        Path missingCopySettings =
            tempDir.resolve("state-copy/settings.settings");

        CommitIntegrationSettingsContainer.initialize(missingCopySettings);

        // 4. Check the global settings after reinitialization
        String valueAfterCopy = CommitIntegrationSettingsContainer
            .getSettingsContainer()
            .getProperty(SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS);

        assertEquals("", valueAfterCopy);
    }
    
    @Test
    void testIsMainStatePath(@TempDir Path tempDir)
            throws Exception {

        Path mainPath = tempDir.resolve("main");

        Method method = CommitIntegrationState.class
            .getDeclaredMethod(
                "isMainStatePath",
                Path.class,
                Path.class
            );

        method.setAccessible(true);

        boolean result = (boolean) method.invoke(
            null,
            mainPath,
            mainPath
        );

        assertTrue(result);
    }
    
    
    @Test
    void testIsStateCopyPath(@TempDir Path tempDir)
            throws Exception {

        Path mainPath = tempDir.resolve("main");
        Path copyPath = tempDir.resolve("main-copy");

        Method method = CommitIntegrationState.class
            .getDeclaredMethod(
                "isMainStatePath",
                Path.class,
                Path.class
            );

        method.setAccessible(true);

        boolean result = (boolean) method.invoke(
            null,
            mainPath,
            copyPath
        );

        assertFalse(result);
    }
    
    @Test
    void testStateCopyDoesNotOverrideMainSettings(
            @TempDir Path tempDir) throws Exception {

        // 1. Prepare the Main State settings
        Path mainDir = tempDir.resolve("main");
        Path copyDir = tempDir.resolve("main-copy");

        Files.createDirectories(mainDir);

        Path mainSettings = mainDir.resolve("settings.settings");

        Files.writeString(
            mainSettings,
            "cipm.consistency.settings.parser.excludes=MAIN_RULE\n"
        );

        // 2. Access the settings initialization method
        Method method = CommitIntegrationState.class
            .getDeclaredMethod(
                "initializeSettingsForState",
                Path.class,
                Path.class,
                Path.class
            );

        method.setAccessible(true);

        // 3. Initialize settings for the Main State
        method.invoke(null, mainDir, mainDir, mainSettings);

        var mainContainer = CommitIntegrationSettingsContainer
            .getSettingsContainer();

        assertEquals(
            "MAIN_RULE",
            mainContainer.getProperty(
                SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS
            )
        );

        // 4. Simulate initialization of a State Copy
        Path copySettings = copyDir.resolve("settings.settings");

        method.invoke(null, mainDir, copyDir, copySettings);

        // 5. Verify that the global settings remain unchanged
        var containerAfterCopy = CommitIntegrationSettingsContainer
            .getSettingsContainer();

        assertSame(mainContainer, containerAfterCopy);

        assertEquals(
            "MAIN_RULE",
            containerAfterCopy.getProperty(
                SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS
            )
        );
    }
}