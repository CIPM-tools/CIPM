package cipm.consistency.tools.evaluation.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class enables the reading and writing of the evaluation data.
 * 
 * @author Martin Armbruster
 */
public final class EvaluationDataContainerReaderWriter {
    private EvaluationDataContainerReaderWriter() {
    }

    /**
     * Reads evaluation data from a file.
     * 
     * @param file
     *            the file from which the data is read.
     * @return the read data.
     */
    public static EvaluationDataContainer read(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return new Gson().fromJson(reader, EvaluationDataContainer.class);
        } catch (IOException e) {
            return null;
        }
    }
    
    public static EvaluationDataContainer readNew(Path file) {
    	Gson gson = new Gson();
    	try(BufferedReader reader = Files.newBufferedReader(file)){
    		JsonObject root = gson.fromJson(reader, JsonObject.class);
    		EvaluationDataContainer container = new EvaluationDataContainer();
    		if(root.has("validated")) {
    			container.setSuccessful(
    				root.get("validated").getAsBoolean()
    				);
    		}
    		if(root.has("evaluationRan")) {
    			container.setEvaluationRan(
    					root.get("evaluationRan").getAsBoolean()
    					);
    		}
    		if(root.has("errorMessage")&&!root.get("errorMessage").isJsonNull()) {
    			container.setErrorMessage(root.get("errorMessage").getAsString());
    		}
    		if (root.has("data") && root.get("data").isJsonObject()) {
    		    JsonObject dataObject = root.getAsJsonObject("data");

    		    for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
    		        String key = entry.getKey();
    		        JsonElement jsonValue = entry.getValue();

    		        Type type = EvaluationDataContainer.getRegisteredType(key);

    		        if (type != null && !jsonValue.isJsonNull()) {
    		            Object value = gson.fromJson(jsonValue, type);
    		            container.put(key, value);
    		        }
    		    }
    		}
    		return container;
    	}catch(IOException e) {
    		return null;
    	}
    }
    
    public static LegacyEvaluationDataContainer readLegacy(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return new Gson().fromJson(
                    reader,
                    LegacyEvaluationDataContainer.class
            );
        } catch (IOException e) {
            return null;
        }
    }
    public static EvaluationDataContainer readLegacyAndConvert(Path file) {
        LegacyEvaluationDataContainer legacy = readLegacy(file);
        if (legacy == null) {
            return null;
        }
        return EvaluationDataContainer.fromLegacy(legacy);
    }
    

    /**
     * Writes the evaluation data to a file.
     * 
     * @param result
     *            the data to write.
     * @param file
     *            the file in which the data is written.
     */
    public static void write(EvaluationDataContainer result, Path file) {
        Gson gson = new Gson();
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            gson.toJson(result, EvaluationDataContainer.class, gson.newJsonWriter(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeNew(EvaluationDataContainer result, Path file) {
        Gson gson = new Gson();
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            gson.toJson(result, EvaluationDataContainer.class, gson.newJsonWriter(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
