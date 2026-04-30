package cipm.consistency.tools.evaluation.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

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
}
