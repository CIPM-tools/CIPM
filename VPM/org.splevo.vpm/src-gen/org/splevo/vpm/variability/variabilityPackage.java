/**
 */
package org.splevo.vpm.variability;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.splevo.vpm.variability.variabilityFactory
 * @model kind="package"
 * @generated
 */
public interface variabilityPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "variability";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://splevo.org/vpm/1.0/variability";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "variability";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	variabilityPackage eINSTANCE = org.splevo.vpm.variability.impl.variabilityPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.splevo.vpm.variability.impl.VariationPointImpl <em>Variation Point</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.splevo.vpm.variability.impl.VariationPointImpl
	 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPoint()
	 * @generated
	 */
	int VARIATION_POINT = 0;

	/**
	 * The feature id for the '<em><b>Variants</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT__VARIANTS = 0;

	/**
	 * The feature id for the '<em><b>Enclosing Software Entity</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT__ENCLOSING_SOFTWARE_ENTITY = 1;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT__GROUP = 2;

	/**
	 * The number of structural features of the '<em>Variation Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.splevo.vpm.variability.impl.VariantImpl <em>Variant</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.splevo.vpm.variability.impl.VariantImpl
	 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariant()
	 * @generated
	 */
	int VARIANT = 1;

	/**
	 * The feature id for the '<em><b>Child Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__CHILD_FEATURE = 0;

	/**
	 * The feature id for the '<em><b>Software Entities</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__SOFTWARE_ENTITIES = 1;

	/**
	 * The feature id for the '<em><b>Leading</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__LEADING = 2;

	/**
	 * The feature id for the '<em><b>Variant Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__VARIANT_ID = 3;

	/**
	 * The feature id for the '<em><b>Variation Point</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__VARIATION_POINT = 4;

	/**
	 * The number of structural features of the '<em>Variant</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.splevo.vpm.variability.impl.VariationPointModelImpl <em>Variation Point Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.splevo.vpm.variability.impl.VariationPointModelImpl
	 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPointModel()
	 * @generated
	 */
	int VARIATION_POINT_MODEL = 2;

	/**
	 * The feature id for the '<em><b>Variation Point Groups</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_MODEL__VARIATION_POINT_GROUPS = 0;

	/**
	 * The feature id for the '<em><b>Software Elements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_MODEL__SOFTWARE_ELEMENTS = 1;

	/**
	 * The number of structural features of the '<em>Variation Point Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_MODEL_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.splevo.vpm.variability.impl.VariationPointGroupImpl <em>Variation Point Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.splevo.vpm.variability.impl.VariationPointGroupImpl
	 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPointGroup()
	 * @generated
	 */
	int VARIATION_POINT_GROUP = 3;

	/**
	 * The feature id for the '<em><b>Variation Points</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_GROUP__VARIATION_POINTS = 0;

	/**
	 * The feature id for the '<em><b>Group Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_GROUP__GROUP_ID = 1;

	/**
	 * The feature id for the '<em><b>Model</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_GROUP__MODEL = 2;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_GROUP__FEATURE = 3;

	/**
	 * The number of structural features of the '<em>Variation Point Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIATION_POINT_GROUP_FEATURE_COUNT = 4;

	/**
	 * Returns the meta object for class '{@link org.splevo.vpm.variability.VariationPoint <em>Variation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variation Point</em>'.
	 * @see org.splevo.vpm.variability.VariationPoint
	 * @generated
	 */
	EClass getVariationPoint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.splevo.vpm.variability.VariationPoint#getVariants <em>Variants</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variants</em>'.
	 * @see org.splevo.vpm.variability.VariationPoint#getVariants()
	 * @see #getVariationPoint()
	 * @generated
	 */
	EReference getVariationPoint_Variants();

	/**
	 * Returns the meta object for the reference '{@link org.splevo.vpm.variability.VariationPoint#getEnclosingSoftwareEntity <em>Enclosing Software Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Enclosing Software Entity</em>'.
	 * @see org.splevo.vpm.variability.VariationPoint#getEnclosingSoftwareEntity()
	 * @see #getVariationPoint()
	 * @generated
	 */
	EReference getVariationPoint_EnclosingSoftwareEntity();

	/**
	 * Returns the meta object for the container reference '{@link org.splevo.vpm.variability.VariationPoint#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Group</em>'.
	 * @see org.splevo.vpm.variability.VariationPoint#getGroup()
	 * @see #getVariationPoint()
	 * @generated
	 */
	EReference getVariationPoint_Group();

	/**
	 * Returns the meta object for class '{@link org.splevo.vpm.variability.Variant <em>Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variant</em>'.
	 * @see org.splevo.vpm.variability.Variant
	 * @generated
	 */
	EClass getVariant();

	/**
	 * Returns the meta object for the reference '{@link org.splevo.vpm.variability.Variant#getChildFeature <em>Child Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Child Feature</em>'.
	 * @see org.splevo.vpm.variability.Variant#getChildFeature()
	 * @see #getVariant()
	 * @generated
	 */
	EReference getVariant_ChildFeature();

	/**
	 * Returns the meta object for the reference list '{@link org.splevo.vpm.variability.Variant#getSoftwareEntities <em>Software Entities</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Software Entities</em>'.
	 * @see org.splevo.vpm.variability.Variant#getSoftwareEntities()
	 * @see #getVariant()
	 * @generated
	 */
	EReference getVariant_SoftwareEntities();

	/**
	 * Returns the meta object for the attribute '{@link org.splevo.vpm.variability.Variant#getLeading <em>Leading</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Leading</em>'.
	 * @see org.splevo.vpm.variability.Variant#getLeading()
	 * @see #getVariant()
	 * @generated
	 */
	EAttribute getVariant_Leading();

	/**
	 * Returns the meta object for the attribute '{@link org.splevo.vpm.variability.Variant#getVariantId <em>Variant Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Variant Id</em>'.
	 * @see org.splevo.vpm.variability.Variant#getVariantId()
	 * @see #getVariant()
	 * @generated
	 */
	EAttribute getVariant_VariantId();

	/**
	 * Returns the meta object for the container reference '{@link org.splevo.vpm.variability.Variant#getVariationPoint <em>Variation Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Variation Point</em>'.
	 * @see org.splevo.vpm.variability.Variant#getVariationPoint()
	 * @see #getVariant()
	 * @generated
	 */
	EReference getVariant_VariationPoint();

	/**
	 * Returns the meta object for class '{@link org.splevo.vpm.variability.VariationPointModel <em>Variation Point Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variation Point Model</em>'.
	 * @see org.splevo.vpm.variability.VariationPointModel
	 * @generated
	 */
	EClass getVariationPointModel();

	/**
	 * Returns the meta object for the containment reference list '{@link org.splevo.vpm.variability.VariationPointModel#getVariationPointGroups <em>Variation Point Groups</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variation Point Groups</em>'.
	 * @see org.splevo.vpm.variability.VariationPointModel#getVariationPointGroups()
	 * @see #getVariationPointModel()
	 * @generated
	 */
	EReference getVariationPointModel_VariationPointGroups();

	/**
	 * Returns the meta object for the containment reference list '{@link org.splevo.vpm.variability.VariationPointModel#getSoftwareElements <em>Software Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Software Elements</em>'.
	 * @see org.splevo.vpm.variability.VariationPointModel#getSoftwareElements()
	 * @see #getVariationPointModel()
	 * @generated
	 */
	EReference getVariationPointModel_SoftwareElements();

	/**
	 * Returns the meta object for class '{@link org.splevo.vpm.variability.VariationPointGroup <em>Variation Point Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variation Point Group</em>'.
	 * @see org.splevo.vpm.variability.VariationPointGroup
	 * @generated
	 */
	EClass getVariationPointGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.splevo.vpm.variability.VariationPointGroup#getVariationPoints <em>Variation Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variation Points</em>'.
	 * @see org.splevo.vpm.variability.VariationPointGroup#getVariationPoints()
	 * @see #getVariationPointGroup()
	 * @generated
	 */
	EReference getVariationPointGroup_VariationPoints();

	/**
	 * Returns the meta object for the attribute '{@link org.splevo.vpm.variability.VariationPointGroup#getGroupId <em>Group Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group Id</em>'.
	 * @see org.splevo.vpm.variability.VariationPointGroup#getGroupId()
	 * @see #getVariationPointGroup()
	 * @generated
	 */
	EAttribute getVariationPointGroup_GroupId();

	/**
	 * Returns the meta object for the container reference '{@link org.splevo.vpm.variability.VariationPointGroup#getModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Model</em>'.
	 * @see org.splevo.vpm.variability.VariationPointGroup#getModel()
	 * @see #getVariationPointGroup()
	 * @generated
	 */
	EReference getVariationPointGroup_Model();

	/**
	 * Returns the meta object for the reference '{@link org.splevo.vpm.variability.VariationPointGroup#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see org.splevo.vpm.variability.VariationPointGroup#getFeature()
	 * @see #getVariationPointGroup()
	 * @generated
	 */
	EReference getVariationPointGroup_Feature();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	variabilityFactory getvariabilityFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.splevo.vpm.variability.impl.VariationPointImpl <em>Variation Point</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.splevo.vpm.variability.impl.VariationPointImpl
		 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPoint()
		 * @generated
		 */
		EClass VARIATION_POINT = eINSTANCE.getVariationPoint();

		/**
		 * The meta object literal for the '<em><b>Variants</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT__VARIANTS = eINSTANCE
				.getVariationPoint_Variants();

		/**
		 * The meta object literal for the '<em><b>Enclosing Software Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT__ENCLOSING_SOFTWARE_ENTITY = eINSTANCE
				.getVariationPoint_EnclosingSoftwareEntity();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT__GROUP = eINSTANCE.getVariationPoint_Group();

		/**
		 * The meta object literal for the '{@link org.splevo.vpm.variability.impl.VariantImpl <em>Variant</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.splevo.vpm.variability.impl.VariantImpl
		 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariant()
		 * @generated
		 */
		EClass VARIANT = eINSTANCE.getVariant();

		/**
		 * The meta object literal for the '<em><b>Child Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIANT__CHILD_FEATURE = eINSTANCE.getVariant_ChildFeature();

		/**
		 * The meta object literal for the '<em><b>Software Entities</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIANT__SOFTWARE_ENTITIES = eINSTANCE
				.getVariant_SoftwareEntities();

		/**
		 * The meta object literal for the '<em><b>Leading</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIANT__LEADING = eINSTANCE.getVariant_Leading();

		/**
		 * The meta object literal for the '<em><b>Variant Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIANT__VARIANT_ID = eINSTANCE.getVariant_VariantId();

		/**
		 * The meta object literal for the '<em><b>Variation Point</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIANT__VARIATION_POINT = eINSTANCE
				.getVariant_VariationPoint();

		/**
		 * The meta object literal for the '{@link org.splevo.vpm.variability.impl.VariationPointModelImpl <em>Variation Point Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.splevo.vpm.variability.impl.VariationPointModelImpl
		 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPointModel()
		 * @generated
		 */
		EClass VARIATION_POINT_MODEL = eINSTANCE.getVariationPointModel();

		/**
		 * The meta object literal for the '<em><b>Variation Point Groups</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT_MODEL__VARIATION_POINT_GROUPS = eINSTANCE
				.getVariationPointModel_VariationPointGroups();

		/**
		 * The meta object literal for the '<em><b>Software Elements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT_MODEL__SOFTWARE_ELEMENTS = eINSTANCE
				.getVariationPointModel_SoftwareElements();

		/**
		 * The meta object literal for the '{@link org.splevo.vpm.variability.impl.VariationPointGroupImpl <em>Variation Point Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.splevo.vpm.variability.impl.VariationPointGroupImpl
		 * @see org.splevo.vpm.variability.impl.variabilityPackageImpl#getVariationPointGroup()
		 * @generated
		 */
		EClass VARIATION_POINT_GROUP = eINSTANCE.getVariationPointGroup();

		/**
		 * The meta object literal for the '<em><b>Variation Points</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT_GROUP__VARIATION_POINTS = eINSTANCE
				.getVariationPointGroup_VariationPoints();

		/**
		 * The meta object literal for the '<em><b>Group Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIATION_POINT_GROUP__GROUP_ID = eINSTANCE
				.getVariationPointGroup_GroupId();

		/**
		 * The meta object literal for the '<em><b>Model</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT_GROUP__MODEL = eINSTANCE
				.getVariationPointGroup_Model();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIATION_POINT_GROUP__FEATURE = eINSTANCE
				.getVariationPointGroup_Feature();

	}

} //variabilityPackage
