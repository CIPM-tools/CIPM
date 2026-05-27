# Fluent API

This is the base plug-in of the fluent API generation, which contains the elements that the generation of fluent APIs for individual (EMF-based) metamodels require. The elements within this plug-in are mostly metamodel agnostic and must be extended with the means to access and work with concrete metamodels. It is intended to have one fluent API plug-in per concrete metamodel (or a sub-metamodel thereof).

This plug-in considers the same setup as the [CIPM repository](../../../README.md).

## Introduction

The purpose of the fluent API is to facilitate building models from their (EMF-based) metamodels. To this end, the fluent API offers methods that use dynamic EMF under the hood, in order to instantiate model elements and modify their features. In doing so, fluent api hides the complication of dynamic EMF from outside and provides simpler to understand methods. In a sense, the fluent API implements an advanced builder pattern for building models.

The core concept of the fluent API is to make dynamic model building more practical via chainable method calls, bookmarking (marking) certain model elements and deferring some model building steps till certain model elements are marked. The latter enables maintaining the flow of the model building in the face of dependencies between model elements, which require certain model elements to exist before others.

Exemplary usage of the fluent API are present under test packages within the plug-ins of the fluent API generation implementations of individual metamodels.

## Plug-in Structure

This plug-in, as well as those of the extending plug-ins, have a package-based structure with the base package having the same name as the plug-in `cipm.consistency.fluentapi.<someMetamodelName>`, where `<someMetamodelName>` should be replaced with the name of the concrete metamodel, or be left out for the base plug-in.

The fluent api generation code is inside the packages within the [src](./src) directory:

- `cipm.consistency.fluentapi.<someMetamodelName>.builder`: Contains the test class that generates the fluent api model
- `cipm.consistency.fluentapi.<someMetamodelName>.extensions`: Contains some static classes with functions that the generated fluent api code uses (extension classes)
- `cipm.consistency.fluentapi.<someMetamodelName>.gen.*`: Contains the logic to generate the fluent api model. Currently only present in the base plug-in.
- `cipm.consistency.fluentapi.<someMetamodelName>.metamodel`: Contains the means to access and work with concrete (EMF-based) metamodels
- `cipm.consistency.fluentapi.<someMetamodelName>.postprocessor`: Contains classes that can be used to post-process the generated fluent api model, which adjust the generated fluent api model after its generation
- `cipm.consistency.fluentapi.<someMetamodelName>.test`: Contains tests for the extension classes and/or the generated fluent api code. This is an optional package and can be removed.
- `cipm.consistency.fluentapi.<someMetamodelName>.test.metamodel`: Contains tests that analyse the generated fluent api model code regarding its generated elements (such as the methods). This is an optional package and can be removed.

The fluent api code will be generated within the [src-gen](./src-gen) directory, following the typical EMF code generation scheme. In most cases, only the `cipm.consistency.fluentapi.<someMetamodelName>.api` package is relevant from outside. It contains the fluent api class `Fluent<someMetamodelName>API`, which is the class that should be used as a facade of the fluent api.

## Fluent API Generation

Under normal circumstances, the following steps should generate the fluent api code in Eclipse IDE:

1) Navigate to the `"cipm.consistency.fluentapi.<someMetamodelName>.builder"` package, where `<someMetamodelName>` should be replaced with the name of the concrete metamodel
2) Run the test case in the builder test class (currently called `Fluent<someMetamodelName>APIBuilder`), which inherits the template test case from the `cipm.consistency.fluentapi.builder.FluentAPIAbstractBuilder` class using Eclipse IDE: "Run As > JUnit Plug-in Test"
3) Navigate to the `cipm.consistency.fluentapi.<someMetamodelName>.builder/metamodel` folder
4) Open the `.genmodel` file (currently named `<someMetamodelName>-fluentapi.genmodel`) and generate the fluent api model using Eclipse IDE: "Right Click on the only node > Generate Model Code". The fluent api files should be generated under the `src-gen` folder
5) (Optional) Refresh and clean the plug-in, then run the tests therein (excluding the test class from 2) )

## Implementing Fluent API for Further Metamodels

Under normal circumstances, the following steps should suffice to implement the fluent API generation for an individual metamodel:

1) Create a new plug-in `"cipm.consistency.fluentapi.<someMetamodelName>.builder"` package, where `<someMetamodelName>` should be replaced with the name of the concrete metamodel
2) Add `"cipm.consistency.fluentapi"` as a required bundle, as well as other plug-ins that are needed for the concrete metamodel
3) Extend the classes `cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter` and `cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelProvider` accordingly
4) Extend the test class `cipm.consistency.fluentapi.builder.FluentAPIAbstractBuilder` accordingly

If certain fluent API methods should be generated specifically for the concrete metamodel (such as convenience methods), consider implementing post-processors and using them in the test class from 4).

## Limitations

The current implementation of the fluent API has the following (non-exhaustive) list of limitations:

- Fluent API generation is implemented in Java 11, which may or may not conform later Java versions
    - The generated fluent api code also conforms Java 11

- Names of the metamodel elements have to be unique within the metamodel; i.e. having 2 metamodel elements "namespace1.elementName" and "namespace2.elementName" is not foreseen, only one metamodel element with the name "elementName" may exist
    - Not abiding this may cause issues with the fluent api model generation, which requires explicit handling

- No generic type support in metamodels, i.e. fluent api will not account for type parameters in metamodel elements, such as the "T" in `"Class<T>"`

- No (explicit) support for metamodel constraints and invariants (such as OCL constraints)
    - All constraints have to be explicitly addressed in the fluent api model generation for individual metamodels
