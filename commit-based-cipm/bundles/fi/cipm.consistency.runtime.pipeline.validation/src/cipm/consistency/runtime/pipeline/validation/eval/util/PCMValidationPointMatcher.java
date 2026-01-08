package cipm.consistency.runtime.pipeline.validation.eval.util;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.springframework.stereotype.Service;

import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.util.PCMElementIDCache;
import cipm.consistency.base.shared.pcm.util.PCMUtils;

@Service
public class PCMValidationPointMatcher {
	private PCMElementIDCache<ExternalCallAction> actionCache = new PCMElementIDCache<>(ExternalCallAction.class);
	private PCMElementIDCache<AssemblyContext> assemblyCache = new PCMElementIDCache<>(AssemblyContext.class);
	private PCMElementIDCache<ResourceDemandingSEFF> seffCache = new PCMElementIDCache<>(ResourceDemandingSEFF.class);
	private PCMElementIDCache<ResourceContainer> containerCache = new PCMElementIDCache<>(ResourceContainer.class);
	private Map<AssemblyContext, ResourceContainer> allocationMapping = new HashMap<>();
	private PCMElementIDCache<EntryLevelSystemCall> entryCallCache = new PCMElementIDCache<>(
			EntryLevelSystemCall.class);

	public void clear() {
		actionCache.clear();
		assemblyCache.clear();
		seffCache.clear();
		allocationMapping.clear();
		containerCache.clear();
		entryCallCache.clear();
	}

	public boolean belongTogetherEntryCall(InMemoryPCM pcm, String pcmContainerId, String serviceCallId,
			String entryLevelCall) {
		ResourceDemandingSEFF seff = seffCache.resolve(pcm.getRepository(), serviceCallId);
		ResourceContainer container = containerCache.resolve(pcm.getResourceEnvironmentModel(), pcmContainerId);
		EntryLevelSystemCall systemCall = entryCallCache.resolve(pcm.getUsageModel(), entryLevelCall);
		if (systemCall == null || seff == null || container == null) {
			return false;
		}

		AssemblyContext connected = getConnectedAssembly(pcm.getSystem(),
				systemCall.getProvidedRole_EntryLevelSystemCall().getId());
		if (connected == null) {
			return false;
		}

		ResourceContainer allocationContainer = resolveAllocation(pcm.getAllocationModel(), connected);
		if (allocationContainer == null) {
			return false;
		}

		boolean compEqual = seff.getBasicComponent_ServiceEffectSpecification().getId()
				.equals(connected.getEncapsulatedComponent__AssemblyContext().getId());
		boolean containerEqual = allocationContainer.getId().equals(container.getId());
		boolean sigEqual = systemCall.getOperationSignature__EntryLevelSystemCall().getId()
				.equals(seff.getDescribedService__SEFF().getId());

		return compEqual && containerEqual && sigEqual;
	}

	public boolean belongTogetherAssemblyOperation(InMemoryPCM pcm, String pcmContainerId, String serviceCallId,
			String assemblyId, String externalCallId) {
		ExternalCallAction action = actionCache.resolve(pcm.getRepository(), externalCallId);
		AssemblyContext ctx = assemblyCache.resolve(pcm.getSystem(), assemblyId);
		ResourceDemandingSEFF seff = seffCache.resolve(pcm.getRepository(), serviceCallId);
		ResourceContainer container = containerCache.resolve(pcm.getResourceEnvironmentModel(), pcmContainerId);

		if (action == null || ctx == null || seff == null || container == null) {
			return false;
		}

		// resolve required comp
		// TODO caching
		AssemblyContext connected = getConnectedAssembly(pcm.getSystem(), action.getRole_ExternalService().getId());
		if (connected == null) {
			return false;
		}

		ResourceContainer allocationContainer = resolveAllocation(pcm.getAllocationModel(), connected);
		if (allocationContainer == null) {
			return false;
		}

		boolean compEqual = seff.getBasicComponent_ServiceEffectSpecification().getId()
				.equals(connected.getEncapsulatedComponent__AssemblyContext().getId());
		boolean containerEqual = allocationContainer.getId().equals(container.getId());
		boolean sigEqual = action.getCalledService_ExternalService().getId()
				.equals(seff.getDescribedService__SEFF().getId());

		return compEqual && containerEqual && sigEqual;
	}

	private ResourceContainer resolveAllocation(Allocation allocationModel, AssemblyContext ctx) {
		ResourceContainer allocationContainer;
		if (allocationMapping.containsKey(ctx)) {
			allocationContainer = allocationMapping.get(ctx);
		} else {
			allocationContainer = allocationModel.getAllocationContexts_Allocation().stream().filter(alloc -> {
				return alloc.getAssemblyContext_AllocationContext().getId().equals(ctx.getId());
			}).map(alloc -> alloc.getResourceContainer_AllocationContext()).findFirst().orElse(null);

			if (allocationContainer != null) {
				allocationMapping.put(ctx, allocationContainer);
			}
		}
		return allocationContainer;
	}

	private AssemblyContext getConnectedAssembly(System system, String roleId) {
		AssemblyConnector connector = PCMUtils.getElementsByType(system, AssemblyConnector.class).stream()
				.filter(ac -> {
					return ac.getRequiredRole_AssemblyConnector().getId().equals(roleId)
							|| ac.getProvidedRole_AssemblyConnector().getId().equals(roleId);
				}).findFirst().orElse(null);

		if (connector == null) {
			// search delegation
			RequiredDelegationConnector connReq = PCMUtils.getElementsByType(system, RequiredDelegationConnector.class)
					.stream().filter(del -> {
						return del.getOuterRequiredRole_RequiredDelegationConnector().getId().equals(roleId);
					}).findFirst().orElse(null);
			ProvidedDelegationConnector connProv = PCMUtils.getElementsByType(system, ProvidedDelegationConnector.class)
					.stream().filter(del -> {
						return del.getOuterProvidedRole_ProvidedDelegationConnector().getId().equals(roleId);
					}).findFirst().orElse(null);

			if (connReq != null) {
				return connReq.getAssemblyContext_RequiredDelegationConnector();
			}
			if (connProv != null) {
				return connProv.getAssemblyContext_ProvidedDelegationConnector();
			}

			return null;
		} else {
			if (connector.getRequiredRole_AssemblyConnector().getId().equals(roleId)) {
				return connector.getProvidingAssemblyContext_AssemblyConnector();
			} else {
				return connector.getRequiringAssemblyContext_AssemblyConnector();
			}
		}
	}

}
