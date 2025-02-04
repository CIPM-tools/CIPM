package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.HexDoubleLiteral;

public interface IHexDoubleLiteralInitialiser extends IDoubleLiteralInitialiser {
	@Override
	public HexDoubleLiteral instantiate();

	public default boolean setHexValue(HexDoubleLiteral hdl, double val) {
		hdl.setHexValue(val);
		return hdl.getHexValue() == val || (Double.isNaN(hdl.getHexValue()) && Double.isNaN(val));
	}
}
