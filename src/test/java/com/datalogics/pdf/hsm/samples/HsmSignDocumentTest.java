/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import static org.junit.Assert.assertFalse;

import com.datalogics.pdf.security.HsmLoginParameters;
import com.datalogics.pdf.security.HsmManager;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.security.Key;
import java.security.cert.Certificate;

/**
 * Unit tests for the HsmSignDocument class.
 */
public class HsmSignDocumentTest extends SampleTest {
    static final String FILE_NAME = "SignedField.pdf";
    static final String QUALIFIED_SIGNATURE_FIELD_NAME = "Approver";

    @Test
    public void failWithUnconnectedHsmManager() throws Exception {
        final URL inputUrl = HsmSignDocument.class.getResource(HsmSignDocument.INPUT_UNSIGNED_PDF_PATH);

        final File file = SampleTest.newOutputFileWithDelete(FILE_NAME);

        // The complete file name will be set in the HsmSignDocument class.
        final URL outputUrl = file.toURI().toURL();

        // Create an unconnected HsmManager
        final HsmManager unconnectedHsmManager = new MockHsmManager() {
            @Override
            public ConnectionState getConnectionState() {
                return ConnectionState.READY;
            }
        };

        HsmSignDocument.signExistingSignatureFields(unconnectedHsmManager, inputUrl, outputUrl);
        // Make sure the Output file does not exist.
        assertFalse(file.getPath() + " must not exist after run", file.exists());
    }

    private class MockHsmManager implements HsmManager {
        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#hsmLogin(com.datalogics.pdf.security.HsmLoginParameters)
         */
        @Override
        public void hsmLogin(final HsmLoginParameters parms) {}

        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#hsmLogout()
         */
        @Override
        public void hsmLogout() {}

        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#getConnectionState()
         */
        @Override
        public ConnectionState getConnectionState() {
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#getKey(java.lang.String, java.lang.String)
         */
        @Override
        public Key getKey(final String password, final String keyLabel) {
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#getCertificateChain(java.lang.String)
         */
        @Override
        public Certificate[] getCertificateChain(final String certLabel) {
            return new Certificate[0];
        }

        /*
         * (non-Javadoc)
         *
         * @see com.datalogics.pdf.security.HsmManager#getProviderName()
         */
        @Override
        public String getProviderName() {
            return null;
        }

    }
}
