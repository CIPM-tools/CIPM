package cipm.consistency.vsum.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public final class ModelElementsCounter {
	private ModelElementsCounter() {
	}

	static ModelCountResult countModelElements(Resource resource) {
		return countModelElementsFromTreeIterator(resource.getAllContents());
	}
	
	public static ModelCountResult countModelElements(EObject eobj) {
		var subCount = countModelElementsFromTreeIterator(eobj.eAllContents());
		return new ModelCountResult(
			subCount.getNumberOfElements() + 1,
			subCount.getNumberOfContainmentReferences() + eobj.eContents().size(),
			subCount.getNumberOfNonContainmentReferences() + eobj.eCrossReferences().size()
		);
	}

	private static ModelCountResult countModelElementsFromTreeIterator(TreeIterator<EObject> iterator) {
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger containCounter = new AtomicInteger();
		AtomicInteger nonContainCounter = new AtomicInteger();

		iterator.forEachRemaining(eObj -> {
			counter.incrementAndGet();
			containCounter.addAndGet(eObj.eContents().size());
			nonContainCounter.addAndGet(eObj.eCrossReferences().size());
		});

		return new ModelCountResult(counter.get(), containCounter.get(), nonContainCounter.get());
	}
}
