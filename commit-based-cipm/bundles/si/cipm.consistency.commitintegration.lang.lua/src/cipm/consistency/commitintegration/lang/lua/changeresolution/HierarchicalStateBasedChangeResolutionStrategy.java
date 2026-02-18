package cipm.consistency.commitintegration.lang.lua.changeresolution;

import cipm.consistency.commitintegration.lang.lua.changeresolution.lua.LuaHierarchicalMatchEngineFactory;
import cipm.consistency.commitintegration.lang.lua.changeresolution.temp.ResourceCopier;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

/**
 * This change resolution strategy tries to uses multiple match engines to resolve
 * changes in PCM, IMM and Lua-code models.
 * 
 * @author Timur Saglam
 * @author Lukas Burgey
 */
public class HierarchicalStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
  private Logger LOGGER = Logger.getLogger(HierarchicalStateBasedChangeResolutionStrategy.class);

  private final EMFCompare.Builder emfCompareBuilder;

  /**
   * @param stringentMatching should not be used for change resolution, but only during the evaluation
   */
  public HierarchicalStateBasedChangeResolutionStrategy(final boolean stringentMatching) {
    final MatchEngineFactoryImpl defaultMatchEngineFactory = new MatchEngineFactoryImpl(UseIdentifiers.WHEN_AVAILABLE);
    defaultMatchEngineFactory.setRanking(10);
    final LuaHierarchicalMatchEngineFactory hierarchicalMatchEngineFactory = new LuaHierarchicalMatchEngineFactory(stringentMatching);
    hierarchicalMatchEngineFactory.setRanking(20);
    EMFCompare.Builder _builder = EMFCompare.builder();
    final Procedure1<EMFCompare.Builder> _function = new Procedure1<EMFCompare.Builder>() {
      public void apply(final EMFCompare.Builder it) {
        MatchEngineFactoryRegistryImpl _matchEngineFactoryRegistryImpl = new MatchEngineFactoryRegistryImpl();
        final Procedure1<MatchEngineFactoryRegistryImpl> _function = new Procedure1<MatchEngineFactoryRegistryImpl>() {
          public void apply(final MatchEngineFactoryRegistryImpl it_1) {
            it_1.add(defaultMatchEngineFactory);
            it_1.add(hierarchicalMatchEngineFactory);
          }
        };
        MatchEngineFactoryRegistryImpl _doubleArrow = ObjectExtensions.<MatchEngineFactoryRegistryImpl>operator_doubleArrow(_matchEngineFactoryRegistryImpl, _function);
        it.setMatchEngineFactoryRegistry(_doubleArrow);
      }
    };
    EMFCompare.Builder _doubleArrow = ObjectExtensions.<EMFCompare.Builder>operator_doubleArrow(_builder, _function);
    this.emfCompareBuilder = _doubleArrow;
  }

  private void checkNoProxies(final Resource resource, final String stateNotice) {
    final Iterable<EObject> proxies = ResourceUtil.getReferencedProxies(resource);
    final Function1<EObject, String> _function = new Function1<EObject, String>() {
      public String apply(final EObject it) {
        return it.toString();
      }
    };
    Preconditions.checkArgument(IterableExtensions.isEmpty(proxies), 
      "%s \'%s\' should not contain proxies, butools.vitruv.change.compositet contains the following: %s", stateNotice, resource.getURI(), String.join(", ", IterableExtensions.<EObject, String>map(proxies, _function)));
  }

  private <T extends Notifier> TransactionalChange record(final Resource resource, final Procedure0 function) {
    List<Throwable> _ts = new ArrayList<Throwable>();
    ChangeRecorder changeRecorder = null;
    try {
      changeRecorder = new ChangeRecorder(resource.getResourceSet());
      changeRecorder.beginRecording();
      changeRecorder.addToRecording(resource);
      function.apply();
      final TransactionalChange change = changeRecorder.endRecording();
      this.logChange(change);
      return change;
    } finally {
      if (changeRecorder != null) {
        try {
          changeRecorder.close();
        } catch (Throwable _t) {
          _ts.add(_t);
        }
      }
      if(!_ts.isEmpty()) throw Exceptions.sneakyThrow(_ts.get(0));
    }
  }

  private void logChange(final TransactionalChange change) {
    if (change.getEChanges().size() > 0) {
      this.LOGGER.debug("Change recorder recorded changes: " + Integer.valueOf(change.getEChanges().size()));
    }
  }

  private EMFCompare getEmfCompare() {
    return this.emfCompareBuilder.build();
  }

  public static boolean isCodeModel(final Resource res) {
    return res.getURI().lastSegment().endsWith(".code.xmi");
  }

  /**
   * print some infos about the differences we found in models
   */
  private void printModelChanges(final Resource res, final EList<Diff> differences) {
    String changeText = ("EMFcompare found changes in model " + res.getURI().lastSegment() + ":");
    for (final DifferenceKind kind : DifferenceKind.VALUES) {
      String _changeText = changeText;
      final Predicate<Diff> _function = new Predicate<Diff>() {
        public boolean test(final Diff it) {
          DifferenceKind _kind = it.getKind();
          return Objects.equal(_kind, kind);
        }
      };
      long _count = differences.stream().filter(_function).count();
      String _plus_1 = ((("  " + kind) + ": ") + Long.valueOf(_count));
      changeText = (_changeText + _plus_1);
    }
    this.LOGGER.info(changeText);
  }

  public Comparison compareStates(final Resource newState, final Resource currentState) {
    Comparison _xblockexpression = null;
    {
      if ((!Objects.equal(currentState.getURI(), newState.getURI()))) {
        currentState.setURI(newState.getURI());
      }
      final DefaultComparisonScope scope = new DefaultComparisonScope(newState, currentState, null);
      final EMFCompare compare = this.getEmfCompare();
      _xblockexpression = compare.compare(scope);
    }
    return _xblockexpression;
  }

  /**
   * Compares states using EMFCompare and replays the changes to the current state.
   */
  private void compareStatesAndReplayChanges(final Resource newState, final Resource currentState) {
    Comparison comparison = this.compareStates(newState, currentState);
    final EList<Diff> differences = comparison.getDifferences();
    boolean _isCodeModel = HierarchicalStateBasedChangeResolutionStrategy.isCodeModel(newState);
    if (_isCodeModel) {
      this.printModelChanges(newState, differences);
    }
    final IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
    final BatchMerger merger = new BatchMerger(mergerRegistry);
    final BasicMonitor monitor = new BasicMonitor();
    merger.copyAllLeftToRight(differences, monitor);
  }

  public VitruviusChange getChangeSequenceBetween(final Resource newState, final Resource currentState) {
    Preconditions.checkArgument(((currentState != null) && (newState != null)), "current and new state must not be null!");
    this.checkNoProxies(newState, "new state");
    this.checkNoProxies(currentState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = ResourceCopier.copyViewResource(currentState, monitoredResourceSet);
    final Procedure0 _function = new Procedure0() {
      public void apply() {
        HierarchicalStateBasedChangeResolutionStrategy.this.compareStatesAndReplayChanges(newState, currentStateCopy);
      }
    };
    return this.<Notifier>record(currentStateCopy, _function);
  }

  public VitruviusChange getChangeSequenceForCreated(final Resource newState) {
    Preconditions.checkArgument((newState != null), "new state must not be null!");
    this.checkNoProxies(newState, "new state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource newResource = monitoredResourceSet.createResource(newState.getURI());
    newResource.getContents().clear();
    final Procedure0 _function = new Procedure0() {
      public void apply() {
        EList<EObject> _contents = newResource.getContents();
        Collection<EObject> _copyAll = EcoreUtil.<EObject>copyAll(newState.getContents());
        Iterables.<EObject>addAll(_contents, _copyAll);
      }
    };
    return this.<Notifier>record(newResource, _function);
  }

  public VitruviusChange getChangeSequenceForDeleted(final Resource oldState) {
    Preconditions.checkArgument((oldState != null), "old state must not be null!");
    this.checkNoProxies(oldState, "old state");
    final ResourceSetImpl monitoredResourceSet = new ResourceSetImpl();
    final Resource currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet);
    final Procedure0 _function = new Procedure0() {
      public void apply() {
        currentStateCopy.getContents().clear();
      }
    };
    return this.<Notifier>record(currentStateCopy, _function);
  }
}
