package cipm.consistency.vsum.test.pcm.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.junit.jupiter.api.Assertions;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import cipm.consistency.cpr.pcmjava.userinteraction.NamespaceConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.ImUpdateEvalData;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.IMUpdateEvaluator;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.PcmVsumFacadeImpl;
import cipm.consistency.vsum.test.pcm.cprunittests.UnnamedModuleComponentDetectionStrategy;
import mir.reactions.allPcm.AllPcmChangePropagationSpecification;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;

import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmToJavaChangePropagationTest {
	private static final Logger LOGGER = Logger.getLogger(PcmToJavaChangePropagationTest.class);

	private PcmVsumFacade vsumFacade;
	private PcmFacade pcmFacade;
	private ImFacade imFacade;
	private JavaModelFacade javaFacade;

	private ExperimentDirLayout dirLayout;
	private ExperimentResourceWrapper experimentResourceWrapper;

	private ResourceSet resSet;

	private JaccardCoefficientResult jcOfJavaInJavaToPcmProp;
	private JaccardCoefficientResult jcOfPcmInJavaToPcmProp;
	private ImUpdateEvalData fScoreOfImInJavaToPcmProp;

	public PcmFacade getPcmFacade() {
		return this.pcmFacade;
	}

	public PcmVsumFacade getPcmVsumFacade() {
		return this.vsumFacade;
	}

	protected Propagation propagateChangesToResource(Resource res, Collection<EChange> changes) {
		this.getPcmVsumFacade().addChanges(changes);
		var prop = this.getPcmVsumFacade().propagateResource(res);
		Assertions.assertNull(prop.getException());
		return prop;
	}

	protected ImFacade getImFacade() {
		return this.imFacade;
	}

	protected JavaModelFacade getJavaFacade() {
		return this.javaFacade;
	}

	public void setUp(ExperimentDirLayout dirLayout) {
		this.dirLayout = dirLayout;

		this.copyTestResources();

		imFacade = this.setupImFacade();
		pcmFacade = this.setupPcmFacade();
		javaFacade = this.setupJavaFacade();
		vsumFacade = this.setupVsumFacade();

		computeEvaluationResultsForJavaToPcmPropagation();
	}

	public void tearDown() {
		// Closes all underlying models too
//		vsumFacade.close();
//		vsumFacade = null;
//
//		dirLayout = null;
//
//		oldWrapper.close();
//		newWrapper.close();
//		oldCopyWrapper.close();
//		newCopyWrapper.close();
//		propWrapper.close();
//
//		oldWrapper = null;
//		newWrapper = null;
//		oldCopyWrapper = null;
//		newCopyWrapper = null;
//		propWrapper = null;
//
//		jcOfJavaInJavaToPcmProp = null;
//		jcOfPcmInJavaToPcmProp = null;
//		fScoreOfImInJavaToPcmProp = null;
//
//		JavaModelAccess.removeJavaModel();
	}

	protected PcmFacade setupPcmFacade() {
		var pcmFacade = new PcmFacade();
		pcmFacade.initialize(dirLayout.getPropagatedDirLayout().getPcmDirPath());
		return pcmFacade;
	}

	private void computeEvaluationResultsForJavaToPcmPropagation() {
		jcOfJavaInJavaToPcmProp = computeJCForJava(experimentResourceWrapper.getJavaPropagationPropagatedJavaModel(),
				experimentResourceWrapper.getJavaPropagationInitialJavaModel());
		jcOfPcmInJavaToPcmProp = computeJCForPcm(experimentResourceWrapper.getJavaPropagationPropagatedPcmRepository(),
				experimentResourceWrapper.getJavaPropagationInitialPcmRepository());
		fScoreOfImInJavaToPcmProp = computeFScoreForIm(
				(Repository) experimentResourceWrapper.getJavaPropagationPropagatedPcmRepository().getContents().get(0),
				(InstrumentationModel) experimentResourceWrapper.getJavaPropagationPropagatedIm().getContents().get(0));
	}

	/**
	 * Use {@link #getRootPath()} as the root directory of the PcmVsumFacade.<br>
	 * <br>
	 * It is not recommended to call the super method from the concrete classes
	 * while overriding this method, in order to keep the construction clear and to
	 * avoid possible side effects. If only a minimal PCM without correspondences is
	 * desired, the super method can be used.
	 * 
	 * @return The VSUM facade for the PCM that will be used in this test.
	 */
	protected PcmVsumFacade setupVsumFacade() {
		return new PcmVsumFacadeImpl(dirLayout.getPropagatedDirLayout().getRootPath(),
				List.of(pcmFacade, imFacade, javaFacade), this.getCPRs());
	}

	/**
	 * Copies all relevant Resource files from existing Teammates tests that
	 * propagate Java code changes to PCM.
	 * <p>
	 * Old model Resources are copied twice, since one copy will be used for
	 * evaluation purposes and will not be modified, while the other copy will be
	 * modified via propagation. Because it is not possible to directly set the
	 * Resource of ModelFacade instances, copy the Resource instances to their
	 * designated paths and let the ModelFacades load them.
	 */
	private void copyTestResources() {
		this.resSet = new ResourceSetImpl();
		this.experimentResourceWrapper = new ExperimentResourceWrapper(this.resSet, this.dirLayout);
		this.experimentResourceWrapper.initialise();
	}

//	private class CreateChangeTrio {
//		private CreateEObject<?> cc;
//		private InsertEReference<?, ?> ir;
//		private ReplaceSingleValuedEAttribute<?, ?> setID;
//
//		private CreateChangeTrio(CreateEObject<?> cc, InsertEReference<?, ?> ir,
//				ReplaceSingleValuedEAttribute<?, ?> setID) {
//			this.cc = cc;
//			this.ir = ir;
//			this.setID = setID;
//		}
//
//		/**
//		 * 1: Repository 2: Repository content 3: Content of Repository content ...
//		 */
//		public int getCreatedObjectDepth() {
//			return URI.createURI(ir.getAffectedEObjectID()).fragment().split("/").length;
//		}
//
//		public boolean isContainer(String containerURIFragment) {
//			return URI.createURI(ir.getAffectedEObjectID()).fragment().equals(containerURIFragment);
//		}
//
//		public boolean isContainerRepository() {
//			return isContainer(propWrapper.getPcmRepository()
//					.getURIFragment(((Repository) propWrapper.getPcmRepository().getContents().get(0))));
//		}
//	}

	public int getMaxDepth(EChange change) {
		var aID = ChangeUtil.getAffectedEObjectID(change);
		var oID = ChangeUtil.getOldValueID(change);
		var nID = ChangeUtil.getNewValueID(change);

		var aDepth = 0;
		var oDepth = 0;
		var nDepth = 0;

		if (aID != null) {
			aDepth = getDepth(URI.createURI(aID));
		}
		if (oID != null) {
			oDepth = getDepth(URI.createURI(oID));
		}
		if (nID != null) {
			nDepth = getDepth(URI.createURI(nID));
		}

		return Math.max(aDepth, Math.max(oDepth, nDepth));
	}

	public int getDepth(URI uri) {
		if (uri == null || !uri.hasFragment())
			return 0;
		var depth = uri.fragment().split("/").length;
		return depth == 0 ? depth : depth - 2;
	}

	public int getCreatedObjectDepth(InsertEReference<?, ?> ir) {
		return URI.createURI(ir.getAffectedEObjectID()).fragment().split("/").length;
	}

	public boolean isContainer(InsertEReference<?, ?> ir, String containerURIFragment) {
		return URI.createURI(ir.getAffectedEObjectID()).fragment().equals(containerURIFragment);
	}

	public boolean isContainerRepository(InsertEReference<?, ?> ir) {
		return isContainer(ir, experimentResourceWrapper.getPcmRepositoryToPropagate().getURIFragment(
				((Repository) experimentResourceWrapper.getPcmRepositoryToPropagate().getContents().get(0))));
	}

	private final static String cachedEObjectURI = "cache:/0";

	private List<EChange> orderPCMchanges(List<EChange> changeSequence) {
		var newChangeList = new ArrayList<EChange>();

		var changes = new HashMap<Integer, ArrayList<EChange>>();
		EChange createChange = null;
		var maxDepth = 0;

		// Regex used to analyse / verify (remove #):
		// </eobject:CreateEObject>(?!\r\n###(?:<reference:InsertEReference|<attribute:ReplaceSingleValuedEAttribute|<reference:ReplaceSingleValuedEReference))

		for (int i = 0; i < changeSequence.size(); i++) {
			var currentChange = changeSequence.get(i);
			if (currentChange instanceof CreateEObject) {
				createChange = currentChange;
				continue;
			}
			// All CreateEObject changes must be preceded by an InsertEReference or
			// ReplaceSingleValuedEReference change that inserts it into the PCM
			var precedsCreate = currentChange instanceof InsertEReference
					|| currentChange instanceof ReplaceSingleValuedEReference
							&& ChangeUtil.getNewValueID(currentChange).equals(cachedEObjectURI);

			// Skip SEFF changes
			if ((ChangeUtil.getAffectedFeature(currentChange) != null
					&& ChangeUtil.getAffectedFeature(currentChange).getName().contains("serviceEffectSpecifications"))
					||

					(ChangeUtil.getOldValueID(currentChange) != null
							&& ChangeUtil.getOldValueID(currentChange).contains("serviceEffectSpecifications"))

					|| (ChangeUtil.getNewValueID(currentChange) != null
							&& ChangeUtil.getNewValueID(currentChange).contains("serviceEffectSpecifications"))

					|| (ChangeUtil.getAffectedEObjectID(currentChange) != null && ChangeUtil
							.getAffectedEObjectID(currentChange).contains("serviceEffectSpecifications"))) {
				createChange = null;
				continue;
			}

			var depth = getMaxDepth(currentChange);

			if (maxDepth < depth)
				maxDepth = depth;

			if (!changes.containsKey(depth)) {
				changes.put(depth, new ArrayList<EChange>());
			}

			if (precedsCreate && createChange != null) {
				changes.get(depth).add(createChange);
				createChange = null;
			}
			changes.get(depth).add(currentChange);
		}

		// Add PCM elements in Breadth-First order, as this will ensure that all PCM
		// elements are known
		for (int i = 0; i <= maxDepth; i++) {
			if (changes.containsKey(i)) {
				newChangeList.addAll(changes.get(i));
			}
		}

//		if (changeSequence.size() != newChangeList.size()) {
//			var largerList = changeSequence.size() > newChangeList.size() ? changeSequence : newChangeList;
//			var smallerList = changeSequence.size() < newChangeList.size() ? changeSequence : newChangeList;
//			
//			var missingChanges = largerList.removeAll(smallerList);
//			System.out.println(missingChanges);
//		}

//		Assertions.assertEquals(changeSequence.size(), newChangeList.size());
//		Assertions.assertTrue(newChangeList.containsAll(changeSequence));

		return newChangeList;
	}

	public void pcmToJavaChangePropagationTestTemplate(ExperimentDirLayout dirLayout) {
		this.setUp(dirLayout);

		var pcmChangeList = new ArrayList<EChange>();
		for (var c : experimentResourceWrapper.getPcmChangeResource().getContents()) {
			pcmChangeList.add((EChange) c);
		}
		var orderedPCMChangeList = orderPCMchanges(pcmChangeList);

		// TODO Ignore DataTypes generated for TypeParameters (such as "T")
		// Filter by name, if name length is 1, ignore

		PcmUserInteractionManager.addConflictResolutionStrategy(new NamespaceConflictResolutionStrategy(
				experimentResourceWrapper.getJavaPropagationPropagatedJavaModel()));

		// Propagate PCM changes
		var pcmToJavaProp = this.propagateChangesToResource(
				pcmFacade.getResources().stream()
						.filter((r) -> r.getURI().lastSegment()
								.equals(ExperimentDirLayoutConstants.getPcmrepositoryfilename()))
						.findFirst().get(),
				orderedPCMChangeList);
		LOGGER.info("Pcm to Java propagation over");

		LOGGER.info("Reloading Java");
		JavaModelAccess.reloadJavaModel();
		LOGGER.info("Reloading PCM");
		this.getPcmFacade().reload();
		LOGGER.info("Reloading IM");
		this.getImFacade().reload();
		LOGGER.info("Reloaded models");

		var newPcmRepoRes = pcmFacade.getResources().stream()
				.filter((r) -> r.getURI().lastSegment().equals(ExperimentDirLayoutConstants.getPcmrepositoryfilename()))
				.findFirst().get();

		LOGGER.info("Computing JC for Java");
		var jcOfJavaInPcmToJavaProp = computeJCForJava(JavaModelAccess.getJavaModel(),
				experimentResourceWrapper.getJavaPropagationInitialJavaModel());
		LOGGER.info("Computing JC for Pcm");
		var jcOfPcmInPcmToJavaProp = computeJCForPcm(newPcmRepoRes,
				experimentResourceWrapper.getJavaPropagationInitialPcmRepository());
		LOGGER.info("Computing F1-Score for Im");
		var fScoreOfImInPcmToJavaProp = computeFScoreForIm((Repository) newPcmRepoRes.getContents().get(0),
				this.getImFacade().getModel());

		LOGGER.info("Computing and saving experiment result");
		computeAndSaveExperimentResult(jcOfJavaInPcmToJavaProp, jcOfPcmInPcmToJavaProp, fScoreOfImInPcmToJavaProp,
				jcOfJavaInJavaToPcmProp, jcOfPcmInJavaToPcmProp, fScoreOfImInJavaToPcmProp);

		LOGGER.info("Tearing down");
		this.tearDown();
	}

	private void computeAndSaveExperimentResult(Object... objs) {
		var result = new ExperimentResult(objs);
		result.interpretResults();
		result.save(dirLayout.getExperimentResultSavePath());
	}

	protected JavaModelFacade setupJavaFacade() {
		var model = new JavaModelFacade();
		model.setComponentDetectionStrategies(List.of(new UnnamedModuleComponentDetectionStrategy()));
		model.initialize(dirLayout.getPropagatedDirLayout().getCodeDirPath());
		var modelRes = model.getResource();
		JavaModelAccess.setJavaModel(modelRes);
		return model;
	}

	protected ImFacade setupImFacade() {
		var imFacade = new ImFacade();
		imFacade.initialize(dirLayout.getPropagatedDirLayout().getImDirPath());
		return imFacade;
	}

	private JaccardCoefficientResult computeJCForJava(Resource newJavaModel, Resource oldJavaModel) {
		return ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(
				JavaModelComparator.compareJavaModels(newJavaModel, oldJavaModel, null, null, null));
	}

	private JaccardCoefficientResult computeJCForPcm(Resource newPcmRepo, Resource oldPcmRepo) {
		return ComparisonBasedJaccardCoefficientCalculator
				.calculateJaccardCoefficient(PCMModelComparator.compareRepositoryModels(newPcmRepo, oldPcmRepo));
	}

	private ImUpdateEvalData computeFScoreForIm(Repository repo, InstrumentationModel im) {
		var evalData = new ImUpdateEvalData();
		var eval = new IMUpdateEvaluator();
		eval.evaluateIMUpdate(repo, im, evalData, null);
		return evalData;
	}

	protected List<ChangePropagationSpecification> getCPRs() {
		List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
		changeSpecs.add(new PcmInitChangePropagationSpecification());
		changeSpecs.add(new ImInitChangePropagationSpecification());
		changeSpecs.add(new AllPcmChangePropagationSpecification());
		changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
		return changeSpecs;
	}
}
