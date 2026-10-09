package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.commons.NamedElement;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;

/**
 * Sets the name of the created {@link NamedElement} to a default name, if it
 * has no name. This way, similarity checking 2 {@link NamedElement} instances
 * does not throw exceptions, due to them not having a name.
 * 
 * @author Alp Torac Genc
 *
 */
public class NamedElementInitialiserAdapter implements IEObjectAdaptationStrategy {
	private static final String defaultName = "";

	/**
	 * @return The default name, to which the name of the created
	 *         {@link NamedElement} instances will be set.
	 */
	private String getDefaultName() {
		return defaultName;
	}

	@Override
	public boolean apply(EObject obj) {
		var castedO = (NamedElement) obj;

		if (castedO.getName() == null) {
			castedO.setName(getDefaultName());
			return castedO.getName().equals(getDefaultName());
		}

		return true;
	}
}
