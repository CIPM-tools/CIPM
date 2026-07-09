package cipm.consistency.fluentapi.gen;

import org.apache.commons.lang.StringUtils;

/**
 * Contains all constants associated with the fluent API. All of the constant
 * templates are stored within {@link IFluentAPITemplate} instances, where some
 * templates are fixed (meaning that their values are not changeable) and some
 * templates are fillable with parameters ({@link IFluentAPIFillableTemplate}).
 * For fillable templates, refer to their JavaDoc to find out what (String)
 * parameters they need.
 * 
 * @author Alp Torac Genc
 */
public class ModelConstants {
	/**
	 * The URI of the root package of the fluent API, which contains the fluent API
	 * class.
	 * <p>
	 * %s: Metamodel name (lower case)
	 */
	public static final IFluentAPIFillableTemplate ROOT_PACKAGE_URI = new FluentAPIFillableTemplate(
			"https://CIPM-tools.github.io/metamodels/fluentapi/1.0/%s/api");

	/**
	 * The base package name for the fluent API. Note that the base packages will
	 * not be explicitly generated during the generation process.
	 * <p>
	 * %s: Metamodel name (lower case)
	 */
	public static final IFluentAPIFillableTemplate BASE_PACKAGE_NAME = new FluentAPIFillableTemplate(
			"cipm.consistency.fluentapi.%s");
	/**
	 * The name of the root package of the fluent API (does not include its
	 * namespaces). This is where the fluent API class (facade of the fluent API)
	 * will be located.
	 * 
	 * @see {@link #BASE_PACKAGE_NAME}
	 * @see {@link #FULL_ROOT_PACKAGE_NAME}
	 */
	public static final IFluentAPITemplate ROOT_PACKAGE_NAME = new FluentAPIFixTemplate("api");
	/**
	 * The name of the root package of the fluent API (includes its namespaces).
	 * This is where the fluent API class (facade of the fluent API) will be
	 * located.
	 * <p>
	 * %s: Metamodel name (lower case)
	 * 
	 * @see {@link #BASE_PACKAGE_NAME}
	 * @see {@link #ROOT_PACKAGE_NAME}
	 */
	public static final IFluentAPIFillableTemplate FULL_ROOT_PACKAGE_NAME = new FluentAPIFillableTemplate(
			BASE_PACKAGE_NAME.get() + "." + ROOT_PACKAGE_NAME.get());

	/**
	 * The name of the package, which will contain the Initialisation classes. This
	 * package is nested under {@link #ROOT_PACKAGE_NAME}.
	 */
	public static final IFluentAPITemplate INITIALISATIONS_PACKAGE_NAME = new FluentAPIFixTemplate("inits");

	/**
	 * An indicator in operation names in fluent api, signaling that the method can
	 * be used for arbitrary types of the metamodel the fluent api is meant for.
	 */
	public static final IFluentAPITemplate PLACEHOLDER = new FluentAPIFixTemplate("x");
	/**
	 * The "body" key of the EAnnotation, whose value stands for the method body in
	 * EOperations
	 */
	public static final IFluentAPITemplate GEN_MODEL_BODY_KEY = new FluentAPIFixTemplate("body");
	/**
	 * The "documentation" key of the EAnnotation, whose value stands for the
	 * documentation of the EMF elements of the fluent api model
	 */
	public static final IFluentAPITemplate GEN_MODEL_DOC_KEY = new FluentAPIFixTemplate("documentation");
	/**
	 * The source of the EAnnotation that is used in EMF elements of the fluent api
	 * model
	 */
	public static final IFluentAPITemplate GEN_MODEL_SOURCE_URL = new FluentAPIFixTemplate(
			"http://www.eclipse.org/emf/2002/GenModel");
	/**
	 * The suffix in the names of classes in fluent api, which are responsible for
	 * creating / modifying elements of the metamodel the fluent api is meant for.
	 */
	public static final IFluentAPITemplate INITIALISATION_NAME_SUFFIX = new FluentAPIFixTemplate("Initialisation");

	/**
	 * The suffix, which EDataTypes for array-types will get in their name
	 */
	public static final IFluentAPITemplate EDATATYPE_ARRAY_WRAPPER_NAME_SUFFIX = new FluentAPIFixTemplate("Array");
	/**
	 * The suffix, which type names in EDataTypes for array-types will get
	 */
	public static final IFluentAPITemplate EDATATYPE_ARRAY_WRAPPER_TYPE_NAME_SUFFIX = new FluentAPIFixTemplate("[]");
	/**
	 * The name suffix, which EDataTypes adapting non-EMF types for EMF will have
	 */
	public static final IFluentAPITemplate EDATATYPE_WRAPPER_NAME_SUFFIX = new FluentAPIFixTemplate(
			"EDataTypePlaceholder");
	/**
	 * The name of the package, which contains the EDataTypes adapting non-EMF types
	 * for EMF
	 */
	public static final IFluentAPITemplate EDATATYPE_WRAPPERS_PACKAGE_NAME = new FluentAPIFixTemplate(
			"placeholderTypes");

	/**
	 * An inner class for recurring EParameters within the fluent api model
	 * 
	 * @author Alp Torac Genc
	 */
	public static class GeneralParameters {
		public static final IFluentAPITemplate USED_EOBJECT_PARAMETER_NAME = new FluentAPIFixTemplate("eobj");
		public static final IFluentAPITemplate USED_EOBJECT_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The EObject that this method will use");

		public static final IFluentAPITemplate MODIFIED_FEATURE_PARAMETER_NAME = new FluentAPIFixTemplate(
				"featToModify");
		public static final IFluentAPITemplate MODIFIED_FEATURE_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The feature, whose value in " + MODIFIED_FEATURE_PARAMETER_NAME.get() + " will be modified");

		public static final IFluentAPITemplate FEATURE_VALUE_PARAMETER_NAME = new FluentAPIFixTemplate("featVal");
		public static final IFluentAPITemplate FEATURE_VALUE_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The value of the feature, which will be used to modify the given feature in certain ways, denoted by the method name. If "
						+ FEATURE_VALUE_PARAMETER_NAME.get()
						+ " is an array or collection, its contents will be used as feature values instead.");

		public static final IFluentAPITemplate MARK_VALUE_PARAMETER_NAME = new FluentAPIFixTemplate("markVal");
		public static final IFluentAPITemplate MARK_VALUE_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The object that is / will be marked / unmarked.");

		public static final IFluentAPITemplate MARK_KEY_PARAMETER_NAME = new FluentAPIFixTemplate("markKey");
		public static final IFluentAPITemplate MARK_KEY_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The object instance (key), whose memory address is serving / will serve as a key in mark-related operations. Note that the contents of the key are fully irrelevant here, only its memory address matters. A key may only mark one element at a time, attempting to use the same key in multiple marks will override its previous mark. If the key is array-typed or collection-typed, it will be interpret as a container of keys and its contents will be used instead. To bypass this behaviour and use the key (the array / collection) itself, nest the key in another array or collection.");

		public static final Class<?> WAIT_FOR_MARK_TASK_CLASS = Runnable.class;
		public static final IFluentAPITemplate WAIT_FOR_MARK_TASK_PARAMETER_NAME = new FluentAPIFixTemplate("task");
		public static final IFluentAPITemplate WAIT_FOR_MARK_TASK_PARAMETER_NAME_DOC = new FluentAPIFixTemplate(
				"The model construction task, which will be executed upon the given " + MARK_KEY_PARAMETER_NAME.get()
						+ "(s) getting used in marks.");

		public static final IFluentAPITemplate ARBITRARY_ECLASS_PARAMETER_NAME = new FluentAPIFixTemplate("eObjEClass");
		public static final IFluentAPITemplate ARBITRARY_ECLASS_PARAMETER_DOC = new FluentAPIFixTemplate(
				"The EClass of the element");
		public static final IFluentAPITemplate ARBITRARY_CLASS_PARAMETER_NAME = new FluentAPIFixTemplate("eObjCls");
		public static final IFluentAPITemplate ARBITRARY_CLASS_PARAMETER_DOC = new FluentAPIFixTemplate(
				"The class of the element");

		public static final IFluentAPITemplate TYPE_PARAMETER_NAME = new FluentAPIFixTemplate("T");
	}

	private static String getMethodName(Class<?> cls) {
		return StringUtils.uncapitalize(cls.getSimpleName());
	}

	private static String getFeatureName(Class<?> cls) {
		return StringUtils.uncapitalize(cls.getSimpleName());
	}

	private static String getTopMethodName(Class<?> cls) {
		return getMethodName(cls) + StringUtils.capitalize(ModelConstants.PLACEHOLDER.get());
	}

	/**
	 * Aggregates constants of the fluent api class. Inner classes are the names of
	 * the methods of the fluent api class.
	 * <p>
	 * <p>
	 * The fluent api class serves as the facade of the fluent api and provides
	 * methods to ease construction of models of the metamodel that the fluent api
	 * is generated for.
	 * 
	 * @author Alp Torac Genc
	 */
	public static class FluentAPI {
		/**
		 * %s: Metamodel name (capitalised)
		 */
		public static final IFluentAPIFillableTemplate CLASS_NAME = new FluentAPIFillableTemplate("Fluent%sAPI");

		public static final IFluentAPIFillableTemplate CLASS_DOC = new FluentAPIFillableTemplate("<p>"
				+ ModelConstants.FluentAPI.CLASS_NAME.getEmpty()
				+ " is at the center of the fluent API and enables creation of EObject sub-types within the EMF-based metamodel MM this API targets. To this end, this class offers various methods that lead to underlying "
				+ ModelConstants.INITIALISATION_NAME_SUFFIX.get()
				+ " classes, each being responsible for a concrete element from MM. For more information on what individual EObject sub-types and their features represent, refer to MM's documentation."
				+ FluentAPIDocumentationUtil.getClassMethodOverviewIntroTemplate());

		public static class Continue {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(
					getMethodName(Continue.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");

			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(
					getTopMethodName(Continue.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate("Delegates to the last "
					+ ModelConstants.INITIALISATION_NAME_SUFFIX.get()
					+ " instance for the element of a certain type, allowing the ongoing element creation to continue");
		}

		public static class ContinueMarked {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(
					getMethodName(ContinueMarked.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");

			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(
					getTopMethodName(ContinueMarked.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Delegates to the " + ModelConstants.INITIALISATION_NAME_SUFFIX.get()
							+ " instance for the element of a certain type, which has been marked with the given "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ " , allowing the ongoing element creation to continue");
		}

		public static class CreateNew {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(
					getMethodName(CreateNew.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");
			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(
					getTopMethodName(CreateNew.class));

			/**
			 * %s: Class name
			 * <p>
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate SUMMARY = new FluentAPIFillableTemplate(
					"Creates a minimal, fully uninitialised %s instance, in order to compact the following into a single method call: "
							+ ModelConstants.FluentAPI.New.NAME.thisCallFor(new String[] { "%s" })
							+ ModelConstants.SuperInitialisation.CreateNow.NAME.call());
		}

		public static class DropInitialisation {
			public static final IFluentAPITemplate INITIALISATION_PARAMETER_NAME = new FluentAPIFixTemplate(
					"initToDrop");
			public static final IFluentAPITemplate INITIALISATION_PARAMETER_DOC = new FluentAPIFixTemplate(
					"The " + ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " instance to drop from ongoing "
							+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + "s");
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(DropInitialisation.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Removes the given " + INITIALISATION_PARAMETER_NAME.get() + " from the list of ongoing "
							+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + "s, meaning that "
							+ INITIALISATION_PARAMETER_NAME.get() + " will no longer be retrievable from the api.");
		}

		public static class GetAllSupportedClasses {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(GetAllSupportedClasses.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Returns a list of all Classes that this " + ModelConstants.FluentAPI.CLASS_NAME.getEmpty()
							+ " instance supports");
		}

		public static class GetInitialisationFor {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(GetInitialisationFor.class)
							+ StringUtils.capitalize(ModelConstants.PLACEHOLDER.get()));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate("Returns an instance of "
					+ ModelConstants.INITIALISATION_NAME_SUFFIX.get()
					+ " matching the given parameter, which can be used to create or modify a certain element. Not intended to be called directly from outside under normal circumstances");
		}

		public static class GetMarked {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(
					getMethodName(GetMarked.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");

			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(
					getTopMethodName(GetMarked.class));
			public static final IFluentAPIFillableTemplate SUMMARY = new FluentAPIFillableTemplate(
					"Returns the element of a certain type, which has been marked with the given "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get());
		}

		public static class Unmark {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(Unmark.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Removes the given " + ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ "'s marking, does not modify the (formerly) marked object.");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(FluentAPIDocumentationUtil
					.appendToDocumentationStart(SUMMARY.get()) + "Removes any associations between the given "
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
					+ " and its corresponding EObject obj. Doing so unmarks obj, meaning that "
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
					+ " can no longer be used to retrieve obj. Does nothing, if this API instance did not mark obj with "
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ".");
		}

		public static class Modify {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(getMethodName(Modify.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");
			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(getTopMethodName(Modify.class));
			public static final IFluentAPIFillableTemplate SUMMARY = new FluentAPIFillableTemplate(
					"Returns a matching " + ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " instance for the given "
							+ ModelConstants.GeneralParameters.USED_EOBJECT_PARAMETER_NAME.get()
							+ ", which can be used to modify it");
		}

		public static class ModifyMarked {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(
					getMethodName(ModifyMarked.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");
			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(
					getTopMethodName(ModifyMarked.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Returns a matching " + ModelConstants.INITIALISATION_NAME_SUFFIX.get()
							+ " instance for the element marked with the given "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ ", which can be used to modify the marked element further");
		}

		public static class New {
			public static final IFluentAPITemplate NAME_PREFIX = new FluentAPIFixTemplate(getMethodName(New.class));
			/**
			 * %s: Class name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					NAME_PREFIX.get() + "%s");
			public static final IFluentAPITemplate TOP_NAME = new FluentAPIFixTemplate(getTopMethodName(New.class));

			public static final IFluentAPITemplate FEATURE_VALUE_PARAMETER_NAME = new FluentAPIFixTemplate("featVal");
			/**
			 * %s: Class name
			 * <p>
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate FEATURE_VALUE_PARAMETER_DOC = new FluentAPIFillableTemplate(
					"The value of the feature '%s.%s'. If " + FEATURE_VALUE_PARAMETER_NAME.get()
							+ " is an array or collection, its contents will be used as the feature values instead.");

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Returns a matching " + ModelConstants.INITIALISATION_NAME_SUFFIX.get()
							+ " instance, which can be used to create an element of a certain type from scratch");
		}

		public static class WaitForMark {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(WaitForMark.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Postphones certain model construction steps till the given mark(s) ("
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ") exist.");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					"Allows specifying model construction steps as a "
							+ ModelConstants.GeneralParameters.WAIT_FOR_MARK_TASK_CLASS.getSimpleName()
							+ " instance, which this " + ModelConstants.FluentAPI.CLASS_NAME.getEmpty()
							+ " will execute after the given "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ "(s) have been used to mark objects. This method enables preserving the flow of model construction by enabling the specification of construction steps on objects that may not yet exist. The main purpose of this method is to facilitate model constructions, where dependencies between model elements either forcefully require bottom-up approaches or require mixing the construction of several model elements, which depend on one another (especially circular dependencies).");
		}

		public static class WithFeat {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					ModelConstants.PLACEHOLDER.get() + WithFeat.class.getSimpleName());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Sets the given value of a certain single-valued EStructuralFeature for a certain EObject.");
		}

		public static class WithoutFeat {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					ModelConstants.PLACEHOLDER.get() + WithoutFeat.class.getSimpleName());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Unsets the value of a certain single-valued EStructuralFeature for a certain EObject.");
		}

		public static class WithAddedFeat {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					ModelConstants.PLACEHOLDER.get() + WithAddedFeat.class.getSimpleName());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Adds the given value(s) to a certain many-valued EStructuralFeature for a certain EObject.");
		}

		public static class WithRemovedFeat {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					ModelConstants.PLACEHOLDER.get() + WithRemovedFeat.class.getSimpleName());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Removes the given value(s) from a certain many-valued EStructuralFeature for a certain EObject.");
		}

		public static class CleanFeat {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					ModelConstants.PLACEHOLDER.get() + CleanFeat.class.getSimpleName());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Removes all values of a certain many-valued EStructuralFeature for a certain EObject.");
		}

		public static class Mark {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(Mark.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Marks the given " + ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + " with "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ", does not modify "
							+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ".");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(SUMMARY.get()) + "Associates the given "
							+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + " with "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ ". Doing so marks the given "
							+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ", meaning that using "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ " in mark-related operations will result in retrieving "
							+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ". Note that "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ " can only mark one object ("
							+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ") at a time. Using "
							+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get()
							+ " in another mark will override its previous mark.");
		}

		public static class GetOngoingInitialisations {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(GetOngoingInitialisations.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Returns an unmodifiable list of all ongoing " + ModelConstants.INITIALISATION_NAME_SUFFIX.get()
							+ ", i.e. all non-finished element constructions. Note that all "
							+ ModelConstants.FluentAPI.CLASS_NAME.getEmpty()
							+ " instances have access to the same list of ongoing "
							+ ModelConstants.INITIALISATION_NAME_SUFFIX.get());
		}

		public static class ClearAllOngoingInitialisations {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(ClearAllOngoingInitialisations.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Clears all ongoing " + ModelConstants.INITIALISATION_NAME_SUFFIX.get()
							+ ", i.e. all non-finished element constructions. This is equivalent to calling "
							+ ModelConstants.FluentAPI.DropInitialisation.NAME.get() + " on all ongoing "
							+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + "s");
		}
	}

	/**
	 * Aggregates constants of the abstract (super) initialisation class. Inner
	 * classes are the names of the methods of the abstract (super) initialisation
	 * class.
	 * <p>
	 * <p>
	 * The super initialisation class is the parent class of all initialisation
	 * classes.
	 * 
	 * @author Alp Torac Genc
	 */
	public static class SuperInitialisation {
		public static final IFluentAPITemplate CLASS_NAME = new FluentAPIFixTemplate("FluentAPISuperInitialisation");

		public static final IFluentAPIFillableTemplate CLASS_DOC = new FluentAPIFillableTemplate("The top-most "
				+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " class, which all concrete "
				+ ModelConstants.INITIALISATION_NAME_SUFFIX.get()
				+ " classes extend. Contains various methods that facilitate the programmatic construction of model object instances."
				+ FluentAPIDocumentationUtil.getClassMethodOverviewIntroTemplate());

		/**
		 * Aggregates constants of the reference to the fluent api class from within the
		 * super initialisation class.
		 * 
		 * @author Alp Torac Genc
		 */
		public static class RootAPI {
			public static final IFluentAPIFeatureTemplate NAME = new FluentAPIFeatureTemplate(
					getFeatureName(RootAPI.class));
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					"The " + ModelConstants.FluentAPI.CLASS_NAME.getEmpty() + ", which created this");
		}

		/**
		 * Aggregates constants of the reference to the current element from within the
		 * super initialisation class. The current element is the element, which the
		 * (super) initialisation instance is currently creating / modifying.
		 * 
		 * @author Alp Torac Genc
		 */
		public static class CurrentElement {
			public static final IFluentAPIFeatureTemplate NAME = new FluentAPIFeatureTemplate(
					getFeatureName(CurrentElement.class));
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate("The element this "
					+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " is currently creating or modifying");
		}

		public static class ToAPI {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(ToAPI.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Swaps to " + ModelConstants.SuperInitialisation.RootAPI.NAME.inThis() + ", usually the "
							+ ModelConstants.FluentAPI.CLASS_NAME.getEmpty() + " that created this.");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(SUMMARY.get()) + "Swaps from this to "
							+ ModelConstants.SuperInitialisation.RootAPI.NAME.inThis()
							+ ". This method is currently the same as this"
							+ ModelConstants.SuperInitialisation.RootAPI.NAME.getterCall()
							+ ". Its purpose is to isolate the use of this"
							+ ModelConstants.SuperInitialisation.RootAPI.NAME.getterCall()
							+ ", which is a getter method that is generated by EMF.");
		}

		public static class Drop {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(Drop.class));
		}

		public static class GetInitialisedEClass {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					getMethodName(GetInitialisedEClass.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate("Returns the EClass that this "
					+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " is meant for");
		}

		public static class NewElement {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(NewElement.class));
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Creates a minimal instance of the targeted type within this "
							+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " instance and sets it as the value of "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + ".");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(FluentAPIDocumentationUtil
					.appendToDocumentationStart(SUMMARY.get())
					+ "Creates a minimal EObject instance, without modifying any of its features, and sets it as the value of "
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + " in concrete "
					+ ModelConstants.INITIALISATION_NAME_SUFFIX.get() + " classes. Does nothing in "
					+ ModelConstants.SuperInitialisation.CLASS_NAME.get() + ".");
		}

		public static class Mark extends FluentAPI.Mark {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					FluentAPI.Mark.NAME.get() + CurrentElement.NAME.getCapitalised());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(FluentAPI.Mark.SUMMARY.get())
							+ "Delegates to " + ModelConstants.SuperInitialisation.RootAPI.NAME.get() + " and "
							+ ModelConstants.FluentAPI.Mark.NAME.get() + "s "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.get());
		}

		public static class Unmark extends FluentAPI.Unmark {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(
					FluentAPI.Unmark.NAME.get() + CurrentElement.NAME.getCapitalised());
			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(FluentAPI.Unmark.SUMMARY.get())
							+ "Delegates to " + ModelConstants.SuperInitialisation.RootAPI.NAME.get() + " and "
							+ ModelConstants.FluentAPI.Unmark.NAME.get() + "s "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.get());
		}

		public static class Reset {
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(Reset.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Sets " + ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis()
							+ " (the element that this is currently building) to null");
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(SUMMARY.get()) + "Discards "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + ". Does not "
							+ ModelConstants.FluentAPI.DropInitialisation.NAME.get() + " this from "
							+ ModelConstants.SuperInitialisation.RootAPI.NAME.inThis()
							+ ", meaning that it will still be accessible via "
							+ ModelConstants.SuperInitialisation.RootAPI.NAME.inThis()
							+ ". This can then be re-used by calling "
							+ ModelConstants.SuperInitialisation.NewElement.NAME.thisCall());
		}

		public static class CreateNow {
			public static final IFluentAPITemplate CLASS_PARAMETER_NAME = new FluentAPIFixTemplate("returnTypeCls");
			public static final IFluentAPITemplate NAME = new FluentAPIFixTemplate(getMethodName(CreateNow.class));

			public static final IFluentAPITemplate SUMMARY = new FluentAPIFixTemplate(
					"Finalises and returns " + ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis());
			public static final IFluentAPITemplate DOC = new FluentAPIFixTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(SUMMARY.get())
							+ "Finalises the construction of "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
							+ " and returns it. " + ModelConstants.FluentAPI.DropInitialisation.NAME.get()
							+ "s this from " + ModelConstants.SuperInitialisation.ToAPI.NAME.thisCall()
							+ ", meaning that this will no longer be accessible from "
							+ ModelConstants.SuperInitialisation.ToAPI.NAME.thisCall());

			public static final IFluentAPITemplate CLASS_PARAMETER_DOC = new FluentAPIFixTemplate(DOC.get()
					+ FluentAPIDocumentationUtil.getDocParagraphSeparator()
					+ "EMF-based metamodels consider interfaces, which allow diamond structures in the type hierarchy of their implementors. To spare type casting in model construction, this method can be given a class parameter, to which the returned value will be cast.");
		}
	}

	/**
	 * Aggregates common constants of the initialisation classes. Inner classes are
	 * the names of the methods of the initialisation class.
	 * <p>
	 * <p>
	 * Initialisation classes aid in the creation and modification of model elements
	 * of the metamodel that the fluent api is meant for.
	 * 
	 * @author Alp Torac Genc
	 */
	public static class Initialiation {
		/**
		 * %s: Class name (capitalised)
		 */
		public static final IFluentAPIFillableTemplate CLASS_NAME = new FluentAPIFillableTemplate(
				"%s" + ModelConstants.INITIALISATION_NAME_SUFFIX.get());

		/**
		 * %s: Initialised class name
		 * <p>
		 * %s: Metamodel name
		 * <p>
		 * %s: Initialised class name
		 * <p>
		 * %s: Serialised method names and summaries
		 */
		public static final IFluentAPIFillableTemplate CLASS_DOC = new FluentAPIFillableTemplate("An "
				+ ModelConstants.INITIALISATION_NAME_SUFFIX.get()
				+ " class that targets the type '%s' within the '%s' metamodel. Contains various methods that facilitate the programmatic construction of '%s' instances."
				+ FluentAPIDocumentationUtil.getClassMethodOverviewIntroTemplate());

		public static class NewElement extends ModelConstants.SuperInitialisation.NewElement {
			/**
			 * %s: Class name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil
							.appendToDocumentationStart(ModelConstants.SuperInitialisation.NewElement.SUMMARY.get())
							+ "Creates a minimal %s instance, without modifying any of its features, and sets it as "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis());
		}

		public static class With {
			public static final IFluentAPITemplate PARAMETER_NAME = new FluentAPIFixTemplate("newFeatVal");
			/**
			 * %s: Feature name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					getMethodName(With.class) + "%s");

			public static final IFluentAPITemplate SUMMARY = ModelConstants.FluentAPI.WithFeat.SUMMARY;
			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil.appendToDocumentationStart(
							ModelConstants.FluentAPI.WithFeat.SUMMARY.get()) + "Sets the value of the feature '%s' in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + " to the given value.");

			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate PARAMETER_DOC = new FluentAPIFillableTemplate(
					"The new value of the feature '%s', which will replace its current value in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis());
		}

		public static class Without {
			/**
			 * %s: Feature name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					getMethodName(Without.class) + "%s");

			public static final IFluentAPITemplate SUMMARY = ModelConstants.FluentAPI.WithoutFeat.SUMMARY;
			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil
							.appendToDocumentationStart(ModelConstants.FluentAPI.WithoutFeat.SUMMARY.get())
							+ "Unsets the value of the feature '%s' in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis()
							+ ", which sets its value to null.");
		}

		public static class WithAdded {
			public static final IFluentAPITemplate PARAMETER_NAME = new FluentAPIFixTemplate("featValToAdd");
			/**
			 * %s: Feature name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					getMethodName(WithAdded.class) + "%s");

			public static final IFluentAPITemplate SUMMARY = ModelConstants.FluentAPI.WithAddedFeat.SUMMARY;
			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil
							.appendToDocumentationStart(ModelConstants.FluentAPI.WithAddedFeat.SUMMARY.get())
							+ "Adds the given values to the current values of the feature '%s' in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + ".");

			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate PARAMETER_DOC = new FluentAPIFillableTemplate(
					"Value(s) for the feature '%s', which will be added to its current values in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + ".");
		}

		public static class WithRemoved {
			public static final IFluentAPITemplate PARAMETER_NAME = new FluentAPIFixTemplate("featValToRemove");
			/**
			 * %s: Feature name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					getMethodName(WithRemoved.class) + "%s");

			public static final IFluentAPITemplate SUMMARY = ModelConstants.FluentAPI.WithRemovedFeat.SUMMARY;
			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil
							.appendToDocumentationStart(ModelConstants.FluentAPI.WithRemovedFeat.SUMMARY.get())
							+ "Removes the given values from the current values of the feature '%s' in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis());

			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate PARAMETER_DOC = new FluentAPIFillableTemplate(
					"Value(s) for the feature '%s', which will be removed from its current values in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis() + ".");
		}

		public static class Clean {
			/**
			 * %s: Feature name (capitalised)
			 */
			public static final IFluentAPIFillableTemplate NAME = new FluentAPIFillableTemplate(
					getMethodName(Clean.class) + "%s");

			public static final IFluentAPITemplate SUMMARY = ModelConstants.FluentAPI.CleanFeat.SUMMARY;
			/**
			 * %s: Feature name
			 */
			public static final IFluentAPIFillableTemplate DOC = new FluentAPIFillableTemplate(
					FluentAPIDocumentationUtil
							.appendToDocumentationStart(ModelConstants.FluentAPI.CleanFeat.SUMMARY.get())
							+ "Clears all values of the (many-valued) feature '%s' in "
							+ ModelConstants.SuperInitialisation.CurrentElement.NAME.inThis());
		}
	}
}
