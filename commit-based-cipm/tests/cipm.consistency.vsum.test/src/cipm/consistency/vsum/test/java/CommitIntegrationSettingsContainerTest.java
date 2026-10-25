package cipm.consistency.vsum.test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;

public class CommitIntegrationSettingsContainerTest {
	@Test
	void testLoadSettings(@TempDir Path tempDir) throws IOException{
		Path settingFile = tempDir.resolve("settings.properties");
		Files.writeString(
				settingFile,
				SettingKeys.PERFORM_FULL_INSTRUMENTATION+ "=true"
				);
		CommitIntegrationSettingsContainer.initialize(settingFile);
		String value = CommitIntegrationSettingsContainer
				.getSettingsContainer()
				.getProperty(SettingKeys.PERFORM_FULL_INSTRUMENTATION);
		assertEquals("true",value);
	}
	
	
	@Test
	void testLoadSettingsThrowsExceptionForDirectory(@TempDir Path tempDir) {
		UncheckedIOException exception = assertThrows(
				UncheckedIOException.class,
				()-> CommitIntegrationSettingsContainer.initialize(tempDir)
				);
		System.out.println(exception.getMessage());
	}
}
