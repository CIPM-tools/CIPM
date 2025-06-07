package cipm.consistency.cpr.pcmjava;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;
import org.emftext.language.java.members.MemberContainer;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.Public;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;

import com.google.common.collect.Sets;

public final class PCMElementUtil {
	private PCMElementUtil() {
	}

	/**
	 * Disregards the potential visibility modifiers of the found classifiers, so
	 * that it is possible to check for dependencies (ex: over imports)
	 * 
	 * @return All Java ConcreteClassifiers, which can be found under obj,
	 *         regardless of their visibility.
	 */
	public static Set<ConcreteClassifier> getAllConcreteClassifiers(EObject obj) {
		Set<ConcreteClassifier> classifiers = Sets.newHashSet();

		var it = obj.eAllContents();
		while (it.hasNext()) {
			var next = it.next();
			if (next instanceof ConcreteClassifier) {
				var castedNext = (ConcreteClassifier) next;
				classifiers.add(castedNext);
				classifiers.addAll(castedNext.getAllInnerClassifiers());

				// Got next as classifier and all its inner classifiers too, no need to look
				// further
				it.prune();
			}
		}
		return classifiers;
	}

	/**
	 * Disregards the potential visibility modifiers of the found classifiers, so
	 * that it is possible to check for dependencies (ex: over imports)
	 * 
	 * @return All Java ConcreteClassifiers, which can be found under objs,
	 *         regardless of their visibility.
	 */
	public static Set<ConcreteClassifier> getAllConcreteClassifiers(Iterable<EObject> objs) {
		Set<ConcreteClassifier> classifiers = Sets.newHashSet();

		for (var obj : objs) {
			classifiers.addAll(getAllConcreteClassifiers(obj));
		}

		return classifiers;
	}

	/**
	 * @return All public ConcreteClassifier instances to be found under jr.
	 */
	public static Set<ConcreteClassifier> getAllPublicConcreteClassifiers(EObject obj) {
		Set<ConcreteClassifier> classifiers = getAllConcreteClassifiers(obj);

		var clss = classifiers.toArray(ConcreteClassifier[]::new);
		for (var cls : clss) {
			if (!cls.hasModifier(Public.class)) {
				classifiers.remove(cls);
				classifiers.removeAll(cls.getAllInnerClassifiers());
			}
		}

		return classifiers;
	}

	/**
	 * @return All public ConcreteClassifier instances to be found under the given
	 *         jrs.
	 */
	public static Set<ConcreteClassifier> getAllPublicConcreteClassifiers(Iterable<EObject> obj) {
		Set<ConcreteClassifier> classifiers = Sets.newHashSet();

		for (var jr : obj) {
			classifiers.addAll(getAllPublicConcreteClassifiers(jr));
		}

		return classifiers;
	}

	/**
	 * @return All public methods from the given MemberContainer.
	 */
	public static Set<Method> getAllPublicMethods(MemberContainer mc) {
		Set<Method> mets = Sets.newHashSet();

		mc.getMethods().stream().filter((m) -> m.hasModifier(Public.class)).forEach((m) -> mets.add(m));

		return mets;
	}

	/**
	 * @return All public methods from the given MemberContainers.
	 */
	public static Set<Method> getAllPublicMethods(Iterable<MemberContainer> mcs) {
		Set<Method> mets = Sets.newHashSet();

		for (var mc : mcs) {
			mets.addAll(getAllPublicMethods(mc));
		}

		return mets;
	}

	/**
	 * @return All public methods from the given JavaRoot.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Set<Method> getAllPublicMethodsInJavaRoot(JavaRoot jr) {
		Set<Method> mets = Sets.newHashSet();

		mets.addAll(getAllPublicMethods((Iterable) getAllPublicConcreteClassifiers(jr)));

		return mets;
	}

	/**
	 * @return All public methods from the given JavaRoots.
	 */
	public static Set<Method> getAllPublicMethodsInJavaRoots(Iterable<JavaRoot> jrs) {
		Set<Method> mets = Sets.newHashSet();

		for (var jr : jrs) {
			mets.addAll(getAllPublicMethodsInJavaRoot(jr));
		}

		return mets;
	}

	/**
	 * @return Whether the given PCM DataType matches the given Java TypeReference
	 * @throws IllegalArgumentException If the PCM DataType is not an instance of
	 *                                  PrimitiveDataType, CompositeDataType or
	 *                                  CollectionDataType
	 */
	public static boolean doTypesMatch(DataType pcmType, TypeReference javaType) {
		if (pcmType instanceof PrimitiveDataType) {
			var castedPCMType = (PrimitiveDataType) pcmType;
			return !castedPCMType.getType().getName().toString()
					.equals(javaType.getTarget().getParentConcreteClassifier().getName());
		} else if (pcmType instanceof CompositeDataType) {
			var castedPCMType = (CompositeDataType) pcmType;
			return !castedPCMType.getEntityName().equals(javaType.getTarget().getParentConcreteClassifier().getName());
		} else if (pcmType instanceof CollectionDataType) {
			var castedPCMType = (CollectionDataType) pcmType;
			return !castedPCMType.getEntityName().equals(javaType.getTarget().getParentConcreteClassifier().getName());
		} else {
			throw new IllegalArgumentException("Unknown PCM DataType");
		}
	}

	/**
	 * TODO Clarify whether generic elements matter here (ex: TypeArguments)
	 * 
	 * @param checkExceptions Whether exceptions should match as well
	 * @return Whether the given PCM method signature matches with the signature of
	 *         the given Java method
	 */
	public static boolean doMethodSignaturesMatch(OperationSignature pcmSig, Method javaMet, boolean checkExceptions) {
		// Check names
		if (!pcmSig.getEntityName().equals(javaMet.getName())) {
			return false;
		}

		// Check return types
		if (!doTypesMatch(pcmSig.getReturnType__OperationSignature(), javaMet.getTypeReference())) {
			return false;
		}

		// Check exception types (if desired)
		if (checkExceptions && !pcmSig.getExceptions__Signature().stream()
				.allMatch((pcmExc) -> javaMet.getExceptions().stream().anyMatch((javaExc) -> javaExc
						.getClassifierAtNamespaces().getName().equals(pcmExc.getExceptionName())))) {
			return false;
		}

		// Check parameter types
		if (!pcmSig.getParameters__OperationSignature().stream()
				.allMatch((pcmParam) -> javaMet.getParameters().stream()
						.anyMatch((javaParam) -> doTypesMatch(pcmParam.getDataType__Parameter(),
								javaParam.getTypeReference())))) {
			return false;
		}

		return true;
	}

	/**
	 * @param checkExceptions Whether exceptions should match as well
	 * @return A mapping of PCM OperationSignatures to their (signature-wise)
	 *         corresponding Java Methods
	 * @see {@link #doMethodSignaturesMatch(OperationSignature, Method, boolean)}
	 */
	public static Map<OperationSignature, Method> getPCMMethodsWithJavaCorrespondences(
			Iterable<OperationSignature> pcmSigs, Iterable<Method> javaMets, boolean checkExceptions) {
		var matches = new HashMap<OperationSignature, Method>();

		for (var pcmSig : pcmSigs) {
			for (var javaMet : javaMets) {
				if (doMethodSignaturesMatch(pcmSig, javaMet, checkExceptions)) {
					matches.put(pcmSig, javaMet);
					break;
				}
			}
		}

		return matches;
	}

	/**
	 * @return Whether the given Java ImportingElement imports the given PCM
	 *         OperationInterface
	 */
	public static boolean importsOperationInterface(OperationInterface pcmIFC, ImportingElement cu) {
		return cu.getImports().stream().anyMatch((imp) -> importsOperationInterface(pcmIFC, imp));
	}

	/**
	 * @return Whether the given Java Commentable imports the given PCM
	 *         OperationInterface
	 */
	public static boolean importsOperationInterface(OperationInterface pcmIFC, Commentable cls) {
		return cls.getContainingCompilationUnit().getImports().stream()
				.anyMatch((imp) -> importsOperationInterface(pcmIFC, imp));
	}

	/**
	 * @return Whether the given Java Import imports the given PCM
	 *         OperationInterface
	 */
	public static boolean importsOperationInterface(OperationInterface pcmIFC, Import imp) {
		return imp.getClassifier().getName().equals(pcmIFC.getEntityName())
				|| imp.getClassifier().getQualifiedName().equals(pcmIFC.getEntityName());
	}

	/**
	 * @param reqRole                         A given PCM RequiredRole
	 * @param requiredInterfaceCorrespondents Java elements that correspond to the
	 *                                        required interface denoted by
	 *                                        RequiredRole
	 * @return Whether the given PCM RequiredRole is redundant. This is the case, if
	 *         the required interface is not imported.
	 */
	public static boolean isRequiredRoleRedundant(OperationRequiredRole reqRole,
			Iterable<EObject> requiredInterfaceCorrespondents) {
		var requiredIfc = reqRole.getRequiredInterface__OperationRequiredRole();
		var requiringClassifierSet = getAllConcreteClassifiers(requiredInterfaceCorrespondents);

		return requiringClassifierSet.stream().anyMatch((cls) -> importsOperationInterface(requiredIfc, cls));
	}

	/**
	 * 
	 * @param pcmIFC                          A given PCM OperationInterface
	 * @param requiredInterfaceCorrespondents Java elements that correspond to the
	 *                                        required interface denoted by
	 *                                        RequiredRole
	 * @return A set of Java Imports, which are used to import the given PCM
	 *         OperationInterface.
	 */
	public static Set<Import> getRequiredInterfaceImports(OperationInterface pcmIFC,
			Iterable<EObject> requiredInterfaceCorrespondents) {
		var requiringClassifierSet = getAllConcreteClassifiers(requiredInterfaceCorrespondents);

		var matches = new HashSet<Import>();
		requiringClassifierSet.forEach((cls) -> {
			cls.getContainingCompilationUnit().getImports().stream()
					.filter((imp) -> importsOperationInterface(pcmIFC, imp)).forEach((imp) -> matches.add(imp));
		});

		return matches;
	}

	/**
	 * @param reqRole                         A given PCM RequiredRole
	 * @param requiredInterfaceCorrespondents Java elements that correspond to the
	 *                                        required interface denoted by
	 *                                        RequiredRole
	 * @return A set of Java Imports, which make the given PCM RequiredRole
	 *         necessary (i.e. not redundant)
	 */
	public static Set<Import> getRequiredRoleImports(OperationRequiredRole reqRole,
			Iterable<EObject> requiredInterfaceCorrespondents) {
		return getRequiredInterfaceImports(reqRole.getRequiredInterface__OperationRequiredRole(),
				requiredInterfaceCorrespondents);
	}
}
