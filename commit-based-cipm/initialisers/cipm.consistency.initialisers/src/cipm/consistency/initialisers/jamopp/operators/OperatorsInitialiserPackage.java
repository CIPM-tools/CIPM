package cipm.consistency.initialisers.jamopp.operators;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class OperatorsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new AdditionInitialiser(), new AssignmentAndInitialiser(),
				new AssignmentDivisionInitialiser(), new AssignmentExclusiveOrInitialiser(),
				new AssignmentInitialiser(), new AssignmentLeftShiftInitialiser(), new AssignmentMinusInitialiser(),
				new AssignmentModuloInitialiser(), new AssignmentMultiplicationInitialiser(),
				new AssignmentOrInitialiser(), new AssignmentPlusInitialiser(), new AssignmentRightShiftInitialiser(),
				new AssignmentUnsignedRightShiftInitialiser(), new ComplementInitialiser(), new DivisionInitialiser(),
				new EqualInitialiser(), new GreaterThanInitialiser(), new GreaterThanOrEqualInitialiser(),
				new LeftShiftInitialiser(), new LessThanInitialiser(), new LessThanOrEqualInitialiser(),
				new MinusMinusInitialiser(), new MultiplicationInitialiser(), new NegateInitialiser(),
				new NotEqualInitialiser(), new PlusPlusInitialiser(), new RemainderInitialiser(),
				new RightShiftInitialiser(), new SubtractionInitialiser(), new UnsignedRightShiftInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAdditionInitialiser.class, IAdditiveOperatorInitialiser.class,
				IAssignmentAndInitialiser.class, IAssignmentDivisionInitialiser.class,
				IAssignmentExclusiveOrInitialiser.class, IAssignmentInitialiser.class,
				IAssignmentLeftShiftInitialiser.class, IAssignmentMinusInitialiser.class,
				IAssignmentModuloInitialiser.class, IAssignmentMultiplicationInitialiser.class,
				IAssignmentOperatorInitialiser.class, IAssignmentOrInitialiser.class, IAssignmentPlusInitialiser.class,
				IAssignmentRightShiftInitialiser.class, IAssignmentUnsignedRightShiftInitialiser.class,
				IComplementInitialiser.class, IDivisionInitialiser.class, IEqualInitialiser.class,
				IEqualityOperatorInitialiser.class, IGreaterThanInitialiser.class, IGreaterThanOrEqualInitialiser.class,
				ILeftShiftInitialiser.class, ILessThanInitialiser.class, ILessThanOrEqualInitialiser.class,
				IMinusMinusInitialiser.class, IMultiplicationInitialiser.class,
				IMultiplicativeOperatorInitialiser.class, INegateInitialiser.class, INotEqualInitialiser.class,
				IOperatorInitialiser.class, IPlusPlusInitialiser.class, IRelationOperatorInitialiser.class,
				IRemainderInitialiser.class, IRightShiftInitialiser.class, IShiftOperatorInitialiser.class,
				ISubtractionInitialiser.class, IUnaryModificationOperatorInitialiser.class,
				IUnaryOperatorInitialiser.class, IUnsignedRightShiftInitialiser.class, });
	}
}
