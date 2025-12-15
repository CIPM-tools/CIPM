/*******************************************************************************
 * Copyright (c) 2025
 * Karlsruhe Institute of Technology (KIT)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Manar Mazkatli - initial API and implementation
 ******************************************************************************/
/**
 * Tests for the measurements reader implementations.
 *
 * <p>This package contains JUnit 5 tests for:</p>
 * <ul>
 *   <li>{@link cipm.consistency.measurements.reader.tests.FileMeasurementsReaderTest} -
 *       Unit tests for JSON file reading</li>
 *   <li>{@link cipm.consistency.measurements.reader.tests.KiekerRecordConverterTest} -
 *       Unit tests for Kieker record conversion</li>
 *   <li>{@link cipm.consistency.measurements.reader.tests.MeasurementsReaderRegistryTest} -
 *       Unit tests for the reader registry</li>
 *   <li>{@link cipm.consistency.measurements.reader.tests.KiekerFileMeasurementsReaderIntegrationTest} -
 *       Integration tests using real Kieker log files from Calibration module</li>
 * </ul>
 *
 * <h2>Test Data</h2>
 * <p>Integration tests use the Kieker monitoring data from:</p>
 * <pre>
 * bundles/Calibration/CIPM-Pipeline/cipm.consistency.root/
 *   cipm.consistency.tools.evaluation.accuracy/kieker/
 * </pre>
 *
 * @author Manar Mazkatli
 */
package cipm.consistency.measurements.reader.tests;
