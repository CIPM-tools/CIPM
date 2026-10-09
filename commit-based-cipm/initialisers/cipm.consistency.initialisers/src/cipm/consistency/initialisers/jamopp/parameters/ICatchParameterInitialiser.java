package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.CatchParameter;
import org.emftext.language.java.types.TypeReference;

public interface ICatchParameterInitialiser extends IOrdinaryParameterInitialiser {
	@Override
	public CatchParameter instantiate();

	public default boolean addTypeReference(CatchParameter cp, TypeReference tRef) {
		if (tRef != null) {
			cp.getTypeReferences().add(tRef);
			return cp.getTypeReferences().contains(tRef);
		}
		return true;
	}

	public default boolean addTypeReferences(CatchParameter cp, TypeReference[] tRefs) {
		return this.doMultipleModifications(cp, tRefs, this::addTypeReference);
	}
}
