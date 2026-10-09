package cipm.consistency.initialisers.jamopp.initadapters;

import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;
import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;

/**
 * An {@link IInitialiserAdapterStrategy} implementation that can be used with
 * {@link IInitialiserBase} implementors that instantiate {@link Member}. <br>
 * <br>
 * Adds the created {@link Member} to a {@link MemberContainer}. Does not modify
 * the {@link Member} instance, if it is already in a {@link MemberContainer}.
 * This way, similarity checking 2 {@link Member} instances does not throw an
 * exception, due to them not being contained by a {@link MemberContainer}.
 * 
 * @author Alp Torac Genc
 *
 */
public class MemberInitialiserAdapter implements IInitialiserAdapterStrategy {
	/**
	 * The initialiser responsible for creating {@link MemberContainer}s to fulfil
	 * this instance's functionality.
	 */
	private IMemberContainerInitialiser mcInit;

	/**
	 * Constructs an instance with the given {@link IMemberContainerInitialiser}.
	 */
	public MemberInitialiserAdapter(IMemberContainerInitialiser mcInit) {
		this.mcInit = mcInit;
	}

	/**
	 * @return The initialiser in this instance that creates
	 *         {@link MemberContainer}s.
	 */
	public IMemberContainerInitialiser getMCInit() {
		return this.mcInit;
	}

	@Override
	public boolean apply(IInitialiser init, Object obj) {
		var castedO = (Member) obj;

		if (castedO.eContainer() == null) {
			var mcInit = this.getMCInit();

			MemberContainer mc = mcInit.instantiate();
			return mcInit.initialise(mc) && mcInit.addMember(mc, castedO);
		}

		return true;
	}

	@Override
	public MemberInitialiserAdapter newStrategy() {
		return new MemberInitialiserAdapter((IMemberContainerInitialiser) this.getMCInit().newInitialiser());
	}
}
