package cipm.consistency.commitintegration.lang.java;

import cipm.consistency.commitintegration.diff.util.JavaChangedMethodDetectorDiffPostProcessor;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.tools.evaluation.data.ChangeStatistic;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.members.Method;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

/**
 * This strategy for diff based state changes of Java models uses EMFCompare to resolve a
 * diff to a sequence of individual changes.
 * 
 * @author Timur Saglam
 * @author Ilia Chupakhin
 * @author Martin Armbruster
 */
@SuppressWarnings("all")
public class JavaStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
  private static final Logger logger = Logger.getLogger(("ci." + JavaStateBasedChangeResolutionStrategy.class.getSimpleName()));

  private void checkNoProxies(final Resource resource, final String stateNotice) {
    final Iterable<EObject> proxies = ResourceUtil.getReferencedProxies(resource);
    final Function1<EObject, String> _function = (EObject it) -> {
      return it.toString();
    };
    Preconditions.checkArgument(IterableExtensions.isEmpty(proxies), "%s \'%s\' should not contain proxies, but contains the following: %s", stateNotice, 
      resource.getURI(), String.join(", ", IterableExtensions.<EObject, String>map(proxies, _function)));
  }

  @Override
  public VitruviusChange getChangeSequenceBetween(final Resource newState, final Resource oldState) {
    Preconditions.checkArgument(((oldState != null) && (newState != null)), "old state or new state must not be null!");
    this.checkNoProxies(newState, "new state");
    this.checkNoProxies(oldState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = this.copyInto(oldState, monitoredResourceSet);
    final Procedure0 _function = () -> {
      URI _uRI = oldState.getURI();
      URI _uRI_1 = newState.getURI();
      boolean _notEquals = (!Objects.equal(_uRI, _uRI_1));
      if (_notEquals) {
        currentStateCopy.setURI(newState.getURI());
      }
      this.compareStatesAndReplayChanges(newState, currentStateCopy, null, null);
    };
    return this.<Resource>record(currentStateCopy, monitoredResourceSet, _function);
  }

  @Override
  public VitruviusChange getChangeSequenceForCreated(final Resource newState) {
    Preconditions.checkArgument((newState != null), "new state must not be null!");
    this.checkNoProxies(newState, "new state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource newResource = monitoredResourceSet.createResource(newState.getURI());
    final Procedure0 _function = () -> {
      this.compareStatesAndReplayChanges(newState, newResource, null, null);
    };
    return this.<Resource>record(newResource, monitoredResourceSet, _function);
  }

  private Collection<EObject> copyResourceContent(final Resource toCopy, final Function1<? super Collection<EObject>, ? extends Collection<EObject>> copyFunction) {
    final Collection<EObject> contents = copyFunction.apply(toCopy.getContents());
    final Consumer<EObject> _function = (EObject it) -> {
      final Function1<EObject, Boolean> _function_1 = (EObject it_1) -> {
        return Boolean.valueOf((it_1 instanceof Commentable));
      };
      final Function1<EObject, Commentable> _function_2 = (EObject it_1) -> {
        return ((Commentable) it_1);
      };
      final Set<Commentable> allCommentable = IteratorExtensions.<Commentable>toSet(IteratorExtensions.<EObject, Commentable>map(IteratorExtensions.<EObject>filter(it.eAllContents(), _function_1), _function_2));
      final Consumer<Commentable> _function_3 = (Commentable it_1) -> {
        it_1.getLayoutInformations().clear();
      };
      allCommentable.forEach(_function_3);
    };
    contents.forEach(_function);
    return contents;
  }

  private Resource copyResource(final Resource toCopy, final ResourceSet targetSet, final Function1<? super Collection<EObject>, ? extends Collection<EObject>> copyFunction) {
    final Resource newResource = targetSet.createResource(toCopy.getURI());
    final Collection<EObject> contents = this.copyResourceContent(toCopy, copyFunction);
    newResource.getContents().addAll(contents);
    return newResource;
  }

  /**
   * Creates a change sequence for resources in a ResourceSet.
   * The resources and their content are not copied.
   */
  public TransactionalChange getChangeSequenceForResourceSet(final ResourceSet set, final List<Resource> resources) {
    Preconditions.checkArgument((set != null), "There must be a ResourceSet!");
    final ResourceSetImpl targetSet = new ResourceSetImpl();
    final ArrayList<Resource> targetResources = new ArrayList<Resource>();
    final Consumer<Resource> _function = (Resource it) -> {
      targetResources.add(targetSet.createResource(it.getURI()));
    };
    resources.forEach(_function);
    final Procedure0 _function_1 = () -> {
      this.compareStatesAndReplayChanges(set, targetSet, resources, targetResources);
    };
    return this.<ResourceSetImpl>record(targetSet, targetSet, _function_1);
  }

  /**
   * Creates a change sequence between multiple resources.
   * The source resources contain the new state while the target resources represent the current state and are copied.
   */
  public TransactionalChange getChangeSequenceBetweenResourceSet(final ResourceSet sourceSet, final ResourceSet targetSet, final List<Resource> sourceResources, final List<Resource> targetResources) {
    Preconditions.checkArgument((sourceSet != null), "There has to be a source set.");
    Preconditions.checkArgument((targetSet != null), "There has to be a target set.");
    final Consumer<Resource> _function = (Resource it) -> {
      this.checkNoProxies(it, "A new resource contains proxy objects.");
    };
    sourceResources.forEach(_function);
    final Consumer<Resource> _function_1 = (Resource it) -> {
      this.checkNoProxies(it, "An old resource contains proxy objects.");
    };
    targetResources.forEach(_function_1);
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final EcoreUtil.Copier copier = new EcoreUtil.Copier();
    final ArrayList<Resource> copies = new ArrayList<Resource>();
    final Consumer<Resource> _function_2 = (Resource it) -> {
      final Function1<Collection<EObject>, Collection<EObject>> _function_3 = (Collection<EObject> it_1) -> {
        return copier.<EObject>copyAll(it_1);
      };
      copies.add(this.copyResource(it, monitoredResourceSet, _function_3));
    };
    targetResources.forEach(_function_2);
    copier.copyReferences();
    final Procedure0 _function_3 = () -> {
      this.compareStatesAndReplayChanges(sourceSet, monitoredResourceSet, sourceResources, copies);
    };
    return this.<ResourceSetImpl>record(monitoredResourceSet, monitoredResourceSet, _function_3);
  }

  @Override
  public VitruviusChange getChangeSequenceForDeleted(final Resource oldState) {
    Preconditions.checkArgument((oldState != null), "old state must not be null!");
    this.checkNoProxies(oldState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = monitoredResourceSet.createResource(oldState.getURI());
    final Procedure0 _function = () -> {
      this.compareStatesAndReplayChanges(currentStateCopy, oldState, null, null);
    };
    return this.<Resource>record(currentStateCopy, monitoredResourceSet, _function);
  }

  private <T extends Notifier> TransactionalChange record(final T resource, final ResourceSet resourceSet, final Procedure0 function) {
    try (final ChangeRecorder changeRecorder = new ChangeRecorder(resourceSet)) {
      changeRecorder.beginRecording();
      changeRecorder.addToRecording(resource);
      function.apply();
      final TransactionalChange result = changeRecorder.endRecording();
      int _size = result.getEChanges().size();
      String _plus = ("Recorded " + Integer.valueOf(_size));
      String _plus_1 = (_plus + " changes for ");
      String _plus_2 = (_plus_1 + resource);
      JavaStateBasedChangeResolutionStrategy.logger.debug(_plus_2);
      ChangeStatistic _changeStatistic = EvaluationDataContainer.get().getChangeStatistic();
      _changeStatistic.setNumberVitruvChanges(result.getEChanges().size());
      return result;
    }
  }

  /**
   * Compares states using EMFCompare and replays the changes to the current state.
   */
  private void compareStatesAndReplayChanges(final Notifier newState, final Notifier currentState, final List<Resource> newResources, final List<Resource> currentResources) {
    final JavaChangedMethodDetectorDiffPostProcessor postProcessor = new JavaChangedMethodDetectorDiffPostProcessor();
    final EList<Diff> changes = JavaModelComparator.compareJavaModels(newState, currentState, newResources, currentResources, postProcessor).getDifferences();
    final IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
    final BatchMerger merger = new BatchMerger(mergerRegistry);
    BasicMonitor _basicMonitor = new BasicMonitor();
    merger.copyAllLeftToRight(changes, _basicMonitor);
    final Consumer<Method> _function = (Method it) -> {
      final String oldName = it.getName();
      it.setName("");
      it.setName(oldName);
    };
    postProcessor.getChangedMethods().forEach(_function);
  }

  /**
   * Creates a new resource set, creates a resource and copies the content of the orignal resource.
   */
  private Resource copyInto(final Resource resource, final ResourceSet resourceSet) {
    final URI uri = resource.getURI();
    final Resource copy = resourceSet.getResourceFactoryRegistry().getFactory(uri).createResource(uri);
    final Collection<EObject> elementsCopy = EcoreUtil.<EObject>copyAll(resource.getContents());
    final Consumer<EObject> _function = (EObject it) -> {
      it.eAdapters().clear();
    };
    elementsCopy.forEach(_function);
    copy.getContents().addAll(elementsCopy);
    EList<Resource> _resources = resourceSet.getResources();
    _resources.add(copy);
    return copy;
  }
}
