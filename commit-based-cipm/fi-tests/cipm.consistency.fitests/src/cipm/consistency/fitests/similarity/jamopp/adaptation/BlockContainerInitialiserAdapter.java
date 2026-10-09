package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.statements.BlockContainer;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Adds a {@link Block} to the {@link BlockContainer}. If the
 * {@link BlockContainer} instance already has a {@link Block}, does not modify
 * it. This way, similarity checking should not cause exceptions while comparing
 * 2 {@link BlockContainer} instances, because of them not having a
 * {@link Block}.
 * 
 * @author Alp Torac Genc
 */
public class BlockContainerInitialiserAdapter implements IEObjectAdaptationStrategy {
	@Override
	public boolean apply(EObject obj) {
		var castedO = (BlockContainer) obj;

		if (castedO.getBlock() == null) {
			var block = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewBlock();
			castedO.setBlock(block);
			return block.eContainer() == castedO;
		}

		return true;
	}
}
