package cipm.consistency.vsum.test.pcm.preprocessing;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.base.Preconditions;

public class PcmDependency {
	private final EObject elem1;
	private final EStructuralFeature elem1Feat;

	private final EObject elem2;
	private final EStructuralFeature elem2Feat;

	private final String correspondenceTag;

	/**
	 * Meant only for pure PCM and pure Java dependencies
	 */
	public PcmDependency(EObject elem1, EObject elem2) {
		this(elem1, null, elem2, null, null);
	}

	/**
	 * Meant only for PCM <-> Java dependencies
	 */
	public PcmDependency(EObject pcmElem, EObject javaElem, String correspondenceTag) {
		this(pcmElem, null, javaElem, null, correspondenceTag);
	}

	/**
	 * Meant only for pure PCM and pure Java dependencies
	 */
	public PcmDependency(EObject container, EObject containee, EAttribute containerFeatureOfContainer) {
		this(container, containerFeatureOfContainer, containee, null, null);
		Preconditions.checkArgument(containerFeatureOfContainer != null, "Containment feature cannot be null");
	}

	/**
	 * Meant only for pure PCM and pure Java dependencies
	 */
	public PcmDependency(EObject container, EObject containee, EReference containmentFeatureOfContainer) {
		this(container, containmentFeatureOfContainer, containee, containmentFeatureOfContainer.getEOpposite(), null);
		Preconditions.checkArgument(containmentFeatureOfContainer != null, "Containment feature cannot be null");
		Preconditions.checkArgument(containmentFeatureOfContainer.getEOpposite() != null,
				"Containment feature must have an opposite");
	}

	/**
	 * Meant only for pure PCM and pure Java dependencies
	 */
	public PcmDependency(EObject elem1, EStructuralFeature elem1Feat, EObject elem2, EStructuralFeature elem2Feat) {
		this(elem1, elem1Feat, elem2, elem2Feat, null);
	}

	/**
	 * Meant only for PCM <-> Java dependencies
	 */
	public PcmDependency(EObject elem1, EStructuralFeature elem1Feat, EObject elem2, EStructuralFeature elem2Feat,
			String correspondenceTag) {
		Preconditions.checkArgument(elem1 != null, "Elements cannot be null");
		Preconditions.checkArgument(elem2 != null, "Elements cannot be null");
		Preconditions.checkArgument(elem1 != elem2, "Elements cannot be the same (reference equal)");
		this.elem1 = elem1;
		this.elem1Feat = elem1Feat;
		this.elem2 = elem2;
		this.elem2Feat = elem2Feat;
		this.correspondenceTag = correspondenceTag;
	}

	public EObject getElem1() {
		return elem1;
	}

	public EStructuralFeature getElem1Feat() {
		return elem1Feat;
	}

	public EObject getElem2() {
		return elem2;
	}

	public EStructuralFeature getElem2Feat() {
		return elem2Feat;
	}

	public String getCorrespondenceTag() {
		return correspondenceTag;
	}

	public boolean hasElement(EObject elem) {
		return elem == elem1 || elem == elem2;
	}

	public boolean hasFeature(EStructuralFeature feat) {
		return feat == elem1Feat || feat == elem2Feat;
	}

	public boolean hasFeature(EObject elem, EStructuralFeature feat) {
		return (elem == elem1 && feat == elem1Feat) || (elem == elem2 && feat == elem2Feat);
	}

	public EStructuralFeature getFeatureOf(EObject elem) {
		if (!this.hasElement(elem))
			return null;

		return elem == elem1 ? elem1Feat : elem2Feat;
	}

	public EObject getElementOf(EStructuralFeature feat) {
		if (!this.hasFeature(feat))
			return null;

		return feat == elem1Feat ? elem1 : elem2;
	}

	public EObject getOtherElement(EObject elem) {
		if (!hasElement(elem))
			return null;

		return elem == elem1 ? elem1 : elem2;
	}

	public EStructuralFeature getOtherFeature(EStructuralFeature feat) {
		if (!hasFeature(feat))
			return null;

		return feat == elem1Feat ? elem1Feat : elem2Feat;
	}

	public boolean isReferenceDependency() {
		return this.elem1Feat == null ^ this.elem2Feat == null;
	}

	/**
	 * @return Whether this indicates that both elements have a mutual feature,
	 *         which makes them dependent on one another
	 */
	public boolean isFeatureDependency() {
		return this.elem1Feat != null && this.elem2Feat != null
				&& ((this.elem1Feat instanceof EAttribute && this.elem2Feat instanceof EAttribute)
						|| !isContainerDependency());
	}

	/**
	 * @return Whether this indicates that both elements depend on one another
	 *         without a concrete, mutual feature
	 */
	public boolean isElementDependency() {
		return this.elem1Feat == null && this.elem2Feat == null;
	}

	/**
	 * @return Whether this indicates that one element contains the other one, which
	 *         makes them dependent on one another
	 */
	public boolean isContainerDependency() {
		return this.elem1Feat instanceof EReference && this.elem2Feat instanceof EReference
				&& ((EReference) this.elem1Feat).getEOpposite() == this.elem2Feat
				&& ((EReference) this.elem2Feat).getEOpposite() == this.elem1Feat;
	}

	public boolean hasCorrespondenceTag() {
		return this.correspondenceTag != null;
	}

	public boolean isPurePCMDependency() {
		return this.elem1 instanceof org.palladiosimulator.pcm.core.entity.NamedElement
				&& this.elem2 instanceof org.palladiosimulator.pcm.core.entity.NamedElement;
	}

	public boolean isPureJavaDependency() {
		return this.elem1 instanceof org.emftext.language.java.commons.Commentable
				&& this.elem2 instanceof org.emftext.language.java.commons.Commentable;
	}

	public boolean isPcmJavaDependency() {
		return !this.isPurePCMDependency() && !this.isPureJavaDependency();
	}

	public boolean tagEquals(String tag) {
		if (this.correspondenceTag == tag)
			return true;
		if (this.correspondenceTag == null ^ tag == null)
			return false;

		return this.correspondenceTag.equals(tag);
	}

	public boolean correspondenceTagsEqual(PcmDependency dep) {
		return this.tagEquals(dep.correspondenceTag);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PcmDependency))
			return false;
		var castedO = (PcmDependency) obj;
		return this.hasFeature(castedO.elem1, castedO.elem1Feat) && this.hasFeature(castedO.elem2, castedO.elem2Feat)
				&& correspondenceTagsEqual(castedO);
	}
}