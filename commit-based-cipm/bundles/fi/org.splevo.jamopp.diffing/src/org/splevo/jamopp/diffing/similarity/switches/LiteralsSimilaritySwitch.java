package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.literals.BinaryIntegerLiteral;
import org.emftext.language.java.literals.BinaryLongLiteral;
import org.emftext.language.java.literals.BooleanLiteral;
import org.emftext.language.java.literals.CharacterLiteral;
import org.emftext.language.java.literals.DecimalDoubleLiteral;
import org.emftext.language.java.literals.DecimalFloatLiteral;
import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.emftext.language.java.literals.DecimalLongLiteral;
import org.emftext.language.java.literals.HexDoubleLiteral;
import org.emftext.language.java.literals.HexFloatLiteral;
import org.emftext.language.java.literals.HexIntegerLiteral;
import org.emftext.language.java.literals.HexLongLiteral;
import org.emftext.language.java.literals.OctalIntegerLiteral;
import org.emftext.language.java.literals.OctalLongLiteral;
import org.emftext.language.java.literals.util.LiteralsSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNullCheckUtil;
import org.splevo.jamopp.diffing.util.JaMoPPStringUtil;

/**
 * Similarity decisions for literal elements.
 */
public class LiteralsSimilaritySwitch extends LiteralsSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public LiteralsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch) {
		this.similaritySwitch = similaritySwitch;
	}

	/**
	 * Checks the similarity of 2 boolean literals. Similarity is checked by comparing
	 * their values ({@link BooleanLiteral#isValue()}).
	 * 
	 * @param boolean1 The boolean literal to compare with compareElement
	 * @return True if values are equal, false otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseBooleanLiteral(BooleanLiteral boolean1) {
		this.logInfoMessage("caseBooleanLiteral");

		BooleanLiteral boolean2 = (BooleanLiteral) this.getCompareElement();
		return (boolean1.isValue() == boolean2.isValue());
	}

	/**
	 * Checks the similarity of 2 character literals. Similarity is checked by comparing
	 * their values ({@link CharacterLiteral#getValue()}).
	 * 
	 * @param char1 The character literal to compare with compareElement
	 * @return True if values are equal, false otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseCharacterLiteral(CharacterLiteral char1) {
		this.logInfoMessage("caseCharacterLiteral");

		CharacterLiteral char2 = (CharacterLiteral) this.getCompareElement();
		return JaMoPPStringUtil.stringsEqual(char1.getValue(), char2.getValue());
	}

	/**
	 * Checks the similarity of 2 decimal float literals. Similarity is checked by comparing
	 * their values ({@link DecimalFloatLiteral#getDecimalValue()}).
	 * 
	 * @param float1 The decimal float literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseDecimalFloatLiteral(DecimalFloatLiteral float1) {
		this.logInfoMessage("caseDecimalFloatLiteral");

		DecimalFloatLiteral float2 = (DecimalFloatLiteral) this.getCompareElement();
		return compareFloat(float1.getDecimalValue(), float2.getDecimalValue());
	}

	/**
	 * Checks the similarity of 2 hex float literals. Similarity is checked by comparing
	 * their values ({@link HexFloatLiteral#getHexValue()}).
	 * 
	 * @param float1 The hex float literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseHexFloatLiteral(HexFloatLiteral float1) {
		this.logInfoMessage("caseHexFloatLiteral");

		HexFloatLiteral float2 = (HexFloatLiteral) this.getCompareElement();
		return compareFloat(float1.getHexValue(), float2.getHexValue());
	}

	/**
	 * Checks the similarity of 2 decimal double literals. Similarity is checked by comparing
	 * their values ({@link DecimalDoubleLiteral#getDecimalValue()}).
	 * 
	 * @param double1 The decimal double literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseDecimalDoubleLiteral(DecimalDoubleLiteral double1) {
		this.logInfoMessage("caseDecimalDoubleLiteral");

		DecimalDoubleLiteral double2 = (DecimalDoubleLiteral) this.getCompareElement();
		return compareDouble(double1.getDecimalValue(), double2.getDecimalValue());
	}

	/**
	 * Checks the similarity of 2 hex double literals. Similarity is checked by comparing
	 * their values ({@link HexDoubleLiteral#getHexValue()}).
	 * 
	 * @param double1 The hex double literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseHexDoubleLiteral(HexDoubleLiteral double1) {
		this.logInfoMessage("caseHexDoubleLiteral");

		HexDoubleLiteral double2 = (HexDoubleLiteral) this.getCompareElement();
		return compareDouble(double1.getHexValue(), double2.getHexValue());
	}

	/**
	 * @return True if the given double values are equal or if they are both NaN. False otherwise.
	 */
	private boolean compareDouble(double d1, double d2) {
		return d1 == d2 || Double.isNaN(d1) && Double.isNaN(d2);
	}

	/**
	 * @return True if the given float values are equal or if they are both NaN. False otherwise.
	 */
	private boolean compareFloat(float f1, float f2) {
		return f1 == f2 || Float.isNaN(f1) && Float.isNaN(f2);
	}

	/**
	 * Checks the similarity of 2 decimal integer literals. Similarity is checked by comparing
	 * their values ({@link DecimalIntegerLiteral#getDecimalValue()}).
	 * 
	 * @param int1 The decimal integer literal to compare to compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseDecimalIntegerLiteral(DecimalIntegerLiteral int1) {
		this.logInfoMessage("caseDecimalIntegerLiteral");

		DecimalIntegerLiteral int2 = (DecimalIntegerLiteral) this.getCompareElement();

		var val1 = int1.getDecimalValue();
		var val2 = int2.getDecimalValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 hex integer literals. Similarity is checked by comparing
	 * their values ({@link HexIntegerLiteral#getHexValue()}).
	 * 
	 * @param int1 The hex integer literal to compare to compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseHexIntegerLiteral(HexIntegerLiteral int1) {
		this.logInfoMessage("caseHexIntegerLiteral");

		HexIntegerLiteral int2 = (HexIntegerLiteral) this.getCompareElement();

		var val1 = int1.getHexValue();
		var val2 = int2.getHexValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 octal integer literals. Similarity is checked by comparing
	 * their values ({@link OctalIntegerLiteral#getOctalValue()}).
	 * 
	 * @param int1 The octal integer literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseOctalIntegerLiteral(OctalIntegerLiteral int1) {
		this.logInfoMessage("caseOctalIntegerLiteral");

		OctalIntegerLiteral int2 = (OctalIntegerLiteral) this.getCompareElement();

		var val1 = int1.getOctalValue();
		var val2 = int2.getOctalValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 decimal long literals. Similarity is checked by comparing
	 * their values ({@link DecimalLongLiteral#getDecimalValue()}).
	 * 
	 * @param long1 The decimal long literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseDecimalLongLiteral(DecimalLongLiteral long1) {
		this.logInfoMessage("caseDecimalLongLiteral");

		DecimalLongLiteral long2 = (DecimalLongLiteral) this.getCompareElement();

		var val1 = long1.getDecimalValue();
		var val2 = long2.getDecimalValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 hex long literals. Similarity is checked by comparing
	 * their values ({@link HexLongLiteral#getHexValue()}).
	 * 
	 * @param long1 The hex long literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseHexLongLiteral(HexLongLiteral long1) {
		this.logInfoMessage("caseHexLongLiteral");

		HexLongLiteral long2 = (HexLongLiteral) this.getCompareElement();

		var val1 = long1.getHexValue();
		var val2 = long2.getHexValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 octal long literals. Similarity is checked by comparing
	 * their values ({@link OctalLongLiteral#getOctalValue()}).
	 * 
	 * @param long1 The octal long literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseOctalLongLiteral(OctalLongLiteral long1) {
		this.logInfoMessage("caseOctalLongLiteral");

		OctalLongLiteral long2 = (OctalLongLiteral) this.getCompareElement();

		var val1 = long1.getOctalValue();
		var val2 = long2.getOctalValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 binary long literals. Similarity is checked by comparing
	 * their values ({@link BinaryLongLiteral#getBinaryValue()}).
	 * 
	 * @param long1 The binary long literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseBinaryLongLiteral(BinaryLongLiteral long1) {
		this.logInfoMessage("caseBinaryLongLiteral");

		BinaryLongLiteral long2 = (BinaryLongLiteral) this.getCompareElement();

		var val1 = long1.getBinaryValue();
		var val2 = long2.getBinaryValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Checks the similarity of 2 binary integer literals. Similarity is checked by comparing
	 * their values ({@link BinaryIntegerLiteral#getBinaryValue()}).
	 * 
	 * @param int1 The binary integer literal to compare with compareElement
	 * @return True if values are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseBinaryIntegerLiteral(BinaryIntegerLiteral int1) {
		this.logInfoMessage("caseBinaryIntegerLiteral");

		BinaryIntegerLiteral int2 = (BinaryIntegerLiteral) this.getCompareElement();

		var val1 = int1.getBinaryValue();
		var val2 = int2.getBinaryValue();
		return JaMoPPNullCheckUtil.bothNullOrEqual(val1, val2);
	}

	/**
	 * Check null literal similarity.<br>
	 * 
	 * Null literals are always assumed to be similar.
	 * 
	 * @param object The literal to compare with the compare element.
	 * @return True As null always means null.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean defaultCase(EObject object) {
		this.logInfoMessage("defaultCase for Literals");

		return Boolean.TRUE;
	}
}