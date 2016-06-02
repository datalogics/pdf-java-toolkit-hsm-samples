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
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.services.digsig.SignatureFieldInterface;
import com.adobe.pdfjt.services.digsig.SignatureManager;

import com.datalogics.pdf.hsm.samples.fakes.AbstractHsmManager;
import com.datalogics.pdf.hsm.samples.fakes.FakeProvider;
import com.datalogics.pdf.hsm.samples.util.DocumentUtils;
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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Unit tests simulating a connected HSM device.
 */
public class FakeConnectedHsmTest extends SampleTest {
    private static final Logger LOGGER = Logger.getLogger(FakeConnectedHsmTest.class.getName());

    static final String FILE_NAME = "SignedField.pdf";
    static final String QUALIFIED_SIGNATURE_FIELD_NAME = "Approver";
    static final String LOG_MESSAGE = "Fully Qualified Name: " + QUALIFIED_SIGNATURE_FIELD_NAME;

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
        }

        // Verify that we got the expected log message
        assertEquals("Must have one log record", 1, logRecords.size());
        final LogRecord logRecord = logRecords.get(0);
        assertEquals(LOG_MESSAGE, logRecord.getMessage());
        assertEquals(Level.INFO, logRecord.getLevel());
    }

    @Test
    public void outputFileIsGenerated() throws Exception {
        HsmSignDocument.signExistingSignatureFields(connectedHsmManager, inputUrl, outputUrl);

        // Verify the Output file exists.
        assertTrue(outputFile.getPath() + " must exist after run", outputFile.exists());
    }

    @Test
    public void signatureIsValid() throws Exception {
        HsmSignDocument.signExistingSignatureFields(connectedHsmManager, inputUrl, outputUrl);

        final PDFDocument doc = DocumentUtils.openPdfDocument(outputUrl);

        try {
            // Make sure that Signature field is signed.
            final SignatureFieldInterface sigField = getSignedSignatureField(doc);
            assertTrue("Signature field must be signed", sigField.isSigned());
            assertTrue("Signature field must be visible", sigField.isVisible());
            assertEquals("Qualified field names must match", QUALIFIED_SIGNATURE_FIELD_NAME,
                         sigField.getQualifiedName());

        } finally {
            doc.close();
        }
    }

    /*
     * Retrieve the first signed signature field.
     */
    private static SignatureFieldInterface getSignedSignatureField(final PDFDocument doc)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        // Set up a signature service and get the first signature field.
        final SignatureManager sigService = SignatureManager.newInstance(doc);
        if (sigService.hasSignedSignatureFields()) {
            final Iterator<SignatureFieldInterface> iter = sigService.getDocSignatureFieldIterator();
            if (iter.hasNext()) {
                return iter.next();
            }
        }
        return null;
    }

    /*
     * An HsmManager which is always connected.
     */
    private static class ConnectedHsmManager extends AbstractHsmManager {
        private static final String DER_KEY_PATH = "pdfjt-key.der";
        private static final String DER_CERT_PATH = "pdfjt-cert.der";

        final InputStream certStream = FakeConnectedHsmTest.class.getResourceAsStream(DER_CERT_PATH);
        final InputStream keyStream = FakeConnectedHsmTest.class.getResourceAsStream(DER_KEY_PATH);

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
            return FakeProvider.PROVIDER_NAME;
        }

        private void initializeProvider() {
            if (Security.getProvider(FakeProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new FakeProvider());
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
