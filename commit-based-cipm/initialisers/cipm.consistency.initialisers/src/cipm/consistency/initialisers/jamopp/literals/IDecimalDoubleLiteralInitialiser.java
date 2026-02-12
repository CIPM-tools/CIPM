package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalDoubleLiteral;

public interface IDecimalDoubleLiteralInitialiser extends IDoubleLiteralInitialiser {
	@Override
	public DecimalDoubleLiteral instantiate();

	public default boolean setDecimalValue(DecimalDoubleLiteral ddl, double val) {
		ddl.setDecimalValue(val);
		return ddl.getDecimalValue() == val || (Double.isNaN(ddl.getDecimalValue()) && Double.isNaN(val));
	}
}
