package tools.vitruv.applications.pcmjava.integrationFromGit.propagation

import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.merge.BatchMerger
import org.eclipse.emf.compare.merge.IMerger
import org.eclipse.emf.compare.scope.DefaultComparisonScope
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.framework.change.recording.ChangeRecorder
import org.eclipse.emf.ecore.resource.ResourceSet
import static com.google.common.base.Preconditions.checkArgument
import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies
import tools.vitruv.framework.domains.StateBasedChangeResolutionStrategy
import org.eclipse.emf.compare.diff.DiffBuilder
import org.eclipse.emf.compare.diff.DefaultDiffEngine
import org.eclipse.emf.compare.diff.FeatureFilter
import org.eclipse.emf.compare.Match
import org.eclipse.emf.ecore.EReference
import org.emftext.language.java.commons.CommonsPackage
import org.emftext.commons.layout.LayoutInformation
import org.emftext.language.java.commons.Commentable
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl
import java.util.regex.Pattern

/**
 * This default strategy for diff based state changes uses EMFCompare to resolve a 
 * diff to a sequence of individual changes.
 * 
 * @author Timur Saglam
 * @author Ilia Chupakhin
 * @author Martin Armbruster
 */
class JavaStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
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
		return currentStateCopy.record [
			if (oldState.URI != newState.URI) {
				currentStateCopy.URI = newState.URI
			}
			compareStatesAndReplayChanges(newState, currentStateCopy)
		]
	}

	override getChangeSequenceForCreated(Resource newState) {
		checkArgument(newState !== null, "new state must not be null!")
		newState.checkNoProxies("new state")
		// It is possible that root elements are automatically generated during resource creation (e.g., Java packages).
		// Thus, we create the resource and then monitor the re-insertion of the elements
		val monitoredResourceSet = new ResourceSetImpl()
		val newResource = monitoredResourceSet.createResource(newState.URI)
		newResource.contents.clear()
		val contents = EcoreUtil.copyAll(newState.contents)
		contents.forEach[
			val allCommentable = it.eAllContents.filter[it instanceof Commentable].map[it as Commentable].toSet
			allCommentable.forEach[it.layoutInformations.clear]
		]
		return newResource.record [
			newResource.contents += contents
		]
	}

	override getChangeSequenceForDeleted(Resource oldState) {
		checkArgument(oldState !== null, "old state must not be null!")
		oldState.checkNoProxies("old state")
		// Setup resolver and copy state:
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = oldState.copyInto(monitoredResourceSet)
		return currentStateCopy.record [
			currentStateCopy.contents.clear()
		]
	}

	private def <T extends Notifier> record(Resource resource, ()=>void function) {
		try (val changeRecorder = new ChangeRecorder(resource.resourceSet)) {
			changeRecorder.beginRecording
			changeRecorder.addToRecording(resource)
			function.apply()
			return changeRecorder.endRecording
		}
	}

	/**
	 * Compares states using EMFCompare and replays the changes to the current state.
	 */
	private def compareStatesAndReplayChanges(Notifier newState, Notifier currentState) {
		val scope = new DefaultComparisonScope(newState, currentState, null)
		val diffProcessor = new DiffBuilder()
		val diffEngine = new DefaultDiffEngine(diffProcessor) {
			override protected FeatureFilter createFeatureFilter() {
				return new FeatureFilter() {
					override protected boolean isIgnoredReference(Match match, EReference reference) {
						return match.left instanceof LayoutInformation
							|| match.right instanceof LayoutInformation
							|| reference == CommonsPackage.Literals.COMMENTABLE__LAYOUT_INFORMATIONS
							|| super.isIgnoredReference(match, reference)
					}
				}
			}
		}
		val postProcessor =  new JavaPostProcessor()
		val processorDescriptor = new BasicPostProcessorDescriptorImpl(postProcessor, Pattern.compile(".*"), null)
		val processorRegistry = new PostProcessorDescriptorRegistryImpl()
		processorRegistry.put("java", processorDescriptor)
		val comparison = EMFCompare.builder.setDiffEngine(diffEngine)
			.setPostProcessorRegistry(processorRegistry).build.compare(scope)
		val changes = comparison.differences
		// Replay the EMF compare differences
		val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
		val merger = new BatchMerger(mergerRegistry)
		merger.copyAllLeftToRight(changes, new BasicMonitor)
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
