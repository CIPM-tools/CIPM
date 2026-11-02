package cipm.consistency.performance.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.cpr.javapcm.CommitIntegrationJavaPCMChangePropagationSpecification;
import cipm.consistency.performance.tests.testutil.CorrespondenceModelViews;
import cipm.consistency.performance.tests.testutil.UnsupportedResourceAccess;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Tests the timing wrapper added around {@link CommitIntegrationJavaPCMChangePropagationSpecification#propagateChange}.
 * Exercising the real Java-to-PCM transformation rules is out of scope here (that's what the
 * TEAMMATES end-to-end run already covers); this only checks that the stopwatch around
 * {@code super.propagateChange} fires correctly even when the change matches none of them.
 */
class CommitIntegrationJavaPCMChangePropagationSpecificationTest {

	@BeforeEach
	void resetGlobalState(@TempDir Path tempDir) {
		// EvaluationDataContainer is process-global static state; every test must start clean.
		EvaluationDataContainer.set(new EvaluationDataContainer());

		// The spec's setup() reads this singleton at construction time with no null-guard.
		// A path that does not exist is fine - the container just starts with empty properties.
		CommitIntegrationSettingsContainer.initialize(tempDir.resolve("does-not-exist.properties"));
	}

	@Test
	void propagateChange_changeMatchesNoTransformationRule_recordsChangePropagationTimeOnly(@TempDir Path tempDir) {
		CommitIntegrationJavaPCMChangePropagationSpecification spec =
				new CommitIntegrationJavaPCMChangePropagationSpecification();
		EditableCorrespondenceModelView<Correspondence> correspondenceModel =
				CorrespondenceModelViews.newEditableView(tempDir);
		// A change of a kind none of the Java-to-PCM rules, nor the method-body preprocessor,
		// recognize - exercises the safe no-match path on both timers.
		EChange change = AttributeFactory.eINSTANCE.createInsertEAttributeValue();
		String changeType = change.getClass().getSimpleName();

		assertDoesNotThrow(() -> spec.propagateChange(change, correspondenceModel, new UnsupportedResourceAccess()));

		var executionTimes = EvaluationDataContainer.get().getExecutionTimes();
		var recorded = executionTimes.getChangePropagationTimePerChangeType().get(changeType);
		// Real production code just ran and was really timed - the numbers are tiny only
		// because the input is deliberately the cheapest possible one (see the class Javadoc).
		// Representative real-world numbers come from the TEAMMATES run's archived JSON instead.
		System.out.println("changePropagationTimePerChangeType[\"" + changeType + "\"]: count=" + recorded.getCount()
				+ ", totalTime=" + recorded.getTotalTime() + "ns, maxTime=" + recorded.getMaxTime() + "ns");

		assertTrue(executionTimes.getChangePropagationTimePerChangeType().containsKey(changeType));
		assertEquals(1, recorded.getCount());
		assertTrue(executionTimes.getBodyTransformationTimePerChangeType().isEmpty());
	}

	@Test
	void propagateChange_calledTwiceForTheSameChangeType_accumulatesIntoOneAggregate(@TempDir Path tempDir) {
		CommitIntegrationJavaPCMChangePropagationSpecification spec =
				new CommitIntegrationJavaPCMChangePropagationSpecification();
		EditableCorrespondenceModelView<Correspondence> correspondenceModel =
				CorrespondenceModelViews.newEditableView(tempDir);
		EChange firstChange = AttributeFactory.eINSTANCE.createInsertEAttributeValue();
		EChange secondChange = AttributeFactory.eINSTANCE.createInsertEAttributeValue();
		String changeType = firstChange.getClass().getSimpleName();

		spec.propagateChange(firstChange, correspondenceModel, new UnsupportedResourceAccess());
		spec.propagateChange(secondChange, correspondenceModel, new UnsupportedResourceAccess());

		var executionTimes = EvaluationDataContainer.get().getExecutionTimes();
		assertEquals(2, executionTimes.getChangePropagationTimePerChangeType().get(changeType).getCount());
	}
}
