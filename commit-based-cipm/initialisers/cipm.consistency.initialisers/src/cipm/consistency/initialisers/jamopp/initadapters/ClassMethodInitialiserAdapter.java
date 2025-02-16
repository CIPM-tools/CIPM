package cipm.consistency.initialisers.jamopp.initadapters;

import org.emftext.language.java.members.ClassMethod;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;
import cipm.consistency.initialisers.jamopp.members.IClassMethodInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IBlockInitialiser;

/**
 * An {@link IInitialiserAdapterStrategy} implementation that can be used with
 * {@link IInitialiserBase} implementors that instantiate {@link ClassMethod}.
 * <br>
 * <br>
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
public class ClassMethodInitialiserAdapter implements IInitialiserAdapterStrategy {
	/**
	 * The initialiser that creates {@link Block}s to realise the functionality of
	 * this instance.
	 */
	private IBlockInitialiser bInit;

	/**
	 * Constructs an instance with the given {@link IBlockInitialiser}.
	 */
	public ClassMethodInitialiserAdapter(IBlockInitialiser bInit) {
		this.bInit = bInit;
	}

	/**
	 * @return The initialiser contained in this instance, which is responsible for
	 *         creating {@link Block}s.
	 */
	public IBlockInitialiser getBInit() {
		return this.bInit;
	}

	@Override
	public boolean apply(IInitialiser init, Object obj) {
		var castedInit = (IClassMethodInitialiser) init;
		var castedO = (ClassMethod) obj;

		if (castedO.getBlock() == null) {
			var formerSt = castedO.getStatement();

			var bInit = this.getBInit();
			var block = bInit.instantiate();

			var res = bInit.initialise(block) && castedInit.setStatement(castedO, block);

			if (formerSt != null) {
				res = res && castedInit.addStatement(castedO, formerSt);
			}

			return res;
		}

		return true;
	}

	@Override
	public IInitialiserAdapterStrategy newStrategy() {
		return new ClassMethodInitialiserAdapter((IBlockInitialiser) this.getBInit().newInitialiser());
	}
}
