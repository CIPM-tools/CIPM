package cipm.consistency.initialisers.jamopp.commons;

import org.emftext.language.java.commons.NamedElement;

/**
 * An interface meant to be implemented by initialisers, which are meant to
 * create {@link NamedElement} instances. <br>
 * <br>
 * Since some {@link NamedElement} implementations' name attribute cannot be
 * modified as expected, {@link #canSetName(NamedElement)} can be used to
 * determine the modifiability of the name attribute in implementors.
 * 
 * @author Alp Torac Genc
 */
public interface INamedElementInitialiser extends ICommentableInitialiser {
	@Override
	public NamedElement instantiate();

	/**
	 * Sets the name attribute of the given element to the given name, if its name
	 * can be set. <br>
	 * <br>
	 * Note: If {@code name == null}, the return value will be true, regardless of
	 * the name attribute being modifiable.
	 * 
	 * @see {@link #canSetName(NamedElement)}
	 */
	public default boolean setName(NamedElement ne, String name) {
		if (!this.canSetName(ne)) {
			return false;
		}
		ne.setName(name);
		return (name == null && ne.getName() == null) || ne.getName().equals(name);
	}

	/**
	 * Extracted from {@link #setName(NamedElement, String)} because there are
	 * implementors, whose name cannot be modified via
	 * {@link #setName(NamedElement, String)}. This way, such implementors can
	 * override this method to indicate, whether their name attribute can be
	 * modified.
	 * 
	 * @return Whether {@link #setName(NamedElement, String)} can be used to modify
	 *         the given {@link NamedElement} instance ne.
	 */
	public default boolean canSetName(NamedElement ne) {
		return true;
	}
}
