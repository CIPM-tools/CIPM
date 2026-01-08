package cipm.consistency.runtime.pipeline.validation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.vitruv.VsumManagerTestBase;
import cipm.consistency.base.vsum.facade.CentralVsumFacade;
import cipm.consistency.runtime.pipeline.validation.data.metric.IValidationMetric;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallAverageDistanceRelative;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallKSTestMetric;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallMedianDistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallQ1DistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallQ3DistanceAbsolute;
import cipm.consistency.runtime.pipeline.validation.data.metric.impl.ServiceCallWassersteinDistanceMetric;
import cipm.consistency.runtime.pipeline.validation.eval.MonitoringDataEnrichment;
import cipm.consistency.runtime.pipeline.validation.eval.ValidationDataExtractor;
import cipm.consistency.runtime.pipeline.validation.eval.util.PCMValidationPointMatcher;
import cipm.consistency.runtime.pipeline.validation.facade.RuntimeEnvironmentQueryImpl;
import cipm.consistency.runtime.pipeline.validation.simulation.HeadlessPCMSimulator;

public class ValidationProcessorTestBase {

	@TestConfiguration
	public static class TestContextConfiguration extends VsumManagerTestBase.VsumManagerTestConfiguration {

		@Bean
		public ValidationFeedbackComponent vfc() {
			return new ValidationFeedbackComponent();
		}

		@Bean
		public IRuntimeEnvironmentQueryFacade remFacade() {
			return new RuntimeEnvironmentQueryImpl();
		}

		@Bean
		public CentralVsumFacade vsumFacade() {
			return new CentralVsumFacade();
		}

		@Bean
		public PCMValidationPointMatcher valPointMatcher() {
			return new PCMValidationPointMatcher();
		}

		@Bean
		public HeadlessPCMSimulator simulator() {
			return new HeadlessPCMSimulator();
		}

		@Bean
		public ValidationDataExtractor extractor() {
			return new ValidationDataExtractor();
		}

		@Bean
		public MonitoringDataEnrichment enricher() {
			return new MonitoringDataEnrichment();
		}

		@Bean
		public IValidationMetric<?> generator1() {
			return new ServiceCallKSTestMetric();
		}

		@Bean
		public IValidationMetric<?> generator2() {
			return new ServiceCallAverageDistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator3() {
			return new ServiceCallAverageDistanceRelative();
		}

		@Bean
		public IValidationMetric<?> generator4() {
			return new ServiceCallMedianDistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator5() {
			return new ServiceCallWassersteinDistanceMetric();
		}

		@Bean
		public IValidationMetric<?> generator6() {
			return new ServiceCallQ1DistanceAbsolute();
		}

		@Bean
		public IValidationMetric<?> generator7() {
			return new ServiceCallQ3DistanceAbsolute();
		}
	}

}
