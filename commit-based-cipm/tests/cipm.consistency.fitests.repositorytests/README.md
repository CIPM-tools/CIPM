# Introduction

Tests under cipm.consistency.fitests.repositorytests package and its sub-packages are referred to as repository parser tests. They are a variant of parser tests, which consider Java code repositories (only GIT repositories at the time of writing this file) and parse EMF-based Java model Resources from them using JaMoPP. To this end, certain commits are checked out and then Java model Resources are parsed for each considered commit. To estimate expected similarity results in tests during run time, GIT diff patches between commits are analyzed for code changes.

Refer to the README of [cipm.consistency.fitests](../cipm.consistency.fitests/README.md) for related tests and terminology.

# Package Structure

The overview of the individual packages within this plug-in are as follows:

- cipm.consistency.fitests.repositorytests: Contains the concrete similarity checking tests, which clone and parse model resource instances from repositories. Also contains extensions to the constructs from `cipm.consistency.fitests` to adjust them for working with repositories.

- cipm.consistency.fitests.repositorytests.util: Contains utility classes for computing and caching expected similarity checking results. Utilises the constructs from its sub-packages.

- cipm.consistency.fitests.repositorytests.util.commentremoval: Contains classes that attempt to remove commentary from GIT diffs.

- cipm.consistency.fitests.repositorytests.util.difffilter: Contains classes that remove the metadata from GIT diffs.

Each package additionally has its own `package-info.java` file, which describes it further.

# Contained Tests

This plug-in currently contains the repository parser tests for the following GIT repositories:
- [Teammates Project](https://github.com/TEAMMATES/teammates)
    - Implemented in [TeammatesRepoTest](src/cipm/consistency/fitests/repositorytests/TeammatesRepoTest.java)
- [Corona Warn App Server](https://github.com/corona-warn-app/cwa-server)
    - Implemented in [CWARepoTest](src/cipm/consistency/fitests/repositorytests/CWARepoTest.java)

## Artefacts

This plug-in contains the following artefacts:
- Test resource for time measurement loading: [timeMeasurementSample](gsonTestResource/timeMeasurementSample.json)

Each repository parser test will generate the following artefacts (depending on test options, see the concrete and abstract test classes):
- GIT repository clones under `target/testResources/repo-clones`
- Cached similarity checking results under `target/testResources/results-cache`
- Cached model resources under `target/testResources/testmodel-cache`
- Time measurements under `target/timeMeasurements`

Note that the generated artefacts under `target/...` are not in this repository, they will instead be generated and persisted (if desired) as repository parser tests are run.
