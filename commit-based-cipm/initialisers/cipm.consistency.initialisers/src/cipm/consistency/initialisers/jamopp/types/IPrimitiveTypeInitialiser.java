package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * instantiate {@link PrimitiveType}. <br>
 * <br>
 * The target attribute of primitive types ({@code tref.getTarget()}) are (as
 * their name imply) primitives and thus not adjustable.
 * 
 * @author Alp Torac Genc
 */
public interface IPrimitiveTypeInitialiser extends IAnnotableInitialiser, ITypeInitialiser, ITypeReferenceInitialiser {
	@Override
	public PrimitiveType instantiate();

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * In the case of {@link PrimitiveType}, this method does nothing, since the
	 * target attribute of primitive types ({@code tref.getTarget()}) are (as their
	 * name imply) primitives and thus not adjustable. <br>
	 * <br>
	 * This method is overridden to provide commentary, it introduces no changes to
	 * its super version in {@link ITypeReferenceInitialiser}.
	 */
	@Override
	public default boolean setTarget(TypeReference tref, Classifier target) {
		return ITypeReferenceInitialiser.super.setTarget(tref, target);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IPrimitiveTypeInitialiser}
	 */
	@Override
	public default boolean canSetTarget(TypeReference tref) {
		return false;
	}
}
