package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AssignmentExpressionChild;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotationValueInitialiser;

public interface IAssignmentExpressionChildInitialiser extends IAnnotationValueInitialiser, IExpressionInitialiser {
	@Override
	public AssignmentExpressionChild instantiate();
}
