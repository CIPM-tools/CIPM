package cipm.consistency.fluentapi.extensions;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * The extension class of fluent api that stores Initialisation instances that
 * are created within the fluent api and are actively being used to create model
 * elements. Since the fluent api model is EMF-based, implementing static
 * attributes in its EClasses is challenging. Making the attribute containing
 * initialisation instances static is important, since all fluent api instances
 * should have access to all ongoing initialisation instances (i.e.
 * initialisation instances that are actively being used), hence the
 * initialisation storing logic is moved to this class. This class does not
 * allow duplicated Initialisation instances.
 * <p>
 * <p>
 * The methods within this class are meant for Initialisation instances,
 * although they take parameters of type EObject. This is due to the fact that
 * fluent api model is generated dynamically (hence the Initialisation classes
 * are not guaranteed to exist at this time).
 * <p>
 * <p>
 * Note: Changing any public member within this file (i.e. either this class or
 * its methods) requires adapting the generation of fluent api. This is due to
 * Java limitations, which do not allow dynamically adjusting static elements,
 * such as method or class names.
 * 
 * @author Alp Torac Genc
 */
public final class FluentAPIInitialisationStorage {
	private static final Collection<EObject> ongoingInits = new LinkedHashSet<>();

	/**
	 * @return An unmodifiable list of all ongoing initialisations.
	 */
	public static List<EObject> getOngoingInitialisations() {
		return List.copyOf(ongoingInits);
	}

	/**
	 * Removes the given Initialisation instance from this class
	 * 
	 * @param init A given Initialisation instance, potentially stored in this class
	 */
	public static void dropOngoingInitialisation(EObject init) {
		ongoingInits.remove(init);
	}

	/**
	 * Adds the given Initialisation instance to this class. Does nothing if init
	 * has already been added previously.
	 * 
	 * @param init A given Initialisation instance
	 */
	public static void addOngoingInitialisation(EObject init) {
		ongoingInits.add(init);
	}

	/**
	 * Removes all stored Initialisation instances from this class
	 */
	public static void clearAllOngoingInitialisations() {
		ongoingInits.clear();
	}
}
