# Commit-Based Continuous Integration of architectural Performance Models

<<<<<<< HEAD
This repository provides the prototypical implementation for the change extraction, change propagation, incremental model update, and adaptive instrumentation of the [CIPM approach](https://sdq.kastel.kit.edu/wiki/CIPM).

# Setup
**Note: a new setup method is currently in development. Therefore, some configurations are not supported yet or can cause issues.**

This project requires Java 11 for all actions. In particular, if a script is executed in the following, it usually uses Maven to build projects. As a result, the `JAVA_HOME` environment variable must be set, pointing to a JDK 11 (the top-level JDK directory, not the `bin` folder so that Maven can find the Java executable in `%JAVA_HOME%\bin\java.exe`). Additionally, any script must be executed from the top-level directory of this repository (and not within the `scripts` directory).

When executing one of the scripts, it is possible that the error `Internal error: java.lang.IllegalArgumentException: bundleLocation not found: [home]/.m2/[...]` occurs. In such a case, it can help to delete the file `[home]/.m2/repository/.meta/p2-artifacts.properties` and restart the script.

## Build

To build the complete project for the first time, the script `scripts/build.bat` needs to be executed. Subsequent builds can be executed with the script `scripts/rebuild.bat`.

If a complete clean build is required, the script `scripts/clean.bat` allows to clean the complete repository with all build artifacts and submodules. Afterward, the script `scripts/build.bat` can be executed again.

## Execute TEAMMATES

With the script `scripts/build-and-test.bat`, the project is built. In addition, the TEAMMATES case is executed. For more information, see [the test plugin description](commit-based-cipm/tests/cipm.consistency.vsum.test).

## Simple Setup with Eclipse

With the following steps, the project can be setup within Eclipse to view the source code. It is possible to also edit the code, but not to test it. As a reminder, this project requires Java 11. As Eclipse version, the Eclipse Modeling Tools 2022-09 are currently supported.

1. In Eclipse, install the plugins from the CIPM update site.

1. Import any CIPM plugin.

## Full Setup with Eclipse

The following steps are necessary to setup the project for development within Eclipse. As a reminder, this project requires Java 11. As Eclipse version, the project currently supports the Eclipse Modeling Tools 2022-09. It requires the installation of Xtext (from the Marketplace), Lombok (from [its update site](https://projectlombok.org/p2)), and Checkstyle (optional, from [update site](https://checkstyle.org/eclipse-cs-update-site)).

1. Execute the `scripts/build.bat` script if it was not executed before.

1. After executing the script, all bundles from `commit-based-cipm/bundles/fi` and `commit-based-cipm/releng-dev` can be imported into the Eclipse instance. The `releng-dev` directory contains the bundle `cipm.consistency.targetplatform` with the `cipm.consistency.targetplatform.target` file. Within Eclipse, open this file and click on `Set as active target platform`. Wait until the target platform is set, loaded, and the plugins are successfully compiled. It is possible that the target platform needs to be reloaded.

1. The project requires a second running Eclipse instance. After all plugins in the first instance has been setup, start the provided `SecondInstance` run configuration. It should start the second instance. In the second instance, import all bundles from `commit-based-cipm/bundles/si` and `commit-based-cipm/tests`. In `cipm.consistency.vsum.test`, the test cases should be executable.

## About the Internal Structure of the Setup

The current build process provides a replicable build. Therefore, dependencies are provided via Eclipse P2 Update Sites with fixed versions or via Git submodules. In particular, the submodules include JaMoPP, SoMoX, and a specialized old Vitruv version. As the submodules only contain source code, they need to be compiled after cloning the repository or if they are cleaned. The build process contains necessary steps to build the submodules.

The artifacts from the submodules are also packaged into local Eclipse P2 Update Sites. Unfortunately, Maven Tycho, which is internally used to build the artifacts, does not support local Eclipse P2 Update Sites via file paths and requires HTTP or HTTPS paths instead. Thus, a simple update site server, which serves the local Eclipse P2 Update Sites, is started and stopped during the build process.

The Reactions language from Vitruv detects the meta-models from Vitruv domains. To find the Vitruv domains, the corresponding lookup mechanisms in Eclipse and in the build process require the domains and domain providers to be built and to be on the classpath. As a consequence, a separation between the bundles is performed. The first half of the bundles (located in `commit-based-cipm/bundles/fi`, also imported into the first Eclipse instance) contain two domains (one for the instrumentation model and one adjusted domain for Java) so that they are built in a first build step and for the second Eclipse instance. In the second build step and in the second Eclipse instance, the built domains can be found by the Reactions language.

Notes on generating code for Reactions in the build process: 

1. The plugin containing Reactions requires a `.maven_enable_dsls-generation` file in the plugin directory (the `[plugin name]` directory, not `[plugin name]/src`). 

2. Furthermore, all classes or methods imported within a Reactiosn file cannot be located in the same plugin as the Reactions. They need to be in separated plugins.

The manual to execute the pipeline with TeaStore or TEAMMATES can be found [here](commit-based-cipm/tests/cipm.consistency.vsum.test). Reference PCM repository models for these executions can be found [here](data).
