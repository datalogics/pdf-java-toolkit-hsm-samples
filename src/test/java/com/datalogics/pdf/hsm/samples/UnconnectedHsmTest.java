/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.datalogics.pdf.hsm.samples.mock.AbstractHsmManager;
import com.datalogics.pdf.hsm.samples.util.LogRecordListCollector;
import com.datalogics.pdf.security.HsmManager;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Unit tests simulating an unconnected HSM device.
 */
public class UnconnectedHsmTest {
    static final String FILE_NAME = "SignedField.pdf";
    static final String MESSAGE = "HsmManager is not connected to HSM device.";

    private static URL inputUrl;
    private static URL outputUrl;
    private static File outputFile;
    private static HsmManager unconnectedHsmManager;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Set up to try and sign a document with an unconnected HSM device.
     *
     * @throws IOException an I/O operation failed or was interrupted
     */
    @BeforeClass
    public static void setUp() throws IOException {
        inputUrl = HsmSignDocument.class.getResource(HsmSignDocument.INPUT_UNSIGNED_PDF_PATH);
        outputFile = SampleTest.newOutputFileWithDelete(FILE_NAME);

        // The complete file name will be set in the HsmSignDocument class.
        outputUrl = outputFile.toURI().toURL();

        // Create an unconnected HsmManager
        unconnectedHsmManager = new UnconnectedHsmManager();
    }

    @Test
    public void illegalStateExceptionIsThrown() throws Exception {
        // Expect an IllegalStateException to be thrown
        expected.expect(IllegalStateException.class);
        expected.expectMessage(MESSAGE);
        HsmSignDocument.signExistingSignatureFields(unconnectedHsmManager, inputUrl, outputUrl);
    }

    @Test
    public void logMessageIsGenerated() throws Exception {
        final ArrayList<LogRecord> logRecords = new ArrayList<LogRecord>();
        final Logger logger = Logger.getLogger(HsmSignDocument.class.getName());
        try (LogRecordListCollector collector = new LogRecordListCollector(logger, logRecords)) {
            HsmSignDocument.signExistingSignatureFields(unconnectedHsmManager, inputUrl, outputUrl);
        } catch (final IllegalStateException e) {
            // Expected exception
        }

        // Verify that we got the expected log message
        assertEquals("Must have one log record", 1, logRecords.size());
        final LogRecord logRecord = logRecords.get(0);
        assertEquals(MESSAGE, logRecord.getMessage());
        assertEquals(Level.SEVERE, logRecord.getLevel());
    }

    @Test
    public void outputFileIsNotGenerated() throws Exception {
        try {
            HsmSignDocument.signExistingSignatureFields(unconnectedHsmManager, inputUrl, outputUrl);
        } catch (final IllegalStateException e) {
            // Expected exception
        }

        // Verify the Output file does not exist.
        assertFalse(outputFile.getPath() + " must not exist after run", outputFile.exists());
    }

    /*
     * An HsmManager which is always unconnected.
     */
    private static class UnconnectedHsmManager extends AbstractHsmManager {
        @Override
        public ConnectionState getConnectionState() {
            return ConnectionState.READY;
        }
    }
}
