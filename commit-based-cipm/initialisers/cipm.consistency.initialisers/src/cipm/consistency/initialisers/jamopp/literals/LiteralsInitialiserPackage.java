package cipm.consistency.initialisers.jamopp.literals;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class LiteralsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new BinaryIntegerLiteralInitialiser(),
				new BinaryLongLiteralInitialiser(), new BooleanLiteralInitialiser(), new CharacterLiteralInitialiser(),
				new DecimalDoubleLiteralInitialiser(), new DecimalFloatLiteralInitialiser(),
				new DecimalIntegerLiteralInitialiser(), new DecimalLongLiteralInitialiser(),
				new HexDoubleLiteralInitialiser(), new HexFloatLiteralInitialiser(), new HexIntegerLiteralInitialiser(),
				new HexLongLiteralInitialiser(), new NullLiteralInitialiser(), new OctalIntegerLiteralInitialiser(),
				new OctalLongLiteralInitialiser(), new SuperInitialiser(), new ThisInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IBinaryIntegerLiteralInitialiser.class, IBinaryLongLiteralInitialiser.class,
				IBooleanLiteralInitialiser.class, ICharacterLiteralInitialiser.class,
				IDecimalDoubleLiteralInitialiser.class, IDecimalFloatLiteralInitialiser.class,
				IDecimalIntegerLiteralInitialiser.class, IDecimalLongLiteralInitialiser.class,
				IDoubleLiteralInitialiser.class, IFloatLiteralInitialiser.class, IHexDoubleLiteralInitialiser.class,
				IHexFloatLiteralInitialiser.class, IHexIntegerLiteralInitialiser.class,
				IHexLongLiteralInitialiser.class, IIntegerLiteralInitialiser.class, ILiteralInitialiser.class,
				ILongLiteralInitialiser.class, INullLiteralInitialiser.class, IOctalIntegerLiteralInitialiser.class,
				IOctalLongLiteralInitialiser.class, ISelfInitialiser.class, ISuperInitialiser.class,
				IThisInitialiser.class, });
	}
}
