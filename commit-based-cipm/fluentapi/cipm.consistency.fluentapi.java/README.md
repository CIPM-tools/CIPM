# Fluent API for Java

This plug-in contains the implementation of the fluent API generation for Java and considers the (EMF-based) [JaMoPP metamodel for Java](https://github.com/MDSD-Tools/TheExtendedJavaModelParserAndPrinter). The structure of this plug-in reflects the structure of the [base plug-in for fluent API generation](../cipm.consistency.fluentapi/README.md).

The fluent API class within this plug-in is called [FluentJavaAPI](./src-gen/cipm.consistency.fluentapi.java.api/FluentJavaAPI.java). Note that this class is not present in this plug-in by default will be generated from the fluent API model.

Note that the Layout package and the relevant JaMoPP features are excluded in this implementation, in order to keep the generated fluent API code to the metamodel elements that are directly related to Java.

For exemplary usage, refer to the test cases within the [test package of this plug-in](./src/cipm.consistency.fluentapi.java.test). For further details, refer to the [base plug-in for fluent API generation](../cipm.consistency.fluentapi/README.md).
