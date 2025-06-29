package cipm.consistency.cpr.javapcm;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import mir.reactions.all.AllChangePropagationSpecification;
import tools.cipm.seff.Java2PcmMethodBodyChangePreprocessor;
import tools.cipm.seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;
import tools.cipm.seff.finegrained.FineGrainedJava2PcmMethodBodyChangePreprocessor;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ResourceAccess;

/**
 * Change propagation specification in order to propagate changes on JaMoPP models to the PCM models.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class CommitIntegrationJavaPCMChangePropagationSpecification extends AllChangePropagationSpecification {
	private Java2PcmMethodBodyChangePreprocessor bodyTransformation;
	
	@Override
	protected void setup() {
		super.setup();
		// Change propagation specification for changes on method bodies.
		// TODO: Move this to another place.
		if (CommitIntegrationSettingsContainer.getSettingsContainer()
				.getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)) {
			this.bodyTransformation = new FineGrainedJava2PcmMethodBodyChangePreprocessor();
		} else {
			this.bodyTransformation = new ExtendedJava2PcmMethodBodyChangePreprocessor();
		}
	}
	
	@Override
	public boolean doesHandleChange(EChange change,
			EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		return super.doesHandleChange(change, correspondenceModel) || this.bodyTransformation.doesHandleChange(change, correspondenceModel);
	}
	
	@Override
	public void propagateChange(EChange change, EditableCorrespondenceModelView<Correspondence> correspondenceModel,
			ResourceAccess resourceAccess) {
		super.propagateChange(change, correspondenceModel, resourceAccess);
		if (this.bodyTransformation.doesHandleChange(change, correspondenceModel)) {
			this.bodyTransformation.propagateChange(change, correspondenceModel, resourceAccess);
		}
	}
}
