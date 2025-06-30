package cipm.consistency.vsum;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
//import org.xtext.lua.lua.ComponentSet;
import org.emftext.language.java.containers.JavaRoot;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.models.ModelFacade;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * Facade to the V-SUM.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
@SuppressWarnings("restriction")
public class VsumFacadeImpl implements VsumFacade {
    private static final Logger LOGGER = Logger.getLogger(VsumFacadeImpl.class.getName());

    private VsumDirLayout dirLayout;
    private List<ChangePropagationSpecification> changeSpecs;
    private InternalVirtualModel vsum;
    private StateBasedChangeResolutionStrategy stateBasedChangeResolutionStrategy;

    private List<ModelFacade> models;

    // initialized is used as a breakpoint conditional
    @SuppressWarnings("unused")
    private boolean initialized = false;

    public VsumFacadeImpl() {
        dirLayout = new VsumDirLayout();
    }

    public void initialize(Path rootPath, List<ModelFacade> models, List<ChangePropagationSpecification> changeSpecs,
            StateBasedChangeResolutionStrategy stateBasedChangeResolutionStrategy) {
        dirLayout.initialize(rootPath);
        this.changeSpecs = changeSpecs;
        this.stateBasedChangeResolutionStrategy = stateBasedChangeResolutionStrategy;
        loadOrCreateVsum();

        this.models = models;
        loadModels(models, false);

        initialized = true;
    }

    /*
     * load the given resource if it is not yet loaded or if we positively want to do it
     */
    private void loadModelResource(Resource res, boolean force) {
        if (force || vsum.getModelInstance(res.getURI()) == null) {
            this.propagateResource(res);
        }
    }

    private void loadModel(ModelFacade model, boolean force) {
        // multiple resources
        var resources = model.getResources();
        if (resources != null) {
            for (var resource : resources) {
                if (resource != null) {
                    loadModelResource(resource, force);
                }
            }
        }

        // single resource
        var resource = model.getResource();
        if (resource != null) {
            loadModelResource(resource, force);
        }
    }

    @Override
    public void loadModels(List<ModelFacade> models, boolean force) {
        for (var model : models) {
            loadModel(model, force);
        }
    }

    @Override
    public void forceReload() {
        loadModels(this.models, true);
    }

    private void loadOrCreateVsum() {
        var vsumBuilder = getVsumBuilder();

        LOGGER.info("Loading VSUM");
        vsum = vsumBuilder.buildAndInitialize();
        getChangeDerivingView(vsum);
    }

    private CommittableView getChangeDerivingView(InternalVirtualModel theVsum) {
        var viewType = ViewTypeFactory.createIdentityMappingViewType("myView");
        var viewSelector = viewType.createSelector(theVsum);

//        // Selecting all elements here
//        viewSelector.getSelectableElements()
//            .forEach(ele -> viewSelector.setSelected(ele, true));

        viewSelector.getSelectableElements()
            .forEach(ele -> {
                if (ele instanceof JavaRoot) {
                    viewSelector.setSelected(ele, true);
                }
            });

        var view = viewSelector.createView()
            .withChangeDerivingTrait(stateBasedChangeResolutionStrategy);

        return view;
    }

    public CommittableView getChangeRecordingView() {
        var viewType = ViewTypeFactory.createIdentityMappingViewType("myRecordingView");
        var viewSelector = viewType.createSelector(vsum);

        // Selecting all elements here
        viewSelector.getSelectableElements()
            .forEach(ele -> {
                if (ele instanceof InstrumentationModel) {
                    viewSelector.setSelected(ele, true);
                }
            });
        var view = viewSelector.createView()
            .withChangeRecordingTrait();

        return view;
    }

//    private List<ChangePropagationSpecification> getChangePropagationSpecs() {
//        List<ChangePropagationSpecification> changePropagationSpecs = new ArrayList<>();
//
//        // the lua->pcm spec is always added
//        changePropagationSpecs.add(new LuaPcmChangePropagationSpecification());
//        changePropagationSpecs.add(new PcmInitChangePropagationSpecification());
//
//        boolean useImUpdateChangeSpec = CommitIntegrationSettingsContainer.getSettingsContainer()
//            .getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)
//                || CommitIntegrationSettingsContainer.getSettingsContainer()
//                    .getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS);
//
//        if (useImUpdateChangeSpec)
//            changePropagationSpecs.add(new ImUpdateChangePropagationSpecification());
//
//        return changePropagationSpecs;
//    }

    private VirtualModelBuilder getVsumBuilder() {
//		ExtendedPcmDomain pcmDomain = new ExtendedPcmDomainProvider().getDomain();
//		pcmDomain.enableTransitiveChangePropagation();

        return new VirtualModelBuilder().withStorageFolder(dirLayout.getRootDirPath())
            .withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
            .withChangePropagationSpecifications(changeSpecs);
    }

    private void checkResourceForProxies(Resource res) {
        // try to resolve all proxies before checking for unresolved ones
        EcoreUtil.resolveAll(res);

        var rootEObject = res.getContents()
            .get(0);
        var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(rootEObject);
        if (!potentialProxies.isEmpty()) {
            var proxies = potentialProxies.keySet();
            var proxyNames = proxies.stream()
                .map(p -> p.toString())
                .collect(Collectors.joining(", "));

            var errorMsg = String.format("Resource contains %d proxies: %s", proxies.size(), proxyNames);
            LOGGER.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    private boolean checkPropagationPreconditions(Resource res) {
        if (res.getContents()
            .size() == 0) {
            LOGGER.error(String.format("Resource has no contents: %s", res.getURI()));
            return false;
        }

        if (res.getErrors()
            .size() > 0) {
            LOGGER.error(String.format("Resource contains %d errors:", res.getErrors()
                .size()));
            var i = 0;
            for (var error : res.getErrors()) {
                LOGGER.error(String.format("%d: %s", i, error.getMessage()));
                i++;
            }
            return false;
        }

        // warnings are only logged, they don't prevent propagation
        if (res.getWarnings()
            .size() > 0) {
            LOGGER.debug(String.format("Resource contains %d warnings:", res.getWarnings()
                .size()));
            var i = 0;
            for (var warning : res.getWarnings()) {
                LOGGER.debug(String.format("%d: %s", i, warning.getMessage()));
                i++;
            }
        }
        checkResourceForProxies(res);
        return true;
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @return The propagated changes
     */
    @Override
    public Propagation propagateResource(Resource resource) {
        return propagateResource(resource, null, null);
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @param targetUri
     *            The uri where vitruv persists the propagated resource
     * @return The propagated changes
     */
    @Override
    public Propagation propagateResource(Resource resource, URI targetUri) {
        return propagateResource(resource, targetUri, null);
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @param targetUri
     *            The uri where vitruv persists the propagated resource
     * @param vsum
     *            Optional, may be used to override the vsum to which the change is propagated
     * @return The propagated changes
     */
    private Propagation propagateResource(Resource resource, URI targetUri, InternalVirtualModel vsum) {
        if (vsum == null) {
            vsum = this.vsum;
        }
        if (targetUri == null) {
            targetUri = resource.getURI();
        }
        final URI actualtargetUri = targetUri;

        // try to resolve all proxies in the resource
        EcoreUtil.resolveAll(resource);

        if (!checkPropagationPreconditions(resource)) {
            LOGGER.error(
                    String.format("Not propagating resource because of missing preconditions: %s", resource.getURI()));
            return null;
        }

        LOGGER.trace(String.format("Propagating resource: %s", resource.getURI()
            .toString()));

        if (resource.getContents()
            .size() == 0) {
            LOGGER.debug(String.format("Not propagating empty resource: %s", resource.getURI()));
            return null;
        }

        var view = getChangeDerivingView(vsum);
//        var newRootEobject = resource.getContents()
//            .get(0);
//        
//        var roots = view.getRootObjects();
//        var possiblyExistingRoot = roots.stream()
//            .filter(root -> root.eResource()
//                .getURI() == actualtargetUri)
//            .findAny();
//        var replaceRootObject = possiblyExistingRoot.isPresent();
//        if (replaceRootObject) {
//            LOGGER.trace(String.format("Replacing old root object (%s) at %s", newRootEobject.getClass(), targetUri));
//            // replace the existing root with the new one
//            var existingContents = possiblyExistingRoot.get()
//                .eResource()
//                .getContents();
//            existingContents.remove(0);
//            existingContents.add(newRootEobject);
//        } else {
//            LOGGER.trace(String.format("Registering new root object (%s) at %s", newRootEobject.getClass(), targetUri));
//            // or register the new root at the view
//            view.registerRoot(newRootEobject, targetUri);
//        }
        
        var roots = view.getRootObjects();
        if (!roots.isEmpty()) {
        	var first = roots.iterator().next();
        	first.eResource().getContents().clear();
        }
        new ArrayList<>(resource.getContents()).forEach(ele -> view.registerRoot(ele, actualtargetUri));

        List<PropagatedChange> changeList = List.of();
        IllegalStateException exception = null;

        try {
            changeList = view.commitChangesAndUpdate();
        } catch (IllegalStateException e) {
            LOGGER.error(e.getMessage());
            exception = e;
        }

        var propagation = new Propagation(changeList);
        propagation.setException(exception);

        logPropagatedChanges(resource, propagation);

        return propagation;
    }

    private void logPropagatedChanges(Resource res, Propagation changes) {
        if (changes.getOriginalChangeCount() > 0 || changes.getConsequentialChangeCount() > 0) {
            LOGGER.info(String.format("Propagated changes in model %s: ORIGINAL: %d  CONSEQUENTIAL: %d", res.getURI()
                .lastSegment(), changes.getOriginalChangeCount(), changes.getConsequentialChangeCount()));
        }
    }

    @Override
    public InternalVirtualModel getVsum() {
        return vsum;
    }

    @Override
    public VsumDirLayout getDirLayout() {
        return dirLayout;
    }

    @Override
    public EditableCorrespondenceModelView<Correspondence> getCorrespondenceView() {
        if (vsum != null) {
            return vsum.getCorrespondenceModel();
        }
        return null;
    }

}
