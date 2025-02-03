package org.splevo.jamopp.diffing.similarity.switches;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.EList;
import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.members.EnumConstant;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.members.util.MembersSwitch;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.Type;
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
		this.logMessage("caseMethod");

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

		/*
		 * ************************************** methods as members of regular classes
		 */
		if (method1.getContainingConcreteClassifier() != null) {
			ConcreteClassifier type1 = method1.getContainingConcreteClassifier();
			ConcreteClassifier type2 = method2.getContainingConcreteClassifier();
			return this.isSimilar(type1, type2);
		}

		/*
		 * ************************************** methods as members of anonymous
		 * classes
		 */
		if (method1.getContainingAnonymousClass() != null) {
			AnonymousClass type1 = method1.getContainingAnonymousClass();
			AnonymousClass type2 = method2.getContainingAnonymousClass();
			Boolean typeSimilarity = this.isSimilar(type1, type2);
			if (typeSimilarity != null) {
				return typeSimilarity;
			}
		}

		var containerString = method1.eContainer() == null ? "" : method1.eContainer().toString();

		this.logMessage("MethodDeclaration in unknown container: " + name1 + " : " + containerString, Level.WARN);
		return super.caseMethod(method1);
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
		this.logMessage("caseConstructor");

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

		/*
		 * ************************************** methods as members of regular classes
		 */
		if (constructor1.getContainingConcreteClassifier() != null) {
			ConcreteClassifier type1 = constructor1.getContainingConcreteClassifier();
			ConcreteClassifier type2 = constructor2.getContainingConcreteClassifier();
			return this.isSimilar(type1, type2);
		}

		/*
		 * ************************************** methods as members of anonymous
		 * classes
		 */
		if (constructor1.getContainingAnonymousClass() != null) {
			AnonymousClass type1 = constructor1.getContainingAnonymousClass();
			AnonymousClass type2 = constructor2.getContainingAnonymousClass();
			Boolean typeSimilarity = this.isSimilar(type1, type2);
			if (typeSimilarity != null) {
				return typeSimilarity;
			}
		}

		var containerString = constructor1.eContainer() == null ? "" : constructor1.eContainer().toString();

		this.logMessage("ConstructorDeclaration in unknown container: " + name1 + " : " + containerString, Level.WARN);
		return super.caseConstructor(constructor1);
	}

	@Override
	public Boolean caseEnumConstant(EnumConstant const1) {
		this.logMessage("caseEnumConstant");

		EnumConstant const2 = (EnumConstant) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(const1, const2);
	}

	@Override
	public Boolean caseMember(Member member1) {
		this.logMessage("caseMember");

		Member member2 = (Member) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(member1, member2);
	}
	}
}