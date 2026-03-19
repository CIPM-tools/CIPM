package cipm.consistency.commitintegration.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

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
        properties = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(this.settingsFile)) {
                properties.load(in);
            } catch (IOException e) {
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
