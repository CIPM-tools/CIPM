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
package cipm.consistency.measurements.reader.kieker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.reader.MeasurementsReader;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.factory.CachedRecordFactoryCatalog;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.DefaultValueDeserializer;
import kieker.common.util.registry.reader.GetValueAdapter;
import kieker.common.util.registry.reader.ReaderRegistry;

/**
 * Implementation of {@link MeasurementsReader} that reads measurements from a Kieker TCP stream.
 *
 * <p>This reader opens a TCP server socket and receives Kieker monitoring records
 * in real-time from a running application instrumented with Kieker. The received
 * records are converted to EMF-based measurement records using {@link KiekerRecordConverter}
 * and passed to a callback for processing.</p>
 *
 * <p>This reader is designed for runtime monitoring scenarios where measurements
 * are streamed continuously from a monitored application. It supports the Kieker
 * binary TCP protocol.</p>
 *
 * <h2>Streaming Mode</h2>
 * <p>Unlike file-based readers, this reader operates in streaming mode only.
 * The batch methods {@link #readRecords(String)} and {@link #readRecordsIntoBlock(String, MeasurementsBlock)}
 * are not supported and will throw an exception.</p>
 *
 * <h2>Source Format</h2>
 * <p>The source can be specified in the following formats:</p>
 * <ul>
 *   <li>{@code tcp://host:port} - TCP connection string (port extracted)</li>
 *   <li>{@code 5678} - Port number only (default: 5678)</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * KiekerStreamMeasurementsReader reader = new KiekerStreamMeasurementsReader(5678);
 *
 * // Start streaming and process records as they arrive
 * reader.startStreaming("tcp://localhost:5678", record -&gt; {
 *     measurementsBlock.getRecords().add(record);
 *     // Trigger CPR propagation...
 * });
 *
 * // ... later, stop streaming
 * reader.stopStreaming();
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This reader runs a dedicated thread for receiving data. The callback
 * is invoked from this thread, so implementations should be thread-safe.</p>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReader
 * @see KiekerRecordConverter
 * @see KiekerFileMeasurementsReader
 */
public class KiekerStreamMeasurementsReader implements MeasurementsReader {

    private static final Logger LOGGER = Logger.getLogger(KiekerStreamMeasurementsReader.class.getName());
    private static final String ID = "kieker-stream-reader";
    private static final String NAME = "Kieker Stream Measurements Reader";

    private static final int INT_BYTES = AbstractMonitoringRecord.TYPE_SIZE_INT;
    private static final int LONG_BYTES = AbstractMonitoringRecord.TYPE_SIZE_LONG;
    private static final Charset ENCODING = Charset.forName("UTF-8");
    private static final int DEFAULT_PORT = 5678;
    private static final int BUFFER_SIZE = 65536;

    private final ReaderRegistry<String> readerRegistry = new ReaderRegistry<>();
    private final CachedRecordFactoryCatalog recordFactories = new CachedRecordFactoryCatalog();

    private ServerSocketChannel serverChannel;
    private Thread streamingThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private MeasurementRecordCallback callback;
    private int port = DEFAULT_PORT;

    /**
     * Creates a new KiekerStreamMeasurementsReader with the default port (5678).
     */
    public KiekerStreamMeasurementsReader() {
        this(DEFAULT_PORT);
    }

    /**
     * Creates a new KiekerStreamMeasurementsReader with the specified port.
     *
     * @param port the TCP port to listen on
     */
    public KiekerStreamMeasurementsReader(int port) {
        this.port = port;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canHandle(String source) {
        // Source should be in format "tcp://host:port" or just a port number
        if (source == null || source.isEmpty()) {
            return false;
        }
        return source.startsWith("tcp://") || source.matches("\\d+");
    }

    @Override
    public List<MeasurementRecord> readRecords(String source) throws MeasurementsReaderException {
        throw new MeasurementsReaderException("Streaming reader does not support batch reading. Use startStreaming() instead.");
    }

    @Override
    public int readRecordsIntoBlock(String source, MeasurementsBlock block) throws MeasurementsReaderException {
        throw new MeasurementsReaderException("Streaming reader does not support batch reading. Use startStreaming() instead.");
    }

    @Override
    public boolean supportsStreaming() {
        return true;
    }

    @Override
    public void startStreaming(String source, MeasurementRecordCallback callback) throws MeasurementsReaderException {
        if (running.get()) {
            throw new MeasurementsReaderException("Streaming is already running");
        }

        this.callback = callback;

        // Parse port from source
        if (source != null && !source.isEmpty()) {
            if (source.startsWith("tcp://")) {
                String portStr = source.substring(source.lastIndexOf(':') + 1);
                this.port = Integer.parseInt(portStr);
            } else if (source.matches("\\d+")) {
                this.port = Integer.parseInt(source);
            }
        }

        running.set(true);

        streamingThread = new Thread(() -> {
            try {
                runServer();
            } catch (IOException e) {
                LOGGER.error("Error in streaming thread", e);
            }
        }, "KiekerStreamReader-" + port);

        streamingThread.start();
        LOGGER.info("Started Kieker streaming reader on port " + port);
    }

    @Override
    public void stopStreaming() {
        running.set(false);

        if (serverChannel != null) {
            try {
                serverChannel.close();
            } catch (IOException e) {
                LOGGER.warn("Error closing server channel", e);
            }
        }

        if (streamingThread != null) {
            streamingThread.interrupt();
            try {
                streamingThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        LOGGER.info("Stopped Kieker streaming reader");
    }

    private void runServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(port));

        LOGGER.info("Kieker stream reader listening on port " + port);

        while (running.get()) {
            try {
                SocketChannel clientChannel = serverChannel.accept();
                if (clientChannel != null) {
                    LOGGER.info("Client connected from " + clientChannel.getRemoteAddress());
                    handleClient(clientChannel);
                }
            } catch (IOException e) {
                if (running.get()) {
                    LOGGER.error("Error accepting client connection", e);
                }
            }
        }
    }

    private void handleClient(SocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            while (running.get() && clientChannel.isOpen()) {
                buffer.clear();
                int bytesRead = clientChannel.read(buffer);

                if (bytesRead == -1) {
                    break;
                }

                if (bytesRead > 0) {
                    buffer.flip();
                    processBuffer(buffer);
                }
            }
        } catch (IOException e) {
            LOGGER.debug("Client disconnected", e);
        } finally {
            try {
                clientChannel.close();
            } catch (IOException e) {
                LOGGER.warn("Error closing client channel", e);
            }
        }
    }

    private void processBuffer(ByteBuffer buffer) {
        while (buffer.remaining() >= INT_BYTES) {
            buffer.mark();

            int clazzId = buffer.getInt();

            if (clazzId == -1) {
                // Registry entry
                if (!registerRegistryEntry(buffer)) {
                    buffer.reset();
                    break;
                }
            } else {
                // Monitoring record
                if (!deserializeRecord(clazzId, buffer)) {
                    buffer.reset();
                    break;
                }
            }
        }
    }

    private boolean registerRegistryEntry(ByteBuffer buffer) {
        if (buffer.remaining() < (INT_BYTES + INT_BYTES)) {
            return false;
        }

        int id = buffer.getInt();
        int stringLength = buffer.getInt();

        if (buffer.remaining() < stringLength) {
            return false;
        }

        byte[] strBytes = new byte[stringLength];
        buffer.get(strBytes);
        String string = new String(strBytes, ENCODING);

        readerRegistry.register(id, string);
        return true;
    }

    private boolean deserializeRecord(int clazzId, ByteBuffer buffer) {
        if (buffer.remaining() < LONG_BYTES) {
            return false;
        }

        long loggingTimestamp = buffer.getLong();

        String recordClassName = readerRegistry.get(clazzId);
        if (recordClassName == null) {
            LOGGER.warn("Unknown class ID: " + clazzId);
            return true; // Skip unknown records
        }

        IRecordFactory<? extends IMonitoringRecord> recordFactory = recordFactories.get(recordClassName);
        if (recordFactory == null) {
            LOGGER.warn("No factory for record class: " + recordClassName);
            return true;
        }

        if (buffer.remaining() < recordFactory.getRecordSizeInBytes()) {
            return false;
        }

        try {
            IMonitoringRecord kiekerRecord = recordFactory.create(
                    DefaultValueDeserializer.create(buffer, new GetValueAdapter<>(readerRegistry)));
            kiekerRecord.setLoggingTimestamp(loggingTimestamp);

            // Convert to EMF record and notify callback
            MeasurementRecord emfRecord = KiekerRecordConverter.convert(kiekerRecord);
            if (emfRecord != null && callback != null) {
                callback.onRecord(emfRecord);
            }

        } catch (RecordInstantiationException e) {
            LOGGER.warn("Failed to create record: " + recordClassName, e);
        }

        return true;
    }
}
