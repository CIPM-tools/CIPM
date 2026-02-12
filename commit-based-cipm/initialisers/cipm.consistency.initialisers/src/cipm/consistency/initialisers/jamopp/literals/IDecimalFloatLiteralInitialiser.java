package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalFloatLiteral;

public interface IDecimalFloatLiteralInitialiser extends IFloatLiteralInitialiser {
	@Override
	public DecimalFloatLiteral instantiate();

	public default boolean setDecimalValue(DecimalFloatLiteral dfl, float val) {
		dfl.setDecimalValue(val);
		return dfl.getDecimalValue() == val || (Float.isNaN(dfl.getDecimalValue()) && Float.isNaN(val));
	}
}
