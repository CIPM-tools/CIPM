package tools.cipm.seff.pojotransformations.code2seff;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.CorrespondenceModelUtil;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Finds the component for a method if the the simple package mapping structure is used.
 *
 * @author langhamm
 *
 */
public class BasicComponentForPackageMappingFinder implements BasicComponentFinding {

    private static final Logger LOGGER = Logger.getLogger(BasicComponentForPackageMappingFinder.class.getSimpleName());

    private final ResourceSet dummyResourceSet;

    public BasicComponentForPackageMappingFinder() {
        this.dummyResourceSet = new ResourceSetImpl();
    }

    /**
     * To find the BasicComponent we try to find a corresponding component for the package of the
     * method. If the class of the method is in a nested package of a component we find the matching
     * component by finding the BasicComponent of the parent component.
     */
    @Override
    public BasicComponent findBasicComponentForMethod(final Method newMethod, final EditableCorrespondenceModelView<Correspondence> ci) {
        final CompilationUnit cu = newMethod.getContainingCompilationUnit();
        if (null == cu) {
            LOGGER.info("Could not find basic component for method " + newMethod
                    + " cause the compilation of the method is null");
            return null;
        }
        final Package jaMoPPPackage = this.createPackage(cu, cu.getNamespaces());
        final BasicComponent correspondingBc = this.findCorrespondingBasicComponentForPackage(jaMoPPPackage, ci);
        if (null == correspondingBc) {
            LOGGER.info("Could not find basic component for method " + newMethod + " in package " + jaMoPPPackage);
        }
        return correspondingBc;
    }

    private Package createPackage(final JavaRoot cu, final List<String> namespace) {
        final Package jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage();
        jaMoPPPackage.getNamespaces().addAll(namespace);
        // attach dummy resource in order to enable Tuid calculation
        final URI vuri = cu.eResource().getURI();
        String packageURIString = vuri.toString();
        final String lastSegment = vuri.lastSegment();
        if (packageURIString.endsWith(lastSegment)) {
            final int newLength = packageURIString.length() - lastSegment.length();
            packageURIString = packageURIString.substring(0, newLength);
            packageURIString = packageURIString + "package-info.java";
        }
        final URI packageVuri = URI.createURI(packageURIString);
        final Resource dummyResource = this.dummyResourceSet.createResource(packageVuri);
        dummyResource.getContents().add(jaMoPPPackage);
        return jaMoPPPackage;
    }

    /**
     * Recursively finds the corresponding basic component for the package. Walks up the package
     * hierarchy and returns the first matching basic component.
     *
     * @param jaMoPPPackage the package model to investigate.
     * @param ci the current correspondence model.
     * @return the first matching basic component.
     */
    private BasicComponent findCorrespondingBasicComponentForPackage(final Package jaMoPPPackage,
            final EditableCorrespondenceModelView<Correspondence> ci) {
        if (0 == jaMoPPPackage.getNamespaces().size()) {
            return null;
        };
        final List<BasicComponent> correspondingComponents = CorrespondenceModelUtil
                .getCorrespondingEObjects(ci, jaMoPPPackage, BasicComponent.class);
        if (null == correspondingComponents || correspondingComponents.isEmpty()) {

            jaMoPPPackage.getNamespaces().remove(jaMoPPPackage.getNamespaces().size() - 1);
            return this.findCorrespondingBasicComponentForPackage(jaMoPPPackage, ci);
        }
        return correspondingComponents.iterator().next();
    }

}
