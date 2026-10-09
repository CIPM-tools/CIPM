package cipm.consistency.initialisers.jamopp;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.emftext.language.java.JavaPackage;

/**
 * A utility class that provides information about EObjects used by JaMoPP, as
 * well as methods to access their types. There are further methods, which map
 * their types to the initialisers implemented in sub-packages. <br>
 * <br>
 * This class is intended to be used in tests, which ensure that all necessary
 * initialisers are implemented and can be accessed.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPHelper {
	/**
	 * A variant of {@link #getAllClasses(Predicate)} with no given predicate.
	 */
	public Collection<Class<? extends EObject>> getAllClasses() {
		return this.getAllClasses(null);
	}

	/**
	 * If the given predicate is null, does not filter the found types.
	 * 
	 * @return All types accessible under the sub-packages of {@link JavaPackage} in
	 *         form of {@link EClass}, whose instance class
	 *         {@code eClass.getInstanceClass()} will be in the return value, which
	 *         fulfill the given predicate.
	 */
	public Collection<Class<? extends EObject>> getAllClasses(Predicate<EClass> pred) {
		var res = new ArrayList<Class<? extends EObject>>();
		Predicate<EClass> predToUse = pred != null ? pred : (a) -> true;
		this.getAllEClasses().stream().filter(predToUse)
				.forEach((eCls) -> res.add(this.getInstanceClassOfEClassifier(eCls)));
		return res;
	}

	/**
	 * @return All {@link EClass}es accessible under the sub-packages of
	 *         {@link JavaPackage}.
	 */
	public Collection<EClass> getAllEClasses() {
		var res = new ArrayList<EClass>();
		var ePacs = JavaPackage.eINSTANCE.getESubpackages();
		ePacs.forEach((pac) -> pac.getEClassifiers().stream().filter((eClsf) -> eClsf instanceof EClass)
				.forEach((c) -> res.add((EClass) c)));
		return res;
	}

	protected <T extends EObject> Class<? extends EObject> getInstanceClassOfEObject(T obj) {
		return this.getInstanceClassOfEClassifier(obj.eClass());
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends EObject> getInstanceClassOfEClassifier(EClassifier eClsfier) {
		return (Class<? extends EObject>) eClsfier.getInstanceClass();
	}

	/**
	 * Finds the {@link EClass} corresponding to the given cls, whose instance class
	 * is equal to cls: {@code eCls.getInstanceClass().equals(cls)}.
	 * 
	 * @param cls The type of the Java element, whose {@link EClass} will be
	 *            returned, if cls is the type of a Java element.
	 * 
	 * @return The {@link EClass} corresponding to the class represented by cls.
	 *         Null, if no such {@link EClass} is found under {@link JavaPackage}.
	 */
	public EClass getEClassForJavaElement(Class<? extends EObject> cls) {
		var ePacs = JavaPackage.eINSTANCE.getESubpackages();
		for (var ePac : ePacs) {
			var eClss = ePac.getEClassifiers();
			for (var eCls : eClss) {
				if (eCls.getInstanceClass().equals(cls)) {
					return (EClass) eCls;
				}
			}
		}
		return null;
	}

	/**
	 * Finds the {@link EClass} corresponding to the given cls, whose instance
	 * class' concrete type is equal to cls.
	 * 
	 * @param cls The type of the concrete implementation of the Java element, whose
	 *            corresponding {@link EClass} will be returned.
	 * 
	 * @return The {@link EClass} corresponding to the interface type of cls. Null,
	 *         if no such {@link EClass} is found under {@link JavaPackage}. <b>Note
	 *         that the returned {@link EClass} will be from the interface of cls.
	 *         This means, if cls represents the type xImpl, the returned
	 *         {@link EClass} will belong to x.</b>
	 */
	public EClass getEClassForJavaElementImpl(Class<? extends EObject> cls) {
		var interfaceType = this.getInterfaceTypeForJavaElementImpl(cls);
		if (interfaceType != null) {
			return this.getEClassForJavaElement(interfaceType);
		}
		return null;
	}

	/**
	 * The interface {@code I} of the implementation of a Java element type
	 * {@code T} within JaMoPP is directly implemented by it and also contains its
	 * name, i.e.: <br>
	 * <br>
	 * T extends I directly and the simple name of T contains the simple name of I.
	 * T can neither be an interface nor abstract.
	 * 
	 * @param <T> The type of the passed parameter. Used to allow making assumptions
	 *            on the return value.
	 * @param cls The type of an implementation of a Java element within JaMoPP.
	 * @return The interface of cls matching the description from above, if it
	 *         exists. Otherwise null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EObject> Class<? extends EObject> getInterfaceTypeForJavaElementImpl(Class<T> cls) {
		if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
			return null;
		}

		var directIfcs = cls.getInterfaces();
		for (var ifc : directIfcs) {
			if (cls.getSimpleName().contains(ifc.getSimpleName())) {
				return (Class<? extends EObject>) ifc;
			}
		}

		return null;
	}

	/**
	 * @return Types of concrete implementations and interfaces of all Java-Model
	 *         elements.
	 */
	public Set<Class<? extends EObject>> getAllPossibleTypes() {
		return this.getAllPossibleTypes(JavaPackage.eINSTANCE.getESubpackages());
	}

	/**
	 * Recursively discovers sub-packages of cPac (including cPac) for
	 * {@link EClassifier}s contained within, aggregates the types represented by
	 * the EClassifiers as a Set and returns the Set.
	 * 
	 * @param cPac The package, which is the start point of the discovery.
	 * @return All types represented by EClassifiers contained in cPac and its
	 *         sub-packages. Includes types of interfaces as well as concrete
	 *         implementation classes.
	 */
	public Set<Class<? extends EObject>> getAllPossibleTypes(EPackage cPac) {
		var clss = cPac.getEClassifiers();
		var subPacs = cPac.getESubpackages();

		var foundClss = new HashSet<Class<? extends EObject>>();

		if (clss != null) {
			for (var cls : clss) {
				foundClss.add(this.getInstanceClassOfEClassifier(cls));

				/*
				 * Although cls is technically of type EClassifier, it also implements EClass
				 */
				if (cls instanceof EClass) {
					var castedCls = (EClass) cls;

					/*
					 * Add the concrete implementation class, if cls represents a concrete class
					 */
					if (!castedCls.isAbstract()) {
						foundClss.add(cPac.getEFactoryInstance().create(castedCls).getClass());
					}
				}
			}
		}

		if (subPacs != null) {
			foundClss.addAll(this.getAllPossibleTypes(subPacs));
		}

		return foundClss;
	}

	/**
	 * @return All types represented by {@link EClassifiers} contained in pacs and
	 *         their sub-packages. Includes types of interfaces as well as concrete
	 *         implementation classes.
	 * @see {@link #getAllPossibleTypes(EPackage)}}
	 */
	public Set<Class<? extends EObject>> getAllPossibleTypes(Collection<EPackage> pacs) {
		var foundClss = new HashSet<Class<? extends EObject>>();

		for (var pac : pacs) {
			foundClss.addAll(this.getAllPossibleTypes(pac));
		}

		return foundClss;
	}

	/**
	 * Used to determine which EObject implementors should have an initialiser
	 * interface.
	 * 
	 * @return The class objects within {@link #getAllEClasses()} which should have
	 *         a corresponding initialiser interface. Currently, all of them.
	 */
	public Collection<Class<? extends EObject>> getAllInitialiserCandidates() {
		var result = new ArrayList<Class<? extends EObject>>();
		this.getAllEClasses().stream().forEach((e) -> result.add(this.getInstanceClassOfEClassifier(e)));
		return result;
	}

	/**
	 * Used to determine which EObject implementors should have a concrete
	 * initialiser implementation.
	 * 
	 * @return The class objects within {@link #getAllEClasses()} which should have
	 *         a corresponding concrete initialiser implementation.
	 */
	public Collection<Class<? extends EObject>> getAllConcreteInitialiserCandidates() {
		var result = new ArrayList<Class<? extends EObject>>();
		this.getAllEClasses().stream().filter((e) -> !e.isAbstract())
				.forEach((e) -> result.add(this.getInstanceClassOfEClassifier(e)));
		return result;
	}
}
