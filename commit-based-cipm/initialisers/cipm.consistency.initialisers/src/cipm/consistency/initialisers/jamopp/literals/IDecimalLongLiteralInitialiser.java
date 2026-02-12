package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.DecimalLongLiteral;

public interface IDecimalLongLiteralInitialiser extends ILongLiteralInitialiser {
	@Override
	public DecimalLongLiteral instantiate();

	public default boolean setDecimalValue(DecimalLongLiteral dll, BigInteger val) {
		dll.setDecimalValue(val);
		return (val == null && dll.getDecimalValue() == null) || dll.getDecimalValue().equals(val);
	}

	public default boolean setDecimalValue(DecimalLongLiteral dll, long val) {
		return this.setDecimalValue(dll, BigInteger.valueOf(val));
	}
}
