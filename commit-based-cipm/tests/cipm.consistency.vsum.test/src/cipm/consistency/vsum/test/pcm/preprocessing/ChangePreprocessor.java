package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

public class ChangePreprocessor {
	private CorrespondenceModelView<?> corView;
	private final ChangeDependencyAnalyser changeDepAnalyser = new ChangeDependencyAnalyser();
	private final PcmDependencyContainer depCon = new PcmDependencyContainer();
	private final List<PcmChangeWrapper> wrappedPcmChanges = new ArrayList<>();

	public void setCorView(CorrespondenceModelView<?> corView) {
		this.corView = corView;
	}

	public void addChanges(Collection<EChange> pcmChanges) {
		for (var c : pcmChanges) {
			var w = new PcmChangeWrapper(c, depCon);
			this.wrappedPcmChanges.add(w);
			changeDepAnalyser.getCorrespondenceDependencies(c, corView).forEach((dep) -> depCon.addDependency(dep));
		}
	}
}
