package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.LambdaBody;
import org.emftext.language.java.expressions.LambdaExpression;
import org.emftext.language.java.expressions.LambdaParameters;

public interface ILambdaExpressionInitialiser extends IExpressionInitialiser {
	@Override
	public LambdaExpression instantiate();

	public default boolean setBody(LambdaExpression le, LambdaBody body) {
		le.setBody(body);
		return (body == null && le.getBody() == null) || le.getBody().equals(body);
	}

	public default boolean setParameters(LambdaExpression le, LambdaParameters param) {
		le.setParameters(param);
		return (param == null && le.getParameters() == null) || le.getParameters().equals(param);
	}
}
