package cipm.consistency.performance.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.lang.java.JavaStateBasedChangeResolutionStrategy;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

/**
 * Tests the two timers added around {@link JavaStateBasedChangeResolutionStrategy}'s internal
 * {@code compareStatesAndReplayChanges} - which is fully {@code private}, so there is no seam
 * to call it directly. Instead, this drives it through the public
 * {@code getChangeSequenceForCreated(Resource)}, which already calls it with {@code null} for
 * both resource lists in production (3 of its 5 public callers do this today) - the cheapest
 * real input is an empty resource, exercising the same "a file was newly created" path.
 */
class JavaStateBasedChangeResolutionStrategyTest {

	private final JavaStateBasedChangeResolutionStrategy strategy = new JavaStateBasedChangeResolutionStrategy();

	@BeforeAll
	static void registerJavaResourceFactory() {
		// In a full IDE/TEAMMATES launch, a bundle's Activator registers a ".java" resource
		// factory as a side effect of starting up. Nothing does that in this headless,
		// useUIHarness=false test run, so ResourceSet#createResource(...) would otherwise
		// silently return null for a ".java" URI instead of throwing.
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new ResourceFactoryImpl());
	}

	@BeforeEach
	void resetGlobalState() {
		// EvaluationDataContainer is process-global static state; every test must start clean.
		EvaluationDataContainer.set(new EvaluationDataContainer());
	}

	@Test
	void getChangeSequenceForCreated_emptyResource_recordsBothComparisonAndReplayTimings() {
		// A plain relative URI, not "scheme://empty.java" - a double slash would put
		// "empty.java" in the URI's authority component instead of its path, and EMF's
		// extension-based factory lookup only looks at the last path segment.
		Resource emptyResource = new ResourceSetImpl().createResource(URI.createURI("empty.java"));

		strategy.getChangeSequenceForCreated(emptyResource);

		var fineGrainedTimings = EvaluationDataContainer.get().getExecutionTimes().getFineGrainedTimings();
		var comparison = fineGrainedTimings.get("javaModelComparison");
		var replay = fineGrainedTimings.get("changeReplay");
		// Real EMF Compare/merge calls just ran and were really timed - tiny numbers only
		// because the input is an empty resource (see the class Javadoc). Representative
		// real-world numbers come from the TEAMMATES run's archived JSON instead.
		System.out.println("javaModelComparison: count=" + comparison.getCount() + ", totalTime="
				+ comparison.getTotalTime() + "ms, maxTime=" + comparison.getMaxTime() + "ms");
		System.out.println("changeReplay: count=" + replay.getCount() + ", totalTime=" + replay.getTotalTime()
				+ "ms, maxTime=" + replay.getMaxTime() + "ms");

		assertTrue(fineGrainedTimings.containsKey("javaModelComparison"));
		assertEquals(1, comparison.getCount());
		assertTrue(fineGrainedTimings.containsKey("changeReplay"));
		assertEquals(1, replay.getCount());
	}
}
