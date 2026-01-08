package cipm.consistency.runtime.pipeline.validation.eval;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.pcm.headless.shared.data.results.AbstractMeasureValue;
import org.pcm.headless.shared.data.results.DoubleMeasureValue;
import org.pcm.headless.shared.data.results.InMemoryResultRepository;
import org.pcm.headless.shared.data.results.LongMeasureValue;
import org.pcm.headless.shared.data.results.PlainDataMeasure;
import org.pcm.headless.shared.data.results.PlainDataSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.runtime.pipeline.validation.data.TimeValueDistribution;
import cipm.consistency.runtime.pipeline.validation.data.ValidationData;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;

@Service
public class ValidationDataExtractor {
	private static final Set<String> SWAP_SET = Sets.newHashSet("_sefjUeJCEeO6l86uYUhhyw");

	@Autowired
	private MonitoringDataEnrichment monitoringEnrichment;

	public ValidationData extractValidationData(InMemoryResultRepository analysis, InMemoryPCM pcm,
			List<PCMContextRecord> monitoringData) {
		ValidationData data = new ValidationData();

		// start with simulation data
		List<ValidationPoint> validationPoints = analysis.getValues().stream().map(v -> {
			return ValidationPoint.builder().measuringPoint(v.getKey().getPoint())
					.id(v.getKey().getDesc().getId() + "-" + String.join("-", v.getKey().getPoint().getSourceIds()))
					.metricDescription(v.getKey().getDesc())
					.analysisDistribution(transformAnalysisData(v.getKey().getDesc().getId(), v.getValue())).build();
		}).collect(Collectors.toList());

		// add monitoring data
		monitoringEnrichment.enrichWithMonitoringData(pcm, validationPoints, monitoringData);

		// set it
		data.setValidationPoints(validationPoints);

		// & finally return
		return data;
	}

	private TimeValueDistribution transformAnalysisData(String metricId, List<PlainDataSeries> series) {
		TimeValueDistribution res = new TimeValueDistribution();
		for (int serNumber = 0; serNumber < series.size(); serNumber++) {
			PlainDataSeries ser = series.get(serNumber);
			boolean isX = serNumber % 2 == 0;
			for (PlainDataMeasure measure : ser.getMeasures()) {
				if (isX) {
					res.addValueX(toDoubleValue(measure.getV()));
				} else {
					res.addValueY(toDoubleValue(measure.getV()));
				}
			}
		}

		if (SWAP_SET.contains(metricId)) {
			res.swapAxis();
		}

		return res;
	}

	private double toDoubleValue(AbstractMeasureValue v) {
		if (v instanceof DoubleMeasureValue) {
			return ((DoubleMeasureValue) v).getV();
		} else if (v instanceof LongMeasureValue) {
			return Long.valueOf(((LongMeasureValue) v).getV()).doubleValue();
		}
		return Double.NaN;
	}

}
