package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.statements.Block;

public class BlockInitialiser extends AbstractInitialiserBase implements IBlockInitialiser {
	@Override
	public Block instantiate() {
		return StatementsFactory.eINSTANCE.createBlock();
	}

	@Override
	public BlockInitialiser newInitialiser() {
		return new BlockInitialiser();
	}
}