package cipm.consistency.commitintegration.lang.lua;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.xtext.lua.LuaStandaloneSetup;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.LuaFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

public class LuaModelFacade implements CodeModelFacade {
    private static final Logger LOGGER = Logger.getLogger(LuaModelFacade.class.getName());
    private LuaDirLayout dirLayout;
    private ComponentDetector componentDetector;

    // TODO tracking the last component set is a bit ugly here
    private ComponentSet currentComponentSet;
    private Resource currentResource;

    @Inject
    private Provider<XtextResourceSet> resourceSetProvider;

    public LuaModelFacade() {
        Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);

        this.componentDetector = new ComponentDetectorImpl();
        this.dirLayout = new LuaDirLayout();
    }

    @Override
    public void initialize(Path dirPath) {
        this.dirLayout.initialize(dirPath);
        if (existsOnDisk()) {
            loadParsedFile();
        }
    }

    public void setComponentDetectionStrategies(List<ComponentDetectionStrategy> strategies) {
        for (var strat : strategies) {
            this.componentDetector.addComponentDetectionStrategy(strat);
        }
    }

    private XtextResourceSet getEmptyResourceSet() {
        var resourceSet = resourceSetProvider.get();
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        return resourceSet;
    }

    private XtextResourceSet parseDirToResourceSet(Path sourceCodeDirPath) {
        LOGGER.debug("Parsing source code directory");
        // get a resource from the provider
        var resourceSet = getEmptyResourceSet();

        var iterator = FileUtils.iterateFiles(sourceCodeDirPath.toFile(), null, true);
        while (iterator.hasNext()) {
            var file = iterator.next();
            var path = file.toPath();

            if (!path.toString()
                .endsWith(".lua")) {
                continue;
            }

            var uri = URI.createFileURI(path.toAbsolutePath()
                .toString());

            Resource res = resourceSet.getResource(uri, true);
            if (res == null) {
                LOGGER.error(String.format("Unable to load resource: %s", uri));
            } else {
                LOGGER.debug(String.format("Loaded resource: %s", uri));
            }

            var warnings = res.getWarnings();
            if (warnings != null && warnings.size() > 0) {
                for (var warning : warnings) {
                    LOGGER.warn(warning.getMessage());
                }
            }

            var errors = res.getErrors();
            if (errors != null && errors.size() > 0) {
                for (var error : errors) {
                    LOGGER.error(error.getMessage());
                }
                return null;
            }
        }
        
        // do a few tasks for the evaluation:
        getFileStats(sourceCodeDirPath);
        CodeModelEvaluator.evaluateCodeModelCorrectness(sourceCodeDirPath);

        return resourceSet;
    }

    private void getFileStats(Path sourceCodeDir) {
        var clocCommand = "cloc --hide-rate --quiet --include-lang=lua " + sourceCodeDir.toAbsolutePath()
            .toString();
        var clocStats = executeShell("sh", "-c", clocCommand + " | grep -P 'Lua'");
//        var clocStats = executeShell("sh", "-c", clocCommand+ " | grep Lua");

        var split = clocStats.split(" +");
        // order: "Lua" <files> <blank> <comment> <code>
        if (split.length == 5) {
            var cs = EvaluationDataContainer.get()
                .getChangeStatistic();
            cs.setNumberClocFiles(Integer.valueOf(split[1].strip()));
            cs.setNumberClocLinesBlanks(Integer.valueOf(split[2].strip()));
            cs.setNumberClocLinesComments(Integer.valueOf(split[3].strip()));
            cs.setNumberClocLinesCode(Integer.valueOf(split[4].strip()));

        } else {
            LOGGER.warn("Could not parse cloc output");
        }

        LOGGER.debug("Commits stats:\n" + clocStats);
    }

    private String executeShell(String... commands) {
        Runtime rt = Runtime.getRuntime();
        Process proc;
        try {
            proc = rt.exec(commands);

            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

//            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//            // Read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
//            String s = null;
//            while ((s = stdOutput.readLine()) != null) {
//                System.out.println(s);
//            }
//
//            // Read any errors from the attempted command
//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//            }

            return IOUtils.toString(stdOutput);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private Resource getCleanResource(URI uri) {
        var rs = resourceSetProvider.get();

        var path = Path.of(uri.toFileString());
        if (path.toFile()
            .exists()) {
            FileUtils.deleteQuietly(path.toFile());
        }

        var res = rs.createResource(uri);
        return res;
    }

    /**
     * Merges all chunks of the resource set into one SuperChunk and puts it in a separate resource
     * for propagation
     * 
     * @param rs
     * @param targetUri
     * @return
     * @throws IOException
     */
    private ComponentSet resolveResourceSetToComponents(Path workTree, ResourceSet rs, URI targetUri) {
        LOGGER.debug("Resolving ResourceSet into a ComponentSet");
        var merge = getCleanResource(targetUri);

        // we use this synthetic model class as a container for all the components
        ComponentSet componentSet = LuaFactory.eINSTANCE.createComponentSet();
        merge.getContents()
            .add(componentSet);

        // merge warnings and errors into the new resource
        rs.getResources()
            .forEach(resource -> {
                // also merge errors and warnings into the new resource
                merge.getErrors()
                    .addAll(resource.getErrors());
                merge.getWarnings()
                    .addAll(resource.getWarnings());

                if (resource.getContents()
                    .size() == 0) {
                    LOGGER.error(String.format("Resource has no contents: %s", resource.getURI()));
                    return;
                }
                var eObj = resource.getContents()
                    .get(0);
                if (!(eObj instanceof Chunk)) {
                    LOGGER.error(String.format("Resource does not contain a chunk: %s", resource.getURI()));
                    return;
                }
            });

        // detect components in the resources
        var componentCandidates = componentDetector.detectModules(rs, workTree, dirLayout.getModuleConfigurationPath());
        if (componentCandidates == null) {
            LOGGER.info("No detected components!");
            return componentSet;
        }
        var actualComponents = componentCandidates.getModulesInState(ComponentState.REGULAR_COMPONENT);
        LOGGER.debug(String.format("Detected %d components", actualComponents.size()));

        // Create the detected components and add them to the component set
        actualComponents.forEach((componentName, resources) -> {
            var component = LuaFactory.eINSTANCE.createComponent();
            component.setName(componentName);

            for (var resource : resources) {
                if (resource.getContents()
                    .size() > 0) {
                    var eObj = resource.getContents()
                        .get(0);
                    if (eObj instanceof Chunk) {
                        var namedChunk = LuaFactory.eINSTANCE.createNamedChunk();
                        namedChunk.setChunk((Chunk) eObj);
                        var chunkName = resource.getURI()
                            .lastSegment();
                        namedChunk.setName(chunkName);
                        component.getChunks()
                            .add(namedChunk);
                    }
                }

            }
            componentSet.getComponents()
                .add(component);
        });

        // call possible post process on the component set
        LuaPostProcessor.postProcessComponentSet(componentSet);

        // save the component set to the merged resource
        try {
            merge.save(null);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot write new resource: %s", targetUri));
        }

        return componentSet;
    }

    @Override
    public Resource parseSourceCodeDir(Path sourceCodeDir) {
        LOGGER.debug("Propagating the current worktree");
        // parse all lua files into one resource set
        var workTreeResourceSet = parseDirToResourceSet(sourceCodeDir);
        if (workTreeResourceSet == null) {
            return null;
        }

        // where the processed resource is stored prior to propagation
        var storeUri = dirLayout.getParsedFileUri();
        currentComponentSet = resolveResourceSetToComponents(sourceCodeDir, workTreeResourceSet, storeUri);

        if (!validateEObject(currentComponentSet)) {
            LOGGER.error("Code model is invalid!");
            return null;
        }

        currentResource = currentComponentSet.eResource();
        return currentResource;
    }

    /*
     * Validate a given EObject and all its children
     */
    private static boolean validateEObject(EObject rootEObject) {
        var diagnostics = new BasicDiagnostic();
        var eObjValidator = new EObjectValidator();
        var allContents = EcoreUtil2.getAllContentsOfType(rootEObject, EObject.class);
        var contentsValid = true;
        for (var eObj : allContents) {
            var valid = eObjValidator.validate(eObj, diagnostics, null);
            contentsValid &= valid;
        }
        if (!contentsValid) {
            for (var diag : diagnostics.getChildren()) {
                LOGGER.warn(diag.getMessage());
            }
        }
        return contentsValid;
    }

    public boolean existsOnDisk() {
        return dirLayout.getParsedFilePath()
            .toFile()
            .exists();
    }

    private void loadParsedFile() {
        var resourceSet = getEmptyResourceSet();
        currentResource = resourceSet.getResource(dirLayout.getParsedFileUri(), true);
    }

    @Override
    public LuaDirLayout getDirLayout() {
        return dirLayout;
    }

    public ComponentSet getCurrentComponentSet() {
        return currentComponentSet;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        return currentResource;
    }

    @Override
    public Path createNamedCopyOfParsedModel(String name) throws IOException {
        var path = getDirLayout().getParsedFilePath();
        var copyPath = path.resolveSibling("parsed-" + name + ".code.xmi");

        FileUtils.copyFile(path.toFile(), copyPath.toFile());

        return copyPath;
    }

    @Override
    public void reload() {
        // TODO Auto-generated method stub

    }
}
