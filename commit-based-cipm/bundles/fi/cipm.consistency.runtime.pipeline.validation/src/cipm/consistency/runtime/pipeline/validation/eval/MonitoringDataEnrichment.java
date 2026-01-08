package cipm.consistency.runtime.pipeline.validation.eval;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.pcm.headless.shared.data.results.MeasuringPointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cipm.consistency.base.core.facade.IRuntimeEnvironmentQueryFacade;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.util.PCMElementIDCache;
import cipm.consistency.base.vsum.facade.ISpecificVsumFacade;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.bridge.monitoring.records.ResourceUtilizationRecord;
import cipm.consistency.bridge.monitoring.records.ServiceCallRecord;
import cipm.consistency.runtime.pipeline.validation.data.TimeValueDistribution;
import cipm.consistency.runtime.pipeline.validation.data.ValidationPoint;
import cipm.consistency.runtime.pipeline.validation.eval.util.PCMValidationPointMatcher;

@Service
// TODO a bit refactor (constants, long method,...)
public class MonitoringDataEnrichment {
	private Cache<Pair<String, String>, List<ValidationPoint>> resourceUtilCache = new Cache2kBuilder<Pair<String, String>, List<ValidationPoint>>() {
	}.expireAfterWrite(2, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	private Cache<Pair<String, String>, List<ValidationPoint>> serviceCallCache = new Cache2kBuilder<Pair<String, String>, List<ValidationPoint>>() {
	}.expireAfterWrite(2, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	private PCMElementIDCache<ResourceContainer> cacheResEnv = new PCMElementIDCache<>(ResourceContainer.class);

	@Autowired
	private PCMValidationPointMatcher pcmValidationPointMatcher;

	@Autowired
	private ISpecificVsumFacade vsumFacade;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remFacade;

	public void enrichWithMonitoringData(InMemoryPCM pcm, List<ValidationPoint> points,
			List<PCMContextRecord> monitoring) {
		// clear caches
		resourceUtilCache.clear();
		serviceCallCache.clear();
		cacheResEnv.clear();
		pcmValidationPointMatcher.clear();

		monitoring.stream().forEach(rec -> {
			processRecord(pcm, points, rec);
		});

	}

	private void processRecord(InMemoryPCM pcm, List<ValidationPoint> points, PCMContextRecord rec) {
		if (rec instanceof ResourceUtilizationRecord) {
			ResourceUtilizationRecord utilRecord = (ResourceUtilizationRecord) rec;
			Pair<String, String> key = Pair.of(utilRecord.getHostId(), utilRecord.getResourceId());

			List<ValidationPoint> assignedPoints = Lists.newArrayList();
			if (resourceUtilCache.containsKey(key)) {
				assignedPoints = resourceUtilCache.get(key);
			} else {
				String resolvedTargetId = resolveResourceTargetId(pcm, utilRecord.getHostId(),
						utilRecord.getResourceId());
				if (resolvedTargetId == null) {
					return;
				}
				assignedPoints = points.stream()
						.filter(p -> p.getMeasuringPoint().getSourceIds().contains(resolvedTargetId)
								&& p.getMeasuringPoint().getSourceIds().size() == 1)
						.collect(Collectors.toList());
				resourceUtilCache.put(key, assignedPoints);
			}

			if (assignedPoints.size() > 0) {
				assignedPoints.forEach(ap -> {
					if (ap.getMonitoringDistribution() == null) {
						ap.setMonitoringDistribution(new TimeValueDistribution());
					}
					ap.getMonitoringDistribution().addValueX(utilRecord.getLoggingTimestamp());
					ap.getMonitoringDistribution().addValueY(utilRecord.getUtilization());
				});
			}
		} else if (rec instanceof ServiceCallRecord) {
			ServiceCallRecord serviceRec = (ServiceCallRecord) rec;
			Pair<String, String> key = Pair.of(serviceRec.getHostId(), serviceRec.getServiceId());
			List<ValidationPoint> assignedPoints = Lists.newArrayList();
			if (serviceCallCache.containsKey(key)) {
				assignedPoints = serviceCallCache.get(key);
			} else {
				List<String> resolvedTargetIds = resolveServiceCallTargetIds(pcm, serviceRec.getHostId(),
						serviceRec.getServiceId());
				if (resolvedTargetIds == null) {
					return;
				}

				assignedPoints = points.stream().filter(p -> {
					if (p.getMeasuringPoint().getType() == MeasuringPointType.ASSEMBLY_OPERATION) {
						return pcmValidationPointMatcher.belongTogetherAssemblyOperation(pcm, resolvedTargetIds.get(0),
								resolvedTargetIds.get(1), p.getMeasuringPoint().getSourceIds().get(0),
								p.getMeasuringPoint().getSourceIds().get(1));
					} else if (p.getMeasuringPoint().getType() == MeasuringPointType.ENTRY_LEVEL_CALL) {
						return pcmValidationPointMatcher.belongTogetherEntryCall(pcm, resolvedTargetIds.get(0),
								resolvedTargetIds.get(1), p.getMeasuringPoint().getSourceIds().get(0));
					}
					return false;
				}).collect(Collectors.toList());
				serviceCallCache.put(key, assignedPoints);
			}

			if (assignedPoints.size() > 0) {
				assignedPoints.forEach(ap -> {
					if (ap.getMonitoringDistribution() == null) {
						ap.setMonitoringDistribution(new TimeValueDistribution());
						ap.setServiceId(serviceRec.getServiceId());
					}
					// TODO SUBTRACT START TIME
					ap.getMonitoringDistribution().addValueX(serviceRec.getEntryTime() / 1000000000.0D);
					ap.getMonitoringDistribution()
							.addValueY((serviceRec.getExitTime() - serviceRec.getEntryTime()) / 1000000.0D);
				});
			}
		} else {
			// TODO future work
		}
	}

	private List<String> resolveServiceCallTargetIds(InMemoryPCM pcm, String hostId, String serviceId) {
		ResourceContainer container = getCorrespondingResourceCotnainer(hostId);
		if (container != null) {
			return Lists.newArrayList(container.getId(), serviceId);
		}
		return null;
	}

	private String resolveResourceTargetId(InMemoryPCM pcm, String hostId, String resourceId) {
		ResourceContainer container = getCorrespondingResourceCotnainer(hostId);
		if (container != null) {
			return container.getActiveResourceSpecifications_ResourceContainer().stream()
					.filter(r -> r.getActiveResourceType_ActiveResourceSpecification().getId().equals(resourceId))
					.map(r -> r.getId()).findFirst().orElse(null);
		}
		return null;
	}

	private ResourceContainer getCorrespondingResourceCotnainer(String hostId) {
		RuntimeResourceContainer remContainer = remFacade.getContainerById(hostId);
		if (remContainer != null) {
			return vsumFacade.getCorrespondingResourceContainer(remContainer).orElse(null);
		}
		return null;
	}
}
