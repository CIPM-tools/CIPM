package cipm.consistency.vsum.test.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaPackage;
import org.junit.jupiter.api.Assertions;
import org.palladiosimulator.pcm.PcmPackage;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.pcm.experiment.JavaToPcmPropagationDirLayout;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.resolve.EChangeResolverAndApplicator;

public class ChangeSaver {
	private final JavaToPcmPropagationDirLayout dirLayout;

	public ChangeSaver(JavaToPcmPropagationDirLayout dirLayout) {
		this.dirLayout = dirLayout;
	}

	public void saveUnresolvedChanges(Propagation prop) {
		// Use LinkedHashSet to ensure that a change is present exactly once and that
		// all changes retain their insertion order

		var javaChanges = new LinkedHashSet<EChange>();
		var pcmChanges = new LinkedHashSet<EChange>();
		var imChanges = new LinkedHashSet<EChange>();

		var propChanges = prop.getChanges();
		if (propChanges != null) {
			propChanges.stream().forEach((pc) -> {
				var oc = pc.getOriginalChange();
				if (oc != null) {
					var metamodelNs = oc.getAffectedEObjectsMetamodelDescriptors().iterator().next().getNsUris()
							.iterator().next();
					if (metamodelNs.contains(JavaPackage.eNS_URI)) {
						javaChanges.addAll(oc.getEChanges());
					} else if (metamodelNs.contains(PcmPackage.eNS_URI)) {
						pcmChanges.addAll(oc.getEChanges());
					} else if (metamodelNs.contains(InstrumentationModelPackage.eNS_URI)) {
						imChanges.addAll(oc.getEChanges());
					}
				}

				var cc = pc.getConsequentialChanges();
				if (cc != null) {
					var changes = cc.getEChanges();
					for (var c : changes) {
						if (c.eCrossReferences().stream()
								.anyMatch((cr) -> JavaPackage.eINSTANCE.getEClassifiers().contains(cr.eClass()))) {
							javaChanges.add(c);
						} else if (c.eCrossReferences().stream()
								.anyMatch((cr) -> PcmPackage.eINSTANCE.getEClassifiers().contains(cr.eClass()))) {
							pcmChanges.add(c);
						} else if (c.eCrossReferences().stream().anyMatch((cr) -> InstrumentationModelPackage.eINSTANCE
								.getEClassifiers().contains(cr.eClass()))) {
							imChanges.add(c);
						}
					}
				}
			});
		}

		saveUnresolvedChanges(javaChanges, dirLayout.getJavaChangesSaveFilePath());
		saveUnresolvedChanges(pcmChanges, dirLayout.getPcmChangesSaveFilePath());
		saveUnresolvedChanges(imChanges, dirLayout.getImChangesSaveFilePath());
	}

	public void saveUnresolvedChanges(Collection<EChange> changes, Path savePath) {
		// Unresolve the changes before saving, since they would otherwise need the
		// model resources to work

		var changesResSet = new ResourceSetImpl();
		var changesRes = changesResSet.createResource(URI.createFileURI(savePath.toFile().getAbsolutePath()));
		changes.stream().forEach((c) -> changesRes.getContents().add(EChangeResolverAndApplicator.unresolve(c)));

		try {
			changesRes.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}
}
