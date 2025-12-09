# Introduction

To evaluate the CIPM extention I introduced in my Practice of Research (Praxis der Forschung, PdF) paper, I ran an experiment which I described in my paper. It is implemented in "commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/pcm/experiment/PcmToJavaChangePropagationTest.java"

# How to Run the Experiment

1) Install and setup CIPM workspace in accordance with [CIPM Readme](https://github.com/CIPM-tools/CIPM/blob/apm-to-code-propagation/README.md)

2) Ensure that TEAMMATES tests (TEAMMATESCITestController.java) "commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/java/TEAMMATESCITestController.java" run as intended. Use the "Run as > JUnit plug-in test" option, as the test will otherwise fail according to [VSUM tests Readme](https://github.com/CIPM-tools/CIPM/blob/apm-to-code-propagation/commit-based-cipm/tests/cipm.consistency.vsum.test/README.md). Note that running TEAMMATES tests is not mandatory for the experiment. If data from TEAMMATES tests already exists, skip 2) - 5).

3) Navigate to the TEAMMATES change generating test (TeammatesChangeGeneratingTest.java) "commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/pcm/experiment/TeammatesChangeGeneratingTest.java" and enable the test class TeammatesChangeGeneratingTest by commenting out or removing the "Disabled" annotation.

4) Delete the target folder "commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/target" folder, if it exists

5) Run the TeammatesChangeGeneratingTest using the "Run as > JUnit plug-in test" (same as in 2) ). Running this test class is mandatory for the experiment, as it generates the models and changes that are required by the experiment.

6) After 5), check the target folder and make sure that all artefacts for TEAMMATES tests are generated as intended. Additionally, ensure that "target/TEAMMATESCITest-X-HASH" folders each contain a changes folder "TEAMMATESCITest-X-HASH/changes" with "zChanges.changes" files where z = {java, pcm, im}.

7) Run the PCM propagation test (PcmToJavaChangePropagationTest.java) "commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/pcm/experiment/PcmToJavaChangePropagationTest.java". Use the "Run as > JUnit plug-in test" option to run the test class. This should replicate the experiment run from the paper.

# TEAMMATES Tests

TEAMMATES Tests in CIPM target the (TEAMMATES repository)[https://github.com/TEAMMATES/teammates] and are used for evaluation as well as testing purposes for the CIPM approach. There are 2 types of TEAMMATES tests, whose main difference is their purpose: Integration tests and propagation tests. Integration tests start with empty models (hence they consider no old commit) and build those empty models through code change propagation. Propagation tests build upon existing models and propagate the code changes introduced by the new commit (hence they consider an old and a new commit).

# Generated Data

The data from my experiment run (PCM -> Java change propagation) includes many model files, which are serialised Resource instances containing the models that were parsed throughout the experiment, as well as the model changes that were captured. Since I mentioned the data from TEAMMATES integration test (in ./commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/java/TEAMMATESCITestController.java) in my paper, I additionally included the results from the TEAMMATES tests (Java -> PCM change propagation). Besides model files, there are also files in JSON format containing various measurements from the tests / experiment run, which are described in the following.

## Model and Change Files

Most model files appear in both TEAMMATES tests (Java -> PCM change propagation) and my experiment (PCM -> Java change propagation). Note that the change propagation these models were subjected to depends on what generated them (i.e. TEAMMATES tests or my experiment):

- Model changes (under "./changes"):
	- "javaChanges.changes": Contains the Java code model changes generated during change propagation.
	- "pcmChanges.changes": Contains the PCM changes generated during change propagation.
	- "imChanges.changes": Contains the IM changes generated during change propagation.

- Java code models (under "./code"):
	- "Java.javaxmi": Contains the Java code model parsed for the new commit. The model within this file is not modified by the change propagation.
	- "parsed-X-HASH": Contains the Java code model parsed for the commit HASH for the propagation number X. The model within this file is not modified by the change propagation. Only appears in TEAMMATES tests (Java -> PCM change propagation).
	- "vsum.code.xmi": Contains the propagated Java code model. Only appears in TEAMMATES tests (Java -> PCM change propagation).

- Instrumentation models (under "./im"):
	- "imm.imm": Contains the propagated IM model.

- PCM (under "./pcm"):
	- "Allocation.allocation": Contains the propagated allocation model.
	- "Repository.repository": Contains the propagated repository model.
		- "Repository-X-HASH": Similar to the "parsed-X-HASH" files under "Java code models". Only appears in TEAMMATES tests (Java -> PCM change propagation).
	- "ResourceEnvironment.resourceenvironment": Contains the propagated resource environment model.
	- "System.system": Contains the propagated system model.
	- "Usage.usagemodel": Contains the propagated usage model.

- VSUM (under "./vsum/vsum"):
	- "correspondences.correspondence": Contains the propagated correspondence model.
	- "models.models": Contains the list of paths to model files, which were a part of the propagation.

## TEAMMATES Test Data (Java -> PCM Change Propagation)

The files generated by the TEAMMATES tests (in "./commit-based-cipm/tests/cipm.consistency.vsum.test/src/cipm/consistency/vsum/test/java/TEAMMATESCITestController.java") in my CIPM branch (apm-to-code-propagation)[https://github.com/CIPM-tools/CIPM/tree/apm-to-code-propagation] are similar to the files, which are generated by the TEAMMATES tests in the current version of CIPM.

TEAMMATES tests save the data they generate under the target folder at "./commit-based-cipm/tests/cipm.consistency.vsum.test/target":


- "TEAMMATES": The local repository clone of the TEAMMATES repository that is used in TEAMMATES tests. Note that TEAMMATES tests perform GIT checkout operations on this local repository clone, meaning that the final state of the repository may not reflect the commit, at which it was initially cloned.
	- Irrelevant for experiment run, provided for completeness

- "TEAMMATESCITEST": Contains the files for the models, which are to be propagated during TEAMMATES tests (Java -> PCM change propagation). Note that these models are propagated throughout all TEAMMATES tests, meaning that the contents of this folder are the result of all Java -> PCM change propagations from TEAMMATES tests (i.e. from the integration test to the final propagation test).
	- Irrelevant for experiment run, provided for completeness

- "TEAMMATESCITEST-X-HASH": Contains the files for the models and model changes for a specific TEAMMATES test (Java -> PCM change propagation), where X is the number of the propagation (X=1 for the integration test, X>1 for propagation tests) and HASH is the short version of the new TEAMMATES commit. The file layout is described in "Model and Change Files". Java changes are the original changes computed by CIPM, which lead to the Java code model in the "vsum.code.xmi" file. PCM and IM changes are consequences of the (original) Java changes, which are applied to PCM and IM respectively. Additionally contains a "evaluationData.json" file, which contains various measurements from the respective TEAMMATES test (Java -> PCM change propagation).
	- "TEAMMATESCITest-1-6484257": Model files for the integration test
	- "TEAMMATESCITest-2-48b67ba": Model files for the first propagation test (irrelevant for experiment run, provided for completeness)
	- "TEAMMATESCITest-3-83f518e": Model files for the second propagation test (irrelevant for experiment run, provided for completeness)
	- "TEAMMATESCITest-4-f33d0bc": Model files for the third propagation test (irrelevant for experiment run, provided for completeness)
	- "TEAMMATESCITest-5-ce4463a": Model files for the fourth propagation test (irrelevant for experiment run, provided for completeness)


The target folder itself also contains the "commitHistoryEvaluationData.json" file, which aggregates all contents of the "evaluationData.json" files from TEAMMATES tests (Java -> PCM change propagation)

## Experiment Data (PCM -> Java Change Propagation)

The data of the experiment run from my paper is under the "Teammates-Experiment-1" folder, whose layout is as follows:

- "./": Top-level folder
	- "copied": Model file copies for the old and the new commit from the TEAMMATES integration test.
		- "new": Model file copies for the new commit 648425746bb9434051647c8266dfab50a8f2d6a3. Similar layout as "TEAMMATESCITEST-X-HASH" folders.
		- "old": Empty model files and copies of changes propagated during the Java -> PCM change propagation. Similar layout as "TEAMMATESCITEST-X-HASH" folders.
	- "propagated": Propagated models and changes propagated during the PCM -> Java change propagation. Similar layout as "TEAMMATESCITEST-X-HASH" folders.
	- "experimentResults.json": Contains various measurements from my experiment run. Also contains some computation results on the TEAMMATES integration test as control.

# Data Used in My Paper

The location of each piece of experiment data I have presented in my paper under "4.1 Setup" is as follows:

- TEAMMATES integration test
	- Original (Java) code change count: "Teammates-Experiment-1/experimentResults.json" as "originalJavaChangeCount"
	- Consequential PCM change count: "Teammates-Experiment-1/experimentResults.json" as "originalPcmChangeCount"
	- Consequential IM change count: "Teammates-Experiment-1/experimentResults.json" as "originalImChangeCount"
	- Execution time of Java -> PCM change propagation: "TEAMMATESCITest-1-6484257/evaluationData.json" as "changePropagationTime"
	- Accuracy for Java code model: "Teammates-Experiment-1/experimentResults.json" as "jaccardCoefficientForJavaModelInJavaToPcmPropagation/Jaccard coefficient"
	- Accuracy for IM: "Teammates-Experiment-1/experimentResults.json" as "fOneScoreForImInJavaToPcmPropagation/fScoreServiceInstrumentationPoints"
	- Automaticity of Java -> PCM change propagation: CIPM paper
- Experiment run
	- PCM change count: "Teammates-Experiment-1/experimentResults.json" as "propagatedPcmChangeCount"
	- Execution time of PCM -> Java change propagation: "Teammates-Experiment-1/experimentResults.json" as "propagationTimeWithoutUserInteractionsInMillis"
	- Consequential code change count: "Teammates-Experiment-1/experimentResults.json" as "propagatedJavaChangeCount"
	- Consequential IM change count: "Teammates-Experiment-1/experimentResults.json" as "propagatedImChangeCount"
	- |UI_All| (total user interaction count): "Teammates-Experiment-1/experimentResults.json" as "numberOfTriggeredUserInteractions" 
	- |UI_FA| (realistically fully automated user interaction count): "Teammates-Experiment-1/experimentResults.json" as "numberOfTriggeredRealisticallyFullyAutomaticUserInteractions" 
	- |UI_M| (realistically manual user interaction count): "Teammates-Experiment-1/experimentResults.json" as "numberOfTriggeredRealisticallySemiAutomaticNonInterceptedUserInteractions" 
	- Automaticity of PCM -> Java change propagation: "Teammates-Experiment-1/experimentResults.json" as "automaticityDegree"
	- Accuracy for PCM: "Teammates-Experiment-1/experimentResults.json" as "jaccardCoefficientForPcmRepositoryInPcmToJavaPropagation"
	- Accuracy for IM: "Teammates-Experiment-1/experimentResults.json" as "fOneScoreForImInPcmToJavaPropagation/fScoreServiceInstrumentationPoints"
	- Accuracy for Java code model: "Teammates-Experiment-1/experimentResults.json" as "jaccardCoefficientForJavaModelInPcmToJavaPropagation/Jaccard coefficient"

About IMs:
	- Since SEFF reconstruction was partially disabled for Java -> PCM change propagation, only service instrumentation points were present

About the automaticity of the experiment run:
	- See "Teammates-Experiment-1/experimentResults.json/triggeredUserInteractionIDs" for what user interactions triggered during experiment run
	- See "Teammates-Experiment-1/experimentResults.json/pcmLogger/serialisedEntries" for user interaction log details triggered during experiment run
	- See "Teammates-Experiment-1/experimentResults.json/testIndependentConflictResolutionStrategyIDs" for what strategies the original UIM used
	- See "Teammates-Experiment-1/experimentResults.json/testSpecificConflictResolutionStrategyIDs" for what strategies the modified UIM used

About the data from the TEAMMATES integration test case, including the full automaticity and accuracy of Java -> PCM propagation:
	- Also see "TEAMMATESCITest-1-6484257/evaluationData.json"
	- Also see CIPM paper

CIPM paper: "Manar Mazkatli et al. Continuous Integration of Architectural Performance Models with Parametric Dependencies – The CIPM Approach. Tech. rep. Institut für Informationssicherheit und Verlässlichkeit (KASTEL), 2025. doi: 10.5445/IR/1000151086/v3"