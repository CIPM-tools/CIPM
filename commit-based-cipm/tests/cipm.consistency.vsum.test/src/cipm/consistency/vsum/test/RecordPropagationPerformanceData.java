package cipm.consistency.vsum.test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;

public class RecordPropagationPerformanceData {
	public static class RecordPropagationPerformanceDataPoint {
		private int iteration;
		private long time;
		
		public RecordPropagationPerformanceDataPoint(int iteration, long time) {
			this.iteration = iteration;
			this.time = time;
		}
		
		public int getIteration() {
			return this.iteration;
		}
		
		public long getTime() {
			return this.time;
		}
	}
	
	private List<RecordPropagationPerformanceDataPoint> points = new ArrayList<>();
	
	public void addPerformance(int iteration, long time) {
		this.points.add(new RecordPropagationPerformanceDataPoint(iteration, time));
	}
	
	public void saveData(Path file) throws IOException {
		JsonMapper mapper = new JsonMapper();
		mapper.writeValue(file.toFile(), this.points);
	}
}
