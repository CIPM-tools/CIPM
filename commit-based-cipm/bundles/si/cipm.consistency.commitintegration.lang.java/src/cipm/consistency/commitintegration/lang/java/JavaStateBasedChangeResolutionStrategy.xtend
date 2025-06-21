package cipm.consistency.domains.java.propagation

import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.compare.merge.BatchMerger
import org.eclipse.emf.compare.merge.IMerger
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.framework.change.recording.ChangeRecorder
import org.eclipse.emf.ecore.resource.ResourceSet
import static com.google.common.base.Preconditions.checkArgument
import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies
import tools.vitruv.framework.domains.StateBasedChangeResolutionStrategy
import org.emftext.language.java.commons.Commentable
import java.util.List
import java.util.ArrayList
import org.eclipse.emf.ecore.EObject
import java.util.Collection
import org.apache.log4j.Logger
import cipm.consistency.commitintegration.diff.util.JavaModelComparator
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer
import cipm.consistency.commitintegration.diff.util.JavaChangedMethodDetectorDiffPostProcessor

/**
 * This strategy for diff based state changes of Java models uses EMFCompare to resolve a 
 * diff to a sequence of individual changes.
 * 
 * @author Timur Saglam
 * @author Ilia Chupakhin
 * @author Martin Armbruster
 */
class JavaStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
	static final Logger logger = Logger.getLogger("ci." + JavaStateBasedChangeResolutionStrategy.simpleName)
	
	private def checkNoProxies(Resource resource, String stateNotice) {
		val proxies = resource.referencedProxies
		checkArgument(proxies.empty, "%s '%s' should not contain proxies, but contains the following: %s", stateNotice,
			resource.URI, String.join(", ", proxies.map[toString]))
	}

	override getChangeSequenceBetween(Resource newState, Resource oldState) {
		checkArgument(oldState !== null && newState !== null, "old state or new state must not be null!")
		newState.checkNoProxies("new state")
		oldState.checkNoProxies("old state")
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = oldState.copyInto(monitoredResourceSet)
		return currentStateCopy.record(monitoredResourceSet, [
			if (oldState.URI != newState.URI) {
				currentStateCopy.URI = newState.URI
			}
			compareStatesAndReplayChanges(newState, currentStateCopy, null, null)
		])
	}

	override getChangeSequenceForCreated(Resource newState) {
		checkArgument(newState !== null, "new state must not be null!")
		newState.checkNoProxies("new state")
		// It is possible that root elements are automatically generated during resource creation (e.g., Java packages).
		// Thus, we create the resource and then monitor the re-insertion of the elements
		val monitoredResourceSet = new ResourceSetImpl()
		val newResource = monitoredResourceSet.createResource(newState.URI)
		return newResource.record(monitoredResourceSet, [
			compareStatesAndReplayChanges(newState, newResource, null, null)
		])
	}
	
	private def copyResourceContent(Resource toCopy, (Collection<EObject>)=>Collection<EObject> copyFunction) {
		val contents = copyFunction.apply(toCopy.contents)
		contents.forEach[
			val allCommentable = it.eAllContents.filter[it instanceof Commentable].map[it as Commentable].toSet
			allCommentable.forEach[it.layoutInformations.clear]
		]
		return contents
	}
	
	private def copyResource(Resource toCopy, ResourceSet targetSet,
			(Collection<EObject>)=>Collection<EObject> copyFunction) {
		val newResource = targetSet.createResource(toCopy.URI)
		val contents = toCopy.copyResourceContent(copyFunction)
		newResource.contents.addAll(contents)
		return newResource
	}
	
	/**
	 * Creates a change sequence for resources in a ResourceSet.
	 * The resources and their content are not copied.
	 */
	def getChangeSequenceForResourceSet(ResourceSet set, List<Resource> resources) {
		checkArgument(set !== null, "There must be a ResourceSet!")
		val targetSet = new ResourceSetImpl()
		val targetResources = new ArrayList
		resources.forEach[
			targetResources.add(targetSet.createResource(it.URI))
		]
		return targetSet.record(targetSet, [
			compareStatesAndReplayChanges(set, targetSet, resources, targetResources)
		])
	}
	
	/**
	 * Creates a change sequence between multiple resources.
	 * The source resources contain the new state while the target resources represent the current state and are copied.
	 */
	def getChangeSequenceBetweenResourceSet(ResourceSet sourceSet, ResourceSet targetSet,
			List<Resource> sourceResources, List<Resource> targetResources) {
		checkArgument(sourceSet !== null, "There has to be a source set.")
		checkArgument(targetSet !== null, "There has to be a target set.")
		sourceResources.forEach[
			it.checkNoProxies("A new resource contains proxy objects.")
		]
		targetResources.forEach[
			it.checkNoProxies("An old resource contains proxy objects.")
		]
		val monitoredResourceSet = new ResourceSetImpl()
		val copier = new EcoreUtil.Copier
		val copies = new ArrayList
		targetResources.forEach[
			copies.add(it.copyResource(monitoredResourceSet, [
				copier.copyAll(it)
			]))
		]
		copier.copyReferences
		return monitoredResourceSet.record(monitoredResourceSet, [
			compareStatesAndReplayChanges(sourceSet, monitoredResourceSet, sourceResources, copies)
		])
	}

	override getChangeSequenceForDeleted(Resource oldState) {
		checkArgument(oldState !== null, "old state must not be null!")
		oldState.checkNoProxies("old state")
		// Setup resolver and copy state:
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = monitoredResourceSet.createResource(oldState.URI)
		return currentStateCopy.record(monitoredResourceSet, [
			compareStatesAndReplayChanges(currentStateCopy, oldState, null, null)
		])
	}

	private def <T extends Notifier> record(T resource, ResourceSet resourceSet, () => void function) {
		try (val changeRecorder = new ChangeRecorder(resourceSet)) {
			changeRecorder.beginRecording
			changeRecorder.addToRecording(resource)
			function.apply()
			val result = changeRecorder.endRecording
			logger.debug("Recorded " + result.EChanges.size + " changes for " + resource)
			EvaluationDataContainer.globalContainer.changeStatistic.numberVitruvChanges = result.EChanges.size
			return result
		}
	}
	
	/**
	 * Compares states using EMFCompare and replays the changes to the current state.
	 */
	private def compareStatesAndReplayChanges(Notifier newState, Notifier currentState,
			List<Resource> newResources, List<Resource> currentResources) {
		val postProcessor = new JavaChangedMethodDetectorDiffPostProcessor()
		val changes = JavaModelComparator.compareJavaModels(newState, currentState,
				newResources, currentResources, postProcessor).differences
		// Replay the EMF compare differences.
		val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
		val merger = new BatchMerger(mergerRegistry)
		merger.copyAllLeftToRight(changes, new BasicMonitor)
		postProcessor.getChangedMethods.forEach[
			val oldName = it.name
			it.name = ""
			it.name = oldName
		]
	}

	/**
	 * Creates a new resource set, creates a resource and copies the content of the orignal resource.
	 */
	private def Resource copyInto(Resource resource, ResourceSet resourceSet) {
		val uri = resource.URI
		val copy = resourceSet.resourceFactoryRegistry.getFactory(uri).createResource(uri)
		val elementsCopy = EcoreUtil.copyAll(resource.contents)
		elementsCopy.forEach[eAdapters.clear]
		copy.contents.addAll(elementsCopy)
		resourceSet.resources += copy
		return copy
	}
}
