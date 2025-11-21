package cipm.consistency.vsum.test.pcm.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.cpr.pcmjava.logger.PcmCprLogger;
import cipm.consistency.cpr.pcmjava.logger.PcmUserInteractionAutomaticityStatistics;
import cipm.consistency.cpr.pcmjava.logger.PcmUserInteractionTimeStatistics;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;

public class ExperimentResult {
	private String vsumTestPath;

	private Map<String, Number> jaccardCoefficientForJavaModelInJavaToPcmPropagation;
	private Map<String, Number> jaccardCoefficientForPcmRepositoryInJavaToPcmPropagation;
	private Map<String, Number> fOneScoreForImInJavaToPcmPropagation;

	private Map<String, Number> jaccardCoefficientForJavaModelInPcmToJavaPropagation;
	private Map<String, Number> jaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation;

	private Map<String, Number> jaccardCoefficientForPcmRepositoryInPcmToJavaPropagation;
	private Map<String, Number> jaccardCoefficientForSEFFlessPcmRepositoryInPcmToJavaPropagation;

	private Map<String, Number> fOneScoreForImInPcmToJavaPropagation;

	private PcmUserInteractionAutomaticityStatistics pcmStats = PcmUserInteractionAutomaticityStatistics.getInstance();
	private PcmCprLogger pcmLogger = PcmCprLogger.getInstance();
	private PcmUserInteractionTimeStatistics pcmTimeMeasurements = PcmUserInteractionTimeStatistics.getInstance();

	private int originalPcmChangeCount = 0;
	private int originalJavaChangeCount = 0;
	private int originalImChangeCount = 0;
	
	private int propagatedPcmChangeCount = 0;
	private int propagatedJavaChangeCount = 0;
	private int propagatedImChangeCount = 0;

	public void setVsumTestPath(Path vsumTestPath) {
		this.vsumTestPath = vsumTestPath.toString();
	}

	private Map<String, Number> getDataFromImUpdateEval(ImUpdateEvalData imUpdate) {
		var result = new LinkedHashMap<String, Number>();
		result.put("numberIP", imUpdate.getNumberIP());
		result.put("numberSIP", imUpdate.getNumberSIP());
		result.put("numberMatchedSIP", imUpdate.getNumberMatchedSIP());
		result.put("numberAIP", imUpdate.getNumberAIP());
		result.put("numberMatchedAIP", imUpdate.getNumberMatchedAIP());
		result.put("numberActiveAIP", imUpdate.getNumberActiveAIP());
		result.put("numberMatchedActiveAIP", imUpdate.getNumberMatchedActiveAIP());
		result.put("numberAddedActions", imUpdate.getNumberAddedActions());
		result.put("numberChangedActions", imUpdate.getNumberChangedActions());
		result.put("ratioActiveAIPs", imUpdate.getRatioActiveAIPs());
		result.put("ratioAipPerSip", imUpdate.getRatioAipPerSip());
		result.put("proportionalOverheadReduction", imUpdate.getProportionalOverheadReduction());
		result.put("fScoreServiceInstrumentationPoints", imUpdate.getfScoreServiceInstrumentation());
		result.put("fScoreActionInstrumentationPoints", imUpdate.getfScoreActionInstrumentation());
		result.put("fScoreActiveActionInstrumentationPoints", imUpdate.getfScoreActiveActionInstrumentationPoints());
		return result;
	}

	private Map<String, Number> getDataFromJCResult(JaccardCoefficientResult jcr) {
		var result = new LinkedHashMap<String, Number>();
		result.put("Jaccard coefficient", jcr.getJC());
		result.put("Intersection cardinality", jcr.getIntersectionCardinality());
		result.put("Union cardinality", jcr.getUnionCardinality());
		return result;
	}

	public void setPropagatedPcmChangeCount(int propagatedPcmChangeCount) {
		this.propagatedPcmChangeCount = propagatedPcmChangeCount;
	}

	public void setPropagatedJavaChangeCount(int propagatedJavaChangeCount) {
		this.propagatedJavaChangeCount = propagatedJavaChangeCount;
	}

	public void setPropagatedImChangeCount(int propagatedImChangeCount) {
		this.propagatedImChangeCount = propagatedImChangeCount;
	}

	public void setOriginalPcmChangeCount(int originalPcmChangeCount) {
		this.originalPcmChangeCount = originalPcmChangeCount;
	}

	public void setOriginalJavaChangeCount(int originalJavaChangeCount) {
		this.originalJavaChangeCount = originalJavaChangeCount;
	}

	public void setOriginalImChangeCount(int originalImChangeCount) {
		this.originalImChangeCount = originalImChangeCount;
	}

	public void setJaccardCoefficientForJavaModelInJavaToPcmPropagation(
			JaccardCoefficientResult jaccardCoefficientForJavaModelInJavaToPcmPropagation) {
		this.jaccardCoefficientForJavaModelInJavaToPcmPropagation = getDataFromJCResult(
				jaccardCoefficientForJavaModelInJavaToPcmPropagation);
	}

	public void setJaccardCoefficientForPcmRepositoryInJavaToPcmPropagation(
			JaccardCoefficientResult jaccardCoefficientForPcmRepositoryInJavaToPcmPropagation) {
		this.jaccardCoefficientForPcmRepositoryInJavaToPcmPropagation = getDataFromJCResult(
				jaccardCoefficientForPcmRepositoryInJavaToPcmPropagation);
	}

	public void setfOneScoreForImInJavaToPcmPropagation(ImUpdateEvalData fOneScoreForImInJavaToPcmPropagation) {
		this.fOneScoreForImInJavaToPcmPropagation = getDataFromImUpdateEval(fOneScoreForImInJavaToPcmPropagation);
	}

	public void setJaccardCoefficientForJavaModelInPcmToJavaPropagation(
			JaccardCoefficientResult jaccardCoefficientForJavaModelInPcmToJavaPropagation) {
		this.jaccardCoefficientForJavaModelInPcmToJavaPropagation = getDataFromJCResult(
				jaccardCoefficientForJavaModelInPcmToJavaPropagation);
	}

	public void setJaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation(
			JaccardCoefficientResult jaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation) {
		this.jaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation = getDataFromJCResult(
				jaccardCoefficientForStatementlessJavaModelInPcmToJavaPropagation);
	}

	public void setJaccardCoefficientForSEFFlessPcmRepositoryInPcmToJavaPropagation(
			JaccardCoefficientResult jaccardCoefficientForSEFFlessPCMInPcmToJavaPropagation) {
		this.jaccardCoefficientForSEFFlessPcmRepositoryInPcmToJavaPropagation = getDataFromJCResult(
				jaccardCoefficientForSEFFlessPCMInPcmToJavaPropagation);
	}

	public void setJaccardCoefficientForPcmRepositoryInPcmToJavaPropagation(
			JaccardCoefficientResult jaccardCoefficientForPcmRepositoryInPcmToJavaPropagation) {
		this.jaccardCoefficientForPcmRepositoryInPcmToJavaPropagation = getDataFromJCResult(
				jaccardCoefficientForPcmRepositoryInPcmToJavaPropagation);
	}

	public void setfOneScoreForImInPcmToJavaPropagation(ImUpdateEvalData fOneScoreForImInPcmToJavaPropagation) {
		this.fOneScoreForImInPcmToJavaPropagation = getDataFromImUpdateEval(fOneScoreForImInPcmToJavaPropagation);
	}

	/**
	 * Compares Java -> PCM Teammates test results to PCM -> Java Teammates test
	 * results
	 */
	public void interpretResults() {

	}

	/**
	 * Saves all metrics and computations under the given path
	 */
	public void save(Path pathToSave) {
		this.write(this, pathToSave);
	}

	/**
	 * Reads evaluation data from a file.
	 * 
	 * @param file the file from which the data is read.
	 * @return the read data.
	 */
	public ExperimentResult read(Path file) {
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			return new GsonBuilder().serializeSpecialFloatingPointValues().create().fromJson(reader,
					ExperimentResult.class);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the evaluation data to a file.
	 * 
	 * @param result the data to write.
	 * @param file   the file in which the data is written.
	 */
	public void write(ExperimentResult result, Path file) {
		Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting()
				.registerTypeHierarchyAdapter(LocalDateTime.class, this.getDateAdapter()).create();
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			gson.toJson(result, ExperimentResult.class, gson.newJsonWriter(writer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonSerializer<LocalDateTime> getDateAdapter() {
		return new JsonSerializer<LocalDateTime>() {
			@Override
			public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(DateTimeFormatter.ISO_DATE_TIME.format(src));
			}
		};
	}
}
