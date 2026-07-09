package cipm.consistency.fluentapi.gen;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * Utility class providing static methods for generating EMF elements during
 * fluent API code generation.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIGenerationUtil {
	/**
	 * A map of EClass representations of primitive types to EClass representations
	 * of their wrapper type.
	 */
	private static final Map<EClassifier, EClassifier> primitiveEClsToWrapperEClsMap;

	static {
		primitiveEClsToWrapperEClsMap = new HashMap<>();
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.EINT, EcorePackage.Literals.EINTEGER_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.ELONG, EcorePackage.Literals.ELONG_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.EFLOAT, EcorePackage.Literals.EFLOAT_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.EDOUBLE, EcorePackage.Literals.EDOUBLE_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.EBOOLEAN, EcorePackage.Literals.EBOOLEAN_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.EBYTE, EcorePackage.Literals.EBYTE_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.ESHORT, EcorePackage.Literals.ESHORT_OBJECT);
		primitiveEClsToWrapperEClsMap.put(EcorePackage.Literals.ECHAR, EcorePackage.Literals.ECHARACTER_OBJECT);
	}

	/**
	 * Note: This method yields a best-effort result by looking at the EPackage
	 * structure of the given EClass. To this end, the assumption is that all
	 * (parent) packages of the class represented by the given EClass also have a
	 * corresponding EPackage and that the Java code structure of the generated
	 * class is reflected in its EMF model:
	 * <p>
	 * <p>
	 * Given EClass eCls representing the Java class ns1.ns2.ns3.Cls, if the EMF
	 * containment tree of eCls is not ns1 -> ns2 -> ns3 -> eCls (with nsI being
	 * EPackages) and {@code eCls.getInstanceClass() == null}, the returned fully
	 * qualified name will be incorrect.
	 * 
	 * @return The fully qualified name for the given EClass. If
	 *         {@code eCls.getInstanceClass() != null}, returns the name of the
	 *         contained instance class. Otherwise, returns the fully qualified name
	 *         based on the EPackage of the given EClass and super EPackages
	 *         thereof.
	 */
	public static String getFullyQualifiedEClassName(FluentAPIGenerationContext context, EClass eCls) {
		// Attempt to get the namespaces from the potentially underlying instance class
		if (eCls.getInstanceClass() != null)
			return eCls.getInstanceClass().getName();
		if (eCls.getInstanceClassName() != null)
			return eCls.getInstanceClassName();
		if (eCls.getInstanceTypeName() != null)
			return eCls.getInstanceTypeName();

		// Attempt to get the namespaces from super packages
		String result = eCls.getName();
		var pac = eCls.getEPackage();
		while (pac != null) {
			result = pac.getName() + "." + result;
			pac = pac.getESuperPackage();
		}

		// Append the base package name, if set
		if (context != null) {
			result = context.getBasePackageName() + "." + result;
		}
		return result;
	}

	/**
	 * A variant of
	 * {@link #getFullyQualifiedEClassName(FluentAPIGenerationContext, EClass)}
	 * without a context object. This variant is meant for EClasses that do not
	 * belong to the fluent API.
	 */
	public static String getFullyQualifiedEClassName(EClass eCls) {
		return getFullyQualifiedEClassName(null, eCls);
	}

	/**
	 * Use {@code genTypeArgument == null} in order to generate a wildcard type
	 * argument.
	 * 
	 * @return An EGenericType instance representing
	 *         {@code genericType<genTypeArgument>}
	 */
	public static EGenericType generateEGenericTypeWithTypeArgument(FluentAPIGenerationContext context,
			Class<?> genericType, EGenericType genTypeArgument) {
		var pureGenType = createOrGetEDataType(context, genericType,
				new ETypeParameter[] { FluentAPIGenerationUtil.generateETypeParameter("T") });
		var genType = FluentAPIGenerationUtil.generateEGenericTypeWithClassifier(pureGenType);
		FluentAPIGenerationUtil.addTypeArgument(genType, genTypeArgument);
		return genType;
	}

	/**
	 * Use {@code colTypeArgument == null} in order to generate a wildcard type
	 * argument.
	 * 
	 * @return An EGenericType instance representing
	 *         {@code Collection<colTypeArgument>}
	 */
	public static EGenericType generateCollectionTypeWithTypeArgument(FluentAPIGenerationContext context,
			EGenericType colTypeArgument) {
		return generateEGenericTypeWithTypeArgument(context, Collection.class, colTypeArgument);
	}

	/**
	 * Use {@code colTypeArgument == null} in order to generate a wildcard type
	 * argument.
	 * 
	 * @return An EGenericType instance representing
	 *         {@code Collection<colTypeArgument>}
	 */
	public static EGenericType generateCollectionTypeParameter(FluentAPIGenerationContext context,
			EClassifier colTypeArgument) {
		// Wrap the given EClassifier, if it is a primitive type
		var colExtendsType = primitiveEClsToWrapperEClsMap.getOrDefault(colTypeArgument, colTypeArgument);
		var colGenTypeArgument = colExtendsType != null
				? FluentAPIGenerationUtil.generateEGenericTypeWithBounds(null,
						FluentAPIGenerationUtil.generateEGenericTypeWithClassifier(colExtendsType))
				: FluentAPIGenerationUtil.generateWildcardTypeArgument();
		return generateCollectionTypeWithTypeArgument(context, colGenTypeArgument);
	}

	/**
	 * The type of the EParameter must be set separately.
	 * 
	 * @param name Name of the EParameter
	 * @return An EParameter instance that considers a single object as value
	 */
	public static EParameter generateSingleValuedEParameter(String name) {
		var param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName(name);
		param.setLowerBound(1);
		param.setUpperBound(1);
		return param;
	}

	/**
	 * @param context The object encapsulating the context of fluent api generation.
	 *                Needed to adapt the given type for EMF.
	 * @param name    Name of the EParameter
	 * @param type    Type of the EParameter
	 * @return An EParameter instance that considers a single instance of the given
	 *         type as value
	 */
	public static EParameter generateSingleValuedEParameter(FluentAPIGenerationContext context, String name,
			Class<?> type) {
		return generateSingleValuedEParameter(name, createOrGetEDataType(context, type));
	}

	/**
	 * @param name Name of the EParameter
	 * @param type Type of the EParameter
	 * @return An EParameter instance that considers a single instance of the given
	 *         type as value
	 */
	public static EParameter generateSingleValuedEParameter(String name, EClassifier type) {
		var param = generateSingleValuedEParameter(name);
		param.setEType(type);
		return param;
	}

	/**
	 * @param name Name of the EParameter
	 * @param type Type of the EParameter
	 * @return An EParameter instance that considers a single instance of the given
	 *         type as value
	 */
	public static EParameter generateSingleValuedEParameter(String name, EGenericType type) {
		var param = generateSingleValuedEParameter(name);
		param.setEGenericType(type);
		return param;
	}

	/**
	 * @param elem          A given EMF element
	 * @param documentation The documentation to add
	 * @return elem
	 */
	public static <T extends EModelElement> T addDocumentation(T elem, String documentation) {
		var anno = createOrGetEAnnotation(elem);
		// Add the documentation
		anno.getDetails().put(ModelConstants.GEN_MODEL_DOC_KEY.get(), documentation);
		if (!elem.getEAnnotations().contains(anno))
			elem.getEAnnotations().add(anno);
		return elem;
	}

	/**
	 * @param elem      A given EMF element
	 * @param docSource Another given EMF element, whose documentation will be
	 *                  copied and used in elem
	 * @return elem
	 */
	public static <T extends EModelElement> T useDocumentationOf(T elem, EModelElement docSource) {
		if (!docSource.getEAnnotations().isEmpty()) {
			var anno = docSource.getEAnnotations().get(0);
			if (anno.getDetails().containsKey(ModelConstants.GEN_MODEL_DOC_KEY.get())) {
				var doc = anno.getDetails().get(ModelConstants.GEN_MODEL_DOC_KEY.get());
				addDocumentation(elem, doc);
			}
		}
		return elem;
	}

	/**
	 * @param elem A given EMF element
	 * @return The documentation of elem. If there elem has no documentation,
	 *         returns null.
	 */
	public static <T extends EModelElement> String getDocumentationOf(T elem) {
		var genModelSourceURL = ModelConstants.GEN_MODEL_SOURCE_URL.get();
		var anno = elem.getEAnnotation(genModelSourceURL);
		if (anno == null) {
			return null;
		} else {
			return anno.getDetails().get(ModelConstants.GEN_MODEL_DOC_KEY.get());
		}
	}

	/**
	 * @param elem       A given EMF element
	 * @param typeParams Type parameters to add to elem
	 * @return elem
	 */
	public static <T extends EOperation> T addTypeParameters(T elem, ETypeParameter... typeParams) {
		if (typeParams != null)
			for (var tp : typeParams)
				elem.getETypeParameters().add(tp);
		return elem;
	}

	/**
	 * @param context The object encapsulating the context of fluent api generation.
	 *                Needed to adapt the array type to EMF.
	 * @param name    Name of the EParameter
	 * @param type    Type of the EParameter
	 * @return An EParameter that takes an array of given type
	 */
	public static EParameter generateArrayValuedEParameter(FluentAPIGenerationContext context, String name,
			EClassifier type) {
		var arrayType = createOrGetArrayEDataType(context, type);
		var param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName(name);
		param.setEType(arrayType);
		param.setLowerBound(1);
		param.setUpperBound(1);
		return param;
	}

	/**
	 * @param name Name of the EOperation
	 * @return An EOperation with the given name
	 */
	public static EOperation generateEOperation(String name) {
		var op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName(name);
		return op;
	}

	/**
	 * @param name       Name of the EOperation
	 * @param returnType The return type of the EOperation
	 * @return An EOperation with the given name and return type
	 */
	public static EOperation generateEOperation(String name, EClassifier returnType) {
		var op = EcoreFactory.eINSTANCE.createEOperation();
		op.setEType(returnType);
		op.setName(name);
		return op;
	}

	/**
	 * @param name       Name of the EOperation
	 * @param returnType The return type of the EOperation
	 * @return An EOperation with the given name and return type
	 */
	public static EOperation generateEOperation(String name, EGenericType returnType) {
		var op = EcoreFactory.eINSTANCE.createEOperation();
		op.setEGenericType(returnType);
		op.setName(name);
		return op;
	}

	/**
	 * @param typeParameterName The name of the ETypeParameter
	 * @return An ETypeParameter with the given name
	 */
	public static ETypeParameter generateETypeParameter(String typeParameterName) {
		var typeParam = EcoreFactory.eINSTANCE.createETypeParameter();
		typeParam.setName(typeParameterName);
		return typeParam;
	}

	/**
	 * @return An EGenericType instance representing
	 *         {@code genericType<genTypeArgument>}
	 */
	public static EGenericType generateWildcardTypeArgument() {
		return generateEGenericTypeWithBounds(null, null);
	}

	/**
	 * The EClassifier ECls of the generated EGenericType must be set separately
	 * <p>
	 * <p>
	 * Do not use with {@code T = eStructuralFeature.getEGenericType()}, as it will
	 * move the type of the feature into the generated EGenericType instance. Use a
	 * fresh EGenericType that uses T as its EClassifier instead.
	 * <p>
	 * <p>
	 * {@code lowerBound = upperBound = null} will result in wildcard "?"
	 * 
	 * @param lowerBound "T" in "? super T"
	 * @param upperBound "T" in "? extends T"
	 * @return An EGenericType instance representing a bounded generic type A, which
	 *         is between lowerBound and upperBound in its type hierarchy.
	 */
	public static EGenericType generateEGenericTypeWithBounds(EGenericType lowerBound, EGenericType upperBound) {
		var genericParamTypeForJavaClass = EcoreFactory.eINSTANCE.createEGenericType();
		genericParamTypeForJavaClass.setELowerBound(lowerBound);
		genericParamTypeForJavaClass.setEUpperBound(upperBound);
		return genericParamTypeForJavaClass;
	}

	/**
	 * @param genericType "Type" in "Type<...>"
	 * @return An EGenericType instance representing {@code genericType<...>}
	 */
	public static EGenericType generateEGenericTypeWithClassifier(EClassifier genericType) {
		var genericParamTypeForJavaClass = EcoreFactory.eINSTANCE.createEGenericType();
		genericParamTypeForJavaClass.setEClassifier(genericType);
		return genericParamTypeForJavaClass;
	}

	/**
	 * Similar to {@link #generateEGenericTypeWithClassifier(EClassifier)} but uses
	 * ETypeParameter instead of EClassifier.
	 * 
	 * @param genericType "Type" in "Type<...>"
	 * @return An EGenericType instance representing {@code genericType<...>}
	 */
	public static EGenericType generateEGenericTypeWithTypeParameter(ETypeParameter typeParameter) {
		var genericParamTypeForJavaClass = EcoreFactory.eINSTANCE.createEGenericType();
		genericParamTypeForJavaClass.setETypeParameter(typeParameter);
		return genericParamTypeForJavaClass;
	}

	/**
	 * @param genericType   "Type" in "Type<...>"
	 * @param typeArguments "..." in "Type<...>"
	 * @return An EGenericType representing {@code genericType<typeArguments>}
	 */
	public static EGenericType addTypeArgument(EGenericType genericType, EGenericType... typeArguments) {
		if (typeArguments != null) {
			for (var ta : typeArguments)
				genericType.getETypeArguments().add(ta);
		}
		return genericType;
	}

	/**
	 * @param elem A given EMF element
	 * @return Returns the EAnnotation with the source
	 *         {@link ModelConstants.GEN_MODEL_SOURCE_URL} in elem. If non-existent,
	 *         creates the EAnnotation first.
	 */
	private static EAnnotation createOrGetEAnnotation(EModelElement elem) {
		EAnnotation anno = null;
		var genModelSourceURL = ModelConstants.GEN_MODEL_SOURCE_URL.get();
		if (elem.getEAnnotation(genModelSourceURL) == null) {
			anno = EcoreFactory.eINSTANCE.createEAnnotation();
			anno.setSource(genModelSourceURL);
		} else {
			anno = elem.getEAnnotation(genModelSourceURL);
		}
		return anno;
	}

	/**
	 * Adds the "body" key to the EAnnotation of the given elem, usually
	 * EOperations. Using this operation on EOperations sets their method body.
	 * 
	 * @param elem A given EMF element
	 * @param body The value of the "body" key
	 * @return elem
	 */
	public static <T extends EModelElement> T addBody(T elem, String body) {
		var anno = createOrGetEAnnotation(elem);
		// Add the body
		anno.getDetails().put(ModelConstants.GEN_MODEL_BODY_KEY.get(), body);
		if (!elem.getEAnnotations().contains(anno))
			elem.getEAnnotations().add(anno);
		return elem;
	}

	/**
	 * Adds the given params to the given op
	 * 
	 * @param op     A given EOperation
	 * @param params The EParameters of the EOperation
	 * @return op
	 */
	public static EOperation addEParameters(EOperation op, EParameter... params) {
		if (params != null) {
			for (var param : params)
				op.getEParameters().add(param);
		}
		return op;
	}

	/**
	 * @param parentPac      The parent EPackage that will contain the generated
	 *                       EPackage
	 * @param subPackageName The name of the EPackage to generate (not its fully
	 *                       qualified name)
	 * @return The generated EPackage
	 */
	public static EPackage generateSubPackage(EPackage parentPac, String subPackageName) {
		var pac = EcoreFactory.eINSTANCE.createEPackage();
		pac.setName(subPackageName);
		pac.setNsPrefix(subPackageName);

		var nsUri = URI.createURI(parentPac.getNsURI()).appendSegment(subPackageName);
		pac.setNsURI(nsUri.toString());
		parentPac.getESubpackages().add(pac);
		return pac;
	}

	/**
	 * @param context        The object encapsulating the context of fluent api
	 *                       generation. Needed to adapt the given type for EMF.
	 * @param type           A given type that will be adapted for EMF
	 * @param typeParameters ETypeParameter instances representing the type
	 *                       parameters of the given type
	 * @return An EDataType instance representing {@code type<typeParameters>}
	 */
	public static EDataType createOrGetEDataType(FluentAPIGenerationContext context, Class<?> type,
			ETypeParameter[] typeParameters) {
		var eDataTypeName = type.getSimpleName() + ModelConstants.EDATATYPE_WRAPPER_NAME_SUFFIX.get();
		EDataType eDataType = (EDataType) context.getPlaceholderEDataTypesPac().getEClassifier(eDataTypeName);

		if (eDataType == null) {
			eDataType = EcoreFactory.eINSTANCE.createEDataType();
			eDataType.setSerializable(false);
			eDataType.setName(eDataTypeName);
			eDataType.setInstanceTypeName(eDataTypeName);
			eDataType.setInstanceClassName(eDataTypeName);
			eDataType.setInstanceClass(type);

			if (typeParameters != null)
				for (var t : typeParameters)
					eDataType.getETypeParameters().add(t);

			context.getPlaceholderEDataTypesPac().getEClassifiers().add(eDataType);
		}

		return eDataType;
	}

	/**
	 * @param context The object encapsulating the context of fluent api generation.
	 *                Needed to adapt the given type for EMF.
	 * @param type    A given type that will be adapted for EMF
	 * @return An EDataType that adapts type for EMF
	 */
	public static EDataType createOrGetEDataType(FluentAPIGenerationContext context, Class<?> type) {
		return createOrGetEDataType(context, type, null);
	}

	/**
	 * @param context The object encapsulating the context of fluent api generation.
	 *                Needed to adapt the array type for EMF.
	 * @param type    A given EMF type, for which an array type will be found /
	 *                generated
	 * @return An EDataType that represents an array of given type
	 */
	public static EDataType createOrGetArrayEDataType(FluentAPIGenerationContext context, EClassifier type) {
		var arrayEDataTypeName = type.getName() + ModelConstants.EDATATYPE_ARRAY_WRAPPER_NAME_SUFFIX.get();
		var arrayTypeInstanceTypeName = type.getName() + ModelConstants.EDATATYPE_ARRAY_WRAPPER_TYPE_NAME_SUFFIX.get();
		EDataType arrayType = (EDataType) context.getPlaceholderEDataTypesPac().getEClassifier(arrayEDataTypeName);

		if (arrayType == null) {
			arrayType = EcoreFactory.eINSTANCE.createEDataType();
			arrayType.setSerializable(false);
			arrayType.setName(arrayEDataTypeName);
			arrayType.setInstanceTypeName(arrayTypeInstanceTypeName);
			arrayType.setInstanceClassName(arrayTypeInstanceTypeName);
			// Get array type this way, since cls.arrayType() is introduced in Java 12
			arrayType.setInstanceClass(Array.newInstance(type.getInstanceClass(), 0).getClass());
			context.getPlaceholderEDataTypesPac().getEClassifiers().add(arrayType);
		}

		return arrayType;
	}
}
