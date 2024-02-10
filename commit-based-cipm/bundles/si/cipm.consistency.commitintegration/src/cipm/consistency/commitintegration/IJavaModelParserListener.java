package cipm.consistency.commitintegration;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.framework.vsum.VirtualModel;

/**
 * An interface that can be implemented by listeners to react to events in
 * {@link JavaParserAndPropagatorUtils}.
 * 
 * @author atora
 */
public interface IJavaModelParserListener {
	void javaModelParsed(Path dir, Path target, VirtualModel vsum, Path configPath, Resource all);
	void setVariables(Object...vars);
}
