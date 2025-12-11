package cipm.consistency.cpr.pcmjava;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.OperationSignature;

/**
 * A utility class for PCM to Java CPRs
 * 
 * @author Alp Torac Genc
 */
public final class PcmJavaCPRUtils {
	/**
	 * Compares parameters wrt. their type and name
	 * 
	 * @return Whether the given list of PCM parameters and the given list of Java
	 *         parameters match
	 */
	public static boolean doMethodParametersMatch(List<org.palladiosimulator.pcm.repository.Parameter> pcmParams,
			List<org.emftext.language.java.parameters.Parameter> javaParams) {
		if (pcmParams.size() != javaParams.size())
			return false;

		for (int i = 0; i < pcmParams.size(); i++) {
			if (!doMethodParametersMatch(pcmParams.get(i), javaParams.get(i)))
				return false;
		}

		return true;
	}

	/**
	 * Compares parameters wrt. their type and name
	 * 
	 * @return Whether the given list of PCM parameters and the given list of Java
	 *         parameters match
	 */
	public static boolean doMethodParametersMatch(org.palladiosimulator.pcm.repository.Parameter pcmParam,
			org.emftext.language.java.parameters.Parameter javaParam) {
		return pcmParam.getParameterName().equals(javaParam.getName())
				&& PcmJavaTypeUtil.doTypesMatch(pcmParam.getDataType__Parameter(), javaParam.getTypeReference());
	}

	/**
	 * Adds correspondences between PCM OperationSignature and Java Method. Assumes
	 * them to match and does not check whether they match.
	 */
	public static void addCorrespondencesForPcmOperationSignatureAndJavaInterfaceMethod(
			EditableCorrespondenceModelView<?> corView, OperationSignature pcmSig, Method javaMet) {
		// Add correspondences between method signatures (as a whole)
		addCorrespondenceToJavaCorrespondent(corView, pcmSig, javaMet);

		// Add correspondences between parameters (if existent)
		// Note: PCM parameters are NOT first class entities
		var pcmParams = pcmSig.getParameters__OperationSignature();
		var javaParams = javaMet.getParameters();
		if (doMethodParametersMatch(pcmParams, javaParams)) {
			for (int i = 0; i < pcmParams.size(); i++) {
				addCorrespondenceToJavaCorrespondent(corView, pcmParams.get(i), javaParams.get(i));
			}
		}
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
	 * Adds all missing imports to given CompilationUnit
	 */
	public static void prepareJavaCompilationUnit(CompilationUnit cu) {
		var allTypeReferences = new ArrayList<TypeReference>();
		cu.eAllContents().forEachRemaining((o) -> {
			if (o instanceof TypeReference)
				allTypeReferences.add((TypeReference) o);
		});

		for (var ref : allTypeReferences) {
			if (cu.getImports().stream().noneMatch((i) -> i.getImportedClassifiers().stream()
					.anyMatch((cls) -> ref.getPureClassifierReference().getTarget() == cls))) {
				cu.addImport(ref.getPureClassifierReference().getTarget().getName());
			}
		}
	}

	/**
	 * Adds all missing imports to given Java Classifier
	 */
	public static void prepareJavaClassifier(ConcreteClassifier javaCls) {
		if (javaCls.getContainingCompilationUnit() != null) {
			prepareJavaCompilationUnit(javaCls.getContainingCompilationUnit());
		}
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
			ConcreteClassifier javaCls, List<String> namespaces) {
		PcmJavaCPRUtils.addJavaClassifierIntoResource(javaCls, namespaces);
	}

	/**
	 * Adds the given Java Classifier into the given Resource, if it is not already
	 * there.
	 * 
	 * @return All JavaRoot instances that were created to add javaCls with given
	 *         namespaces to r
	 */
	public static List<JavaRoot> addJavaClassifierIntoResource(ConcreteClassifier javaCls, List<String> javaClsNss) {
		if (JavaModelAccess.isInJavaModelResource(javaCls)) {
			return null;
		}

		var topContents = JavaModelAccess.getTopLevelJavaModelElements();

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

		var containers = addJavaClassifierIntoJavaRoot(bottomMostExistingParentContainer, javaCls, javaClsNss);
		JavaModelAccess.getJavaModel().getContents().addAll(containers);
		return containers;
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
			return List.of();
		} else {
			return addJavaClassifierIntoJavaPackage(moduleOfJavaCls, null, javaCls, javaClsNss);
		}
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
				if (javaCls.getContainingCompilationUnit() == null) {
					return List.of(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
				} else {
					return List.of();
				}
			}
		} else {
			longestNsPrefix = List.of();
		}

		var createdContainers = new ArrayList<JavaRoot>();
		var remainingNss = javaClsNss.stream().skip(longestNsPrefix.size())
				.collect(Collectors.toCollection(ArrayList::new));

		var createdPacs = new ArrayList<org.emftext.language.java.containers.Package>();
		// Create the necessary packages
		for (int i = 0; i < remainingNss.size(); i++) {
			createdPacs.add(createJavaPackage(moduleOfJavaCls, javaClsNss.subList(0, i + longestNsPrefix.size() + 1)));
		}

		if (!createdPacs.isEmpty()) {
			var pacOfCls = createdPacs.get(createdPacs.size() - 1);
			// Package of javaCls should be set to its package, but Package.classifiers
			// should not contain javaCls (per current convention in Java -> PCM CPRs)
			javaCls.setPackage(pacOfCls);
			createdContainers.addAll(createdPacs);
		}

		// Create the necessary CompilationUnit
		if (javaCls.getContainingCompilationUnit() == null) {
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
			moduleOfJavaCls.getPackages().add(pac);
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
