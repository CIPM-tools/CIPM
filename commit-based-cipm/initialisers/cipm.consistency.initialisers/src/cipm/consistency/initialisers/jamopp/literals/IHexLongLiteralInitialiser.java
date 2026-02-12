package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.HexLongLiteral;

public interface IHexLongLiteralInitialiser extends ILongLiteralInitialiser {
	@Override
	public HexLongLiteral instantiate();

	public default boolean setHexValue(HexLongLiteral hll, BigInteger val) {
		hll.setHexValue(val);
		return (val == null && hll.getHexValue() == null) || hll.getHexValue().equals(val);
	}

	public default boolean setHexValue(HexLongLiteral hll, long val) {
		return this.setHexValue(hll, BigInteger.valueOf(val));
	}
}
