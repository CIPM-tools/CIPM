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
 * Measurements reader API for CIPM.
 *
 * <p>This package provides an extensible framework for reading measurement records
 * from various sources into the EMF-based measurement model.</p>
 *
 * <h2>Main Components</h2>
 * <ul>
 *   <li>{@link cipm.consistency.measurements.reader.MeasurementsReader} -
 *       Main interface for reading measurements</li>
 *   <li>{@link cipm.consistency.measurements.reader.MeasurementsReaderRegistry} -
 *       Central registry for reader implementations</li>
 *   <li>{@link cipm.consistency.measurements.reader.MeasurementsReaderException} -
 *       Exception for reading errors</li>
 * </ul>
 *
 * <h2>Sub-packages</h2>
 * <ul>
 *   <li>{@link cipm.consistency.measurements.reader.file} - JSON file reader</li>
 *   <li>{@link cipm.consistency.measurements.reader.kieker} - Kieker integration readers</li>
 * </ul>
 *
 * <h2>Custom Reader Implementation</h2>
 * <p>To implement a custom reader:</p>
 * <ol>
 *   <li>Implement {@link cipm.consistency.measurements.reader.MeasurementsReader}</li>
 *   <li>Register it with {@link cipm.consistency.measurements.reader.MeasurementsReaderRegistry#register(MeasurementsReader)}</li>
 * </ol>
 *
 * @author Manar Mazkatli
 */
package cipm.consistency.measurements.reader;
