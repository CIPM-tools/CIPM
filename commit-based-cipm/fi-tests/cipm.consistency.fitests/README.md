# Introduction

cipm.consistency.fitests ("fitests" for short) plug-in contains tests for various aspects of similarity checking and model comparison. fitests is structured in a way that minimises dependencies to outside plug-ins. To this end, there are several abstract test classes that extend one another. This way, many different parts of fitests can be re-used in the future to implement further tests for EMF-based models. The "package-info.java" files under packages of fitests contain more information on their respective packages.

# Contained Tests

## Unittests

TODO Write once the corresponding pull request is adapted

## Parser Tests

Tests under cipm.consistency.fitests.similarity.jamopp.parser package and its sub-packages are referred to as parser tests. They consider Java models and perform similarity checking and model comparison on them, according to concrete test classes. Throughout those tests, Java models in form of Java source files are parsed with JaMoPP, resulting in EMF-based Java model Resource instances. These Resource instances are then used in dynamic tests created by parser tests, in order to test various aspects of CIPM.

The main difference between parser tests and the tests in the cipm.consistency.vsum.test package is, the latter runs commits from repositories through the entire CIPM pipeline, whereas the former only perform similarity checking and model comparison. Since CIPM pipeline additionally considers change propagation, code instrumentation and performance parameter calibration; parser tests are more lightweight in comparison. Therefore, parser tests are better suited to cover isolated cases in model comparison.

### Repository Parser Tests

Refer to the README of [cipm.consistency.fitests.repositorytests](../cipm.consistency.fitests.repositorytests/README.md) and the "package-info.java" files of packages under cipm.consistency.fitests.repositorytests plug-in for more information.

# Terminology

There are numerous terms that are commonly used across fitests, which are defined below:

- Similarity checking
	- Similarity result / Similarity checking result: (usually) a Boolean that indicates whether 2 or more objects are similar: Similar if the result is true, not similar if the result is false, undecidable if the result is null. Under normal circumstances, the result should not be null.
	- Similarity Checking: Computation of the similarity of objects, especially in-memory models. The result of this operation is the similarity result.
	- Similarity Checker: Mechanisms that perform similarity checking
	- Similarity Checker Container (SCC): A provider of similarity checkers that contains similarity checker(s)

- Models
	- Model source file: A file containing parts of or information about a model. These are the original files of a model provided as input to parsing methods, which parse an in-memory version of the model. These files typically only contain direct contents of the model, i.e. the contents of the actual model that are declared directly in the model. If the model has any outside dependencies, typically only references to those dependencies are stored.
	- Model source file directory: A directory that contains all model source files belonging to a (and only one) model. Passing a model source directory to a model parsing method will result in an in-memory model, which consists of the parsed contents of model source files. Model source files therein may or may not be nested in further directories.
	- Model source parent directory: A directory that contains model source file directories of one or more models. Each nested model source file directory should be passed to model parsing methods separately, if in-memory models to be parsed should be separate.
	- Model resource content: An in-memory representation of a model element, which is a part of a model.
	- Model resource: An in-memory representation of a model (usually in form of a Resource instance). If multiple model resources are involved, their in-memory representation may be an object that aggregates individual model resources (such as a ResourceSet instance). Model resources consist of model resource contents and may include other metadata.
	- Parsed model file / model resource file: A file containing parts of or information about a parsed model. These files persist the parsed in-memory models, allowing them to be loaded for speeding up tests. If all necessary parsed model files for a model are present, parsing the model from scratch is not necessary, as the resulting parsed model should have the same content as parsed model files. Keep in mind that the order of contents may differ, if model parsing methods are not fully deterministic.

- Model comparison
	- Model comparison: Thorough comparison of 2 or more model resources based on their contents. The result of this operation is a model comparison result.
	- Model comparison result: (typically) an object, which yields all details about the comparison process, such as the concrete differences (if any) and which model resource contents were matched. It is more elaborate compared to a similarity checking result, as it provides more than whether multiple objects are similar, such as the means to locate the detected difference.