package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.HexFloatLiteral;

public interface IHexFloatLiteralInitialiser extends IFloatLiteralInitialiser {
	@Override
	public HexFloatLiteral instantiate();

	public default boolean setHexValue(HexFloatLiteral hdl, float val) {
		hdl.setHexValue(val);
		return hdl.getHexValue() == val || (Float.isNaN(hdl.getHexValue()) && Float.isNaN(val));
	}
}
