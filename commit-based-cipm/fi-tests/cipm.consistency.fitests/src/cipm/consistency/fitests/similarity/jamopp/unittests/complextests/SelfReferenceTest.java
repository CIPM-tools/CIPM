package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.IStructuralFeatureTest;
import cipm.consistency.initialisers.eobject.IEObjectInitialiser;

/**
 * A test class that checks the robustness of similarity checking against cyclic
 * references, where {@link EObject} sub-types T used in JaMoPP have
 * {@link EStructuralFeature}s, whose type is T. By setting the value of those
 * {@link EStructuralFeature}s to the object containing it, it is possible to
 * realise such scenarios. <br>
 * <br>
 * Note: For performance reasons, the type of the objects used in such cyclic
 * references (T from above) will be kept constant, meaning that other possible
 * types that could cause similar cyclic references will not be considered.
 * 
 * @author Alp Torac Genc
 */
@Disabled("Until cycle checking mechanisms are implemented")
public class SelfReferenceTest extends AbstractJaMoPPSimilarityTest implements IStructuralFeatureTest {
	/**
	 * Scans the object type generated with {@code init} for references
	 * ({@link EReference}) {@code Ref}, which can point at the object containing
	 * it. Then sets {@code Ref} to the object containing it, which results in the
	 * said object referencing itself. Then constructs 2 similar reference chains
	 * with the form: <br>
	 * <br>
	 * {@code eo_0 -> eo_1 -> ... -> eo_N-1 = ec_0 -> ec_1 -> ... -> ec_M-1 -> eo_N-1 = ec_0},
	 * where:
	 * <ul>
	 * <li>{@code eo_i} are the elements leading to the cycle, with
	 * {@code i = 0, ..., N-1} being the offset of the cycle
	 * <li>{@code ec_i} are the elements in the cycle, with {@code i = 0, ..., M-1}
	 * being the length of the cycle
	 * <li>N being cycleOffset
	 * <li>M being cycleLength
	 * </ul>
	 * Finally, generates {@link DynamicTest}s asserting that similarity checking
	 * can handle the generated reference chains without throwing exceptions.
	 * 
	 * @param cycleLength Amount of elements in the cycle - 1. Passing 0 here will
	 *                    result in the cycle to consist of one self-referencing
	 *                    element.
	 * @param cycleOffset Amount of elements leading to the cycle - 1. Passing 0
	 *                    here will result in the generated reference chain to only
	 *                    have the cycle and no elements leading to it.
	 */
	private Collection<DynamicTest> initialiseCyclicFeatures(IEObjectInitialiser init, int cycleLength,
			int cycleOffset) {
		var tests = new ArrayList<DynamicTest>();

		var obj = init.instantiate();
		var objCls = init.getInstanceClassOfInitialiser();

		for (var attr : obj.eClass().getEAllStructuralFeatures()) {
			if (attr.isChangeable()) {
				var attrType = attr.getEType().getInstanceClass();

				/*
				 * Construct 2 objects that reference each other and compare them to each other.
				 * Make sure to not clone those objects, since cloning method itself may cause
				 * an endless recursion.
				 */
				if (attrType.isAssignableFrom(objCls)) {
					var objs1 = new ArrayList<EObject>();
					var objs2 = new ArrayList<EObject>();

					// First objects
					objs1.add(init.instantiate());
					objs2.add(init.instantiate());

					// Add objects leading to the cycle
					for (int offset = 0; offset < cycleOffset; offset++) {
						this.setValueOf(objs1.get(objs1.size() - 1), attr, init.instantiate());
						this.setValueOf(objs2.get(objs2.size() - 1), attr, init.instantiate());
					}

					var objs1CycleStart = objs1.get(objs1.size() - 1);
					var objs2CycleStart = objs2.get(objs2.size() - 1);

					// Add objects within the cycle
					for (int cLen = 0; cLen < cycleLength; cLen++) {
						this.setValueOf(objs1.get(objs1.size() - 1), attr, init.instantiate());
						this.setValueOf(objs2.get(objs2.size() - 1), attr, init.instantiate());
					}

					// Close the cycle
					this.setValueOf(objs1.get(objs1.size() - 1), attr, objs1CycleStart);
					this.setValueOf(objs2.get(objs2.size() - 1), attr, objs2CycleStart);

					tests.add(DynamicTest.dynamicTest(String.format("%s.%s cyclic with (length=%d, offset=%d)",
							objCls.getSimpleName(), attr.getName(), cycleLength, cycleOffset), () -> {
								for (int i = 0; i < objs1.size(); i++) {
									final var idx = i;
									Assertions.assertDoesNotThrow(() -> this.isSimilar(objs1.get(idx), objs2.get(idx)));
									Assertions.assertTrue(this.isSimilar(objs1.get(idx), objs2.get(idx)));
								}
							}));
				}
			}
		}

		return tests;
	}

	/**
	 * Ensures that similarity checking can handle {@link EObject} instances used in
	 * JaMoPP that contain a reference to themselves and therefore have cyclic
	 * references to themselves.
	 * 
	 * @see {@link #initialiseCyclicFeatures(IEObjectInitialiser, int, int)}
	 */
	@TestFactory
	public Collection<DynamicTest> testSelfReference() {
		var tests = new ArrayList<DynamicTest>();
		for (var init : this.getUsedInitialiserPackage().getAllInitialiserInstances()) {
			for (int length = 0; length < 4; length++) {
				for (int offset = 0; offset < 3; offset++) {
					tests.addAll(this.initialiseCyclicFeatures((IEObjectInitialiser) init, length, offset));
				}
			}
		}
		return tests;
	}
}
