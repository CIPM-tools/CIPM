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
 * Kieker-based implementations of the measurements reader.
 *
 * <p>This package provides readers that integrate with Kieker monitoring framework:</p>
 * <ul>
 *   <li>{@link cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader} -
 *       Reads measurements from Kieker log files (directory with kieker.map)</li>
 *   <li>{@link cipm.consistency.measurements.reader.kieker.KiekerStreamMeasurementsReader} -
 *       Reads measurements from a Kieker TCP stream in real-time</li>
 *   <li>{@link cipm.consistency.measurements.reader.kieker.KiekerRecordConverter} -
 *       Utility to convert Kieker records to EMF-based measurement records</li>
 * </ul>
 *
 * @author Manar Mazkatli
 */
package cipm.consistency.measurements.reader.kieker;
