package cipm.consistency.cpr.pcmjava;

import java.util.HashSet;
import java.util.Set;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class PcmJavaTypeUtil {
	/**
	 * @return The Java field in javaType, which matches the given inner type
	 */
	public static Field getJavaFieldForIn(DataType pcmInnerType, ConcreteClassifier javaType) {
		return javaType.getFields().stream().filter((javaF) -> doTypesMatch(pcmInnerType, javaF)).findFirst()
				.orElse(null);
	}

	/**
	 * @return The Java field, which matches the given inner declaration
	 */
	public static Field getJavaFieldForIn(InnerDeclaration pcmInnerDeclaration, ConcreteClassifier javaType) {
		return javaType.getFields().stream().filter((javaF) -> doTypesMatch(pcmInnerDeclaration, javaF)).findFirst()
				.orElse(null);
	}

	/**
	 * Does not account for type hierarchy of javaType, as primitive types should
	 * not have any (user defined) super types
	 * 
	 * @return Whether given types match, based on their names
	 */
	public static boolean doTypesMatch(PrimitiveDataType pcmType, ConcreteClassifier javaType) {
		return pcmType.getType().getName().toString().equals(javaType.getName());
	}

	/**
	 * Does not account for type hierarchy of javaType, as pcmType and javaType (and
	 * their inner type) have to be exact matches.
	 * <p>
	 * For inner type comparison, inspects Field declarations in {@code javaType}
	 * for the Java classifiers they use. If any Field targets a Java classifier
	 * matching pcmType, returns true.
	 * 
	 * @return Whether given types match
	 */
	public static boolean doTypesMatch(CollectionDataType pcmType, ConcreteClassifier javaType) {
		var pcmName = pcmType.getEntityName();
		if (!pcmName.equals(javaType.getName()))
			return false;

		return getJavaFieldForIn(pcmType.getInnerType_CollectionDataType(), javaType) != null;
	}

	/**
	 * Does not account for type hierarchy of javaType, as pcmType and javaType (and
	 * their inner type) have to be exact matches.
	 * <p>
	 * For inner type comparison, inspects Java Field declarations in
	 * {@code javaType} for the Java classifiers they use. Java Field names here are
	 * important, since InnerDeclarations nested in pcmType have a name, which
	 * should correspond to the name of the Java Field.
	 * 
	 * @return Whether given types match
	 */
	public static boolean doTypesMatch(CompositeDataType pcmType, ConcreteClassifier javaType) {
		var pcmName = pcmType.getEntityName();
		if (!pcmName.equals(javaType.getName()))
			return false;

		return pcmType.getInnerDeclaration_CompositeDataType().stream()
				.allMatch((pcmT) -> getJavaFieldForIn(pcmT, javaType) != null);
	}

	/**
	 * @return Whether pcmType and javaField match, based on their names and types
	 *         they use
	 */
	public static boolean doTypesMatch(InnerDeclaration pcmType, Field javaField) {
		if (!pcmType.getEntityName().equals(javaField.getName()))
			return false;

		var javaTarget = getConcreteClassifierTarget(javaField);
		return javaTarget != null
				&& doTypesMatch(pcmType.getDatatype_InnerDeclaration(), (ConcreteClassifier) javaTarget);
	}

	/**
	 * @return The declared type of javaField, if it is a ConcreteClassifier.
	 *         Otherwise null
	 */
	private static ConcreteClassifier getConcreteClassifierTarget(Field javaField) {
		var target = javaField.getTypeReference().getPureClassifierReference().getTarget();
		return target instanceof ConcreteClassifier ? (ConcreteClassifier) target : null;
	}

	/**
	 * @return Whether the given PCM DataType matches the given Java TypeReference
	 * @throws IllegalArgumentException If the PCM DataType is not an instance of
	 *                                  PrimitiveDataType, CompositeDataType or
	 *                                  CollectionDataType
	 */
	public static boolean doTypesMatch(DataType pcmType, ConcreteClassifier javaType) {
		if (pcmType instanceof PrimitiveDataType) {
			return doTypesMatch((PrimitiveDataType) pcmType, javaType);
		} else if (pcmType instanceof CompositeDataType) {
			return doTypesMatch((CompositeDataType) pcmType, javaType);
		} else if (pcmType instanceof CollectionDataType) {
			return doTypesMatch((CollectionDataType) pcmType, javaType);
		} else {
			throw new IllegalArgumentException("Unknown PCM DataType");
		}
	}

	/**
	 * Variant of {@link #doTypesMatch(DataType, ConcreteClassifier)} for Java
	 * Fields.
	 */
	public static boolean doTypesMatch(DataType pcmType, Field javaField) {
		var javaInnerType = getConcreteClassifierTarget(javaField);
		return javaInnerType != null && doTypesMatch(pcmType, (ConcreteClassifier) javaInnerType);
	}

	/**
	 * Variant of {@link #doTypesMatch(DataType, ConcreteClassifier)} for Java
	 * TypeReferences.
	 */
	public static boolean doTypesMatch(DataType pcmType, TypeReference javaType) {
		return doTypesMatch(pcmType, javaType.getPureClassifierReference());
	}

	public static String getDataTypeName(DataType dt) {
		if (dt instanceof PrimitiveDataType) {
			return ((PrimitiveDataType) dt).getType().getName();
		} else if (dt instanceof CompositeDataType) {
			return ((CompositeDataType) dt).getEntityName();
		} else if (dt instanceof CollectionDataType) {
			return ((CollectionDataType) dt).getEntityName();
		} else {
			throw new IllegalArgumentException("Unknown DataType implementor");
		}
	}

	/**
	 * @param pcmType            The PCM DataType
	 * @param javaType           The Java Classifier corresponding to pcmType
	 * @param toSearchForImports Java elements, which should be searched for imports
	 *                           to javaType
	 * @return A set of all direct, 1 to 1 imports to javaType (i.e. all imports,
	 *         which only import javaType)
	 */
	public static Set<Import> getAllDataTypeImports(DataType pcmType, EditableCorrespondenceModelView<?> cm) {
		var javaType = (ConcreteClassifier) cm.getCorrespondingEObjects(pcmType).stream()
				.filter((javaObj) -> (javaObj instanceof ConcreteClassifier)).findFirst().get();
		if (!doTypesMatch(pcmType, javaType)) {
			return null;
		}

		// TODO Account for DataType's inner types too

		var importSet = new HashSet<Import>();
		for (var je : PCMElementUtil.getAllJavaCorrespondentsOfType(pcmType.getRepository__DataType(), cm,
				Commentable.class, null)) {
			var it = je.eAllContents();
			while (it.hasNext()) {
				var next = it.next();
				if (next instanceof ImportingElement) {
					var castedJE = (ImportingElement) next;
					castedJE.getImports().stream()
							.filter((imp) -> imp.getClassifier().getName().equals(javaType.getName()))
							.forEach((imp) -> importSet.add(imp));
					it.prune();
				}
			}
		}

		return importSet;
	}

	/**
	 * @param pcmType            The PCM DataType
	 * @param javaType           The Java Classifier corresponding to pcmType
	 * @param toSearchForImports Java elements, which should be searched for
	 *                           references to javaType
	 * @return A set of all direct java references to javaType
	 */
	public static Set<TypeReference> getAllDataTypeReferences(DataType pcmType, EditableCorrespondenceModelView<?> cm) {
		var javaType = (ConcreteClassifier) cm.getCorrespondingEObjects(pcmType).stream()
				.filter((javaObj) -> (javaObj instanceof ConcreteClassifier)).findFirst().get();
		if (!doTypesMatch(pcmType, javaType)) {
			return null;
		}

		// TODO Account for DataType's inner types too

		var typeReferenceSet = new HashSet<TypeReference>();
		for (var je : PCMElementUtil.getAllJavaCorrespondentsOfType(pcmType.getRepository__DataType(), cm,
				Commentable.class, null)) {
			var it = je.eAllContents();
			while (it.hasNext()) {
				var next = it.next();
				if (next instanceof TypeReference) {
					var castedJE = (TypeReference) next;
					if (doTypesMatch(pcmType, castedJE)) {
						typeReferenceSet.add(castedJE);
						it.prune();
					}
				}
			}
		}

		return typeReferenceSet;
	}

}
