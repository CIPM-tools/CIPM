package edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.extractors;

import com.github.gumtreediff.actions.RootsClassifier;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import com.google.common.base.Objects;
import edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.converters.GumTree2JdtAstConverterImpl;
import edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.extractors.IAtomicChangeExtractor;
import edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.extractors.IContentValidator;
import edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.extractors.OrderByDstOrderComparator;
import edu.kit.ipd.sdq.vitruvius.codeintegration.scmchanges.extractors.OrderbyBreadthFirstOrderingOfCompleteTree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class GumTreeChangeExtractor implements IAtomicChangeExtractor {
  private Set<ITree> alreadyAdded;
  
  private MappingStore workingTreeMappings;
  
  private final static Logger logger = Logger.getLogger(GumTreeChangeExtractor.class);
  
  private String oldContent;
  
  private String newContent;
  
  private IContentValidator validator;
  
  private URI fileUri;
  
  private int validExtractions;
  
  private int totalExtractions;
  
  private boolean removeNeighbouringDuplicates = true;
  
  public GumTreeChangeExtractor(final String oldContent, final String newContent, final URI fileUri) {
    this.oldContent = oldContent;
    this.newContent = newContent;
    this.fileUri = fileUri;
    this.validator = null;
    this.validExtractions = 0;
    this.totalExtractions = 0;
    GumTreeChangeExtractor.logger.setLevel(Level.ALL);
  }
  
  public GumTreeChangeExtractor(final String oldContent, final String newContent, final URI fileUri, final boolean removeNeighbouringDuplicates) {
    this(oldContent, newContent, fileUri);
    this.removeNeighbouringDuplicates = removeNeighbouringDuplicates;
  }
  
  @Override
  public List<String> extract() {
    try {
      MappingStore _mappingStore = new MappingStore();
      this.workingTreeMappings = _mappingStore;
      HashSet<ITree> _hashSet = new HashSet<ITree>();
      this.alreadyAdded = _hashSet;
      final JdtTreeGenerator generator = new JdtTreeGenerator();
      final TreeContext srcTreeContext = generator.generateFromString(this.oldContent);
      final TreeContext dstTreeContext = generator.generateFromString(this.newContent);
      GumTreeChangeExtractor.logger.info(" --- SOURCE TREE --- ");
      ITree _root = srcTreeContext.getRoot();
      String _prettyString = _root.toPrettyString(srcTreeContext);
      GumTreeChangeExtractor.logger.info(_prettyString);
      GumTreeChangeExtractor.logger.info(" --- DESTINATION TREE --- ");
      ITree _root_1 = dstTreeContext.getRoot();
      String _prettyString_1 = _root_1.toPrettyString(dstTreeContext);
      GumTreeChangeExtractor.logger.info(_prettyString_1);
      final ArrayList<String> contentList = new ArrayList<String>();
      final GumTree2JdtAstConverterImpl converter = new GumTree2JdtAstConverterImpl();
      ITree _root_2 = srcTreeContext.getRoot();
      CompilationUnit _convertTree = converter.convertTree(_root_2);
      String _string = _convertTree.toString();
      contentList.add(_string);
      Matchers _instance = Matchers.getInstance();
      ITree _root_3 = srcTreeContext.getRoot();
      ITree _root_4 = dstTreeContext.getRoot();
      final Matcher m = _instance.getMatcher(_root_3, _root_4);
      m.match();
      final MappingStore mappings = m.getMappings();
      final RootsClassifier classifier = new RootsClassifier(srcTreeContext, dstTreeContext, m);
      final ArrayList<ITree> lazyDelete = new ArrayList<ITree>();
      ITree _root_5 = srcTreeContext.getRoot();
      this.processDels(classifier, _root_5, mappings, contentList, converter, lazyDelete);
      ITree _root_6 = srcTreeContext.getRoot();
      ITree _root_7 = dstTreeContext.getRoot();
      this.processAdds(classifier, _root_6, mappings, contentList, converter, _root_7, lazyDelete);
      boolean _isEmpty = lazyDelete.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        GumTreeChangeExtractor.logger.warn("Not all lazy deletes successful. FIXME");
      }
      ITree _root_8 = srcTreeContext.getRoot();
      this.processUpds(classifier, _root_8, mappings, contentList, converter);
      ITree _root_9 = srcTreeContext.getRoot();
      this.processMvs(classifier, _root_9, mappings, contentList, converter);
      ITree _root_10 = dstTreeContext.getRoot();
      this.appendExtractedContent(contentList, converter, _root_10);
      int _size = contentList.size();
      int _plus = (this.totalExtractions + _size);
      this.totalExtractions = _plus;
      GumTreeChangeExtractor.logger.info("Validating content list...");
      boolean _notEquals = (!Objects.equal(this.validator, null));
      if (_notEquals) {
        final HashSet<String> toRemove = new HashSet<String>();
        for (final String contentString : contentList) {
          boolean _isValid = this.validator.isValid(contentString, this.fileUri);
          boolean _not_1 = (!_isValid);
          if (_not_1) {
            toRemove.add(contentString);
          }
        }
        contentList.removeAll(toRemove);
      }
      int _size_1 = contentList.size();
      int _plus_1 = (this.validExtractions + _size_1);
      this.validExtractions = _plus_1;
      return contentList;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void processUpds(final RootsClassifier classifier, final ITree workingTree, final MappingStore mappings, final ArrayList<String> contentList, final GumTree2JdtAstConverterImpl converter) {
    Set<ITree> _srcUpdTrees = classifier.getSrcUpdTrees();
    final ArrayList<ITree> rootUpds = this.getRootChanges(_srcUpdTrees);
    for (final ITree updTree : rootUpds) {
      {
        final ITree nodeInDstTree = mappings.getDst(updTree);
        GumTreeChangeExtractor.logger.info("Found UPD");
        final boolean removed = this.removeNodeFromWorkingTree(updTree, mappings);
        final boolean added = this.addNodeToWorkingTree(nodeInDstTree, mappings);
        boolean _or = false;
        if (added) {
          _or = true;
        } else {
          _or = removed;
        }
        if (_or) {
          this.appendExtractedContent(contentList, converter, workingTree);
        } else {
          GumTreeChangeExtractor.logger.info("Couldn\'t add or remove for UPD. Happens if changes were already covered by ADDs and DELs");
        }
      }
    }
  }
  
  private Boolean appendExtractedContent(final ArrayList<String> contentList, final GumTree2JdtAstConverterImpl converter, final ITree workingTree) {
    boolean _xblockexpression = false;
    {
      CompilationUnit _convertTree = converter.convertTree(workingTree);
      final String extractedContent = _convertTree.toString();
      boolean _xifexpression = false;
      boolean _and = false;
      if (!this.removeNeighbouringDuplicates) {
        _and = false;
      } else {
        String _last = IterableExtensions.<String>last(contentList);
        boolean _equals = extractedContent.equals(_last);
        _and = _equals;
      }
      if (_and) {
        GumTreeChangeExtractor.logger.info("Content did not change compared to last element in content list. Ignoring.");
      } else {
        _xifexpression = contentList.add(extractedContent);
      }
      _xblockexpression = _xifexpression;
    }
    return Boolean.valueOf(_xblockexpression);
  }
  
  private void processMvs(final RootsClassifier classifier, final ITree workingTree, final MappingStore mappings, final ArrayList<String> contentList, final GumTree2JdtAstConverterImpl converter) {
    Set<ITree> _srcMvTrees = classifier.getSrcMvTrees();
    final ArrayList<ITree> rootMvs = this.getRootChanges(_srcMvTrees);
    for (final ITree mvTree : rootMvs) {
      {
        final ITree nodeInDstTree = mappings.getDst(mvTree);
        GumTreeChangeExtractor.logger.info("Found MV");
        final boolean removed = this.removeNodeFromWorkingTree(mvTree, mappings);
        final boolean added = this.addNodeToWorkingTree(nodeInDstTree, mappings);
        boolean _or = false;
        if (removed) {
          _or = true;
        } else {
          _or = added;
        }
        if (_or) {
          this.appendExtractedContent(contentList, converter, workingTree);
        } else {
          GumTreeChangeExtractor.logger.info("Couldn\'t add or remove for MV. Happens if changes were already covered by ADDs and DELs");
        }
      }
    }
  }
  
  private void processAdds(final RootsClassifier classifier, final ITree workingTree, final MappingStore mappings, final ArrayList<String> contentList, final GumTree2JdtAstConverterImpl converter, final ITree completeDst, final ArrayList<ITree> lazyDelete) {
    Set<ITree> _dstAddTrees = classifier.getDstAddTrees();
    final ArrayList<ITree> rootAdds = this.getRootChanges(_dstAddTrees);
    OrderbyBreadthFirstOrderingOfCompleteTree _orderbyBreadthFirstOrderingOfCompleteTree = new OrderbyBreadthFirstOrderingOfCompleteTree(completeDst);
    rootAdds.sort(_orderbyBreadthFirstOrderingOfCompleteTree);
    for (final ITree addTree : rootAdds) {
      {
        GumTreeChangeExtractor.logger.info("Found ADD");
        this.handleLazyRemovalIfNeeded(addTree, mappings, lazyDelete);
        boolean _addNodeToWorkingTree = this.addNodeToWorkingTree(addTree, mappings);
        if (_addNodeToWorkingTree) {
          this.appendExtractedContent(contentList, converter, workingTree);
        } else {
          GumTreeChangeExtractor.logger.warn("Couldn\'t add node. How can this happen? FIXME");
        }
      }
    }
  }
  
  private boolean handleLazyRemovalIfNeeded(final ITree addTree, final MappingStore mappings, final ArrayList<ITree> lazyDelete) {
    boolean _xifexpression = false;
    boolean _or = false;
    int _type = addTree.getType();
    boolean _equals = (_type == 40);
    if (_equals) {
      _or = true;
    } else {
      int _type_1 = addTree.getType();
      boolean _equals_1 = (_type_1 == 42);
      _or = _equals_1;
    }
    if (_or) {
      boolean _xblockexpression = false;
      {
        GumTreeChangeExtractor.logger.info("Found added Name Tree. Looking for lazy removable opposite");
        ITree _parent = addTree.getParent();
        final ITree srcParent = mappings.getSrc(_parent);
        ITree deleted = null;
        for (final ITree lazyTree : lazyDelete) {
          boolean _and = false;
          ITree _parent_1 = lazyTree.getParent();
          boolean _equals_2 = Objects.equal(_parent_1, srcParent);
          if (!_equals_2) {
            _and = false;
          } else {
            int _positionInParent = lazyTree.positionInParent();
            int _positionInParent_1 = addTree.positionInParent();
            boolean _equals_3 = (_positionInParent == _positionInParent_1);
            _and = _equals_3;
          }
          if (_and) {
            GumTreeChangeExtractor.logger.info("Found lazy opposite. Removing");
            this.removeNodeFromWorkingTree(lazyTree, mappings);
            deleted = lazyTree;
          }
        }
        boolean _xifexpression_1 = false;
        boolean _notEquals = (!Objects.equal(deleted, null));
        if (_notEquals) {
          _xifexpression_1 = lazyDelete.remove(deleted);
        }
        _xblockexpression = _xifexpression_1;
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  private void processDels(final RootsClassifier classifier, final ITree workingTree, final MappingStore mappings, final ArrayList<String> contentList, final GumTree2JdtAstConverterImpl converter, final ArrayList<ITree> lazyDelete) {
    Set<ITree> _srcDelTrees = classifier.getSrcDelTrees();
    final ArrayList<ITree> rootDels = this.getRootChanges(_srcDelTrees);
    OrderbyBreadthFirstOrderingOfCompleteTree _orderbyBreadthFirstOrderingOfCompleteTree = new OrderbyBreadthFirstOrderingOfCompleteTree(workingTree, true);
    rootDels.sort(_orderbyBreadthFirstOrderingOfCompleteTree);
    for (final ITree delTree : rootDels) {
      boolean _shouldPerformLazyDelete = this.shouldPerformLazyDelete(delTree, lazyDelete);
      boolean _not = (!_shouldPerformLazyDelete);
      if (_not) {
        GumTreeChangeExtractor.logger.info("Found DEL");
        boolean _removeNodeFromWorkingTree = this.removeNodeFromWorkingTree(delTree, mappings);
        if (_removeNodeFromWorkingTree) {
          this.appendExtractedContent(contentList, converter, workingTree);
        } else {
          GumTreeChangeExtractor.logger.warn("Couldn\'t delete node. How can this happen? FIXME");
        }
      }
    }
  }
  
  private boolean shouldPerformLazyDelete(final ITree delTree, final ArrayList<ITree> lazyDelete) {
    boolean _or = false;
    int _type = delTree.getType();
    boolean _equals = (_type == 40);
    if (_equals) {
      _or = true;
    } else {
      int _type_1 = delTree.getType();
      boolean _equals_1 = (_type_1 == 42);
      _or = _equals_1;
    }
    if (_or) {
      GumTreeChangeExtractor.logger.info("Found removed Name Tree. Avoid broken code with \"MISSING\" by doing lazy delete");
      lazyDelete.add(delTree);
      return true;
    }
    return false;
  }
  
  private ArrayList<ITree> getRootChanges(final Set<ITree> allChanges) {
    final ArrayList<ITree> rootChanges = new ArrayList<ITree>();
    for (final ITree tree : allChanges) {
      ITree _parent = tree.getParent();
      boolean _contains = allChanges.contains(_parent);
      boolean _not = (!_contains);
      if (_not) {
        rootChanges.add(tree);
      }
    }
    return rootChanges;
  }
  
  private boolean addNodeToWorkingTree(final ITree addTree, final MappingStore mappings) {
    boolean _contains = this.alreadyAdded.contains(addTree);
    if (_contains) {
      return false;
    }
    final ITree addCopy = addTree.deepCopy();
    ITree dstParent = addTree.getParent();
    final ITree srcParent = mappings.getSrc(dstParent);
    boolean _notEquals = (!Objects.equal(srcParent, null));
    if (_notEquals) {
      final List<ITree> children = srcParent.getChildren();
      int pos = addTree.positionInParent();
      int _size = children.size();
      boolean _greaterThan = (pos > _size);
      if (_greaterThan) {
        int _size_1 = children.size();
        pos = _size_1;
      }
      children.add(pos, addCopy);
      OrderByDstOrderComparator _orderByDstOrderComparator = new OrderByDstOrderComparator(dstParent, mappings, this.workingTreeMappings);
      children.sort(_orderByDstOrderComparator);
      srcParent.setChildren(children);
      this.alreadyAdded.add(addTree);
      this.addWorkingTreeMapping(addCopy, addTree);
      return true;
    } else {
      return false;
    }
  }
  
  private void addWorkingTreeMapping(final ITree addCopy, final ITree addTree) {
    Iterable<ITree> _breadthFirst = addCopy.breadthFirst();
    final Iterator<ITree> newSrcSubTreeIterator = _breadthFirst.iterator();
    Iterable<ITree> _breadthFirst_1 = addTree.breadthFirst();
    final Iterator<ITree> dstTreeIterator = _breadthFirst_1.iterator();
    while (newSrcSubTreeIterator.hasNext()) {
      {
        final ITree nextNew = newSrcSubTreeIterator.next();
        final ITree nextDst = dstTreeIterator.next();
        this.workingTreeMappings.link(nextNew, nextDst);
      }
    }
  }
  
  private boolean removeNodeFromWorkingTree(final ITree delTree, final MappingStore mappings) {
    ITree srcParent = delTree.getParent();
    boolean _notEquals = (!Objects.equal(srcParent, null));
    if (_notEquals) {
      final List<ITree> children = srcParent.getChildren();
      boolean _contains = children.contains(delTree);
      if (_contains) {
        children.remove(delTree);
        srcParent.setChildren(children);
        return true;
      }
    }
    return false;
  }
  
  @Override
  public void setValidator(final IContentValidator validator) {
    this.validator = validator;
  }
  
  @Override
  public int getNumberOfTotalExtractions() {
    return this.totalExtractions;
  }
  
  @Override
  public int getNumberOfValidExtractions() {
    return this.validExtractions;
  }
}
