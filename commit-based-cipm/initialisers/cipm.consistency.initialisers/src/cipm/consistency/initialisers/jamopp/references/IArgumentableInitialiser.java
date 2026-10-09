package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.Argumentable;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

/**
 * An interface meant for {@link IInitialiser} implementors that are supposed to
 * create {@link Argumentable} instances. <br>
 * <br>
 * {@code argable.getArgumentTypes().add(...)} cannot be used to modify argable.
 * 
 * @author Alp Torac Genc
 */
public interface IArgumentableInitialiser extends ICommentableInitialiser {
	@Override
	public Argumentable instantiate();

	/**
	 * Adds the given expression arg to argable as an argument. Uses
	 * {@code argable.getArguments().add(...)} to do so.
	 * 
	 * @see {@link IArgumentableInitialiser}
	 */
	public default boolean addArgument(Argumentable argable, Expression arg) {
		if (arg != null) {
			argable.getArguments().add(arg);
			return argable.getArguments().contains(arg);
		}
		return true;
	}

	public default boolean addArguments(Argumentable argable, Expression[] args) {
		return this.doMultipleModifications(argable, args, this::addArgument);
	}
}
