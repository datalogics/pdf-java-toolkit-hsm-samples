/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.adobe.pdfjt.core.credentials.PrivateKeyHolder;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolderFactory;
import com.adobe.pdfjt.core.credentials.impl.ByteArrayKeyHolder;
import com.adobe.pdfjt.core.credentials.impl.utils.CertUtils;

import com.datalogics.pdf.hsm.samples.mock.MockProvider;
import com.datalogics.pdf.hsm.samples.util.LogRecordListCollector;
import com.datalogics.pdf.security.HsmManager;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Unit tests simulating a connected HSM device.
 */
public class MockedHsmTest extends SampleTest {
    private static final Logger LOGGER = Logger.getLogger(MockedHsmTest.class.getName());

    static final String FILE_NAME = "SignedField.pdf";
    static final String MESSAGE = "Fully Qualified Name: Approver";

    private static URL inputUrl;
    private static URL outputUrl;
    private static File outputFile;
    private static HsmManager connectedHsmManager;

    /**
     * Set up to try and sign a document with an unconnected HSM device.
     *
     * @throws Exception a general exception was thrown
     */
    @BeforeClass
    public static void setUpForMock() throws Exception {
        inputUrl = HsmSignDocument.class.getResource(HsmSignDocument.INPUT_UNSIGNED_PDF_PATH);
        outputFile = SampleTest.newOutputFileWithDelete(FILE_NAME);

        // The complete file name will be set in the HsmSignDocument class.
        outputUrl = outputFile.toURI().toURL();

        // Create a connected HsmManager
        connectedHsmManager = new ConnectedHsmManager();
    }

    @Test
    public void logMessageIsGenerated() throws Exception {
        final ArrayList<LogRecord> logRecords = new ArrayList<LogRecord>();
        final Logger logger = Logger.getLogger(HsmSignDocument.class.getName());
        try (LogRecordListCollector collector = new LogRecordListCollector(logger, logRecords)) {
            HsmSignDocument.signExistingSignatureFields(connectedHsmManager, inputUrl, outputUrl);
        } catch (final IllegalStateException e) {
            // Expected exception
        }

        // Verify that we got the expected log message
        assertEquals("Must have one log record", 1, logRecords.size());
        final LogRecord logRecord = logRecords.get(0);
        assertEquals(MESSAGE, logRecord.getMessage());
        assertEquals(Level.INFO, logRecord.getLevel());
    }

    @Test
    public void outputFileIsGenerated() throws Exception {
        try {
            HsmSignDocument.signExistingSignatureFields(connectedHsmManager, inputUrl, outputUrl);
        } catch (final IllegalStateException e) {
            // Expected exception
        }

        // Verify the Output file exists.
        assertTrue(outputFile.getPath() + " must exist after run", outputFile.exists());
    }

    /*
     * An HsmManager which is always unconnected.
     */
    private static class ConnectedHsmManager extends AbstractHsmManager {
        private static final String DER_KEY_PATH = "pdfjt-key.der";
        private static final String DER_CERT_PATH = "pdfjt-cert.der";

        final InputStream certStream = MockedHsmTest.class.getResourceAsStream(DER_CERT_PATH);
        final InputStream keyStream = MockedHsmTest.class.getResourceAsStream(DER_KEY_PATH);

        private Key key;
        private Certificate[] certificateChain;

        public ConnectedHsmManager() throws Exception {
            initializeProvider();
            loadKey();
            loadCertificateChain();
        }

        @Override
        public ConnectionState getConnectionState() {
            return ConnectionState.CONNECTED;
        }

        @Override
        public Key getKey(final String password, final String keyLabel) {
            return key;
        }

        @Override
        public Certificate[] getCertificateChain(final String certLabel) {
            return certificateChain;
        }

        @Override
        public String getProviderName() {
            return MockProvider.PROVIDER_NAME;
        }

        private void initializeProvider() {
            if (Security.getProvider(MockProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new MockProvider());
            }
        }

        private void loadKey() throws Exception {
            try {
                final byte[] derEncodedPrivateKey = getDerEncodedData(keyStream);
                final PrivateKeyHolder privateKeyHolder = PrivateKeyHolderFactory
                                                                                 .newInstance()
                                                                                 .createPrivateKey(derEncodedPrivateKey,
                                                                                                   "RSA");
                key = CertUtils.createJCEPrivateKey(((ByteArrayKeyHolder) privateKeyHolder).getDerEncodedKey(),
                                                    ((ByteArrayKeyHolder) privateKeyHolder).getAlgorithm());
            } catch (final IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.severe("Could not create private key: " + e.getMessage());
                }
                throw new Exception(e);
            }
        }

        private void loadCertificateChain() throws Exception {
            try {
                final byte[] derEncodedCert = getDerEncodedData(certStream);
                final X509Certificate jceCert = (X509Certificate) CertUtils.importCertificate(derEncodedCert);
                certificateChain = new X509Certificate[] { jceCert };
            } catch (final IOException | CertificateException e) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.severe("Could not create certificate: " + e.getMessage());
                }
                throw new Exception(e);
            }
        }

        private static byte[] getDerEncodedData(final InputStream inputStream) throws IOException {
            final byte[] derData = new byte[inputStream.available()];
            final int totalBytes = inputStream.read(derData, 0, derData.length);
            if (totalBytes == 0) {
                LOGGER.info("getDerEncodedData(): No bytes read from InputStream");
            }
            inputStream.close();

            return derData;
        }
    }
}
