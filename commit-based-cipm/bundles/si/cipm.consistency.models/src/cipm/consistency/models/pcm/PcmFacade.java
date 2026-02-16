package cipm.consistency.models.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.models.ModelFacade;

public class PcmFacade implements ModelFacade {
    private static final Logger LOGGER = Logger.getLogger(PcmFacade.class.getName());

    private InMemoryPCM pcm;
    private PcmDirLayout fileLayout;

    public PcmFacade() {
        fileLayout = new PcmDirLayout();
    }

    @Override
    public void initialize(Path rootPath) {
        fileLayout.initialize(rootPath);
        loadOrCreateModelResources();
    }
    
    @Override
    public void reload() {
        loadOrCreateModelResources();
    }

    private void loadOrCreateModelResources() {
        if (!existsOnDisk()) {
            createModelResources();
        } else {
            loadFromDisk();
        }
    }

    public void createModelResources() {
        LOGGER.info("Creating new PCM");

        var systemModel = SystemFactory.eINSTANCE.createSystem();
        var repoModel = RepositoryFactory.eINSTANCE.createRepository();
        var resourceEnvModel = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
        var usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
        var allocationModel = AllocationFactory.eINSTANCE.createAllocation();

        pcm = new InMemoryPCM(repoModel, systemModel, usageModel, allocationModel, resourceEnvModel);

        // Create files and resources before binding the allocation
        saveToDisk();

        // Bind the allocation
        // This needs to occur after pcm.saveToFile
//        allocationModel.setSystem_Allocation(systemModel);
//        allocationModel.setTargetResourceEnvironment_Allocation(resourceEnvModel);
//
//        // save again for the allocation model
//        saveToDisk();
//        try {
//            allocationModel.eResource()
//                .save(null);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    private boolean existsOnDisk() {
        return !List
            .of(fileLayout.getPcmRepositoryPath(), fileLayout.getPcmResourceEnvironmentPath(),
                    fileLayout.getPcmUsageModelPath(), fileLayout.getPcmAllocationPath(), fileLayout.getPcmSystemPath())
            .stream()
            .map(p -> p.toFile()
                .isFile())
            .collect(Collectors.toList())
            .contains(false);
    }

    private void loadFromDisk() {
        LOGGER.debug("Loading PCM from disk");

        var files = fileLayout.getFilePCM();
        pcm = new InMemoryPCM();

        // 1. Create shared ResourceSet for all PCM models
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
                .getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        // 2. Map pathmap://PCM_MODELS/ to the local PCM directory (where PrimitiveTypes.repository lives)
        //    Register GLOBALLY so the VSUM's internal ResourceSet also resolves to the local file
        java.io.File pcmDir = files.getRepositoryFile().getParentFile();
        URI pcmDirUri = URI.createFileURI(pcmDir.getAbsolutePath() + java.io.File.separator);
        org.eclipse.emf.ecore.resource.URIConverter.URI_MAP.put(
                URI.createURI("pathmap://PCM_MODELS/"), pcmDirUri);

        // 3. Pre-load PrimitiveTypes.repository directly from the local PCM directory
        resourceSet.getResource(
                URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository"), true);

        // 4. Load all PCM models into the shared ResourceSet
        pcm.setSystem(loadFromResourceSet(resourceSet, files.getSystemFile(), System.class));
        pcm.setRepository(loadFromResourceSet(resourceSet, files.getRepositoryFile(), Repository.class));
        pcm.setResourceEnvironmentModel(
                loadFromResourceSet(resourceSet, files.getResourceEnvironmentFile(), ResourceEnvironment.class));
        pcm.setUsageModel(loadFromResourceSet(resourceSet, files.getUsageModelFile(), UsageModel.class));
        pcm.setAllocationModel(loadFromResourceSet(resourceSet, files.getAllocationModelFile(), Allocation.class));

        // 5. Resolve all cross-references now
        EcoreUtil.resolveAll(resourceSet);
    }

    private <T> T loadFromResourceSet(ResourceSet resourceSet, java.io.File file, Class<T> clazz) {
        URI fileUri = URI.createFileURI(file.getAbsolutePath());
        Resource resource = resourceSet.getResource(fileUri, true);
        return clazz.cast(resource.getContents().get(0));
    }

    public void saveToDisk() {
        pcm.saveToFilesystem(fileLayout.getFilePCM());
    }

    @Override
    public List<Resource> getResources() {
        return List.of(pcm.getSystem()
            .eResource(),
                pcm.getRepository()
                    .eResource(),
                pcm.getResourceEnvironmentModel()
                    .eResource(),
                pcm.getUsageModel()
                    .eResource(),
                pcm.getAllocationModel()
                    .eResource());
    }

    @Override
    public Resource getResource() {
        return null;
    }

    public PcmDirLayout getDirLayout() {
        return fileLayout;
    }

    public InMemoryPCM getInMemoryPCM() {
        return pcm;
    }

    public Path createNamedCopyOfRepositoryModel(String name) throws IOException {
        var path = getDirLayout().getPcmRepositoryPath();
        var copyPath = path.resolveSibling("Repository-" + name + ".repository");

        FileUtils.copyFile(path.toFile(), copyPath.toFile());

        return copyPath;
    }
}
