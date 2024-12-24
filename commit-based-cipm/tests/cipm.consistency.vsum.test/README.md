# The cipm.consistency.vsum.test Plugin

This plugin provides test cases for the prototypical implementation of the commit-based CIPM approach.

## Tests for the TeaStore and How to run them

The plugin contains tests within the `TeaStoreCITest.java` file for the [TeaStore](https://github.com/DescartesResearch/TeaStore) case. The test cases differentiate between the integration of the initial commit and the propagation of further commits building upon the initial commit. Furthermore, it divides the history of the TeaStore in intervals based on its versions. As a result, there are test cases for integrating the versions 1.0, 1.1, 1.2, 1.2.1, 1.3, and 1.3.1 as initial commits. Because only one version can be integrated at once and some test cases depend on other ones, all test cases are disabled per default, and only one case for the integration shall be enabled. Afterward, the changes of commits between the aforementioned versions can be propagated. One test case allows to propagate the changes of every single commit while another test case propagates all changes of the commits at once. After the integration of the initial commit, the corresponding following commits shall be propagated, i. e., after the integration of, e. g., version 1.2, the changes between version 1.2 and 1.2.1 shall be propagated. To preserve the order of the propagation and to avoid potential `OutOfMemoryErrors`, it is recommended to enable and run only one test case in one test run. A test case is enabled by removing the `Disabled` annotation of JUnit 5 from the test case or by converting the annotation into a comment.

__Please note: in Eclipse, run the test as `JUnit Plug-in Test`. Using a `JUnit` configuration, it will fail.__

Every test case performs several checks after the propagation of the changes to evaluate the result. The checks are divided into a dependent part directly executed after the propagation and an independent part, which shall be executed independent of the propagation because it loads multiple models into the working memory and can cause an `OutOfMemoryError` if it is executed with the propagation. Therefore, there are two test cases for each integration / propagation. The first test case executes the propagation and dependent evaluation. The second test case executes the independent evaluation. After the propagation has been performed, the independent evaluation can be run by removing the `Disabled` annotation and disabling the first test case.

Additional files for the TeaStore-specific test execution are located in the `teastore-exec-files` directory.

## Tests for TEAMMATES and How to run them

Beside the TeaStore, the plugin also contains tests within the `TEAMMATESCITest.java` file for the [TEAMMATES](https://github.com/TEAMMATES/teammates) case. Similar to the TeaStore tests, there is one test case for the integration of the initial commit 64842 (conforming to version 8.0.0-rc.0) while further test cases propagate the changes between two commits for four commits overall. Additional files for the TEAMMATES-specific test execution are located in the `teammates-exec-files` directory.
