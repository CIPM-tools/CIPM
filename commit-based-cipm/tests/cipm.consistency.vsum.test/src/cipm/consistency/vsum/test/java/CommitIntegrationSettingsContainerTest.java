package cipm.consistency.vsum.test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	
	
	@Test
	void testDefaultSettingsResource() throws IOException{
		try(InputStream in = 
				CommitIntegrationSettingsContainer.class
                .getResourceAsStream("/default-settings.properties")
				){
			assertNotNull(in,"Default settings file not found");
			Properties defaults = new Properties();
			defaults.load(in);
			assertEquals("false",
					defaults.getProperty(
							SettingKeys.PERFORM_FULL_INSTRUMENTATION
							)
					);
		}
	}
	
	
	@Test
	void testSpecificSettingsOverrideDefaults(@TempDir Path tempDir) throws IOException{
			Path settingFile = tempDir.resolve("settings.properties");
			Files.writeString(settingFile, SettingKeys.PERFORM_FULL_INSTRUMENTATION+"=true");
			CommitIntegrationSettingsContainer.initialize(settingFile);
			
			CommitIntegrationSettingsContainer container = 
					CommitIntegrationSettingsContainer.getSettingsContainer();
			
			assertEquals("true",
					container.getProperty(SettingKeys.PERFORM_FULL_INSTRUMENTATION)
					);
			
			assertEquals("false",
					container.getProperty(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)
					);
		}
	
}
