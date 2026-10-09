package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.ClassMethod;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Adds a {@link Block} instance to the {@link ClassMethod} via
 * {@code classMethod.setStatement(...)}, if its statement accessed via
 * {@code classMethod.getStatement()} is not a {@link Block} (in that case
 * {@code classMethod.getBlock()} will return null). If the {@link ClassMethod}
 * has its {@link Statement} st set, st will be added to the added block
 * instance. <br>
 * <br>
 * Due to inconsistencies regarding {@link ClassMethod}, it provides 2 methods
 * for adding {@link Statement} instances to it. Since adding multiple
 * statements is only possible via {@code classMethod.getStatement().add(...)},
 * which only works if classMethod has a block as its statement, this adaptation
 * is necessary. Otherwise, one may only add a single statement to
 * {@link ClassMethod} via {@code classMethod.setStatement(...)}. <br>
 * <br>
 * <b>Note that the said method will REPLACE the former statement when used.
 * </b>
 * 
 * @author Alp Torac Genc
 *
 */
public class ClassMethodInitialiserAdapter implements IEObjectAdaptationStrategy {
	@Override
	public boolean apply(EObject obj) {
		var castedO = (ClassMethod) obj;

		if (castedO.getBlock() == null) {
			var formerSt = castedO.getStatement();

			var block = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewBlock();
			castedO.setStatement(block);

			if (formerSt != null) {
				castedO.getStatements().add(formerSt);
			}

			return block.eContainer() == castedO && (formerSt == null || formerSt.eContainer() == block);
		}

		return true;
	}
}
