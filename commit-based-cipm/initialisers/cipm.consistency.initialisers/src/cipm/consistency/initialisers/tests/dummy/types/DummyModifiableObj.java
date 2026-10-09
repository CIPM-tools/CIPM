package cipm.consistency.initialisers.tests.dummy.types;

import java.util.HashSet;
import java.util.Set;

/**
 * A dummy object, to which modifiable attributes can be added. The said
 * attributes are then stored in a {@link Set}.
 * 
 * @author Alp Torac Genc
 */
public class DummyModifiableObj {
	/**
	 * A set of modifiable attributes.
	 */
	private final Set<Object> someModifiableAttrCol = new HashSet<Object>();

	/**
	 * @param attrToAdd An attribute to be added to this
	 * @return Whether the given attibute was added successfully
	 */
	public boolean addAttr(Object attrToAdd) {
		return attrToAdd != null && this.someModifiableAttrCol.add(attrToAdd);
	}

	/**
	 * @return A set containing all attributes stored in this. Modifying the
	 *         returned attributes will affect the attributes stored in this.
	 *         Modifying the returned set will not affect the stored values,
	 *         however.
	 */
	public Set<Object> getAttrs() {
		var attrs = new HashSet<Object>();
		this.someModifiableAttrCol.forEach((attr) -> attrs.add(attr));
		return attrs;
	}
}