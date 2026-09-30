package tools.cipm.seff.pojotransformations.code2seff;

import java.util.List;

import org.apache.log4j.Logger;
import org.emftext.language.java.members.ClassMethod;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;

import tools.cipm.seff.CorrespondenceModelUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Package-mapping implementation of {@link ResourceDemandingBehaviourForClassMethodFinding}:
 * looks up the SEFF or internal behaviour corresponding to a class method through the
 * correspondence model. If several correspond, the first is returned and a warning is
 * logged.
 */
public class ResourceDemandingBehaviourForClassMethodFinderForPackageMapping
        implements ResourceDemandingBehaviourForClassMethodFinding {

    private static final Logger LOGGER = Logger
            .getLogger(ResourceDemandingBehaviourForClassMethodFinderForPackageMapping.class.getSimpleName());

    private final EditableCorrespondenceModelView<Correspondence> correspondenceModel;

    public ResourceDemandingBehaviourForClassMethodFinderForPackageMapping(
            final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
        this.correspondenceModel = correspondenceModel;
    }

    /**
     * @return the SEFF corresponding to the class method, or {@code null} if there is none.
     */
    @Override
    public ResourceDemandingSEFF getCorrespondingRDSEFForClassMethod(final ClassMethod classMethod) {
        return this.getFirstCorrespondingEObjectIfAny(classMethod, ResourceDemandingSEFF.class);
    }

    private <T> T getFirstCorrespondingEObjectIfAny(final ClassMethod classMethod, final Class<T> correspondingClass) {
        final List<T> correspondingObjects = CorrespondenceModelUtil
                .getCorrespondingEObjects(this.correspondenceModel, classMethod, correspondingClass);
        if (correspondingObjects == null || correspondingObjects.isEmpty()) {
            return null;
        }
        if (1 < correspondingObjects.size()) {
            LOGGER.warn("Found " + correspondingObjects.size() + " corresponding Objects from Type "
                    + correspondingClass + " for ClassMethod " + classMethod + " Returning the first.");
        }
        return correspondingObjects.iterator().next();
    }

    /**
     * @return the internal behaviour corresponding to the class method, or {@code null} if
     *         there is none.
     */
    @Override
    public ResourceDemandingInternalBehaviour getCorrespondingResourceDemandingInternalBehaviour(
            final ClassMethod classMethod) {
        return this.getFirstCorrespondingEObjectIfAny(classMethod, ResourceDemandingInternalBehaviour.class);
    }

}
