package cipm.consistency.commitintegration.lang.detection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class provides a configuration for modules to store and load it between different change
 * propagations.
 * 
 * @author Martin Armbruster
 */
public class ComponentStatePreferences {
    private static final String SEPARATOR = "/";
    private Path configPath;
    private HashMap<String, ComponentState> moduleClassification = new HashMap<>();
    private HashMap<String, String> subModuleMapping = new HashMap<>();

    /**
     * Creates a new instance.
     * 
     * @param configPath
     *            path to a file in which the configuration is stored and loaded from.
     */
    public ComponentStatePreferences(Path configPath) {
        this.configPath = configPath;
        load(configPath);
    }

    private void load(Path configPath) {
        if (Files.exists(configPath)) {
            Properties p = new Properties();
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                p.load(reader);
                p.propertyNames()
                    .asIterator()
                    .forEachRemaining(k -> {
                        String key = (String) k;
                        String val = p.getProperty(key);
                        if (val.contains(SEPARATOR)) {
                            moduleClassification.put(key, ComponentState.PART_OF_COMPONENT);
                            subModuleMapping.put(key, val.split(SEPARATOR)[1]);
                        } else {
                            moduleClassification.put(key, ComponentState.valueOf(val));
                        }
                    });
            } catch (IOException e) {
            }
        }
    }

    public void clear() {
        moduleClassification.clear();
        subModuleMapping.clear();
    }

    /**
     * Returns the classification of modules.
     * 
     * @return the classification.
     */
    public Map<String, ComponentState> getModuleClassification() {
        return moduleClassification;
    }

    /**
     * Returns a mapping for modules which are part of other modules.
     * 
     * @return the map in which a key represents a module and the value its containing module.
     */
    public Map<String, String> getSubModuleMapping() {
        return subModuleMapping;
    }

    /**
     * Stores the configuration.
     * 
     * @throws IOException
     */
    public void save() throws IOException {
        Properties p = new Properties();
        moduleClassification.forEach((k, v) -> {
            if (v == ComponentState.PART_OF_COMPONENT) {
                p.setProperty(k, v.name() + SEPARATOR + subModuleMapping.get(k));
            } else {
                p.setProperty(k, v.name());
            }
        });
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            p.store(writer, null);
        } catch (IOException e) {
            throw e;
        }
    }
}
