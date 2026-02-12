package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.arrays.IArrayTypeableInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * instantiate {@link TypeReference}. <br>
 * <br>
 * {@link TypeReference} itself does not have the target attribute, yet its
 * implementors do. Its implementors' relations to that attribute vary.
 * Therefore, whether the said attribute is modifiable in the individual
 * implementors and the acceptable target types differ. Because of this, 2
 * methods {@link #canSetTarget(TypeReference)} and
 * {@link #canSetTargetTo(TypeReference, Classifier)} are provided.
 * 
 * @author Alp Torac Genc
 */
public interface ITypeReferenceInitialiser extends IArrayTypeableInitialiser {
	@Override
	public TypeReference instantiate();

	/**
	 * Since {@link TypeReference} itself does not have the "target" attribute and
	 * its implementors' relations to that attribute vary, this method is
	 * implemented as a template. <br>
	 * <br>
	 * Therefore, <b>what this method does can change immensely between different
	 * implementors</b>. Check the concrete implementations within implementors for
	 * further details. <br>
	 * <br>
	 * Attempting to set null as target will return true, regardless of the concrete
	 * implementor, since there is no modification to perform.
	 * 
	 * @see {@link #canSetTarget(TypeReference)}
	 * @see {@link #canSetTargetTo(TypeReference, Classifier)}
	 * @see {@link #setTargetAssertion(TypeReference, Classifier)}
	 */
	public default boolean setTarget(TypeReference tref, Classifier target) {
		if (target != null && !this.canSetTargetTo(tref, target)) {
			return false;
		}

		tref.setTarget(target);
		return this.setTargetAssertion(tref, target);
	}

	/**
	 * @return Whether calling {@link #setTarget(TypeReference, Classifier)} with
	 *         the given parameters results in the expected behaviour.
	 */
	public default boolean setTargetAssertion(TypeReference tref, Classifier target) {
		var cTarget = tref.getTarget();
		return (target == null && cTarget == null) || cTarget.equals(target);
	}

	/**
	 * @return Whether {@code tref.setTarget(...)} method can be used to modify
	 *         tref.
	 */
	public boolean canSetTarget(TypeReference tref);

	/**
	 * Expects {@link #canSetTarget(TypeReference)} to return true for tref, since
	 * otherwise its target attribute cannot be changed in the first place.
	 * 
	 * @return Whether {@code tref.setTarget(target)} method can be used in the
	 *         context of the given target parameter. Returns false if
	 *         {@code target == null}.
	 */
	public default boolean canSetTargetTo(TypeReference tref, Classifier target) {
		return target != null && this.canSetTarget(tref);
	}
}
