/**
 */
package org.splevo.jamopp.diffing.jamoppdiff;

import org.emftext.language.java.statements.Statement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Statement Change</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A change on a statement level.
 * Depending on the type of change (delete, add, remove, move) the left, right or both statement references are filled.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.splevo.jamopp.diffing.jamoppdiff.StatementChange#getChangedStatement <em>Changed Statement</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.splevo.jamopp.diffing.jamoppdiff.JaMoPPDiffPackage#getStatementChange()
 * @model
 * @generated
 */
public interface StatementChange extends JaMoPPDiff {
	/**
	 * Returns the value of the '<em><b>Changed Statement</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Changed Statement</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Changed Statement</em>' reference.
	 * @see #setChangedStatement(Statement)
	 * @see org.splevo.jamopp.diffing.jamoppdiff.JaMoPPDiffPackage#getStatementChange_ChangedStatement()
	 * @model required="true"
	 * @generated
	 */
	Statement getChangedStatement();

	/**
	 * Sets the value of the '{@link org.splevo.jamopp.diffing.jamoppdiff.StatementChange#getChangedStatement <em>Changed Statement</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Changed Statement</em>' reference.
	 * @see #getChangedStatement()
	 * @generated
	 */
	void setChangedStatement(Statement value);

} // StatementChange
