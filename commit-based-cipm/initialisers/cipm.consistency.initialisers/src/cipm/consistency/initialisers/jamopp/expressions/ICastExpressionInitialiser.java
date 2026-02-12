package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.CastExpression;
import org.emftext.language.java.expressions.MultiplicativeExpressionChild;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * create {@link CastExpression} instances. <br>
 * <br>
 * For a {@link CastExpression} instance ce, {@code ce.getChild()} has the same
 * return value as {@code ce.getGeneralChild()}, it merely returns the child
 * attribute as {@link Expression} rather than
 * {@link MultiplicativeExpressionChild}. <br>
 * <br>
 * Similarly, {@code ce.setGeneralChild(...)} is equivalent to
 * {@code ce.setChild(...)}, just with a more general parameter type.
 * 
 * @author Alp Torac Genc
 */
public interface ICastExpressionInitialiser
		extends ITypedElementInitialiser, IUnaryModificationExpressionChildInitialiser {
	@Override
	public CastExpression instantiate();

	public default boolean addAdditionalBound(CastExpression ce, TypeReference additionalBounds) {
		if (additionalBounds != null) {
			ce.getAdditionalBounds().add(additionalBounds);
			return ce.getAdditionalBounds().contains(additionalBounds);
		}
		return true;
	}

	public default boolean addAdditionalBounds(CastExpression ce, TypeReference[] additionalBoundsArr) {
		return this.doMultipleModifications(ce, additionalBoundsArr, this::addAdditionalBound);
	}

	/**
	 * Adds the given child to ce. Uses {@code ce.setChild(...)} to do so.
	 * 
	 * @see {@link ICastExpressionInitialiser}
	 */
	public default boolean setChild(CastExpression ce, MultiplicativeExpressionChild child) {
		ce.setChild(child);
		return (child == null && ce.getChild() == null) || ce.getChild().equals(child);
	}
}
