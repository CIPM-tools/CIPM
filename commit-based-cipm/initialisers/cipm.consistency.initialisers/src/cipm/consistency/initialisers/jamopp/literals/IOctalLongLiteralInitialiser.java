package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.OctalLongLiteral;

public interface IOctalLongLiteralInitialiser extends ILongLiteralInitialiser {
	@Override
	public OctalLongLiteral instantiate();

	public default boolean setOctalValue(OctalLongLiteral oll, BigInteger val) {
		oll.setOctalValue(val);
		return (val == null && oll.getOctalValue() == null) || oll.getOctalValue().equals(val);
	}

	public default boolean setOctalValue(OctalLongLiteral oll, long val) {
		return this.setOctalValue(oll, BigInteger.valueOf(val));
	}
}
