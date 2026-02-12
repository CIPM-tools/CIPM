package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;
import cipm.consistency.initialisers.jamopp.classifiers.IClassifierInitialiser;

/**
 * An interface meant for {@link IInitialiser} implementors that are supposed to
 * create {@link TypeParameter} instances. <br>
 * <br>
 * <b>Note: {@link TypeParameter} cannot add imports to its container, so
 * attempting to add imports to it has no effect. This is caused by the
 * inconsistency in the {@link Classifier} sub-hierarchy.</b>
 * 
 * @author Alp Torac Genc
 *
 */
public interface ITypeParameterInitialiser extends IClassifierInitialiser, IAnnotableInitialiser {
	@Override
	public TypeParameter instantiate();

	public default boolean addExtendType(TypeParameter tp, TypeReference extType) {
		if (extType != null) {
			tp.getExtendTypes().add(extType);
			return tp.getExtendTypes().contains(extType);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return Returns false in case of {@link TypeParameter}.
	 * 
	 * @see {@link ITypeParameterInitialiser}
	 */
	@Override
	public default boolean canAddImports(Classifier cls) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return Returns false in case of {@link TypeParameter}.
	 * 
	 * @see {@link ITypeParameterInitialiser}
	 */
	@Override
	public default boolean canAddPackageImports(Classifier cls) {
		return false;
	}

	public default boolean addExtendTypes(TypeParameter tp, TypeReference[] extTypes) {
		return this.doMultipleModifications(tp, extTypes, this::addExtendType);
	}
}
