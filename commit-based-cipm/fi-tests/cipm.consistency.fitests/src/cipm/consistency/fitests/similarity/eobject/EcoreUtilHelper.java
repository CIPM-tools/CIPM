package cipm.consistency.fitests.similarity.eobject;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A helper class that contains utility methods for {@link EObject}. This class
 * encapsulates such methods, so that other classes can spare dependencies to
 * various {@link EObject} related packages. <br>
 * <br>
 * Methods in this class include various cloning methods for {@link EObject}
 * instances. Furthermore, there are methods that compare {@link EObject}
 * instances at meta-metamodel level, without any other context (all their
 * contents and attributes are checked).
 * 
 * @author Alp Torac Genc
 * @see {@link EcoreUtil}
 */
public class EcoreUtilHelper {
	private static final Logger LOGGER = Logger.getLogger("cipm." + EcoreUtilHelper.class.getSimpleName());

	/**
	 * Creates a clone copy of the given obj and its contents. <br>
	 * <br>
	 * <b>Note: DOES NOT clone the container {@code obj.eContainer()} of this
	 * object. Only copies the given object and the contents nested in it.</b>
	 * 
	 * @return A clone of obj without its container and clones of its contents.
	 * @see {@link EcoreUtil#copy(EObject)}
	 */
	public <T extends EObject> T cloneEObj(T obj) {
		return EcoreUtil.copy(obj);
	}

	/**
	 * Finds the topmost EObject (objTop) that can be reached from {@code obj},
	 * clones objTop, finds the clone of {@code obj} among the contents of objTop
	 * and returns that clone.
	 * 
	 * @return A clone of obj, which preserves obj's place in its hierarchy. The
	 *         returned obj clone contains clones of original obj's contents. All
	 *         objects containing obj are also implicitly cloned, so that obj
	 *         clone's position (among object clones) matches to the original obj's
	 *         position (among original objects).
	 * @see {@link EcoreUtil#copy(EObject)}
	 */
	@SuppressWarnings("unchecked")
	public <T extends EObject> T cloneEObjWithContainers(T obj) {
		if (obj.eContainer() == null) {
			return this.cloneEObj(obj);
		}

		EObject cObj = obj;

		while (cObj.eContainer() != null) {
			cObj = cObj.eContainer();
		}

		EObject clone = this.cloneEObj(cObj);
		var contents = clone.eAllContents();

		while (contents.hasNext()) {
			var cCloneObj = contents.next();
			if (this.getActualEquality(obj, cCloneObj)) {
				return (T) cCloneObj;
			}
		}

		LOGGER.error("Cloning EObject with cloneEObjWithContainers failed");
		return null;
	}

	/**
	 * Creates a clone copy of all given objs. <br>
	 * <br>
	 * <b>Note: DOES NOT clone any containers {@code obj.eContainer()} of the
	 * objects. Only copies the given objects and the contents nested in them.</b>
	 * 
	 * @see {@link EcoreUtil#copyAll(Collection)}
	 */
	public <T extends EObject> Collection<T> cloneEObjList(Collection<T> objs) {
		return EcoreUtil.copyAll(objs);
	}

	/**
	 * Computes the equality of two {@link EObject} instances using
	 * {@link EcoreUtil}. <br>
	 * <br>
	 * <b>Note: The equality here is not necessarily the same as similarity checking
	 * that is being tested. This form of equality is much stricter than similarity,
	 * since there might be some differences in attributes and/or nested content,
	 * which are irrelevant for similarity in certain cases.</b>
	 */
	public boolean getActualEquality(EObject elem1, EObject elem2) {
		return EcoreUtil.equals(elem1, elem2);
	}

	/**
	 * Computes the equality of two lists of {@link EObject} using
	 * {@link EcoreUtil}. <br>
	 * <br>
	 * <b>Note: The equality here is not necessarily the same as similarity checking
	 * that is being tested. This form of equality is much stricter than similarity,
	 * since there might be some differences in attributes and/or nested content,
	 * which are irrelevant for similarity in certain cases.</b>
	 */
	public boolean getActualEquality(List<? extends EObject> elems1, List<? extends EObject> elems2) {
		return EcoreUtil.equals(elems1, elems2);
	}
}
