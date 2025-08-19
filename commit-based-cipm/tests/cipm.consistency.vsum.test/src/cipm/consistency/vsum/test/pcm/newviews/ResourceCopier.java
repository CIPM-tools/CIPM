package cipm.consistency.vsum.test.pcm.newviews;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.activextendannotations.Utility;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * A utility class to create copies of resources.
 */
@Utility
@SuppressWarnings("all")
public final class ResourceCopier {
	/**
	 * Indicates whether the given resource requires a full copy. If
	 * <code>false</code> is returned, the resource supports a partial copy, i.e.
	 * not all root elements need to be contained in the copy.
	 */
	public static boolean requiresFullCopy(final Resource resource) {
		return ResourceCopier.isWritableUmlResource(resource);
	}

	/**
	 * Copies some or all of the elements and references in the given view source
	 * resources to the given new resource set. The copied root elements can be
	 * restricted with the <code>rootElementPredicate</code>.
	 * 
	 * @param originalResources    The view source resources to copy
	 * @param newResourceSet       The resource set to which the copies are attached
	 * @param rootElementPredicate A predicate to include only a subset of the root
	 *                             elements of the <code>originalResources</code>
	 * @returns A mapping from each original resource to its copy
	 */
	public static Map<Resource, Resource> copyViewSourceResources(final Iterable<Resource> originalResources,
			final ResourceSet newResourceSet,
			final Function1<? super EObject, ? extends Boolean> rootElementPredicate) {
		return ResourceCopier.copyResources(originalResources, newResourceSet, Boolean.valueOf(false),
				rootElementPredicate);
	}

	/**
	 * Copies all elements and references in the given view resources to the given
	 * new resource set.
	 * 
	 * @param originalResources The view resources to copy
	 * @param newResourceSet    The resource set to which the copies are attached
	 * @returns A mapping from each original resource to its copy
	 */
	public static Map<Resource, Resource> copyViewResources(final Iterable<Resource> originalResources,
			final ResourceSet newResourceSet) {
		final Function1<EObject, Boolean> _function = (EObject it) -> {
			return Boolean.valueOf(true);
		};
		return ResourceCopier.copyResources(originalResources, newResourceSet, Boolean.valueOf(true), _function);
	}

	/**
	 * Copies all elements and references in the give view resource to the given new
	 * resource set.
	 * 
	 * @param originalResource The view resource to copy
	 * @param newResourceSet   The resource set to which the copy is attached
	 * @returns The newly created copy of the resource
	 */
	public static Resource copyViewResource(final Resource originalResource, final ResourceSet newResourceSet) {
		final Map<Resource, Resource> mapping = ResourceCopier.copyViewResources(
				Collections.<Resource>unmodifiableList(CollectionLiterals.<Resource>newArrayList(originalResource)),
				newResourceSet);
		return mapping.get(originalResource);
	}

	private static Map<Resource, Resource> copyResources(final Iterable<Resource> originalResources,
			final ResourceSet newResourceSet, final Boolean copyXmlIds,
			final Function1<? super EObject, ? extends Boolean> rootElementPredicate) {
		final HashMap<Resource, Resource> resourceMapping = new HashMap<Resource, Resource>();
		final Function1<Resource, Boolean> _function = (Resource it) -> {
			return Boolean.valueOf(ResourceCopier.isWritableUmlResource(it));
		};
		List<Resource> _list = IterableExtensions
				.<Resource>toList(IterableExtensions.<Resource>filter(originalResources, _function));
		for (final Resource umlResource : _list) {
			{
				final Resource copy = ResourceCopier.copyUmlResource(umlResource, newResourceSet);
				if ((copyXmlIds).booleanValue()) {
					if (((umlResource instanceof XMLResource) && (copy instanceof XMLResource))) {
						ResourceCopier.copyUmlIds(((XMLResource) umlResource), ((XMLResource) copy));
					}
				}
				resourceMapping.put(umlResource, copy);
			}
		}
		final Function1<Resource, Boolean> _function_1 = (Resource it) -> {
			boolean _isWritableUmlResource = ResourceCopier.isWritableUmlResource(it);
			return Boolean.valueOf((!_isWritableUmlResource));
		};
		final Map<Resource, Resource> otherMapping = ResourceCopier.copyResourcesInternal(
				IterableExtensions
						.<Resource>toList(IterableExtensions.<Resource>filter(originalResources, _function_1)),
				newResourceSet, copyXmlIds, rootElementPredicate);
		final Consumer<Resource> _function_2 = (Resource it) -> {
			resourceMapping.put(it, otherMapping.get(it));
		};
		otherMapping.keySet().forEach(_function_2);
		return resourceMapping;
	}

	/**
	 * NOTE: This is a hack, because {@link EcoreUtil#copyAll} does not work for UML
	 * models. This is due to the fact that the UML metamodel uses some weird
	 * manually and partly derived collections, which are not properly handled by
	 * the ordinary {@link EcoreUtil.Copier}. For example, the
	 * {@linkplain InterfaceRealization.clients} are a set of manually added
	 * elements combined with the
	 * {@linkplain InterfaceRealization.implementingClassifier}, which, in turn, is
	 * just the {@code eContainer} of the interface realization. The copier copies
	 * this implementing classifier reference although it is automatically inferred
	 * by the metamodel implementation, such that after the copy process the
	 * {@linkplain InterfaceRelization.clients} reference contains the according
	 * element twice. This happens at different places in the UML models. To
	 * circumvent the issue, we store the UML model to a temporary resource and
	 * reload it, because save/load properly handles the situation not covered by
	 * the copier.
	 */
	private static Resource copyUmlResource(final Resource originalResource, final ResourceSet newResourceSet) {
		try {
			final URI originalURI = originalResource.getURI();
			String _fileExtension = originalURI.fileExtension();
			String _plus = ("." + _fileExtension);
			final Path tempFilePath = Files.createTempFile(null, _plus);
			final URI tempURI = URI.createFileURI(tempFilePath.toString());
			originalResource.setURI(tempURI);
			originalResource.save(null);
			originalResource.setURI(originalURI);
			final Resource copiedResource = newResourceSet.getResource(tempURI, true);
			copiedResource.setURI(originalURI);
			Files.delete(tempFilePath);
			EcoreUtil.resolveAll(copiedResource);
			return copiedResource;
		} catch (Throwable _e) {
			throw Exceptions.sneakyThrow(_e);
		}
	}

	/**
	 * Copies the selected elements in the given resources to the given new resource
	 * set. It is necessary to process all resources together, because there may be
	 * elements that are root elements of one resource but have a container in
	 * another resource (e.g. a Java CompilationUnit is root of a resource but can
	 * be contained in a package persisted in another resource). We must NOT copy
	 * these elements but only copy the containing element and then resolve it to
	 * avoid element duplication.
	 */
	private static Map<Resource, Resource> copyResourcesInternal(final Iterable<Resource> originalResources,
			final ResourceSet newResourceSet, final Boolean copyXmlIds,
			final Function1<? super EObject, ? extends Boolean> rootElementPredicate) {
		final EcoreUtil.Copier copier = new EcoreUtil.Copier(true);
		HashMap<Resource, Resource> resourceMapping = new HashMap<Resource, Resource>();
		for (final Resource originalResource : originalResources) {
			{
				final Function1<EObject, Boolean> _function = (EObject it) -> {
					boolean _isContainedInOtherThanOwnResource = ResourceCopier.isContainedInOtherThanOwnResource(it,
							originalResources);
					return Boolean.valueOf((!_isContainedInOtherThanOwnResource));
				};
				final List<EObject> elementsContainedInResource = IterableExtensions
						.<EObject>toList(IterableExtensions.<EObject>filter(originalResource.getContents(), _function));
				copier.<EObject>copyAll(elementsContainedInResource);
			}
		}
		copier.copyReferences();
		for (final Resource originalResource_1 : originalResources) {
			{
				Resource.Factory.Registry _resourceFactoryRegistry = newResourceSet.getResourceFactoryRegistry();
				Resource.Factory _factory = null;
				if (_resourceFactoryRegistry != null) {
					_factory = _resourceFactoryRegistry.getFactory(originalResource_1.getURI());
				}
				Resource _createResource = null;
				if (_factory != null) {
					_createResource = _factory.createResource(originalResource_1.getURI());
				}
				final Resource copiedResource = Preconditions.<Resource>checkNotNull(_createResource,
						"Cannot create resource copy: %s", originalResource_1.getURI());
				final Function1<EObject, Boolean> _function = (EObject it) -> {
					return rootElementPredicate.apply(it);
				};
				final Iterable<EObject> selectedRootElements = IterableExtensions
						.<EObject>filter(originalResource_1.getContents(), _function);
				final Function1<EObject, EObject> _function_1 = (EObject it) -> {
					return Preconditions.<EObject>checkNotNull(copier.get(it), "corresponding object for %s is null",
							it);
				};
				final Iterable<EObject> mappedRootElements = IterableExtensions
						.<EObject, EObject>map(selectedRootElements, _function_1);
				Iterables.<EObject>addAll(copiedResource.getContents(), mappedRootElements);
				EList<Resource> _resources = newResourceSet.getResources();
				_resources.add(copiedResource);
				resourceMapping.put(originalResource_1, copiedResource);
			}
		}
		if ((copyXmlIds).booleanValue()) {
			Set<Map.Entry<EObject, EObject>> _entrySet = copier.entrySet();
			for (final Map.Entry<EObject, EObject> entry : _entrySet) {
				{
					final EObject sourceElement = entry.getKey();
					final EObject targetElement = entry.getValue();
					if (((sourceElement.eResource() instanceof XMLResource)
							&& (targetElement.eResource() instanceof XMLResource))) {
						Resource _eResource = sourceElement.eResource();
						final String id = ((XMLResource) _eResource).getID(sourceElement);
						Resource _eResource_1 = targetElement.eResource();
						((XMLResource) _eResource_1).setID(targetElement, id);
					}
				}
			}
		}
		return resourceMapping;
	}

	private static boolean isContainedInOtherThanOwnResource(final EObject eObject,
			final Iterable<Resource> resources) {
		final Resource rootContainerResource = EcoreUtil.getRootContainer(eObject).eResource();
		return ((rootContainerResource != eObject.eResource())
				&& IterableExtensions.contains(resources, rootContainerResource));
	}

	private static boolean isWritableUmlResource(final Resource resource) {
		return (Objects.equal(resource.getURI().fileExtension(), "uml") && (!URIUtil.isPathmap(resource.getURI())));
	}

	/**
	 * Copies the IDs of the source's elements to the target. It is expected that
	 * both resources are in an identical state.
	 */
	private static void copyUmlIds(final XMLResource source, final XMLResource target) {
		final TreeIterator<EObject> sourceIterator = source.getAllContents();
		final TreeIterator<EObject> targetIterator = target.getAllContents();
		while ((sourceIterator.hasNext() && targetIterator.hasNext())) {
			{
				final EObject sourceObject = sourceIterator.next();
				final EObject targetObject = targetIterator.next();
				EClass _eClass = sourceObject.eClass();
				EClass _eClass_1 = targetObject.eClass();
				boolean _tripleEquals = (_eClass == _eClass_1);
				Preconditions.checkState(_tripleEquals, "non matching elements %s and %s", sourceObject, targetObject);
				target.setID(targetObject, source.getID(sourceObject));
			}
		}
		boolean _hasNext = sourceIterator.hasNext();
		boolean _not = (!_hasNext);
		Preconditions.checkState(_not, "source uml resource has too many elements");
		boolean _hasNext_1 = targetIterator.hasNext();
		boolean _not_1 = (!_hasNext_1);
		Preconditions.checkState(_not_1, "target uml resource has too many elements");
	}

	private ResourceCopier() {

	}
}