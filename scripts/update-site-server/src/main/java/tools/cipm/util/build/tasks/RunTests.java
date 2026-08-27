package tools.cipm.util.build.tasks;

import java.nio.file.Path;
import java.nio.file.Paths;

import tools.cipm.util.build.common.MavenWrapperUtil;
import tools.cipm.util.build.common.UpdateSiteServer;

import static tools.cipm.util.build.common.ErrorUtil.*;

public class RunTests implements Runnable {
    public void run() {
        Path root = Paths.get("..", "..");

        UpdateSiteServer server = new UpdateSiteServer();
        try {
            server.start();
            server.addDirectoryWithStaticContent("/vitruv", Paths.get("..", "..", "Vitruv", "releng", "cipm.consistency.vitruv.updatesite", "target", "repository"));
            server.addDirectoryWithStaticContent("/cipm3", Paths.get("..", "..", "commit-based-cipm", "releng", "cipm.consistency.updatesite.ti", "target", "repository"));
        } catch (Exception e) {
            exitAfterError("Could not start update site server:", e);
        }

        Path actualCipmRoot = root.resolve("commit-based-cipm");

        MavenWrapperUtil.copyMavenWrapper(Paths.get("."), actualCipmRoot);
        
        MavenWrapperUtil.executeMavenWrapper(actualCipmRoot, "clean verify -P test");

        MavenWrapperUtil.deleteMavenWrapper(actualCipmRoot);

        try {
            server.stop();
        } catch (Exception e) {
            exitAfterError("Could not stop update site server:", e);
        }
    }
}
