package cipm.consistency.cpr.pcmjava;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.AnnotableAndModifiable;
import org.emftext.language.java.modifiers.Public;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;

import com.google.common.collect.Sets;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public final class PCMElementUtil {
	private PCMElementUtil() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends Commentable> Set<T> getAllJavaCorrespondentsOfType(EObject obj,
			EditableCorrespondenceModelView<?> cm, Class<T> cls, Predicate<T> filter) {
		Set<T> correspondents = Sets.newHashSet();

		for (var javaCorrespondent : cm.getCorrespondingEObjects(obj)) {
			if (cls.isAssignableFrom(javaCorrespondent.getClass())
					&& (filter == null || filter.test((T) javaCorrespondent)))
				correspondents.add((T) javaCorrespondent);
		}

		for (var objContent : obj.eContents()) {
			correspondents.addAll(getAllJavaCorrespondentsOfType(objContent, cm, cls, filter));
		}

		return correspondents;
	}

	public static <T extends Commentable> Set<T> getAllJavaCorrespondentsOfType(Collection<EObject> objs,
			EditableCorrespondenceModelView<?> cm, Class<T> cls, Predicate<T> filter) {
		Set<T> classifiers = Sets.newHashSet();

		for (var obj : objs) {
			classifiers.addAll(getAllJavaCorrespondentsOfType(obj, cm, cls, filter));
		}

		return classifiers;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Commentable> Set<T> getAllVisibleJavaCorrespondentsOfType(EObject obj,
			EditableCorrespondenceModelView<?> cm, Class<T> cls, Predicate<T> filter) {
		Set<T> correspondents = Sets.newHashSet();

		for (var javaCorrespondent : cm.getCorrespondingEObjects(obj)) {
			if (cls.isAssignableFrom(javaCorrespondent.getClass())
					&& (!(javaCorrespondent instanceof AnnotableAndModifiable)
							|| ((AnnotableAndModifiable) javaCorrespondent).hasModifier(Public.class))
					&& (filter == null || filter.test((T) javaCorrespondent)))
				correspondents.add((T) javaCorrespondent);
		}

		for (var objContent : obj.eContents()) {
			correspondents.addAll(getAllJavaCorrespondentsOfType(objContent, cm, cls, filter));
		}

		return correspondents;
	}

	public static <T extends Commentable> Set<T> getAllVisibleJavaCorrespondentsOfType(Collection<EObject> objs,
			EditableCorrespondenceModelView<?> cm, Class<T> cls, Predicate<T> filter) {
		Set<T> classifiers = Sets.newHashSet();

		for (var obj : objs) {
			classifiers.addAll(getAllVisibleJavaCorrespondentsOfType(obj, cm, cls, filter));
		}

		return classifiers;
	}

	/**
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
		if (!PcmJavaTypeUtil.doTypesMatch(pcmSig.getReturnType__OperationSignature(), javaMet.getTypeReference())) {
			return false;
		}

		// Check exception types (if desired)
		if (checkExceptions && !pcmSig.getExceptions__Signature().stream()
				.allMatch((pcmExc) -> javaMet.getExceptions().stream().anyMatch((javaExc) -> javaExc
						.getClassifierAtNamespaces().getName().equals(pcmExc.getExceptionName())))) {
			return false;
		}

		// Check parameter types
		if (!doMethodParametersMatch(pcmSig.getParameters__OperationSignature(), javaMet.getParameters())) {
			return false;
		}

		return true;
	}

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

	public static boolean doMethodParametersMatch(org.palladiosimulator.pcm.repository.Parameter pcmParam,
			org.emftext.language.java.parameters.Parameter javaParam) {
		return pcmParam.getParameterName().equals(javaParam.getName())
				&& PcmJavaTypeUtil.doTypesMatch(pcmParam.getDataType__Parameter(), javaParam.getTypeReference());
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
	 * @param checkExceptions Whether exceptions should match as well
	 * @return A set of PCM OperationSignatures, which do not have a corresponding
	 *         Java Method in javaMets
	 * @see {@link #doMethodSignaturesMatch(OperationSignature, Method, boolean)}
	 */
	public static Set<OperationSignature> getPCMMethodsWithoutJavaCorrespondences(Iterable<OperationSignature> pcmSigs,
			Iterable<Method> javaMets, boolean checkExceptions) {
		var matches = getPCMMethodsWithJavaCorrespondences(pcmSigs, javaMets, checkExceptions);

		var nonMatches = new HashSet<OperationSignature>();
		pcmSigs.forEach((sig) -> {
			if (!matches.containsKey(sig))
				nonMatches.add(sig);
		});

		return nonMatches;
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
	 * @return Whether the given PCM RequiredRole is necessary. This is the case, if
	 *         the required interface is imported.
	 */
	public static boolean isRequiredRoleNecessary(OperationRequiredRole reqRole,
			EditableCorrespondenceModelView<?> cm) {
		var entity = reqRole.getRequiringEntity_RequiredRole();
		var requiredIfc = reqRole.getRequiredInterface__OperationRequiredRole();
		var requiringClassifierSet = getAllJavaCorrespondentsOfType(entity, cm, ConcreteClassifier.class, null);

		return requiringClassifierSet.stream().anyMatch((cls) -> importsOperationInterface(requiredIfc, cls));
	}

	/**
	 * @return A set of Java Imports, which are used to import the given PCM
	 *         OperationInterface.
	 */
	public static Set<Import> getRequiredInterfaceImports(OperationInterface pcmIFC,
			EditableCorrespondenceModelView<?> cm) {
		var entity = pcmIFC.getRepository__Interface();
		var requiringClassifierSet = getAllJavaCorrespondentsOfType(entity, cm, ConcreteClassifier.class, null);

		var matches = new HashSet<Import>();
		requiringClassifierSet.forEach((cls) -> {
			cls.getContainingCompilationUnit().getImports().stream()
					.filter((imp) -> importsOperationInterface(pcmIFC, imp)).forEach((imp) -> matches.add(imp));
		});

		return matches;
	}

	/**
	 * @return A set of Java Imports, which make the given PCM RequiredRole
	 *         necessary (i.e. not redundant)
	 */
	public static Set<Import> getRequiredRoleImports(OperationRequiredRole reqRole,
			EditableCorrespondenceModelView<?> cm) {
		return getRequiredInterfaceImports(reqRole.getRequiredInterface__OperationRequiredRole(), cm);
	}

}
