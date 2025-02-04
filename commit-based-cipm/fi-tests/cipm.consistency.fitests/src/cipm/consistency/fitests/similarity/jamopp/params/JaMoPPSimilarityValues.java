package cipm.consistency.fitests.similarity.jamopp.params;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.emftext.language.java.arrays.ArrayInstantiation;
import org.emftext.language.java.arrays.ArraysPackage;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.containers.Module;
import org.emftext.language.java.containers.EmptyModel;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.expressions.ExplicitlyTypedLambdaParameters;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.ImplicitlyTypedLambdaParameters;
import org.emftext.language.java.expressions.InstanceOfExpression;
import org.emftext.language.java.expressions.NestedExpression;
import org.emftext.language.java.expressions.SingleImplicitLambdaParameter;
import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.imports.ImportsPackage;
import org.emftext.language.java.imports.StaticClassifierImport;
import org.emftext.language.java.instantiations.Instantiation;
import org.emftext.language.java.instantiations.InstantiationsPackage;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.instantiations.NewConstructorCallWithInferredTypeArguments;
import org.emftext.language.java.members.AdditionalField;
import org.emftext.language.java.members.EnumConstant;
import org.emftext.language.java.members.MembersPackage;
import org.emftext.language.java.modifiers.ModifiersPackage;
import org.emftext.language.java.modules.ModulesPackage;
import org.emftext.language.java.modules.ProvidesModuleDirective;
import org.emftext.language.java.modules.UsesModuleDirective;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.PackageReference;
import org.emftext.language.java.references.PrimitiveTypeReference;
import org.emftext.language.java.references.ReferencesPackage;
import org.emftext.language.java.references.ReflectiveClassReference;
import org.emftext.language.java.references.SelfReference;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.references.TextBlockReference;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypesPackage;
import org.emftext.language.java.variables.VariablesPackage;

import cipm.consistency.fitests.similarity.params.AbstractSimilarityValues;
import cipm.consistency.fitests.similarity.params.ISimilarityValues;

/**
 * Contains expected similarity values for tests, in which a certain attribute
 * is different. <br>
 * <br>
 * Entries for directly matching entries will take precedence over entries of
 * their parent types. If the behaviour of a certain type differs from its
 * parents' and/or sibling types, an entry can be created to account for it.
 * This spares having to create explicit entries for everything in a part of the
 * hierarchy, if there are small deviations. <br>
 * <br>
 * To spare creating entries for every possibility, a default similarity value
 * can be defined by using {@link #setDefaultSimilarityResult(Boolean)}. If
 * there are no entries for a specific attribute, the default similarity value
 * will be assumed. The default value is null, if not explicitly defined.
 * 
 * @see {@link ISimilarityValues} for more information on the methods used
 *      below.
 * @see {@link AbstractSimilarityValues}
 */
public class JaMoPPSimilarityValues extends AbstractSimilarityValues {
	public void addAnnotationsSimilarityEntries() {
		this.addSimilarityEntry(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS, Boolean.TRUE);
		this.addSimilarityEntry(AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__VALUE, Boolean.TRUE);
		this.addSimilarityEntry(AnnotationsPackage.Literals.ANNOTATION_INSTANCE__PARAMETER, Boolean.TRUE);
		this.addSimilarityEntry(AnnotationsPackage.Literals.ANNOTATION_PARAMETER_LIST__SETTINGS, Boolean.TRUE);
		this.addSimilarityEntry(AnnotationsPackage.Literals.SINGLE_ANNOTATION_PARAMETER__VALUE, Boolean.TRUE);
	}

	public void addArraysSimilarityEntries() {
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_INITIALIZER__INITIAL_VALUES, Boolean.TRUE);
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_SIZE__SIZES, Boolean.TRUE);
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER, Boolean.TRUE);
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_SELECTOR__POSITION, Boolean.TRUE);
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER, Boolean.TRUE);
		this.addSimilarityEntry(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE, Boolean.TRUE);
	}

	public void addContainersSimilarityEntries() {
		this.addSimilarityEntry(ContainersPackage.Literals.COMPILATION_UNIT__CLASSIFIERS, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.MODULE__OPEN, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.MODULE__PACKAGES, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.MODULE__TARGET, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.PACKAGE__CLASSIFIERS, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.PACKAGE__MODULE, Boolean.TRUE);
		this.addSimilarityEntry(ContainersPackage.Literals.JAVA_ROOT__ORIGIN, Boolean.TRUE);
	}

	public void addCommonsSimilarityEntries() {
		this.addSimilarityEntry(new Class[] { Package.class, Block.class }, CommonsPackage.Literals.NAMED_ELEMENT__NAME,
				Boolean.TRUE);
		this.addSimilarityEntry(new Class[] { Module.class },
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, Boolean.TRUE);
		this.addSimilarityEntry(new Class[] { EmptyModel.class, PackageReference.class },
				new EStructuralFeature[] { CommonsPackage.Literals.NAMED_ELEMENT__NAME,
						CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES },
				new Boolean[] { Boolean.TRUE, Boolean.TRUE });
	}

	public void addClassifiersSimilarityEntries() {
		this.addSimilarityEntry(ClassifiersPackage.Literals.CLASS__DEFAULT_EXTENDS, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.CLASS__EXTENDS, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.ENUMERATION__CONSTANTS, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.INTERFACE__DEFAULT_EXTENDS, Boolean.TRUE);
		this.addSimilarityEntry(ClassifiersPackage.Literals.INTERFACE__EXTENDS, Boolean.TRUE);
	}

	public void addExpressionsSimilarityEntries() {
		this.addSimilarityEntry(ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.CAST_EXPRESSION__GENERAL_CHILD, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__EXPRESSION_IF, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__GENERAL_EXPRESSION_ELSE,
				Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.INCLUSIVE_OR_EXPRESSION__CHILDREN, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__CHILDREN, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__MULTIPLICATIVE_OPERATORS,
				Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__CHILD,
				Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__METHOD_REFERENCE,
				Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.LAMBDA_EXPRESSION__BODY, Boolean.TRUE);
		this.addSimilarityEntry(ExpressionsPackage.Literals.LAMBDA_EXPRESSION__PARAMETERS, Boolean.TRUE);
	}

	public void addGenericsSimilarityEntries() {
		this.addSimilarityEntry(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS, Boolean.TRUE);
		this.addSimilarityEntry(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS, Boolean.TRUE);
		this.addSimilarityEntry(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS, Boolean.TRUE);
	}

	public void addInstantiationsSimilarityEntries() {
		this.addSimilarityEntry(InstantiationsPackage.Literals.NEW_CONSTRUCTOR_CALL__ANONYMOUS_CLASS, Boolean.TRUE);
		this.addSimilarityEntry(InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE, Boolean.TRUE);
	}

	public void addImportsSimilarityEntries() {
		this.addSimilarityEntry(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS, Boolean.TRUE);
		this.addSimilarityEntry(ImportsPackage.Literals.IMPORT__CLASSIFIER, Boolean.TRUE);
		this.addSimilarityEntry(ImportsPackage.Literals.STATIC_IMPORT__STATIC, Boolean.TRUE);
		this.addSimilarityEntry(StaticClassifierImport.class, ImportsPackage.Literals.IMPORT__CLASSIFIER,
				Boolean.FALSE);
		this.addSimilarityEntry(ClassifierImport.class, ImportsPackage.Literals.IMPORT__CLASSIFIER, Boolean.FALSE);
	}

	public void addMembersSimilarityEntries() {
		this.addSimilarityEntry(MembersPackage.Literals.ENUM_CONSTANT__ANONYMOUS_CLASS, Boolean.TRUE);
		this.addSimilarityEntry(MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS, Boolean.TRUE);
		this.addSimilarityEntry(MembersPackage.Literals.INTERFACE_METHOD__DEFAULT_VALUE, Boolean.TRUE);
		this.addSimilarityEntry(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS, Boolean.TRUE);
		this.addSimilarityEntry(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS, Boolean.TRUE);
		this.addSimilarityEntry(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS, Boolean.TRUE);
	}

	public void addModifiersSimilarityEntries() {
		this.addSimilarityEntry(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
				Boolean.TRUE);
		this.addSimilarityEntry(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS, Boolean.TRUE);
	}

	public void addModulesSimilarityEntries() {
		this.addSimilarityEntry(ModulesPackage.Literals.MODULE_REFERENCE__TARGET, Boolean.TRUE);
		this.addSimilarityEntry(ModulesPackage.Literals.PROVIDES_MODULE_DIRECTIVE__SERVICE_PROVIDERS, Boolean.TRUE);
		this.addSimilarityEntry(ModulesPackage.Literals.REQUIRES_MODULE_DIRECTIVE__MODIFIER, Boolean.TRUE);
		this.addSimilarityEntry(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__ACCESSABLE_PACKAGE,
				Boolean.TRUE);
		this.addSimilarityEntry(ModulesPackage.Literals.ACCESS_PROVIDING_MODULE_DIRECTIVE__MODULES, Boolean.TRUE);
	}

	public void addStatementsSimilarityEntries() {
		this.addSimilarityEntry(StatementsPackage.Literals.ASSERT__ERROR_MESSAGE, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.CONDITION__ELSE_STATEMENT, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.FOR_EACH_LOOP__COLLECTION, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.FOR_EACH_LOOP__NEXT, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.FOR_LOOP__INIT, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.FOR_LOOP__UPDATES, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.NORMAL_SWITCH_RULE__ADDITIONAL_CONDITIONS, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.SWITCH__CASES, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.THROW__THROWABLE, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.TRY_BLOCK__CATCH_BLOCKS, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.TRY_BLOCK__FINALLY_BLOCK, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.TRY_BLOCK__RESOURCES, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.YIELD_STATEMENT__YIELD_EXPRESSION, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.BLOCK_CONTAINER__BLOCK, Boolean.TRUE);
		this.addSimilarityEntry(StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT, Boolean.TRUE);
		this.addSimilarityEntry(StatementListContainer.class, StatementsPackage.Literals.BLOCK__STATEMENTS,
				Boolean.TRUE);
	}

	public void addParametersSimilarityEntries() {
		this.addSimilarityEntry(ParametersPackage.Literals.CATCH_PARAMETER__TYPE_REFERENCES, Boolean.TRUE);
		this.addSimilarityEntry(ParametersPackage.Literals.RECEIVER_PARAMETER__THIS_REFERENCE, Boolean.TRUE);
		this.addSimilarityEntry(ParametersPackage.Literals.RECEIVER_PARAMETER__OUTER_TYPE_REFERENCE, Boolean.TRUE);
		this.addSimilarityEntry(
				new Class[] { ExplicitlyTypedLambdaParameters.class, ImplicitlyTypedLambdaParameters.class,
						SingleImplicitLambdaParameter.class },
				ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, Boolean.TRUE);
	}

	public void addReferencesSimilarityEntries() {
		this.addSimilarityEntry(ReferencesPackage.Literals.PRIMITIVE_TYPE_REFERENCE__PRIMITIVE_TYPE, Boolean.TRUE);
		this.addSimilarityEntry(ReferencesPackage.Literals.SELF_REFERENCE__SELF, Boolean.TRUE);
		this.addSimilarityEntry(ReferencesPackage.Literals.TEXT_BLOCK_REFERENCE__VALUE, Boolean.TRUE);
		this.addSimilarityEntry(ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET, Boolean.TRUE);
		this.addSimilarityEntry(EnumConstant.class, ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS, Boolean.TRUE);
		this.addSimilarityEntry(MethodCall.class, ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS, Boolean.TRUE);
		this.addSimilarityEntry(
				new Class[] { ArrayInstantiation.class, NestedExpression.class, Instantiation.class,
						PrimitiveTypeReference.class, ReflectiveClassReference.class, SelfReference.class,
						StringReference.class, TextBlockReference.class },
				new EStructuralFeature[] { ReferencesPackage.Literals.REFERENCE__NEXT,
						ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS },
				new Boolean[] { Boolean.TRUE, Boolean.TRUE });
	}

	public void addTypesSimilarityEntries() {
		this.addSimilarityEntry(new Class[] { InferableType.class, PrimitiveType.class },
				TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET, Boolean.TRUE);
		this.addSimilarityEntry(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS, Boolean.TRUE);
		this.addSimilarityEntry(TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE, Boolean.TRUE);
		this.addSimilarityEntry(
				new Class[] { AdditionalField.class, InstanceOfExpression.class, QualifiedTypeArgument.class,
						NewConstructorCall.class, NewConstructorCallWithInferredTypeArguments.class,
						ProvidesModuleDirective.class, UsesModuleDirective.class },
				TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE, Boolean.FALSE);
	}

	public void addVariablesSimilarityEntries() {
		this.addSimilarityEntry(VariablesPackage.Literals.LOCAL_VARIABLE__ADDITIONAL_LOCAL_VARIABLES, Boolean.TRUE);
	}

	public void setDefaultSimilarityResult() {
		this.setDefaultSimilarityResult(Boolean.FALSE);
	}

	/**
	 * Adds similarity entries, which have a one-to-one connection
	 * to certain attributes of certain object types. This means that the results
	 * contained within these similarity entries are tied to only one attribute
	 * of an object type per entry.
	 */
	public void addFixedSimilarityEntries() {
		this.addAnnotationsSimilarityEntries();
		this.addArraysSimilarityEntries();
		this.addContainersSimilarityEntries();
		this.addCommonsSimilarityEntries();
		this.addClassifiersSimilarityEntries();
		this.addExpressionsSimilarityEntries();
		this.addGenericsSimilarityEntries();
		this.addInstantiationsSimilarityEntries();
		this.addImportsSimilarityEntries();
		this.addMembersSimilarityEntries();
		this.addModifiersSimilarityEntries();
		this.addModulesSimilarityEntries();
		this.addStatementsSimilarityEntries();
		this.addParametersSimilarityEntries();
		this.addReferencesSimilarityEntries();
		this.addTypesSimilarityEntries();
		this.addVariablesSimilarityEntries();
	}

	public JaMoPPSimilarityValues() {
		this.setDefaultSimilarityResult();
		this.addFixedSimilarityEntries();
	}

	/**
	 * @return The type of the class that has the attribute attr.
	 */
	protected Class<?> getClassFromStructuralFeature(EStructuralFeature attr) {
		return attr.getContainerClass();
	}

	/**
	 * A variant of {@link #addSimilarityEntry(Class, Object, Boolean)}, where
	 * the second parameter is attr and the first parameter is derived from attr.
	 */
	public void addSimilarityEntry(EStructuralFeature attr, Boolean expectedSimResult) {
		this.addSimilarityEntry(this.getClassFromStructuralFeature(attr), attr, expectedSimResult);
	}
}