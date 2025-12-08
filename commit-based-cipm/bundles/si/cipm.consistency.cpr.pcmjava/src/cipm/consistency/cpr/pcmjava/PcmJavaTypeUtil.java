package cipm.consistency.cpr.pcmjava;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;

public class PcmJavaTypeUtil {
	/**
	 * @return The Java field in javaType, which matches the given inner type
	 */
	public static Field getJavaFieldForIn(DataType pcmInnerType, ConcreteClassifier javaType) {
		return getJavaFieldForIn(pcmInnerType, javaType, true);
	}

	/**
	 * @return The Java field in javaType, which matches the given inner type
	 */
	public static Field getJavaFieldForIn(DataType pcmInnerType, ConcreteClassifier javaType,
			boolean compareInnerTypes) {
		return javaType.getFields().stream().filter((javaF) -> doTypesMatch(pcmInnerType, javaF, compareInnerTypes))
				.findFirst().orElse(null);
	}

	/**
	 * @return The Java field, which matches the given inner declaration
	 */
	public static Field getJavaFieldForIn(InnerDeclaration pcmInnerDeclaration, ConcreteClassifier javaType,
			boolean compareInnerTypes) {
		return javaType.getFields().stream()
				.filter((javaF) -> doTypesMatch(pcmInnerDeclaration, javaF, compareInnerTypes)).findFirst()
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
	public static boolean doTypesMatch(CollectionDataType pcmType, ConcreteClassifier javaType,
			boolean compareInnerTypes) {
		var pcmName = pcmType.getEntityName();
		if (!pcmName.equals(javaType.getName()))
			return false;
		if (!compareInnerTypes)
			return true;

		var matchingField = getJavaFieldForIn(pcmType.getInnerType_CollectionDataType(), javaType, compareInnerTypes);
		return pcmType.getInnerType_CollectionDataType() == null || matchingField != null;
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
	public static boolean doTypesMatch(CompositeDataType pcmType, ConcreteClassifier javaType,
			boolean compareInnerTypes) {
		var pcmName = pcmType.getEntityName();
		if (!pcmName.equals(javaType.getName()))
			return false;
		if (!compareInnerTypes)
			return true;

		return pcmType.getInnerDeclaration_CompositeDataType().stream()
				.allMatch((pcmT) -> getJavaFieldForIn(pcmT, javaType, compareInnerTypes) != null);
	}

	/**
	 * @return Whether pcmType and javaField match, based on their names and types
	 *         they use
	 */
	public static boolean doTypesMatch(InnerDeclaration pcmType, Field javaField, boolean compareInnerTypes) {
		if (!pcmType.getEntityName().equals(javaField.getName()))
			return false;

		if (pcmType.getDatatype_InnerDeclaration() == javaField)
			return true;
		if (pcmType.getDatatype_InnerDeclaration() == null ^ javaField == null)
			return false;

		if (!compareInnerTypes)
			return true;

		var javaTarget = getConcreteClassifierTarget(javaField);
		return javaTarget != null
				&& doTypesMatch(pcmType.getDatatype_InnerDeclaration(), (ConcreteClassifier) javaTarget,
						pcmType.getDatatype_InnerDeclaration() != pcmType.getCompositeDataType_InnerDeclaration());
	}

	/**
	 * @return The declared type of javaField, if it is a ConcreteClassifier.
	 *         Otherwise null
	 */
	private static ConcreteClassifier getConcreteClassifierTarget(Field javaField) {
		var javaFieldTR = javaField.getTypeReference();
		if (javaFieldTR == null)
			return null;
		var javaFieldPCR = javaFieldTR.getPureClassifierReference();
		if (javaFieldPCR == null)
			return null;
		var target = javaFieldPCR.getTarget();
		return target instanceof ConcreteClassifier ? (ConcreteClassifier) target : null;
	}

	/**
	 * @return Whether the given PCM DataType matches the given Java TypeReference
	 * @throws IllegalArgumentException If the PCM DataType is not an instance of
	 *                                  PrimitiveDataType, CompositeDataType or
	 *                                  CollectionDataType
	 */
	public static boolean doTypesMatch(DataType pcmType, ConcreteClassifier javaType) {
		return doTypesMatch(pcmType, javaType, true);
	}

	/**
	 * @return Whether the given PCM DataType matches the given Java TypeReference
	 * @throws IllegalArgumentException If the PCM DataType is not an instance of
	 *                                  PrimitiveDataType, CompositeDataType or
	 *                                  CollectionDataType
	 */
	public static boolean doTypesMatch(DataType pcmType, ConcreteClassifier javaType, boolean compareInnerTypes) {
		if (pcmType instanceof PrimitiveDataType) {
			return doTypesMatch((PrimitiveDataType) pcmType, javaType);
		} else if (pcmType instanceof CompositeDataType) {
			return doTypesMatch((CompositeDataType) pcmType, javaType, compareInnerTypes);
		} else if (pcmType instanceof CollectionDataType) {
			return doTypesMatch((CollectionDataType) pcmType, javaType, compareInnerTypes);
		} else if (pcmType == null) {
			return javaType == null;
		} else {
			throw new IllegalArgumentException("Unknown PCM DataType: " + pcmType);
		}
	}

	/**
	 * Variant of {@link #doTypesMatch(DataType, ConcreteClassifier)} for Java
	 * Fields.
	 */
	public static boolean doTypesMatch(DataType pcmType, Field javaField, boolean compareInnerTypes) {
		var javaInnerType = getConcreteClassifierTarget(javaField);
		return javaInnerType != null && doTypesMatch(pcmType, (ConcreteClassifier) javaInnerType, compareInnerTypes);
	}

	/**
	 * Variant of {@link #doTypesMatch(DataType, ConcreteClassifier)} for Java
	 * TypeReferences.
	 */
	public static boolean doTypesMatch(DataType pcmType, TypeReference javaType) {
		return doTypesMatch(pcmType, (ConcreteClassifier) javaType.getPureClassifierReference().getTarget());
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
}
