package cipm.consistency.fluentapi.extensions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.fluentapi.gen.ModelConstants;

/**
 * Contains static methods that are used from within the generated fluent API.
 * Since fluent API generation strives to be metamodel independent, the use of
 * dynamic EMF and EMF-Reflection become a necessity, which allow navigating the
 * contents of EMF model elements at runtime. Furthermore, using static methods
 * to encapsulate messy or complicated operations reduces the amount of
 * generated code.
 * <p>
 * <p>
 * Note: Changing any public member within this file (i.e. either this class or
 * its methods) requires adapting the generation of fluent api. This is due to
 * Java limitations, which do not allow dynamically adjusting static elements,
 * such as method or class names.
 * 
 * @author Alp Torac Genc
 */
public final class FluentEObjectAPIMethods {
	/**
	 * Invokes the method using EMF-Reflection, which sets
	 * {@code objToModify.feat = featVal}. Only usable on changeable single-valued
	 * features (feat) in objToModify, for featVal of a supported type.
	 * 
	 * @return api
	 */
	public static EObject xWithFeat(EObject api, EObject objToModify, EStructuralFeature feat, Object featVal) {
		var init = getInitialisationForX(api, objToModify);

		var opName = ModelConstants.Initialiation.With.NAME.getFor(StringUtils.capitalize(feat.getName()));
		var withOp = init.eClass().getEOperations().stream().filter((op) -> op.getName().equals(opName)).findFirst()
				.get();
		try {
			init.eInvoke(withOp, new BasicEList<>(Collections.singleton(featVal)));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		FluentAPIInitialisationStorage.dropOngoingInitialisation(init);
		return api;
	}

	/**
	 * Invokes the method using EMF-Reflection, which sets
	 * {@code objToModify.feat = null}. Only usable on changeable single-valued
	 * features (feat) in objToModify.
	 * 
	 * @return api
	 */
	public static EObject xWithoutFeat(EObject api, EObject objToModify, EStructuralFeature feat) {
		var init = getInitialisationForX(api, objToModify);
		var opName = ModelConstants.Initialiation.Without.NAME.getFor(StringUtils.capitalize(feat.getName()));
		var withoutOp = init.eClass().getEOperations().stream().filter((op) -> op.getName().equals(opName)).findFirst()
				.get();
		try {
			init.eInvoke(withoutOp, new BasicEList<>());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		FluentAPIInitialisationStorage.dropOngoingInitialisation(init);
		return api;
	}

	/**
	 * Invokes the method using EMF-Reflection, which sets
	 * {@code objToModify.feat = []}. Only usable on changeable many-valued features
	 * (feat) in objToModify.
	 * 
	 * @return api
	 */
	public static EObject xCleanFeat(EObject api, EObject objToModify, EStructuralFeature feat) {
		var init = getInitialisationForX(api, objToModify);
		var opName = ModelConstants.Initialiation.Clean.NAME.getFor(StringUtils.capitalize(feat.getName()));
		var cleanOp = init.eClass().getEOperations().stream().filter((op) -> op.getName().equals(opName)).findFirst()
				.get();
		try {
			init.eInvoke(cleanOp, new BasicEList<>());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		FluentAPIInitialisationStorage.dropOngoingInitialisation(init);
		return api;
	}

	private static boolean isArrayType(EParameter p) {
		return ((p.getEType() != null && p.getEType().getInstanceClass() != null
				&& p.getEType().getInstanceClass().isArray())
				|| (p.getEGenericType() != null && p.getEGenericType().getERawType() != null
						&& p.getEGenericType().getERawType().getInstanceClass() != null
						&& p.getEGenericType().getERawType().getInstanceClass().isArray()));
	}

	private static boolean isCollectionType(EParameter p) {
		return ((p.getEType() != null && p.getEType().getInstanceClass() != null
				&& Collection.class.isAssignableFrom(p.getEType().getInstanceClass()))
				|| (p.getEGenericType() != null && p.getEGenericType().getERawType() != null
						&& p.getEGenericType().getERawType().getInstanceClass() != null
						&& Collection.class.isAssignableFrom(p.getEGenericType().getERawType().getInstanceClass())));
	}

	private static EOperation getArrayVariant(List<EOperation> ops) {
		return ops.stream().filter((o) -> o.getEParameters().stream().anyMatch((p) -> !p.isMany() && isArrayType(p)))
				.findFirst().get();
	}

	private static EOperation getCollectionVariant(List<EOperation> ops) {
		return ops.stream()
				.filter((o) -> o.getEParameters().stream().anyMatch((p) -> !p.isMany() && isCollectionType(p)))
				.findFirst().get();
	}

	private static EOperation getSingleValueVariant(List<EOperation> ops) {
		return ops.stream()
				.filter((o) -> o.getEParameters().stream().anyMatch((p) -> !p.isMany() && !isCollectionType(p)))
				.findFirst().get();
	}

	private static EOperation getVariantForFeatureValue(Object featVal, List<EOperation> ops) {
		if (featVal.getClass().isArray()) {
			return getArrayVariant(ops);
		} else if (featVal instanceof Collection) {
			return getCollectionVariant(ops);
		} else {
			return getSingleValueVariant(ops);
		}
	}

	/**
	 * Invokes the method using EMF-Reflection, which adds
	 * {@code objToModify.feat += featVal}. Only usable on changeable many-valued
	 * features (feat) in objToModify, for featVal of a supported type.
	 * 
	 * @return api
	 */
	public static EObject xWithAddedFeat(EObject api, EObject objToModify, EStructuralFeature feat, Object featVal) {
		var init = getInitialisationForX(api, objToModify);
		var opName = ModelConstants.Initialiation.WithAdded.NAME.getFor(StringUtils.capitalize(feat.getName()));
		var withAddedOps = init.eClass().getEOperations().stream().filter((op) -> op.getName().equals(opName))
				.collect(Collectors.toList());
		var op = getVariantForFeatureValue(featVal, withAddedOps);
		var argList = new BasicEList<>();

		argList.add(featVal);
		try {
			init.eInvoke(op, argList);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		FluentAPIInitialisationStorage.dropOngoingInitialisation(init);
		return api;
	}

	/**
	 * Invokes the method using EMF-Reflection, which removes
	 * {@code objToModify.feat -= featVal}. Only usable on changeable many-valued
	 * features (feat) in objToModify, for featVal of a supported type.
	 * 
	 * @return api
	 */
	public static EObject xWithRemovedFeat(EObject api, EObject objToModify, EStructuralFeature feat, Object featVal) {
		var init = getInitialisationForX(api, objToModify);
		var opName = ModelConstants.Initialiation.WithRemoved.NAME.getFor(StringUtils.capitalize(feat.getName()));
		var withRemovedOps = init.eClass().getEOperations().stream().filter((op) -> op.getName().equals(opName))
				.collect(Collectors.toList());
		var op = getVariantForFeatureValue(featVal, withRemovedOps);
		var argList = new BasicEList<>();
		argList.add(featVal);
		try {
			init.eInvoke(op, argList);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		FluentAPIInitialisationStorage.dropOngoingInitialisation(init);
		return api;
	}

	/**
	 * @return A list of all classes that the given api instance supports the
	 *         creation / modification of.
	 */
	@SuppressWarnings("unchecked")
	public static EList<Class<? extends EObject>> getAllSupportedClasses(EObject api) {
		var result = new BasicEList<Class<? extends EObject>>();

		var initsPac = api.eClass().getEPackage().getESubpackages().stream()
				.filter((pac) -> pac.getName().equals(ModelConstants.INITIALISATIONS_PACKAGE_NAME.get())).findFirst()
				.get();

		var initEClasses = initsPac.getEClassifiers().stream()
				.filter((eCls) -> eCls instanceof EClass
						&& eCls.getName().endsWith(ModelConstants.INITIALISATION_NAME_SUFFIX.get()))
				.map((eCls) -> (EClass) eCls).collect(Collectors.toList());

		initEClasses.stream().map((eCls) -> eCls.getInstanceClass()).map((cls) -> {
			try {
				return cls.getDeclaredMethod(ModelConstants.SuperInitialisation.CreateNow.NAME.get());
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}).map((met) -> (Class<? extends EObject>) met.getReturnType()).forEach(result::add);

		return result;
	}

	/**
	 * The returned initialisation instance will not have a current element.
	 * 
	 * @return An initialisation instance for the given class eobjCls, which can be
	 *         used to create / modify an instance of that type.
	 */
	public static EObject getInitialisationInstanceForX(EObject api, Class<?> eobjCls) {
		var initsPac = api.eClass().getEPackage().getESubpackages().stream()
				.filter((pac) -> pac.getName().equals(ModelConstants.INITIALISATIONS_PACKAGE_NAME.get())).findFirst()
				.get();

		var initEClass = (EClass) initsPac.getEClassifiers().stream().filter((eCls) -> eCls instanceof EClass)
				.map((eCls) -> (EClass) eCls).filter((eCls) -> isInitialisationFor(eCls, eobjCls)).findFirst().get();
		var initInstance = initEClass.getEPackage().getEFactoryInstance().create(initEClass);
		initInstance.eSet(
				initInstance.eClass().getEStructuralFeature(ModelConstants.SuperInitialisation.RootAPI.NAME.get()),
				api);
		FluentAPIInitialisationStorage.addOngoingInitialisation(initInstance);
		return initInstance;
	}

	/**
	 * The returned initialisation instance will have a current element.
	 * 
	 * @return An initialisation instance for the given class eobjCls, which can be
	 *         used to create / modify an instance of that type
	 */
	public static EObject getInitialisationInstanceForXWithNewElement(EObject api, Class<?> eobjCls) {
		var initInstance = getInitialisationInstanceForX(api, eobjCls);
		var newElemOp = initInstance.eClass().getEOperations().stream()
				.filter((op) -> op.getName().equals(ModelConstants.Initialiation.NewElement.NAME.get())).findFirst()
				.get();
		try {
			initInstance.eInvoke(newElemOp, new BasicEList<>());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return initInstance;
	}

	/**
	 * The returned initialisation instance will have a current element.
	 * 
	 * @return An initialisation instance for the given EClass eCls, which can be
	 *         used to create / modify an instance of that type
	 */
	public static EObject getInitialisationForX(EObject api, EClass eCls) {
		return getInitialisationForX(api, eCls.getInstanceClass());
	}

	/**
	 * The returned initialisation instance will have a current element.
	 * 
	 * @return An initialisation instance for the given class eobjCls, which can be
	 *         used to create / modify an instance of that type
	 */
	public static EObject getInitialisationForX(EObject api, Class<?> eobjCls) {
		return getInitialisationInstanceForXWithNewElement(api, eobjCls);
	}

	/**
	 * The returned initialisation instance will have a current element.
	 * 
	 * @return An initialisation instance for the given EObject eobjToInit, which
	 *         can be used to create / modify an instance of that type
	 */
	public static EObject getInitialisationForX(EObject api, EObject eobjToInit) {
		var initInstance = getInitialisationInstanceForX(api, eobjToInit.eClass().getInstanceClass());

		initInstance.eSet(initInstance.eClass()
				.getEStructuralFeature(ModelConstants.SuperInitialisation.CurrentElement.NAME.get()), eobjToInit);

		return initInstance;
	}

	/**
	 * @return Whether the given initialisation EClass initECls can be used on an
	 *         instance of the given type eobjCls
	 */
	public static boolean isInitialisationFor(EClass initECls, Class<?> eobjCls) {
		return !initECls.isAbstract() && initECls.getName()
				.substring(0, initECls.getName().length() - ModelConstants.INITIALISATION_NAME_SUFFIX.get().length())
				.equals(eobjCls.getSimpleName());
	}

	/**
	 * @return The most recent ongoing initialisation instance for the given class
	 *         eobjCls present under {@link FluentAPIInitialisationStorage}
	 */
	public static EObject continueElement(Class<?> eobjCls) {
		var initsOfMatchingType = FluentAPIInitialisationStorage.getOngoingInitialisations().stream()
				.filter((i) -> isInitialisationFor(i.eClass(), eobjCls))
				.collect(Collectors.toCollection(ArrayList::new));
		return !initsOfMatchingType.isEmpty() ? initsOfMatchingType.get(initsOfMatchingType.size() - 1) : null;
	}
}
