package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.DecimalIntegerLiteral;

public interface IDecimalIntegerLiteralInitialiser extends IIntegerLiteralInitialiser {
	@Override
	public DecimalIntegerLiteral instantiate();

	public default boolean setDecimalValue(DecimalIntegerLiteral dil, BigInteger val) {
		dil.setDecimalValue(val);
		return (val == null && dil.getDecimalValue() == null) || dil.getDecimalValue().equals(val);
	}

	public default boolean setDecimalValue(DecimalIntegerLiteral dil, int val) {
		return this.setDecimalValue(dil, BigInteger.valueOf(val));
	}
}
