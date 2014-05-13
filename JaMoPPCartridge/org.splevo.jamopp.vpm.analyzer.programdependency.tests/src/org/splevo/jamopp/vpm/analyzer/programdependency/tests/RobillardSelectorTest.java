/*******************************************************************************
 * Copyright (c) 2014
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.splevo.jamopp.vpm.analyzer.programdependency.tests;

import static org.splevo.jamopp.vpm.analyzer.programdependency.tests.TestUtil.assertDependencyCount;
import static org.splevo.jamopp.vpm.analyzer.programdependency.tests.TestUtil.assertNodeCount;

import org.junit.BeforeClass;
import org.junit.Test;
import org.splevo.vpm.analyzer.VPMAnalyzerResult;
import org.splevo.vpm.analyzer.graph.VPMGraph;

/**
 * Unit test to prove the dependency analysis using the robillard reference selector.
 */
public class RobillardSelectorTest {

    private static final String BASE_PATH = "testcode/robillard/";

    /**
     * Prepare the test. Initializes a log4j logging environment.
     */
    @BeforeClass
    public static void setUp() {
        TestUtil.initLogging();
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testMethodCall() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "MethodCall/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 3);
        assertDependencyCount(result, 1);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testReadField() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "ReadField/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 4);
        assertDependencyCount(result, 2);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testNestedStatements() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "NestedStatements/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 10);
        assertDependencyCount(result, 5);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testWriteField() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "WriteField/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 4);
        assertDependencyCount(result, 2);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testNewConstructorCall() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "NewConstructorCall/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 2);
        assertDependencyCount(result, 1);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testInstanceOf() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "InstanceOf/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 2);
        assertDependencyCount(result, 1);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testClassCreate() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "ClassCreate/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 2);
        assertDependencyCount(result, 1);
    }

    /**
     * Test method to detect changes in the class and package declarations.
     *
     * @throws Exception
     *             Identifies a failed diffing.
     */
    @Test
    public void testClassCast() throws Exception {

        VPMGraph graph = TestUtil.prepareVPMGraph(BASE_PATH + "ClassCast/");
        VPMAnalyzerResult result = TestUtil.analyze(graph);

        assertNodeCount(graph, 2);
        assertDependencyCount(result, 1);
    }
}
