package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;

public interface INamespaceClassifierReferenceInitialiser
		extends INamespaceAwareElementInitialiser, ITypeReferenceInitialiser {
	@Override
	public NamespaceClassifierReference instantiate();

	@Override
	public default boolean setTargetAssertion(TypeReference tref, Classifier target) {
		if (target == null && tref.getTarget() == null) {
			return true;
		} else if (target == null) {
			return false;
		}

		var castedTref = (NamespaceClassifierReference) tref;
		var containerName = target.getContainingContainerName();
		var nss = castedTref.getNamespaces();
		var clsRefs = castedTref.getClassifierReferences();

		/*
		 * Assertions for the implementation at
		 * https://github.com/DevBoost/JaMoPP/blob/master/Core/org.emftext.language.java
		 * /src/org/emftext/language/java/extensions/types/TypeReferenceExtension.java
		 */
		return ITypeReferenceInitialiser.super.setTargetAssertion(tref, target) && nss != null && containerName != null
				&& nss.size() == containerName.size() && nss.containsAll(containerName) && clsRefs != null
				&& clsRefs.size() == 1 && clsRefs.stream().anyMatch((cr) -> cr.getTarget().equals(target));
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * In the case of {@link NamespaceClassifierReference}, this method:
	 * <ul>
	 * <li>Clears ALL {@link ClassifierReference} instances stored in tref,
	 * <li>Sets target of tref ({@code tref.getTarget()}) to target,
	 * <li>Internally constructs and adds a {@link ClassifierReference} pointing at
	 * target to tref.
	 * </ul>
	 * The overridden version of this method in {@link NamespaceClassifierReference}
	 * introduces no changes to its super version in
	 * {@link ITypeReferenceInitialiser}. It is overridden for the sake of providing
	 * commentary. <br>
	 * <br>
	 * 
	 * @see {@link #canSetTarget(TypeReference)}
	 * @see {@link #canSetTargetTo(TypeReference, Classifier)}
	 * @see {@link #setTargetAssertion(TypeReference, Classifier)}
	 * @see {@link #addClassifierReference(NamespaceClassifierReference, ClassifierReference)}
	 */
	@Override
	public default boolean setTarget(TypeReference tref, Classifier target) {
		return ITypeReferenceInitialiser.super.setTarget(tref, target);
	}

	/**
	 * This method:
	 * 
	 * <ul>
	 * <li>Adds the given clsRef to {@code ncr.getClassifierReferences()},
	 * <li>Replaces the target of ncr ({@code ncr.getTarget()}) with
	 * {@code clsRef.getTarget()}.
	 * </ul>
	 * 
	 * <b>Using this method modifies the target attribute of ncr
	 * ({@code ncr.getTarget()}).</b> Therefore, it should normally NOT be used
	 * along with {@link #setTarget(TypeReference, Classifier)}, as they will modify
	 * the said target attribute in conflicting ways. <br>
	 * <br>
	 * 
	 * @see {@link #setTarget(TypeReference, Classifier)}
	 */
	public default boolean addClassifierReference(NamespaceClassifierReference ncr, ClassifierReference clsRef) {
		if (clsRef != null) {
			ncr.getClassifierReferences().add(clsRef);
			return ncr.getClassifierReferences().contains(clsRef);
		}
		return true;
	}

	@Override
	public default boolean canSetTarget(TypeReference tref) {
		return true;
	}

	@Override
	public default boolean canSetTargetTo(TypeReference tref, Classifier target) {
		return ITypeReferenceInitialiser.super.canSetTargetTo(tref, target)
				&& target.getContainingContainerName() != null;
	}

	public default boolean addClassifierReferences(NamespaceClassifierReference ncr, ClassifierReference[] clsRefs) {
		return this.doMultipleModifications(ncr, clsRefs, this::addClassifierReference);
	}
}
