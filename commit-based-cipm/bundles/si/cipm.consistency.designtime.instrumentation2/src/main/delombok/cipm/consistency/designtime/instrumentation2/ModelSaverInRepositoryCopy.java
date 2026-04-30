package cipm.consistency.designtime.instrumentation2;

import cipm.consistency.designtime.instrumentation2.instrumenter.MinimalMonitoringEnvironmentModelGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.commons.NamespaceAwareElement;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;

/**
 * Saves the instrumented model by merging it with the original sources.
 * 
 * @author Martin Armbruster
 */
public final class ModelSaverInRepositoryCopy {
    private ModelSaverInRepositoryCopy() {
    }

    static void saveModels(ResourceSet copyContainer, Resource copiedResource, Path target, Path source,
            MinimalMonitoringEnvironmentModelGenerator monitoringEnv) {
        try {
            // Copy the repository.
            FileUtils.copyDirectory(source.toFile(), target.toFile());
            var origJavaFiles = Files.walk(target)
                .filter(Files::isRegularFile)
                .map(Path::toAbsolutePath)
                .filter(p -> {
                    String abs = p.toString();
                    return abs.endsWith(LogicalJavaURIGenerator.JAVA_FILE_EXTENSION)
                            && !(abs.matches(".*?/src/test/java/.*?"))
                            && !(abs.matches(".*?/utilities/tools.descartes.teastore.kieker/.*?"));
                })
                .collect(Collectors.toList());
            // Provide Resources for the monitoring environment.
            Resource monRes = copyContainer.createResource(URI.createURI("empty:/ThreadMonitoringController.java"));
            monRes.getContents()
                .add(monitoringEnv.threadMonitoringControllerCU);
            Resource serviceRes = copyContainer.createResource(URI.createURI("empty:/ServiceParameters.java"));
            serviceRes.getContents()
                .add(monitoringEnv.serviceParametersCU);
            Set<String> injectedProjects = new HashSet<>();
            for (EObject root : new ArrayList<>(copiedResource.getContents())) {
                JavaRoot cu = (JavaRoot) root;
                // All Java models originating from a file are printed.
                if (cu.getOrigin() == Origin.FILE) {
                    String lastPart = createLastPathPart(cu);
                    for (Path path : origJavaFiles) {
                        // Relate the Java model to its original file.
                        String absPath = path.toString();
                        if (absPath.endsWith(lastPart)) {
                            // Because all Java models are contained within one Resource,
                            // a new Resource is
                            // created for every model to be printed.
                            Resource newResource = copyContainer.createResource(URI.createFileURI(path.toString()));
                            newResource.getContents()
                                .add(cu);
                            newResource.save(null);
                            copiedResource.getContents()
                                .add(cu);
                            checkMonitoringEnvironmentPrinting(injectedProjects,
                                    absPath.substring(0, absPath.length() - lastPart.length()), monRes,
                                    monitoringEnv.threadMonitoringControllerCU, serviceRes,
                                    monitoringEnv.serviceParametersCU);
                        }
                    }
                }
            }
            copiedResource.getContents()
                .add(monitoringEnv.threadMonitoringControllerCU);
            copiedResource.getContents()
                .add(monitoringEnv.serviceParametersCU);
        } catch (IOException e) {
        }
    }

    // Converts a Java model to a file name.
    private static String createLastPathPart(JavaRoot cu) {
        StringBuilder builder = new StringBuilder();
        if (cu instanceof NamespaceAwareElement) {
            for (String ns : ((NamespaceAwareElement) cu).getNamespaces()) {
                builder.append(File.separator);
                builder.append(ns);
            }
        }
        builder.append(File.separator);
        if (cu instanceof CompilationUnit) {
            builder.append(cu.getName());
            builder.append(LogicalJavaURIGenerator.JAVA_FILE_EXTENSION);
        } else if (cu instanceof org.emftext.language.java.containers.Package) {
            builder.append(LogicalJavaURIGenerator.JAVA_PACKAGE_FILE_NAME);
        } else if (cu instanceof org.emftext.language.java.containers.Module) {
            builder.append(LogicalJavaURIGenerator.JAVA_MODULE_FILE_NAME);
        }
        return builder.toString();
    }

    // Checks if the monitoring environment can be printed.
    private static void checkMonitoringEnvironmentPrinting(Set<String> injectedProjects, String project,
            Resource monitoringRes, CompilationUnit monitoringCU, Resource serviceRes, CompilationUnit serviceCU)
            throws IOException {
        if (!injectedProjects.contains(project)) {
            monitoringRes.setURI(URI.createFileURI(project + createLastPathPart(monitoringCU)));
            monitoringRes.save(null);
            serviceRes.setURI(URI.createFileURI(project + createLastPathPart(serviceCU)));
            serviceRes.save(null);
            injectedProjects.add(project);
        }
    }
}
