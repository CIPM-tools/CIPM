package cipm.consistency.fitests.repositorytests.util.commentremoval;

/**
 * An interface for classes that are meant to remove (Java) comments from code
 * snippets. <br>
 * <br>
 * Since comments may break when splitting a piece of code, it is theoretically
 * not possible to accurately remove all comments, without having access to the
 * entirety of the code. Due to this being potentially performance and memory
 * intensive, removing comments from a large code can mostly be done
 * approximately. As such, how the comments are detected and removed depends on
 * the concrete implementation.
 * 
 * @author Alp Torac Genc
 */
public interface ICommentRemover {
	/**
	 * Depending on the concrete implementor, the result may be an approximation.
	 * 
	 * @return The given text without comments.
	 */
	public String removeComments(String text);

	/**
	 * Depending on the concrete implementor, the result may be an approximation.
	 * 
	 * @return Whether a comment broke at the beginning of the text; i.e. if there
	 *         is a token that ends a comment is present at the beginning of the
	 *         text (i.e. {@code * /}), without a preceding token starting the
	 *         comment (such as {@code /*} ).
	 */
	public boolean hasLeadingBrokenComment(String text);

	/**
	 * Depending on the concrete implementor, the result may be an approximation.
	 * 
	 * @return Whether a comment broke at the end of the text; i.e. if there is a
	 *         token that starts a comment is present at the end of the text (such
	 *         as {@code /*}), without a following token ending the comment (i.e.
	 *         {@code * /}).
	 */
	public boolean hasTrailingBrokenComment(String text);
}
