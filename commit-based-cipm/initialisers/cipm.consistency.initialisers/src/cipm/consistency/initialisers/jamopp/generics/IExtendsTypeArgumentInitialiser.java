package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.ExtendsTypeArgument;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * create {@link ExtendsTypeArgument} instances. <br>
 * <br>
 * For an {@link ExtendsTypeArgument} instance eta,
 * {@code eta.getExtendTypes().add(...)} does not modify eta.
 * 
 * @author Alp Torac Genc
 */
public interface IExtendsTypeArgumentInitialiser extends IAnnotableInitialiser, ITypeArgumentInitialiser {
	@Override
	public ExtendsTypeArgument instantiate();

	/**
	 * Sets the extend type of the given {@link ExtendsTypeArgument} to extType.
	 * Uses {@code eta.setExtendType(...)} to do so.
	 * 
	 * @see {@link IExtendsTypeArgumentInitialiser}
	 */
	public default boolean setExtendType(ExtendsTypeArgument eta, TypeReference extType) {
		eta.setExtendType(extType);
		return (extType == null && eta.getExtendType() == null)
				|| eta.getExtendType().equals(extType) && eta.getExtendTypes().contains(extType);
	}
}
