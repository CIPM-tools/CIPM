package cipm.consistency.vsum.test.pcm.compchanges;

import mir.routines.dummyPCMCompCPRs.DummyPCMCompCPRsRoutinesFacade;
import tools.vitruv.change.atomic.EChange;

public class DummyCompositeChangeCPRSwitch {
	public static void handleCompositeChange(EChange change, Object facade, String facadeClsName) {
		if (!DummyCompositeChangeMarker.isChangeHandled(change)) {
			var markedChanges = DummyCompositeChangeMarker.getMarkedChanges(change);
			switch (facadeClsName) {
			case "DummyPCMCompCPRsRoutinesFacade":
				((DummyPCMCompCPRsRoutinesFacade) facade).specificCompositeChangeHandler(markedChanges);
				DummyCompositeChangeMarker.changeHandled(markedChanges);
				break;
			}
		}
	}
}
