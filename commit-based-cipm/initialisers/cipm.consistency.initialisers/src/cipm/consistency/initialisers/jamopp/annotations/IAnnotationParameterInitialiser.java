package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationParameter;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotationParameterInitialiser extends ICommentableInitialiser {
	@Override
	public AnnotationParameter instantiate();

}
