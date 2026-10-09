package cipm.consistency.initialisers.jamopp.expressions;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ExpressionsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new AdditiveExpressionInitialiser(), new AndExpressionInitialiser(),
				new ArrayConstructorReferenceExpressionInitialiser(), new AssignmentExpressionInitialiser(),
				new CastExpressionInitialiser(), new ClassTypeConstructorReferenceExpressionInitialiser(),
				new ConditionalAndExpressionInitialiser(), new ConditionalExpressionInitialiser(),
				new ConditionalOrExpressionInitialiser(), new EqualityExpressionInitialiser(),
				new ExclusiveOrExpressionInitialiser(), new ExplicitlyTypedLambdaParametersInitialiser(),
				new ExpressionListInitialiser(), new ImplicitlyTypedLambdaParametersInitialiser(),
				new InclusiveOrExpressionInitialiser(), new InstanceOfExpressionInitialiser(),
				new LambdaExpressionInitialiser(), new MultiplicativeExpressionInitialiser(),
				new NestedExpressionInitialiser(), new PrefixUnaryModificationExpressionInitialiser(),
				new PrimaryExpressionReferenceExpressionInitialiser(), new RelationExpressionInitialiser(),
				new ShiftExpressionInitialiser(), new SingleImplicitLambdaParameterInitialiser(),
				new SuffixUnaryModificationExpressionInitialiser(), new UnaryExpressionInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAdditiveExpressionChildInitialiser.class,
				IAdditiveExpressionInitialiser.class, IAndExpressionChildInitialiser.class,
				IAndExpressionInitialiser.class, IArrayConstructorReferenceExpressionInitialiser.class,
				IAssignmentExpressionChildInitialiser.class, IAssignmentExpressionInitialiser.class,
				ICastExpressionInitialiser.class, IClassTypeConstructorReferenceExpressionInitialiser.class,
				IConditionalAndExpressionChildInitialiser.class, IConditionalAndExpressionInitialiser.class,
				IConditionalExpressionChildInitialiser.class, IConditionalExpressionInitialiser.class,
				IConditionalOrExpressionChildInitialiser.class, IConditionalOrExpressionInitialiser.class,
				IEqualityExpressionChildInitialiser.class, IEqualityExpressionInitialiser.class,
				IExclusiveOrExpressionChildInitialiser.class, IExclusiveOrExpressionInitialiser.class,
				IExplicitlyTypedLambdaParametersInitialiser.class, IExpressionInitialiser.class,
				IExpressionListInitialiser.class, IImplicitlyTypedLambdaParametersInitialiser.class,
				IInclusiveOrExpressionChildInitialiser.class, IInclusiveOrExpressionInitialiser.class,
				IInstanceOfExpressionChildInitialiser.class, IInstanceOfExpressionInitialiser.class,
				ILambdaBodyInitialiser.class, ILambdaExpressionInitialiser.class, ILambdaParametersInitialiser.class,
				IMethodReferenceExpressionChildInitialiser.class, IMethodReferenceExpressionInitialiser.class,
				IMultiplicativeExpressionChildInitialiser.class, IMultiplicativeExpressionInitialiser.class,
				INestedExpressionInitialiser.class, IPrefixUnaryModificationExpressionInitialiser.class,
				IPrimaryExpressionInitialiser.class, IPrimaryExpressionReferenceExpressionInitialiser.class,
				IRelationExpressionChildInitialiser.class, IRelationExpressionInitialiser.class,
				IShiftExpressionChildInitialiser.class, IShiftExpressionInitialiser.class,
				ISingleImplicitLambdaParameterInitialiser.class, ISuffixUnaryModificationExpressionInitialiser.class,
				IUnaryExpressionChildInitialiser.class, IUnaryExpressionInitialiser.class,
				IUnaryModificationExpressionChildInitialiser.class, IUnaryModificationExpressionInitialiser.class, });
	}
}
