package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.common.util.EList;
import org.emftext.language.java.members.AdditionalField;
import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.members.EnumConstant;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.members.util.MembersSwitch;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypedElement;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNullCheckUtil;

import com.google.common.base.Strings;

/**
 * Similarity decisions for the member elements.
 */
public class MembersSimilaritySwitch extends MembersSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public MembersSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
	 * Check abstract method declaration similarity. Similarity is checked by
	 * <ul>
	 * <li>name</li>
	 * <li>parameter list size</li>
	 * <li>parameter types</li>
	 * <li>name</li>
	 * <li>container for
	 * <ul>
	 * <li>AbstractTypeDeclaration</li>
	 * <li>AnonymousClassDeclaration</li>
	 * <li>Model</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * The container must be checked to check similarity for referenced methods.
	 * 
	 * 
	 * @param method1 The abstract method declaration to compare with the compare
	 *                element.
	 * @return True/False if the abstract method declarations are similar or not.
	 */
	@Override
	public Boolean caseMethod(Method method1) {
		this.logInfoMessage("caseMethod");

		Method method2 = (Method) this.getCompareElement();

		// if methods have different names they are not similar.
		var nameSimilarity = JaMoPPNameComparisonUtil.namesEqual(method1, method2);
		if (JaMoPPBooleanUtil.isFalse(nameSimilarity)) {
			return Boolean.FALSE;
		}

		var params1 = method1.getParameters();
		var params2 = method2.getParameters();

		// Null check to avoid NullPointerExceptions
		if (JaMoPPNullCheckUtil.onlyOneIsNull(params1, params2)) {
			return Boolean.FALSE;
		} else if (JaMoPPNullCheckUtil.allNonNull(params1, params2)) {
			if (params1.size() != params2.size()) {
				return Boolean.FALSE;
			}

			for (int i = 0; i < params1.size(); i++) {
				Parameter param1 = params1.get(i);
				Parameter param2 = params2.get(i);

				var tref1 = param1.getTypeReference();
				var tref2 = param2.getTypeReference();

				if (JaMoPPNullCheckUtil.onlyOneIsNull(tref1, tref2)) {
					return Boolean.FALSE;
				} else if (JaMoPPNullCheckUtil.allNonNull(tref1, tref2)) {
					Type type1 = tref1.getTarget();
					Type type2 = tref2.getTarget();
					Boolean typeSimilarity = this.isSimilar(type1, type2);
					if (JaMoPPBooleanUtil.isFalse(typeSimilarity)) {
						return Boolean.FALSE;
					}
					if (tref1.getArrayDimension() != tref2.getArrayDimension()) {
						return Boolean.FALSE;
					}
				}
			}
		}

		var method1Container = method1.eContainer();
		var method2Container = method2.eContainer();

		if (method1Container == null) {
			this.logWarnMessage("MethodDeclaration (method1, parameter of caseMethod) " + Strings.nullToEmpty(method1.getName()) + " has no container");
		}

		if (method2Container == null) {
			this.logWarnMessage("MethodDeclaration (method2, compare element) " + Strings.nullToEmpty(method2.getName()) + " has no container");
		}

		return this.isSimilar(method1Container, method2Container);
	}

	/**
	 * Check constuctor declaration similarity. Similarity is checked by
	 * <ul>
	 * <li>name</li>
	 * <li>parameter list size</li>
	 * <li>parameter types</li>
	 * <li>name</li>
	 * <li>container for
	 * <ul>
	 * <li>AbstractTypeDeclaration</li>
	 * <li>AnonymousClassDeclaration</li>
	 * <li>Model</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * The container must be checked to check similarity for referenced methods.
	 * 
	 * 
	 * @param constructor1 The abstract method declaration to compare with the
	 *                     compare element.
	 * @return True/False if the abstract method declarations are similar or not.
	 */
	@Override
	public Boolean caseConstructor(Constructor constructor1) {
		this.logInfoMessage("caseConstructor");

		Constructor constructor2 = (Constructor) this.getCompareElement();

		// if methods have different names they are not similar.
		var nameSimilarity = JaMoPPNameComparisonUtil.namesEqual(constructor1, constructor2);
		if (JaMoPPBooleanUtil.isFalse(nameSimilarity)) {
			return Boolean.FALSE;
		}

		EList<Parameter> params1 = constructor1.getParameters();
		EList<Parameter> params2 = constructor2.getParameters();
		Boolean parameterSimilarity = this.areSimilar(params1, params2);
		if (JaMoPPBooleanUtil.isFalse(parameterSimilarity)) {
			return Boolean.FALSE;
		}

		var constructor1Container = constructor1.eContainer();
		var constructor2Container = constructor2.eContainer();

		if (constructor1Container == null) {
			this.logWarnMessage("ConstructorDeclaration (constructor1, parameter of caseConstructor) " + Strings.nullToEmpty(constructor1.getName()) + " has no container");
		}

		if (constructor2Container == null) {
			this.logWarnMessage("ConstructorDeclaration (constructor2, compare element) " + Strings.nullToEmpty(constructor2.getName()) + " has no container");
		}

		return this.isSimilar(constructor1Container, constructor2Container);
	}

	@Override
	public Boolean caseEnumConstant(EnumConstant const1) {
		this.logInfoMessage("caseEnumConstant");

		EnumConstant const2 = (EnumConstant) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(const1, const2);
	}

	@Override
	public Boolean caseMember(Member member1) {
		this.logInfoMessage("caseMember");

		Member member2 = (Member) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(member1, member2);
	}

	/**
	 * TODO Review this method to make sure it is correct.
	 * 
	 * <i><b>This method was added later, because comparing improperly
	 * initialised additional fields could result in null otherwise.</b></i>
	 * <br><br>
	 * 
	 * Additional fields are considered similar, if:
	 * <ul>
	 * <li> Their names ({@link AdditionalField#getName()}) are equal,
	 * <li> Their types ({@link AdditionalField#getTypeReference()}) are similar,
	 * <li> Types of their containers ({@code additionalField.eContainer().getTypeReference()}) are similar,
	 * <li> Containers of their containers ({@code additionalField.eContainer().eContainer()}) are similar.
	 * </ul>
	 * 
	 * @param additionalField1 The additional field to compare with compareElement
	 * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseAdditionalField(AdditionalField additionalField1) {
		this.logInfoMessage("caseAdditionalField");

		AdditionalField additionalField2 = (AdditionalField) this.getCompareElement();
		var nameSimilarity = JaMoPPNameComparisonUtil.namesEqual(additionalField1, additionalField2);
		if (JaMoPPBooleanUtil.isFalse(nameSimilarity)) {
			return Boolean.FALSE;
		}

		var type1 = additionalField1.getTypeReference();
		var type2 = additionalField2.getTypeReference();

		// Compare additional field types
		// Account for similarity result being null
		if (JaMoPPBooleanUtil.isNotTrue(this.isSimilar(type1, type2))) {
			return Boolean.FALSE;
		}

		var container1 = additionalField1.eContainer();
		var container2 = additionalField2.eContainer();

		if (JaMoPPNullCheckUtil.onlyOneIsNull(container1, container2)) {
			return Boolean.FALSE;
		}

		// Null check to avoid null pointer exceptions
		if (JaMoPPNullCheckUtil.allNonNull(container1, container2)) {
			var castedCon1 = (TypedElement) container1;
			var castedCon2 = (TypedElement) container2;

			var conType1 = castedCon1.getTypeReference();
			var conType2 = castedCon2.getTypeReference();

			// Compare container types
			// Account for similarity result being null
			if (JaMoPPBooleanUtil.isNotTrue(this.isSimilar(conType1, conType2))) {
				return Boolean.FALSE;
			}

			var containerOfCon1 = castedCon1.eContainer();
			var containerOfCon2 = castedCon2.eContainer();

			// Compare container of container
			// Account for similarity result being null
			if (JaMoPPBooleanUtil.isNotTrue(this.isSimilar(containerOfCon1, containerOfCon2))) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
}