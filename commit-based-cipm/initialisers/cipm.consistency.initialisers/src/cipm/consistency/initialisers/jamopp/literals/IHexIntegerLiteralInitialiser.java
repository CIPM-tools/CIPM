package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.HexIntegerLiteral;

public interface IHexIntegerLiteralInitialiser extends IIntegerLiteralInitialiser {
	@Override
	public HexIntegerLiteral instantiate();

	public default boolean setHexValue(HexIntegerLiteral hil, BigInteger val) {
		hil.setHexValue(val);
		return (val == null && hil.getHexValue() == null) || hil.getHexValue().equals(val);
	}

	public default boolean setHexValue(HexIntegerLiteral hil, int val) {
		return this.setHexValue(hil, BigInteger.valueOf(val));
	}
}
