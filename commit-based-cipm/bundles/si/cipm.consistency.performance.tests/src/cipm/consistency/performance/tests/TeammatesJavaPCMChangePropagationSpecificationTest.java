package cipm.consistency.performance.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cipm.consistency.cpr.javapcm.teammates.TeammatesJavaPCMChangePropagationSpecification;
import cipm.consistency.performance.tests.testutil.CorrespondenceModelViews;
import cipm.consistency.performance.tests.testutil.UnsupportedResourceAccess;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Tests the timing wrapper added around {@link TeammatesJavaPCMChangePropagationSpecification#propagateChange}
 * - the sibling class actually exercised by the live TEAMMATES pipeline, instrumented after
 * discovering the original target class ({@code CommitIntegrationJavaPCMChangePropagationSpecification})
 * is dead code on that path. Same shape and same scope limits as that class's test; the only
 * difference is this class's setup() does not depend on the settings singleton, so no
 * CommitIntegrationSettingsContainer initialization is needed here.
 */
class TeammatesJavaPCMChangePropagationSpecificationTest {

	@BeforeEach
	void resetGlobalState() {
		// EvaluationDataContainer is process-global static state; every test must start clean.
		EvaluationDataContainer.set(new EvaluationDataContainer());
	}

	@Test
	void propagateChange_changeMatchesNoTransformationRule_recordsChangePropagationTimeOnly(@TempDir Path tempDir) {
		TeammatesJavaPCMChangePropagationSpecification spec = new TeammatesJavaPCMChangePropagationSpecification();
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
		TeammatesJavaPCMChangePropagationSpecification spec = new TeammatesJavaPCMChangePropagationSpecification();
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
