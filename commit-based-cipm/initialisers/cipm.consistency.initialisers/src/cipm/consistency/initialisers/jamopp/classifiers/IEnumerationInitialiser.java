package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.Enumeration;
import org.emftext.language.java.members.EnumConstant;

public interface IEnumerationInitialiser extends IConcreteClassifierInitialiser, IImplementorInitialiser {
	@Override
	public Enumeration instantiate();

	public default boolean addConstant(Enumeration enm, EnumConstant cnst) {
		if (cnst != null) {
			enm.getConstants().add(cnst);
			return enm.getConstants().contains(cnst) && enm.getContainedConstant(cnst.getName()).equals(cnst);
		}
		return true;
	}

	public default boolean addConstants(Enumeration enm, EnumConstant[] cnsts) {
		return this.doMultipleModifications(enm, cnsts, this::addConstant);
	}
}
