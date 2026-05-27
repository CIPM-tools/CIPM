# Fluent API for PCM

This plug-in contains the implementation of the fluent API generation for PCM and considers the [(EMF-based) PCM metamodel](https://github.com/PalladioSimulator/Palladio-Core-PCM/blob/master/bundles/org.palladiosimulator.pcm/model/pcm.ecore). The generated fluent api for PCM will consider the entirety of the 'pcm' package.

The fluent API class within this plug-in is called [FluentPcmAPI](./src-gen/cipm.consistency.fluentapi.pcm.api/FluentPcmAPI.java). Note that this class is not present in this plug-in by default will be generated from the fluent API model.

For exemplary usage, refer to the test cases within the [test package of this plug-in](./src/cipm.consistency.fluentapi.pcm.test). For further details, refer to the [base plug-in for fluent API generation](../cipm.consistency.fluentapi/README.md).
