package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.OctalIntegerLiteral;

public interface IOctalIntegerLiteralInitialiser extends IIntegerLiteralInitialiser {
	@Override
	public OctalIntegerLiteral instantiate();

	public default boolean setOctalValue(OctalIntegerLiteral oil, BigInteger val) {
		oil.setOctalValue(val);
		return (val == null && oil.getOctalValue() == null) || oil.getOctalValue().equals(val);
	}

	public default boolean setOctalValue(OctalIntegerLiteral oil, int val) {
		return this.setOctalValue(oil, BigInteger.valueOf(val));
	}
}
