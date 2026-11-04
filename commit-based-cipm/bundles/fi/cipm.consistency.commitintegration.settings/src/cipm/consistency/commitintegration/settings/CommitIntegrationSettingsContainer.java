package cipm.consistency.commitintegration.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import java.io.UncheckedIOException;

/**
 * A container for the settings of the commit-based integration.
 * 
 * @author Martin Armbruster
 */
public final class CommitIntegrationSettingsContainer {
	private static CommitIntegrationSettingsContainer instance;
	private Path settingsFile;
	private Properties properties;

	public static void initialize(Path path) {
		instance = new CommitIntegrationSettingsContainer(path);
	}

	public static CommitIntegrationSettingsContainer getSettingsContainer() {
		return instance;
	}

	private CommitIntegrationSettingsContainer(Path path) {
		this.settingsFile = path;
		// 1.Load default settings
		Properties defaults = new Properties();
		try (InputStream in = CommitIntegrationSettingsContainer.class
				.getResourceAsStream("/default-settings.properties")) {
			if (in == null) {
				throw new IllegalStateException("Default settings resource not found");
			}
			defaults.load(in);
		} catch (IOException e) {
			throw new UncheckedIOException("failed to load default settings", e);
		}
		// 2.Create properties with defaults
		this.properties = new Properties(defaults);

		// 3. Load specific settings if the file exists
		if (Files.exists(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				properties.load(in);
			} catch (IOException e) {
				throw new UncheckedIOException("failed to load setting file: " + path, e);
			}
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public boolean getPropertyAsBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}
}
