package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.BinaryIntegerLiteral;

public interface IBinaryIntegerLiteralInitialiser extends IIntegerLiteralInitialiser {
	@Override
	public BinaryIntegerLiteral instantiate();

	public default boolean setBinaryValue(BinaryIntegerLiteral bil, BigInteger val) {
		bil.setBinaryValue(val);
		return (val == null && bil.getBinaryValue() == null) || bil.getBinaryValue().equals(val);
	}

	public default boolean setBinaryValue(BinaryIntegerLiteral bil, int val) {
		return this.setBinaryValue(bil, BigInteger.valueOf(val));
	}
}
