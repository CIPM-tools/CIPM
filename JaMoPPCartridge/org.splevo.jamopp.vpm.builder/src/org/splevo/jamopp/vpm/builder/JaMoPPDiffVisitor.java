package org.splevo.jamopp.vpm.builder;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Enumeration;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Statement;
import org.splevo.jamopp.diffing.jamoppdiff.ClassChange;
import org.splevo.jamopp.diffing.jamoppdiff.EnumChange;
import org.splevo.jamopp.diffing.jamoppdiff.FieldChange;
import org.splevo.jamopp.diffing.jamoppdiff.ImportChange;
import org.splevo.jamopp.diffing.jamoppdiff.MethodChange;
import org.splevo.jamopp.diffing.jamoppdiff.PackageChange;
import org.splevo.jamopp.diffing.jamoppdiff.StatementChange;
import org.splevo.jamopp.diffing.jamoppdiff.util.JaMoPPDiffSwitch;
import org.splevo.jamopp.vpm.software.JaMoPPSoftwareElement;
import org.splevo.jamopp.vpm.software.softwareFactory;
import org.splevo.vpm.variability.Variant;
import org.splevo.vpm.variability.VariationPoint;
import org.splevo.vpm.variability.VariationPointModel;
import org.splevo.vpm.variability.variabilityFactory;

import com.google.common.collect.Maps;

/**
 * A visitor for diff elements to fill up a variation point mode.
 */
class JaMoPPDiffVisitor extends JaMoPPDiffSwitch<VariationPoint> {

	/** The logger used by this class. */
	private Logger logger = Logger.getLogger(JaMoPPDiffVisitor.class);

	/** The id to set for leading variants. */
	private String variantIDLeading = null;

	/** The id to set for integration variants. */
	private String variantIDIntegration = null;

	/** The variation point model to fill. */
	private VariationPointModel vpm = null;

	/** The comparison model to read information from. */
	private Comparison comparison = null;

	/** Registry to store already created software elements. */
	private Map<Commentable, JaMoPPSoftwareElement> elementRegistry = Maps
			.newLinkedHashMap();

	/**
	 * Constructor to set the variant ids.
	 *
	 * @param variantIDLeading
	 *            The id for the leading variants.
	 * @param variantIDIntegration
	 *            The id for the integration variants.
	 * @param vpm
	 *            The variation point model to add new software element nodes
	 *            to.
	 * @param comparison
	 *            The comparison model to use for matches etc.
	 */
	public JaMoPPDiffVisitor(String variantIDLeading,
			String variantIDIntegration, VariationPointModel vpm,
			Comparison comparison) {
		this.variantIDIntegration = variantIDIntegration;
		this.variantIDLeading = variantIDLeading;
		this.vpm = vpm;
		this.comparison = comparison;
	}

	/**
	 * Handle import inserts. VP references the compilation unit. The leading
	 * variant references the inserted import declaration.
	 *
	 * @param change
	 *            The package change to process.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint casePackageChange(PackageChange change) {
		org.emftext.language.java.containers.Package changedElement = change
				.getChangedPackage();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle import changes. VP references the compilation unit. The leading
	 * variant references the inserted import declaration.
	 *
	 * @param change
	 *            The import change diff element.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseImportChange(ImportChange change) {
		Import changedElement = change.getChangedImport();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle class changes.
	 *
	 * @param change
	 *            The class change diff element.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseClassChange(ClassChange change) {
		org.emftext.language.java.classifiers.Class changedElement = change
				.getChangedClass();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle field inserts. VP references the enclosing class. The integration
	 * variant references the inserted import declaration.
	 *
	 * @param change
	 *            The field change diff element.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseFieldChange(FieldChange change) {
		Field changedElement = change.getChangedField();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle method changes.<br>
	 * VP references the changed method.<br>
	 *
	 * @param change
	 *            The method change diff element.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseMethodChange(MethodChange change) {
		Method changedElement = change.getChangedMethod();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle a statement change by creating a variation point describing this
	 * change.
	 *
	 * @param change
	 *            The diff element to handle.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseStatementChange(StatementChange change) {
		Statement changedElement = change.getChangedStatement();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	/**
	 * Handle an enumeration declaration change by creating a variation point
	 * describing this change.
	 *
	 * @param change
	 *            The diff element to handle.
	 * @return The prepared variation point.
	 */
	@Override
	public VariationPoint caseEnumChange(EnumChange change) {
		Enumeration changedElement = change.getChangedEnum();
		return buildKindSpecificVariationPoint(change, changedElement);
	}

	@Override
	public VariationPoint defaultCase(EObject object) {
		logger.error("Unhandled DiffType: " + object.getClass());
		return null;
	}

	/**
	 * Build a variation point according to the change kind of the provided
	 * Change.
	 *
	 * @param diff
	 *            The Diff element to build the VP for.
	 * @param changedCommentable
	 *            The changed software AST node.
	 * @return The variation point for the provided Diff.
	 */
	private VariationPoint buildKindSpecificVariationPoint(Diff diff,
			Commentable changedCommentable) {
		Commentable parent = getParentNode(changedCommentable);

		if (parent == null) {
			logger.warn("No parent node identified (changedCommentable: "
					+ changedCommentable + ", diff: " + diff + ")");
			return null;
		}

		switch (diff.getKind()) {
		case ADD:
			return createVariationPointInsert(changedCommentable, parent);

		case DELETE:
			return createVariationPointDelete(changedCommentable, parent);

		case CHANGE:
			return createVariationPointChange(changedCommentable, parent);

		default:
			logger.error("Unhandled change: " + diff);
			return null;
		}
	}

	/**
	 * Create a new variation point for an inserted element.
	 *
	 * @param newElement
	 *            The new ast element.
	 * @param parentNode
	 *            The parent node the change was made in.
	 * @return The prepared variation point.
	 */
	private VariationPoint createVariationPointInsert(Commentable newElement,
			Commentable parentNode) {

		// create the variation point
		VariationPoint variationPoint = variabilityFactory.eINSTANCE
				.createVariationPoint();

		JaMoPPSoftwareElement enclosingSoftwareElement = createSoftwareElement(parentNode);
		variationPoint.setEnclosingSoftwareEntity(enclosingSoftwareElement);

		// create the variants
		Variant integrationVariant = createVariant(newElement, false,
				variantIDIntegration);
		variationPoint.getVariants().add(integrationVariant);

		return variationPoint;
	}

	/**
	 * Create a new variation point for a deleted element.
	 *
	 * @param element
	 *            The deleted ast element.
	 * @param enclosingCommentable
	 *            The parent node the change was made in.
	 * @return The prepared variation point.
	 */
	private VariationPoint createVariationPointDelete(Commentable element,
			Commentable enclosingCommentable) {

		JaMoPPSoftwareElement enclosingSoftwareElement = createSoftwareElement(enclosingCommentable);

		// create the variation point
		VariationPoint variationPoint = variabilityFactory.eINSTANCE
				.createVariationPoint();
		variationPoint.setEnclosingSoftwareEntity(enclosingSoftwareElement);

		// create the variants
		Variant leadingVariant = createVariant(element, true, variantIDLeading);
		variationPoint.getVariants().add(leadingVariant);

		return variationPoint;
	}

	/**
	 * Create a variation point for a CHANGE with an existing left and right
	 * side.
	 *
	 * @param changedElement
	 *            The changed model element.
	 * @param parent
	 *            The parent Commentable of the change.
	 * @return The new VariationPoint.
	 */
	private VariationPoint createVariationPointChange(EObject changedElement,
			Commentable parent) {
		Match match = comparison.getMatch(changedElement);
		Commentable CommentableLeft = (Commentable) match.getLeft();
		Commentable CommentableRight = (Commentable) match.getRight();
		return createVariationPointChange(CommentableLeft, CommentableRight,
				parent);
	}

	/**
	 * Factory method for a variation point describing a program change.
	 *
	 * @param CommentableLeft
	 *            The AST node of the left variant.
	 * @param CommentableRight
	 *            The AST node of the right variant.
	 * @param parent
	 *            The parent AST node containing the change.
	 * @return The initialized variation point.
	 */
	private VariationPoint createVariationPointChange(
			Commentable CommentableLeft, Commentable CommentableRight,
			Commentable parent) {

		JaMoPPSoftwareElement enclosingSoftwareElement = createSoftwareElement(parent);

		// create the variation point
		VariationPoint variationPoint = variabilityFactory.eINSTANCE
				.createVariationPoint();
		variationPoint.setEnclosingSoftwareEntity(enclosingSoftwareElement);

		// create the integration variant
		Variant integrationVariant = createVariant(CommentableLeft, false,
				variantIDIntegration);
		variationPoint.getVariants().add(integrationVariant);

		// create the leading variant
		Variant leadingVariant = createVariant(CommentableRight, true,
				variantIDLeading);
		variationPoint.getVariants().add(leadingVariant);
		return variationPoint;
	}

	/**
	 * Get the parent node for an AST node. Using the comparison model, the
	 * match for the parent element will be searched and if available the right
	 * side will be used, the left side as fall back.
	 *
	 * @param Commentable
	 *            The {@link Diff} to get the parent for.
	 * @return The most reasonable parent AST node.
	 */
	private Commentable getParentNode(Commentable Commentable) {

		Match parentMatch = comparison.getMatch(Commentable.eContainer());

		EObject parent = null;
		if (parentMatch.getRight() != null) {
			parent = parentMatch.getRight();
		} else {
			parent = parentMatch.getLeft();
		}

		// FIXME: Check if Block in JaMoPP exists inside method
		if (parent instanceof Block && parent.eContainer() instanceof Method) {
			return (Commentable) parent.eContainer();
		}

		if (parent instanceof Commentable) {
			return (Commentable) parent;
		} else {
			logger.warn("Diff with a non-Commentable parent detected. Diff child: "
					+ Commentable);
			return null;
		}
	}

	/**
	 * Factory method to create an initialized variant element.
	 *
	 * @param Commentable
	 *            The element to be linked with the variant.
	 * @param leading
	 *            The flag whether this variant is a leading one.
	 * @param variantID
	 *            The id of the product variant this one belongs to.
	 * @return The prepared variant object.
	 */
	private Variant createVariant(Commentable Commentable, Boolean leading,
			String variantID) {

		JaMoPPSoftwareElement softwareElement = createSoftwareElement(Commentable);

		Variant integrationVariant = null;
		integrationVariant = variabilityFactory.eINSTANCE.createVariant();
		integrationVariant.getSoftwareEntities().add(softwareElement);
		integrationVariant.setLeading(leading);
		integrationVariant.setVariantId(variantID);
		return integrationVariant;
	}

	/**
	 * Create a software element wrapping an Commentable.
	 *
	 * Uses a registry map to prevent creating multiple SoftwareElements for the
	 * same Commentable.
	 *
	 * @param Commentable
	 *            The Commentable to link.
	 * @return The prepared software element.
	 */
	private JaMoPPSoftwareElement createSoftwareElement(Commentable Commentable) {

		if (elementRegistry.containsKey(Commentable)) {
			return elementRegistry.get(Commentable);
		}

		JaMoPPSoftwareElement element = softwareFactory.eINSTANCE
				.createJaMoPPSoftwareElement();
		element.setJamoppElement(Commentable);
		vpm.getSoftwareElements().add(element);
		elementRegistry.put(Commentable, element);

		return element;
	}
}