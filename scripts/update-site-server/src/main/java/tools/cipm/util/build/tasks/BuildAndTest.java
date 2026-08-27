package tools.cipm.util.build.tasks;

public class BuildAndTest implements Runnable {
    public void run() {
        new FullCIPMBuild().run();
        new RunTests().run();
    }
}
