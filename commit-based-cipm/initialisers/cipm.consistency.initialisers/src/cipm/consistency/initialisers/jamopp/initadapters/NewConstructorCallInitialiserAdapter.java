package cipm.consistency.initialisers.jamopp.initadapters;

import org.emftext.language.java.instantiations.NewConstructorCall;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;
import cipm.consistency.initialisers.jamopp.classifiers.IClassifierInitialiser;
import cipm.consistency.initialisers.jamopp.instantiations.INewConstructorCallInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypeReferenceInitialiser;

/**
 * An {@link IInitialiserAdapterStrategy} implementation that can be used with
 * {@link IInitialiserBase} implementors that instantiate
 * {@link NewConstructorCall}. <br>
 * <br>
 * Adds a {@link TypeReference} to the created {@link NewConstructorCall}
 * <i>ncc</i>. The steps it takes are:
 * <ol>
 * <li>Checks whether <i>ncc</i> has a {@link TypeReference}. If yes, stops.
 * Otherwise continues.
 * <li>Creates a {@link Classifier} instance <i>cls</i>
 * <li>Creates a {@link TypeReference} instance <i>tref</i>
 * <li>Sets <i>tref's</i> target to <i>cls</i>
 * <li>Sets <i>ncc's</i> type reference to <i>tref</i>
 * </ol>
 * Does not modify the created {@link NewConstructorCall}, if it already has a
 * {@link TypeReference}.
 * 
 * @author Alp Torac Genc
 *
 */
public class NewConstructorCallInitialiserAdapter implements IInitialiserAdapterStrategy {

	/**
	 * The initialiser responsible for creating {@link TypeReference} to fulfil this
	 * instance's functionality.
	 */
	private ITypeReferenceInitialiser tRefInit;
	/**
	 * The initialiser responsible for creating {@link Classifier} to fulfil this
	 * instance's functionality.
	 */
	private IClassifierInitialiser clsInit;

	/**
	 * Constructs an instance with the given parameters.
	 */
	public NewConstructorCallInitialiserAdapter(ITypeReferenceInitialiser tRefInit, IClassifierInitialiser clsInit) {
		this.tRefInit = tRefInit;
		this.clsInit = clsInit;
	}

	/**
	 * @return The initialiser responsible for creating {@link TypeReference}.
	 */
	public ITypeReferenceInitialiser gettRefInit() {
		return tRefInit;
	}

	/**
	 * @return The initialiser responsible for creating {@link Classifier}.
	 */
	public IClassifierInitialiser getClsInit() {
		return clsInit;
	}

	@Override
	public boolean apply(IInitialiser init, Object obj) {
		var castedInit = (INewConstructorCallInitialiser) init;
		var castedO = (NewConstructorCall) obj;

		if (castedO.getTypeReference() == null) {
			var cls = this.getClsInit().instantiate();

			var tref = this.gettRefInit().instantiate();

			return this.getClsInit().initialise(cls) && this.gettRefInit().initialise(tref)
					&& this.gettRefInit().setTarget(tref, cls) && castedInit.setTypeReference(castedO, tref)
					&& castedO.getTypeReference().equals(tref);
		}

		return true;
	}

	@Override
	public IInitialiserAdapterStrategy newStrategy() {
		return new NewConstructorCallInitialiserAdapter((ITypeReferenceInitialiser) this.gettRefInit().newInitialiser(),
				(IClassifierInitialiser) this.getClsInit().newInitialiser());
	}
}
