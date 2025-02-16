package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.Annotable;
import org.emftext.language.java.annotations.AnnotationInstance;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotableInitialiser extends ICommentableInitialiser {
	@Override
	public Annotable instantiate();

	public default boolean addAnnotation(Annotable aObj, AnnotationInstance anno) {
		if (anno != null) {
			aObj.getAnnotations().add(anno);
			return aObj.getAnnotations().contains(anno);
		}

		return true;
	}

	public default boolean addAnnotations(Annotable aObj, AnnotationInstance[] annos) {
		return this.doMultipleModifications(aObj, annos, this::addAnnotation);
	}
}
