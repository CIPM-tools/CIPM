package cipm.consistency.vsum.test.evaluationdata;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.ExecutionTimeData;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;
import cipm.consistency.tools.evaluation.data.ChangeStatistic;
import cipm.consistency.tools.evaluation.data.CodeModelUpdateEvalData;
import cipm.consistency.tools.evaluation.data.CodeModelCorrectnessEval;
import cipm.consistency.tools.evaluation.data.LegacyEvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.tools.evaluation.data.PcmUpdateEvalData;
import cipm.consistency.tools.evaluation.data.InstrumentationEvaluationData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EvaluationDataContainerTest {
	@TempDir
    Path tempDir;
	@Test
	void readLegacyReadsOldEvaluationDataFile() throws IOException {
	    String legacyJson = "{"
	            + "\"validated\":true,"
	            + "\"evaluationRan\":true,"
	            + "\"errorMessage\":\"legacy error\","
	            + "\"executionTimes\":{"
	            + "\"changePropagationTime\":456"
	            + "}"
	            + "}";

	    Path legacyFile = tempDir.resolve("evaluationData.json");
	    Files.writeString(legacyFile, legacyJson);
	    LegacyEvaluationDataContainer legacy =
	            EvaluationDataContainerReaderWriter.readLegacy(legacyFile);

	    assertNotNull(legacy);
	    assertTrue(legacy.isValidated());
	    assertTrue(legacy.isEvaluationRan());
	    assertEquals("legacy error", legacy.getErrorMessage());
	    assertEquals(
	            456L,
	            legacy.getExecutionTimes().getChangePropagationTime()
	    );
	}
	
	
	@Test
	void getExecutionTimesReturnsSameInstance() {
		EvaluationDataContainer container = new EvaluationDataContainer();
		ExecutionTimeData first = container.getExecutionTimes();
		ExecutionTimeData second = container.getExecutionTimes();
		assertSame(first,second);
	}
	@Test
	void  getExecutionTimesKeepsUpdatedValues() {
		EvaluationDataContainer container = new EvaluationDataContainer();
		ExecutionTimeData first = container.getExecutionTimes();
		first.setChangePropagationTime(123L);
		ExecutionTimeData second = container.getExecutionTimes();
		assertEquals(123L,second.getChangePropagationTime());
	}
	
	
	@Test
	void getImUpdateEvalDataReturnsSameInstance() {
		EvaluationDataContainer container = new  EvaluationDataContainer();
		ImUpdateEvalData first = container.getImUpdateEvalData();
		ImUpdateEvalData second = container.getImUpdateEvalData();
		assertSame(first,second);
	}
	@Test
	void resetImUpdateEvalReplacesInstance() {
		EvaluationDataContainer container = new  EvaluationDataContainer();
		ImUpdateEvalData first = container.getImUpdateEvalData();
		ImUpdateEvalData reset = container.resetImUpdateEval();
		ImUpdateEvalData afterReset = container.getImUpdateEvalData();
		assertNotSame(first,reset);
		assertSame(reset,afterReset);
	}
	
	
	@Test
	void getChangeStatisticReturnsSameInstance() {
		EvaluationDataContainer container = new EvaluationDataContainer();

	    ChangeStatistic first = container.getChangeStatistic();
	    ChangeStatistic second = container.getChangeStatistic();

	    assertSame(first, second);
	}
	@Test
	void resetChangeStatisticReplacesInstance() {
	    EvaluationDataContainer container = new EvaluationDataContainer();

	    ChangeStatistic first = container.getChangeStatistic();
	    ChangeStatistic reset = container.resetChangeStatistic();
	    ChangeStatistic afterReset = container.getChangeStatistic();

	    assertNotSame(first, reset);
	    assertSame(reset, afterReset);
	}
	
	
	@Test
	void getCodeModelUpdateEvalDataReturnsSameInstance() {
		EvaluationDataContainer container = new EvaluationDataContainer();
		CodeModelUpdateEvalData first = container.getCodeModelUpdateEvalData();
	    CodeModelUpdateEvalData second = container.getCodeModelUpdateEvalData();
	    assertSame(first, second);
	}
	
	
	@Test
	void getCodeModelCorrectnessReturnsSameInstance() {
		EvaluationDataContainer container = new EvaluationDataContainer();
	    CodeModelCorrectnessEval first = container.getCodeModelCorrectness();
	    CodeModelCorrectnessEval second = container.getCodeModelCorrectness();
	    assertSame(first, second);
	}
	@Test
	void setCodeModelCorrectnessReplacesInstance() {
	    EvaluationDataContainer container = new EvaluationDataContainer();
	    CodeModelCorrectnessEval first = container.getCodeModelCorrectness();
	    CodeModelCorrectnessEval replacement = new CodeModelCorrectnessEval();
	    container.setCodeModelCorrectness(replacement);
	    CodeModelCorrectnessEval afterSet = container.getCodeModelCorrectness();
	    assertNotSame(first, replacement);
	    assertSame(replacement, afterSet);
	}
	
	
	@Test
	void fromLegacyPreservesExistingData() {
		String legacyJson = "{"
		            + "\"validated\":true,"
		            + "\"evaluationRan\":true,"
		            + "\"errorMessage\":\"test error\","
		            + "\"executionTimes\":{"
		            + "\"changePropagationTime\":123"
		            + "}"
		            + "}";
		LegacyEvaluationDataContainer legacy =
	            new Gson().fromJson(
	                    legacyJson,
	                    LegacyEvaluationDataContainer.class
	            );
		EvaluationDataContainer container =
	            EvaluationDataContainer.fromLegacy(legacy);

	    assertTrue(container.valid());
	    assertTrue(container.isEvaluationRan());
	    assertEquals("test error", container.getErrorMessage());
	    assertEquals(123L,container.getExecutionTimes().getChangePropagationTime()
	    );
	}
	
	@Test
	void readLegacyAndConvertPreservesExistingData()throws IOException {
		String legacyJson = "{"
	            + "\"validated\":true,"
	            + "\"evaluationRan\":true,"
	            + "\"errorMessage\":\"converted error\","
	            + "\"executionTimes\":{"
	            + "\"changePropagationTime\":789"
	            + "}"
	            + "}";
		Path legacyFile = tempDir.resolve("evaluationData.json");
	    Files.writeString(legacyFile, legacyJson);
	    EvaluationDataContainer container =
	            EvaluationDataContainerReaderWriter.readLegacyAndConvert(legacyFile);
	    
	    assertNotNull(container);
	    assertTrue(container.valid());
	    assertTrue(container.isEvaluationRan());
	    assertEquals("converted error",container.getErrorMessage());
	    assertEquals(789L,container.getExecutionTimes().getChangePropagationTime());
	}
	
	@Test
	void getPcmUpdateEvalsReturnsSameInstanceAndKeepsValues() {
		EvaluationDataContainer container = new EvaluationDataContainer();

	    List<PcmUpdateEvalData> first = container.getPcmUpdateEvals();

	    PcmUpdateEvalData item = new PcmUpdateEvalData();
	    first.add(item);

	    List<PcmUpdateEvalData> second = container.getPcmUpdateEvals();

	    assertSame(first, second);
	    assertEquals(1, second.size());
	    assertSame(item, second.get(0));
	}
	
	@Test
	void getInstrumentationDataReturnsSameInstance() {
	    EvaluationDataContainer container = new EvaluationDataContainer();

	    InstrumentationEvaluationData first =
	            container.getInstrumentationData();
	    InstrumentationEvaluationData second =
	            container.getInstrumentationData();

	    assertNotNull(first);
	    assertSame(first, second);
	}
	
	@Test
	void readNewRestoresRegisteredDataTypes() throws IOException {
	    String newJson = "{"
	            + "\"validated\":true,"
	            + "\"evaluationRan\":true,"
	            + "\"errorMessage\":\"new format error\","
	            + "\"data\":{"
	            + "\"executionTimes\":{"
	            + "\"changePropagationTime\":123"
	            + "},"
	            + "\"pcmUpdateEvals\":[{}]"
	            + "}"
	            + "}";

	    Path newFile = tempDir.resolve("evaluation-new.json");
	    Files.writeString(newFile, newJson);

	    EvaluationDataContainer container =
	            EvaluationDataContainerReaderWriter.readNew(newFile);

	    assertNotNull(container);
	    assertTrue(container.valid());
	    assertTrue(container.isEvaluationRan());
	    assertEquals(
	            "new format error",
	            container.getErrorMessage()
	    );

	    assertEquals(
	            123L,
	            container.getExecutionTimes().getChangePropagationTime()
	    );

	    assertEquals(1, container.getPcmUpdateEvals().size());

	    Object pcmUpdateEval =
	            container.getPcmUpdateEvals().get(0);

	    assertTrue(pcmUpdateEval instanceof PcmUpdateEvalData);
	}
	@Test
	void writeAndReadNewPreservesData() throws IOException {
	    EvaluationDataContainer original =
	            new EvaluationDataContainer();

	    original.setSuccessful(true);
	    original.setEvaluationRan(true);
	    original.setErrorMessage("round trip error");

	    original.getExecutionTimes()
	            .setChangePropagationTime(321L);

	    PcmUpdateEvalData pcmUpdateEval =
	            new PcmUpdateEvalData();

	    original.getPcmUpdateEvals().add(pcmUpdateEval);

	    Path newFile =
	            tempDir.resolve("evaluation-new.json");

	    EvaluationDataContainerReaderWriter.writeNew(
	            original,
	            newFile
	    );

	    EvaluationDataContainer restored =
	            EvaluationDataContainerReaderWriter.readNew(
	                    newFile
	            );

	    assertNotNull(restored);
	    assertTrue(restored.valid());
	    assertTrue(restored.isEvaluationRan());

	    assertEquals(
	            "round trip error",
	            restored.getErrorMessage()
	    );

	    assertEquals(
	            321L,
	            restored.getExecutionTimes()
	                    .getChangePropagationTime()
	    );

	    assertEquals(
	            1,
	            restored.getPcmUpdateEvals().size()
	    );

	    assertTrue(
	            restored.getPcmUpdateEvals().get(0)
	                    instanceof PcmUpdateEvalData
	    );
	}
	
}
