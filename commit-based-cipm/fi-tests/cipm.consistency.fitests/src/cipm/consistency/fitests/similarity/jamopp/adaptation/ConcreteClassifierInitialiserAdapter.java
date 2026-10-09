package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Adds the {@link ConcreteClassifier} instance to a {@link CompilationUnit}.
 * Does not modify the {@link ConcreteClassifier} instance, if it already is
 * contained in a {@link CompilationUnit}. This way, similarity checking 2
 * {@link ConcreteClassifier} instances does not throw exceptions, due to them
 * not having a container.
 * 
 * @author Alp Torac Genc
 */
public class ConcreteClassifierInitialiserAdapter implements IEObjectAdaptationStrategy {
	@Override
	public boolean apply(EObject obj) {
		var castedO = (ConcreteClassifier) obj;

		if (castedO.getContainingCompilationUnit() == null) {
			var cu = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewCompilationUnit();
			cu.getClassifiers().add(castedO);
			return castedO.eContainer() == cu;
		}

		return true;
	}
}
