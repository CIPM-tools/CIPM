package cipm.consistency.initialisers.jamopp.literals;

import java.math.BigInteger;

import org.emftext.language.java.literals.BinaryLongLiteral;

public interface IBinaryLongLiteralInitialiser extends ILongLiteralInitialiser {
	@Override
	public BinaryLongLiteral instantiate();

	public default boolean setBinaryValue(BinaryLongLiteral bil, BigInteger val) {
		bil.setBinaryValue(val);
		return (val == null && bil.getBinaryValue() == null) || bil.getBinaryValue().equals(val);
	}

	public default boolean setBinaryValue(BinaryLongLiteral bil, long val) {
		return this.setBinaryValue(bil, BigInteger.valueOf(val));
	}
}
