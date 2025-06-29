package cipm.consistency.cpr.javapcm.teammates;

import cipm.consistency.cpr.javapcm.teammates.seff.TeammatesJava2PcmMethodBodyChangePreprocessor;
import mir.reactions.allTeammatesRules.AllTeammatesRulesChangePropagationSpecification;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ResourceAccess;

/**
 * Change propagation specification in order to propagate changes on JaMoPP models to the PCM models.
 * Specific for TEAMMATES.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class TeammatesJavaPCMChangePropagationSpecification extends AllTeammatesRulesChangePropagationSpecification {
	private TeammatesJava2PcmMethodBodyChangePreprocessor bodyTransformation = new TeammatesJava2PcmMethodBodyChangePreprocessor();
	
	@Override
	protected void setup() {
		super.setup();
		// Change propagation specification for changes on method bodies.
		// TODO: Needs to be added somewhere else
		// this.addChangeMainprocessor(new TeammatesJava2PcmMethodBodyChangePreprocessor());
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
