package cipm.consistency.base.shared.pcm;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.pcm.headless.api.client.transform.TransitiveModelTransformerUtil;

import com.google.common.collect.Lists;

import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.shared.pcm.util.PCMUtils;

/**
 * Represents an instance of a Palladio Component Model (PCM) with all corresponding model types
 * (repository, system, usage model, allocation and resource environment). Furthermore provides
 * features for copying, modifying and synchronizing it.
 * 
 * @author David Monschein
 *
 */
public class InMemoryPCM {

    /**
     * Contained repository model.
     */
    private Repository repository;

    /**
     * Contained system model.
     */
    private System system;

    /**
     * Contained usage model.
     */
    private UsageModel usageModel;

    /**
     * Contained allocation model.
     */
    private Allocation allocationModel;

    /**
     * Contained resource environment model.
     */
    private ResourceEnvironment resourceEnvironmentModel;

    /**
     * This instance can be synchronized with the file system.
     */
    private LocalFilesystemPCM reflected;

    /**
     * Creates a new instance which holds the references to the PCM parts.
     * 
     * @param repository
     *            the repository model
     * @param system
     *            the system model
     * @param usageModel
     *            the usage model
     * @param allocationModel
     *            the allocation model
     * @param resourceEnvironmentModel
     *            the resource environment model
     */
    public InMemoryPCM(Repository repository, System system, UsageModel usageModel, Allocation allocationModel,
            ResourceEnvironment resourceEnvironmentModel) {
        this.repository = repository;
        this.system = system;
        this.usageModel = usageModel;
        this.allocationModel = allocationModel;
        this.resourceEnvironmentModel = resourceEnvironmentModel;
    }

    /**
     * Creates an empty PCM instance in the memory.
     */
    public InMemoryPCM() {
    }

    /**
     * Synchronizes the model with given file system paths.
     * 
     * @param pcm
     *            the file system paths for the models
     */
    public void syncWithFilesystem(LocalFilesystemPCM pcm) {
        // The order is important!
        FileBackedModelUtil.synchronize(this.getRepository(), pcm.getRepositoryFile(), Repository.class);
        FileBackedModelUtil.synchronize(this.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile(),
                ResourceEnvironment.class);
        FileBackedModelUtil.synchronize(this.getSystem(), pcm.getSystemFile(), System.class);
        FileBackedModelUtil.synchronize(this.getAllocationModel(), pcm.getAllocationModelFile(), Allocation.class);
        FileBackedModelUtil.synchronize(this.getUsageModel(), pcm.getUsageModelFile(), UsageModel.class);
        this.reflected = pcm;
    }

    /**
     * Loads the model from the file system and synchronizes it with the underlying files.
     * 
     * @param pcm
     *            the file system paths of the models
     * @return the created PCM model container
     */
    public static InMemoryPCM createFromFilesystemSynced(LocalFilesystemPCM pcm) {
        InMemoryPCM ret = createFromFilesystem(pcm);
        ret.syncWithFilesystem(pcm);
        return ret;
    }

    /**
     * Loads the palladio models from the files. If a file does not exist, this method initializes
     * an empty model.
     * 
     * @param pcm
     *            file backed palladio models
     * @return loaded palladio models (guarantees that all parts are not null)
     */
    public static InMemoryPCM createFromFilesystem(LocalFilesystemPCM pcm) {
        InMemoryPCM ret = new InMemoryPCM();
        ret.reflected = pcm;

        // empty models if not available
        if (pcm.getRepositoryFile()
            .exists()) {
            ret.repository = ModelUtil.readFromFile(pcm.getRepositoryFile()
                .getAbsolutePath(), Repository.class);
        } else {
            ret.repository = RepositoryFactory.eINSTANCE.createRepository();
        }

        if (pcm.getResourceEnvironmentFile()
            .exists()) {
            ret.resourceEnvironmentModel = ModelUtil.readFromFile(pcm.getResourceEnvironmentFile()
                .getAbsolutePath(), ResourceEnvironment.class);
        } else {
            ret.resourceEnvironmentModel = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
        }

        if (pcm.getSystemFile()
            .exists()) {
            ret.system = ModelUtil.readFromFile(pcm.getSystemFile()
                .getAbsolutePath(), System.class);
        } else {
            ret.system = SystemFactory.eINSTANCE.createSystem();
        }

        if (pcm.getAllocationModelFile()
            .exists()) {
            ret.allocationModel = ModelUtil.readFromFile(pcm.getAllocationModelFile()
                .getAbsolutePath(), Allocation.class);
        } else {
            ret.allocationModel = AllocationFactory.eINSTANCE.createAllocation();
            ret.allocationModel.setSystem_Allocation(ret.system);
            ret.allocationModel.setTargetResourceEnvironment_Allocation(ret.resourceEnvironmentModel);
        }

        if (pcm.getUsageModelFile()
            .exists()) {
            ret.usageModel = ModelUtil.readFromFile(pcm.getUsageModelFile()
                .getAbsolutePath(), UsageModel.class);
        } else {
            ret.usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
        }

        // this fixes the internal links
        return ret.copyDeep();
    }

    /**
     * Saves the models to the file system.
     * 
     * @param pcm
     *            file system paths where the PCM should be saved
     */
    public void saveToFilesystem(LocalFilesystemPCM pcm) {
        // The order is important
        // Because of the dependencies in the models
        ModelUtil.saveToFile(this.getRepository(), pcm.getRepositoryFile());
        ModelUtil.saveToFile(this.getResourceEnvironmentModel(), pcm.getResourceEnvironmentFile());
        ModelUtil.saveToFile(this.getSystem(), pcm.getSystemFile());
        ModelUtil.saveToFile(this.getAllocationModel(), pcm.getAllocationModelFile());
        ModelUtil.saveToFile(this.getUsageModel(), pcm.getUsageModelFile());
    }

    /**
     * Exchanges the system model and makes sure that it is synchronized with the underlying file
     * system.
     * 
     * @param currentSystem
     *            the new system that should be applied
     */
    public void swapSystem(System currentSystem) {
        FileBackedModelUtil.clear(this.system);
        if (reflected != null) {
            this.system = FileBackedModelUtil.synchronize(currentSystem, reflected.getSystemFile(), System.class);
        } else {
            this.system = currentSystem;
        }

        // update link to system
        this.allocationModel.setSystem_Allocation(this.system);
        Set<String> containedIds = PCMUtils.getElementsByType(this.system, AssemblyContext.class)
            .stream()
            .map(ac -> ac.getId())
            .collect(Collectors.toSet());
        List<AllocationContext> toRemoveCtxs = PCMUtils.getElementsByType(this.allocationModel, AllocationContext.class)
            .stream()
            .filter(ac -> !containedIds.contains(ac.getId()))
            .collect(Collectors.toList());
        toRemoveCtxs.forEach(ac -> this.allocationModel.getAllocationContexts_Allocation()
            .remove(ac));
    }

    /**
     * Clears all listeners and therefore disables the synchronization.
     */
    public void clearListeners() {
        FileBackedModelUtil.clear(allocationModel);
        FileBackedModelUtil.clear(repository);
        FileBackedModelUtil.clear(resourceEnvironmentModel);
        FileBackedModelUtil.clear(usageModel);
        FileBackedModelUtil.clear(system);
    }

    /**
     * Copies all model references, the underlying models are equal!
     * 
     * @return a new model container instance, having the same references as this instance
     */
    public InMemoryPCM copyReference() {
        return InMemoryPCM.builder()
            .repository(getRepository())
            .system(getSystem())
            .resourceEnvironmentModel(getResourceEnvironmentModel())
            .allocationModel(getAllocationModel())
            .usageModel(getUsageModel())
            .build();
    }

    /**
     * Makes a deep copy of the models. Copies the content of the models and transforms the
     * references.
     * 
     * @return a new model container with own models, that are encapsulated from this instance
     */
    public InMemoryPCM copyDeep() {
        TransitiveModelTransformerUtil transformer = new TransitiveModelTransformerUtil();
        List<EObject> orgs = Lists.newArrayList(allocationModel, repository, resourceEnvironmentModel, usageModel,
                system);
        List<EObject> copies = transformer.copyObjects(orgs);
        transformer.relinkObjects(copies);

        Repository repo = copies.stream()
            .filter(f -> f instanceof Repository)
            .map(Repository.class::cast)
            .findFirst()
            .orElse(null);
        System systemInner = copies.stream()
            .filter(f -> f instanceof System)
            .map(System.class::cast)
            .findFirst()
            .orElse(null);
        ResourceEnvironment env = copies.stream()
            .filter(f -> f instanceof ResourceEnvironment)
            .map(ResourceEnvironment.class::cast)
            .findFirst()
            .orElse(null);
        Allocation alloc = copies.stream()
            .filter(f -> f instanceof Allocation)
            .map(Allocation.class::cast)
            .findFirst()
            .orElse(null);
        UsageModel usage = copies.stream()
            .filter(f -> f instanceof UsageModel)
            .map(UsageModel.class::cast)
            .findFirst()
            .orElse(null);

        return InMemoryPCM.builder()
            .repository(repo)
            .system(systemInner)
            .resourceEnvironmentModel(env)
            .allocationModel(alloc)
            .usageModel(usage)
            .build();
    }

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public UsageModel getUsageModel() {
		return usageModel;
	}

	public void setUsageModel(UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	public Allocation getAllocationModel() {
		return allocationModel;
	}

	public void setAllocationModel(Allocation allocationModel) {
		this.allocationModel = allocationModel;
	}

	public ResourceEnvironment getResourceEnvironmentModel() {
		return resourceEnvironmentModel;
	}

	public void setResourceEnvironmentModel(ResourceEnvironment resourceEnvironmentModel) {
		this.resourceEnvironmentModel = resourceEnvironmentModel;
	}

	public LocalFilesystemPCM getReflected() {
		return reflected;
	}

	public void setReflected(LocalFilesystemPCM reflected) {
		this.reflected = reflected;
	}
	
	public static class InMemoryPCMBuilder {
		private Repository repository;
		private System system;
		private UsageModel usageModel;
		private Allocation allocationModel;
		private ResourceEnvironment resourceEnvironmentModel;

		InMemoryPCMBuilder() {
		}

		/**
		 * @param repository               the repository model
		 * @return {@code this}.
		 */
		public InMemoryPCM.InMemoryPCMBuilder repository(final Repository repository) {
			this.repository = repository;
			return this;
		}

		/**
		 * @param system                   the system model
		 * @return {@code this}.
		 */
		public InMemoryPCM.InMemoryPCMBuilder system(final System system) {
			this.system = system;
			return this;
		}

		/**
		 * @param usageModel               the usage model
		 * @return {@code this}.
		 */
		public InMemoryPCM.InMemoryPCMBuilder usageModel(final UsageModel usageModel) {
			this.usageModel = usageModel;
			return this;
		}

		/**
		 * @param allocationModel          the allocation model
		 * @return {@code this}.
		 */
		public InMemoryPCM.InMemoryPCMBuilder allocationModel(final Allocation allocationModel) {
			this.allocationModel = allocationModel;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public InMemoryPCM.InMemoryPCMBuilder resourceEnvironmentModel(final ResourceEnvironment resourceEnvironmentModel) {
			this.resourceEnvironmentModel = resourceEnvironmentModel;
			return this;
		}

		public InMemoryPCM build() {
			return new InMemoryPCM(this.repository, this.system, this.usageModel, this.allocationModel, this.resourceEnvironmentModel);
		}
	}

	@java.lang.SuppressWarnings("all")
	public static InMemoryPCM.InMemoryPCMBuilder builder() {
		return new InMemoryPCM.InMemoryPCMBuilder();
	}
}