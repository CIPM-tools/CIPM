package cipm.consistency.util.build.server.updatesite;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "cipm.consistency.util.build.server.updatesite")
public class UpdateSiteServerAppplication implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String vitruvUpdateSite = Paths.get("..", "..", "Vitruv", "releng",
            "cipm.consistency.vitruv.updatesite", "target", "repository").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/vitruv/**").addResourceLocations(vitruvUpdateSite);
        String cipmUpdateSite = Paths.get("..", "..", "commit-based-cipm", "releng", "cipm.consistency.updatesite.fi", "target", "repository").toAbsolutePath().toUri().toString();
		registry.addResourceHandler("/cipm/**").addResourceLocations(cipmUpdateSite);
		String cipm2UpdateSite = Paths.get("..", "..", "commit-based-cipm", "releng", "cipm.consistency.updatesite.si", "target", "repository").toAbsolutePath().toUri().toString();
		registry.addResourceHandler("/cipm2/**").addResourceLocations(cipm2UpdateSite);
    }

    public static void main(String[] args) {
        SpringApplication.run(UpdateSiteServerAppplication.class);
    }
}
