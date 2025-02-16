package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationValue;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotationValueInitialiser extends ICommentableInitialiser {
	@Override
	public AnnotationValue instantiate();

}
