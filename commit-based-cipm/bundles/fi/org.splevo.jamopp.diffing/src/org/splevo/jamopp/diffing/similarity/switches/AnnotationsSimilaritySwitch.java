package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.util.AnnotationsSwitch;
import org.emftext.language.java.classifiers.Classifier;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNamespaceUtil;

/**
 * Similarity decisions for annotation elements.
 */
public class AnnotationsSimilaritySwitch extends AnnotationsSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public AnnotationsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Boolean caseAnnotationInstance(AnnotationInstance instance1) {
		this.logInfoMessage("caseAnnotationInstance");

		AnnotationInstance instance2 = (AnnotationInstance) this.getCompareElement();

		Classifier class1 = instance1.getAnnotation();
		Classifier class2 = instance2.getAnnotation();

		Boolean classifierSimilarity = this.isSimilar(class1, class2);
		if (JaMoPPBooleanUtil.isFalse(classifierSimilarity)) {
			return Boolean.FALSE;
		}

		return JaMoPPNamespaceUtil.compareNamespacesAsString(instance1, instance2);
	}

	/**
	 * Checks the similarity of 2 {@link AnnotationAttributeSetting}s. Similarity is checked by comparing
	 * the attributes {@link AnnotationAttributeSetting#getAttribute()}.
	 * 
	 * @param setting1 The annotation attribute setting to compare with compareElement
	 * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseAnnotationAttributeSetting(AnnotationAttributeSetting setting1) {
		this.logInfoMessage("caseAnnotationAttributeSetting");

		AnnotationAttributeSetting setting2 = (AnnotationAttributeSetting) this.getCompareElement();

		Boolean attrSimilarity = this.isSimilar(setting1.getAttribute(), setting2.getAttribute());
		return JaMoPPBooleanUtil.isNotFalse(attrSimilarity);
	}

	@Override
	public Boolean defaultCase(EObject object) {
		this.logInfoMessage("defaultCase for Annotation");

		this.logInfoMessage("Default annotation comparing case for " + AnnotationsSimilaritySwitch.class.getSimpleName()
				+ ", similarity: true");
		return Boolean.TRUE;
	}
}