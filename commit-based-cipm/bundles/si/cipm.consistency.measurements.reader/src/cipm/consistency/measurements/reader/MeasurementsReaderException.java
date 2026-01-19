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
package cipm.consistency.measurements.reader;

/**
 * Exception thrown when reading measurements fails.
 *
 * <p>This exception is used by {@link MeasurementsReader} implementations
 * to indicate errors during the reading process, such as:</p>
 * <ul>
 *   <li>File not found or not accessible</li>
 *   <li>Invalid file format or corrupted data</li>
 *   <li>Network connection failures (for streaming readers)</li>
 *   <li>Unsupported record types</li>
 * </ul>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReader
 */
public class MeasurementsReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    public MeasurementsReaderException(String message) {
        super(message);
    }

    public MeasurementsReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeasurementsReaderException(Throwable cause) {
        super(cause);
    }
}
