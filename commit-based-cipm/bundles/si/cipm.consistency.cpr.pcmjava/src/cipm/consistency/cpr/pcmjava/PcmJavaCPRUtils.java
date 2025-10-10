package cipm.consistency.cpr.pcmjava;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.OperationSignature;

import com.google.common.base.Preconditions;

public final class PcmJavaCPRUtils {

	/**
	 * Method for finding or creating or deciding or ignoring the Java correspondent
	 * of a PCM element.
	 * <p>
	 * Extracted here, since this is a recurring operation across many CPRs.
	 * 
	 * @param pcmElement                       The PCM element, for which a Java
	 *                                         correspondent is sought
	 * @param listOfPossibleJavaCorrespondents Collection of possible Java
	 *                                         correspondents for the given PCM
	 *                                         element
	 * @return The Java element, which should correspond to the given PCM element.
	 *         The resulting Java element could be created freshly and may require
	 *         inserting into the Java model resource. This method returns null,
	 *         there should be no Java correspondent for the given PCM element among
	 *         the given possible Java correspondents. The returned element may not
	 *         always be of the same type as elements of
	 *         listOfPossibleJavaCorrespondents, as sometimes it is necessary to
	 *         create the container of a correspondent instead (example: A Java
	 *         ConcreteClassifier has to be created alongside its CompilationUnit,
	 *         since its namespace comes from its container).
	 */
	public static <O extends Commentable, C extends Iterable<O>> Commentable findOrCreateOrDecideJavaCorrespondent(
			EObject pcmElement, C listOfPossibleJavaCorrespondents) {
		var it = listOfPossibleJavaCorrespondents.iterator();
		if (it.hasNext()) {
			var firstElem = it.next();
			if (!it.hasNext()) {
				// Only one possible Java correspondent, return it
				return firstElem;
			} else {
				var allElems = List.of(listOfPossibleJavaCorrespondents);
				// Multiple possible Java correspondents, pick one

				// TODO User interaction for deciding which corresponding is the correct one
				// TODO Make sure to allow taking no action / leaving a developer task
			}
		} else {
			// No possible Java correspondents, create one

			// TODO User interaction for corresponding element (stub) creation
			// TODO Make sure to allow taking no action / leaving a developer task
		}
	}

	/**
	 * Adds correspondences between PCM OperationSignature and Java Method. Assumes
	 * them to match and does not check whether they match.
	 */
	public static void addCorrespondencesForPcmOperationSignatureAndJavaInterfaceMethod(
			EditableCorrespondenceModelView<?> corView, OperationSignature pcmSig, Method javaMet) {
		Preconditions.checkArgument(javaMet.eContainer() instanceof Interface,
				"Given Java method is not an interface method");

		// Add correspondences between method signatures (as a whole)
		addCorrespondenceToJavaCorrespondent(corView, pcmSig, javaMet);

		// Add correspondences between parameters (if existent)
		// Note: PCM parameters are NOT first class entities
		var pcmParams = pcmSig.getParameters__OperationSignature();
		var javaParams = javaMet.getParameters();
		for (int i = 0; i < pcmParams.size(); i++) {
			addCorrespondenceToJavaCorrespondent(corView, pcmParams.get(i), javaParams.get(i));
		}

		// Add correspondences between PCM exception types and Java References to
		// exception Classifiers (if existent)
		//
		// Note: PCM exception types are NOT first class entities
		//
		// Since exception types could be excluded while looking for matching PCM and
		// Java Method signatures, it is important to re-check whether they match
		pcmSig.getExceptions__Signature().forEach((pcmExc) -> {
			var javaExc = javaMet.getExceptions().stream().filter((javaExcRef) -> javaExcRef
					.getPureClassifierReference().getTarget().getName().equals(pcmExc.getExceptionName())).findFirst();
			if (javaExc.isPresent()) {
				addCorrespondenceToJavaCorrespondent(corView, pcmExc, javaExc.get());
			}
		});
	}

	/**
	 * Adds the correspondence (pcmElement, javaCorrespondent, tag) to given corView
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addCorrespondenceToJavaCorrespondent(
			EditableCorrespondenceModelView<?> corView, EObject pcmElement, O javaCorrespondent, String tag) {
		if (javaCorrespondent != null) {
			corView.addCorrespondenceBetween(pcmElement, javaCorrespondent, tag);
		}

		return javaCorrespondent;
	}

	/**
	 * Adds the correspondence (pcmElement, javaCorrespondent, null) to given
	 * corView
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addCorrespondenceToJavaCorrespondent(
			EditableCorrespondenceModelView<?> corView, EObject pcmElement, O javaCorrespondent) {
		return addCorrespondenceToJavaCorrespondent(corView, pcmElement, javaCorrespondent, null);
	}

	/**
	 * Adds javaCorrespondent to javaModelResource by using
	 * placeInJavaModelResourceFunc, if javaCorrespondent is not already in a
	 * Resource. placeInJavaModelResourceFunc denotes how and to which location in
	 * javaModelResource javaCorrespondent is to be inserted.
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addToJavaModelIfNotThere(Resource javaModelResource, O javaCorrespondent,
			BiConsumer<Resource, O> placeInJavaModelResourceFunc) {
		if (javaCorrespondent != null && javaCorrespondent.eResource() == null) {
			placeInJavaModelResourceFunc.accept(javaModelResource, javaCorrespondent);
		}

		return javaCorrespondent;
	}

	/**
	 * Adds javaCorrespondent to a Resource by using placeInJavaModelResourceFunc,
	 * if javaCorrespondent is not already in a Resource.
	 * placeInJavaModelResourceFunc denotes how and to which location in a Resource
	 * javaCorrespondent is to be inserted.
	 * <p>
	 * Use this method, if javaCorrespondent should be nested in another Java model
	 * element, which is already in a Resource.
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addToJavaModelIfNotThere(O javaCorrespondent,
			Consumer<O> placeInJavaModelResourceFunc) {
		return addToJavaModelIfNotThere(null, javaCorrespondent, (r, o) -> placeInJavaModelResourceFunc.accept(o));
	}

	/**
	 * Adds a correspondence between pcmElem and Java Classifier.
	 * <p>
	 * If Java correspondent is located, it will be a ConcreteClassifier. If Java
	 * correspondent is freshly created, it will be a ConcreteClassifier contained
	 * in a CompilationUnit. The reason is that ConcreteClassifiers do not directly
	 * support the namespace feature, it is instead derived from its containers.
	 */
	public static void integrateJavaClassifierCorrespondent(EditableCorrespondenceModelView<?> corView, EObject pcmElem,
			Resource javaModelResource, Commentable javaClsOrCU) {
		if (javaClsOrCU instanceof ConcreteClassifier) {
			PcmJavaCPRUtils.addCorrespondenceToJavaCorrespondent(corView, pcmElem, javaClsOrCU);
		} else if (javaClsOrCU instanceof CompilationUnit) {
			var castedCU = (CompilationUnit) javaClsOrCU;
			var javaCls = castedCU.getClassifiers().get(0);
			PcmJavaCPRUtils.addCorrespondenceToJavaCorrespondent(corView, pcmElem, javaCls);
			PcmJavaCPRUtils.addJavaClassifierIntoResource(javaModelResource, javaCls, castedCU.getNamespaces());
		}
	}

	/**
	 * @return Whether the given obj is in the given resource r
	 */
	public static boolean isInResource(Resource r, EObject obj) {
		EObject objInR = null;
		var it = r.getAllContents();
		while (it.hasNext() && objInR == null) {
			var currentObj = it.next();
			if (currentObj == obj) {
				objInR = currentObj;
				break;
			}
		}
		return objInR != null;
	}

	/**
	 * Adds the given Java Classifier into the given Resource, if it is not already
	 * there.
	 * 
	 * @return All JavaRoot instances that were created to add javaCls with given
	 *         namespaces to r
	 */
	public static List<JavaRoot> addJavaClassifierIntoResource(Resource r, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (isInResource(r, javaCls)) {
			return null;
		}

		var topContents = r.getContents();

		var possibleContainers = topContents.stream().filter((c) -> c instanceof JavaRoot).map((c) -> (JavaRoot) c)
				.collect(Collectors.toCollection(ArrayList::new));

		JavaRoot bottomMostExistingParentContainer = null;
		int longestCommonNsPrefixLen = 0;
		var javaClsNssSize = javaClsNss.size();

		for (var pc : possibleContainers) {
			var currentLongestCommonNsPrefixLen = getLongestCommonNamespacePrefix(pc.getNamespaces(), javaClsNss)
					.size();

			if (currentLongestCommonNsPrefixLen == javaClsNssSize) {
				// Direct parent container (wrt. namespaces) of javaCls found
				bottomMostExistingParentContainer = pc;
				break;
			}

			if (currentLongestCommonNsPrefixLen > 0 && longestCommonNsPrefixLen < currentLongestCommonNsPrefixLen) {
				longestCommonNsPrefixLen = currentLongestCommonNsPrefixLen;
				bottomMostExistingParentContainer = pc;
			}
		}

		return addJavaClassifierIntoJavaRoot(bottomMostExistingParentContainer, javaCls, javaClsNss);
	}

	/**
	 * Inserts javaCls (directly or indirectly depending on concrete JavaRoot type)
	 * into jrOfJavaCls, which in return inserts javaCls into the Java model
	 * resource. If jrOfJavaCls is null, all packages and the compilation unit
	 * leading to javaCls will be constructed.
	 * <p>
	 * Assumption: If a Java Module for javaCls were to exist, it would have existed
	 * already. Therefore, no Java Modules will be constructed.
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaRoot(JavaRoot jrOfJavaCls, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (jrOfJavaCls == null) {
			// javaCls has no pre-existing container, build all packages and the compilation
			// unit leading to it
			return addJavaClassifierIntoJavaPackage(null, null, javaCls, javaClsNss);
		} else if (jrOfJavaCls instanceof org.emftext.language.java.containers.Module) {
			// javaCls' most specific parent container is a module, build all packages and
			// the compilation unit leading to it
			return addJavaClassifierIntoJavaModule(((org.emftext.language.java.containers.Module) jrOfJavaCls), javaCls,
					javaClsNss);
		} else if (jrOfJavaCls instanceof org.emftext.language.java.containers.Package) {
			// javaCls' most specific parent container is a package, build all sub-packages
			// and the compilation unit leading to it
			return addJavaClassifierIntoJavaPackage(null, ((org.emftext.language.java.containers.Package) jrOfJavaCls),
					javaCls, javaClsNss);
		} else if (jrOfJavaCls instanceof CompilationUnit) {
			// javaCls' most specific parent container is a CompilationUnit, just add
			// javaCls to it
			((CompilationUnit) jrOfJavaCls).getClassifiers().add(javaCls);
			return List.of();
		} else {
			throw new IllegalStateException(
					"Unknown container for javaCls exists of type: " + jrOfJavaCls.getClass().getName());
		}
	}

	/**
	 * Inserts javaCls (indirectly) into moduleOfJavaCls, which in return inserts
	 * javaCls into the Java model resource. All packages and the compilation unit
	 * leading to javaCls will be constructed.
	 * <p>
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaModule(
			org.emftext.language.java.containers.Module moduleOfJavaCls, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (javaCls.getPackage() != null) {
			return null;
		}

		var createdContainers = new ArrayList<JavaRoot>();

		var longestNsPrefix = getLongestCommonNamespacePrefix(moduleOfJavaCls.getNamespaces(), javaClsNss);
		if (longestNsPrefix.size() == javaClsNss.size()) {
			// Module's namespace matches directly with that of Java Classifier
			// create a package for the Java Classifier as well as a CompilationUnit
			var pac = createJavaPackage(moduleOfJavaCls, javaClsNss);
			pac.getClassifiers().add(javaCls);
			createdContainers.add(pac);

			if (javaCls.getContainingCompilationUnit() == null) {
				createdContainers.add(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
			}
		}

		// Create the necessary Packages
		createdContainers.addAll(addJavaClassifierIntoJavaPackage(moduleOfJavaCls, null, javaCls, javaClsNss));

		return createdContainers;
	}

	/**
	 * Inserts javaCls (directly or indirectly depending on whether parentPackage's
	 * namespace matches) into parentPackage, which in return inserts javaCls into
	 * the Java model resource. All packages and the compilation unit leading to
	 * javaCls will be constructed.
	 * <p>
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaPackage(
			org.emftext.language.java.containers.Module moduleOfJavaCls,
			org.emftext.language.java.containers.Package parentPackage, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (javaCls.getPackage() != null) {
			return List.of();
		}

		List<String> longestNsPrefix = null;

		if (parentPackage != null) {
			longestNsPrefix = getLongestCommonNamespacePrefix(parentPackage.getNamespaces(), javaClsNss);
			if (longestNsPrefix.size() == javaClsNss.size()) {
				// Parent package is the direct container of javaCls, insert it there, create
				// a compilation unit if necessary and return
				parentPackage.getClassifiers().add(javaCls);

				if (javaCls.getContainingCompilationUnit() == null) {
					return List.of(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
				} else {
					return List.of();
				}
			}

		} else if (moduleOfJavaCls != null) {
			longestNsPrefix = getLongestCommonNamespacePrefix(moduleOfJavaCls.getNamespaces(), javaClsNss);
		} else {
			longestNsPrefix = List.of();
		}

		var createdContainers = new ArrayList<JavaRoot>();
		var remainingNss = javaClsNss.stream().skip(longestNsPrefix.size())
				.collect(Collectors.toCollection(ArrayList::new));

		var createdPacs = new ArrayList<org.emftext.language.java.containers.Package>();
		// Create the necessary packages
		for (int i = 1; i < remainingNss.size(); i++) {
			createdPacs.add(createJavaPackage(moduleOfJavaCls, javaClsNss.subList(0, i + longestNsPrefix.size())));
		}

		if (!createdPacs.isEmpty()) {
			createdContainers.addAll(createdPacs);
			createdPacs.get(createdPacs.size() - 1).getClassifiers().add(javaCls);
		}

		// Create the necessary CompilationUnit
		if (javaCls.getContainingCompilationUnit() != null) {
			createdContainers.add(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
		}

		return createdContainers;
	}

	/**
	 * Inserts given Java Classifier into an appropriately created CompilationUnit
	 * 
	 * @return A CompilationUnit for the given Java Classifier with the given
	 *         namespaces
	 */
	public static CompilationUnit createCompilationUnitForJavaClassifier(ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		var cu = ContainersFactory.eINSTANCE.createCompilationUnit();

		cu.setName(javaCls.getName());
		// Since all added Java model elements are to be saved into source files,
		// the origin should be FILE
		cu.setOrigin(Origin.FILE);

		cu.getNamespaces().addAll(javaClsNss);
		cu.getClassifiers().add(javaCls);
		return cu;
	}

	/**
	 * @return A Java Package with the given module and namespaces. The given module
	 *         may be null.
	 */
	public static org.emftext.language.java.containers.Package createJavaPackage(
			org.emftext.language.java.containers.Module moduleOfJavaCls, List<String> javaPacNss) {
		var pac = ContainersFactory.eINSTANCE.createPackage();

		if (moduleOfJavaCls != null) {
			pac.setModule(moduleOfJavaCls);
		}

		// Since all added Java model elements are to be saved into source files,
		// the origin should be FILE
		pac.setOrigin(Origin.FILE);

		// Package names are irrelevant for CPRs and JaMoPP context, only namespaces
		// matter. Here, use empty String as name, in order to avoid modelling issues
		pac.setName("");
		pac.getNamespaces().addAll(javaPacNss);

		return pac;
	}

	/**
	 * @return The longest common namespace prefix for the given namespaces (as
	 *         list)
	 */
	public static List<String> getLongestCommonNamespacePrefix(List<String> nss1, List<String> nss2) {
		if (nss1.isEmpty() || nss2.isEmpty())
			return List.of();

		var minSize = nss1.size() < nss2.size() ? nss1.size() : nss2.size();

		var prefix = new ArrayList<String>();
		for (int i = 0; i < minSize; i++) {
			if (nss1.get(i).equals(nss2.get(i))) {
				prefix.add(nss1.get(i));
			} else {
				break;
			}
		}

		return prefix;
	}
}
