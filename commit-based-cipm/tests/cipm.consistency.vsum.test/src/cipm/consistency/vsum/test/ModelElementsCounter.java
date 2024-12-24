package cipm.consistency.vsum.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.resource.Resource;

public final class ModelElementsCounter {
	private ModelElementsCounter() {
	}
	
	public static int countModelElements(Resource resource) {
		AtomicInteger counter = new AtomicInteger();
		resource.getAllContents().forEachRemaining(o -> counter.incrementAndGet());
		return counter.get();
	}
}
