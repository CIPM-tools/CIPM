# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.1.1] - 2024-12-20

### Changed

- Upgraded Eclipse dependencies to: 2022-12

## [0.1.0] - 2024-10-08

Initial release, which adds these features:

- Parsing of Java source code with the [extended Java Model Parser and Printer](https://github.com/MDSD-Tools/TheExtendedJavaModelParserAndPrinter) (custom version after 5.1.0) from Git repositories
- Derivation of changes in Java models (based on [SPLevo](https://github.com/kopl/SPLevo)) corresponding to changes between two Git commits
- Propagation of changes within [Vitruvius](https://github.com/vitruv-tools/) (custom 2.0.0-dev version)
- Consistency preservation rules (Reactions) from Java to the [Palladio Component Model](https://github.com/palladioSimulator/) (5.1.0) and from the PCM to the Instrumentation Model
    - Reuses rules from the [Co-evolution approach](https://publikationen.bibliothek.kit.edu/1000081447)
    - Supports Servlets, Jax-RS, and Java packages for interface detection
	- SEFF reconstruction based on [SoMoX](https://github.com/MDSD-Tools/SourceCodeModelExtractor-JaMoPP) (unreleased version)
- Evaluation cases with the [TeaStore](https://github.com/DescartesResearch/TeaStore) and [TEAMMATES](https://github.com/TEAMMATES/teammates)
- Build pipeline (only works on Windows)
- Supported Java version: Java 11
- Supported Eclipse Modeling Tools IDE version: 2022-09
- Supported Eclipse dependencies: 2021-09

[Unreleased]: https://github.com/CIPM-tools/CIPM/compare/releases/0.1.1...HEAD
[0.1.1]: https://github.com/CIPM-tools/CIPM/releases/tag/v0.1.1
[0.1.0]: https://github.com/CIPM-tools/CIPM/releases/tag/v0.1.0
