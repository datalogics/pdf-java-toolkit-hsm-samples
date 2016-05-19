/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.adobe.internal.io.ByteWriter;
import com.adobe.pdfjt.core.credentials.CredentialFactory;
import com.adobe.pdfjt.core.credentials.Credentials;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolder;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolderFactory;
import com.adobe.pdfjt.core.exceptions.PDFException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.core.securityframework.CryptoMode;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.services.digsig.SignatureFieldInterface;
import com.adobe.pdfjt.services.digsig.SignatureManager;
import com.adobe.pdfjt.services.digsig.SignatureOptions;
import com.adobe.pdfjt.services.digsig.UserInfo;
import com.adobe.pdfjt.services.digsig.cryptoprovider.JCEProvider;
import com.adobe.pdfjt.services.digsig.spi.CryptoContext;

import com.datalogics.pdf.hsm.samples.util.DocumentUtils;
import com.datalogics.pdf.hsm.samples.util.IoUtils;
import com.datalogics.pdf.hsm.samples.util.SampleConfigurationUtils;
import com.datalogics.pdf.security.HsmManager;
import com.datalogics.pdf.security.HsmManagerFactory;
import com.datalogics.pdf.security.LunaHsmLoginParameters;

import org.apache.commons.configuration2.Configuration;

import java.io.File;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This is a sample that demonstrates how to sign fields in a document using a Hardware Security Module (HSM). It will
 * find a specific signature field in a document so that API users can sign the correct field. Note that the password,
 * private key label, certificate label, and other parameters all must be customized for a particular HSM installation.
 * This sample is set up to work with a Luna SA HSM device, with an evaluation certificate installed on the default
 * partition; this HSM is not publicly accessible.
 */
public final class HsmSignDocument {
    private static final Logger LOGGER = Logger.getLogger(HsmSignDocument.class.getName());

    private static final String TOKEN_LABEL = null; // The HSM partition name or null
    private static final String PRIVATE_KEY_LABEL = "pdfjt-eval-key"; // The private key label/alias
    private static final String CERTIFICATE_LABEL = "pdfjt-eval-cert"; // The certificate label/alias
    private static final String DIGESTER_ALG = "SHA256";

    private static final String PROPERTIES_FILE = "hsm.properties";
    private static final String PASSWORD_PROPERTY = "hsm.password";

    public static final String INPUT_UNSIGNED_PDF_PATH = "UnsignedDocument.pdf";
    public static final String OUTPUT_SIGNED_PDF_PATH = "SignedField.pdf";

    private static HsmManager hsmManager;
    private static String password;

    /**
     * This is a utility class, and won't be instantiated.
     */
    private HsmSignDocument() {}


    /**
     * Main program.
     *
     * @param args command line arguments. Only one is expected in order to specify the output path. If no arguments are
     *        given, the sample will output to the root of the samples directory by default.
     * @throws Exception a general exception was thrown
     */
    public static void main(final String... args) throws Exception {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");

        // Retrieve the password, stored in the hsm.properties file
        final Configuration loginConfiguration = SampleConfigurationUtils.getConfiguration(PROPERTIES_FILE);
        password = loginConfiguration.getString(PASSWORD_PROPERTY);

        hsmManager = HsmManagerFactory.newInstance(HsmManagerFactory.LUNA_HSM_TYPE);

        if (hsmManager.getConnectionState()
                      .equals(HsmManager.ConnectionState.READY)) {
            // Log in to the HSM
            hsmManager.hsmLogin(new LunaHsmLoginParameters(TOKEN_LABEL, password));
        }

        // Report whether we successfully logged in
        if (hsmManager.getConnectionState()
                      .equals(HsmManager.ConnectionState.CONNECTED)) {
            LOGGER.info("Logged into HSM");
        } else {
            LOGGER.severe("Failed to log into HSM, exiting");
            return;
        }

        final URL inputUrl = HsmSignDocument.class.getResource(INPUT_UNSIGNED_PDF_PATH);

        URL outputUrl = null;
        if (args.length > 0) {
            outputUrl = new File(args[0]).toURI().toURL();
        } else {
            outputUrl = new File(OUTPUT_SIGNED_PDF_PATH).toURI().toURL();
        }

        // Query and sign all permissible signature fields.
        signExistingSignatureFields(inputUrl, outputUrl);

        // Log out of the HSM
        hsmManager.hsmLogout();
    }

    /**
     * Sign existing signature fields found in the example document.
     *
     * @param inputUrl the URL to the input file
     * @param outputUrl the path to the file to contain the signed document
     * @throws Exception a general exception was thrown
     */
    public static void signExistingSignatureFields(final URL inputUrl, final URL outputUrl) throws Exception {
        PDFDocument pdfDoc = null;
        try {
            // Get the PDF file.
            pdfDoc = DocumentUtils.openPdfDocument(inputUrl);

            // Set up a signature service and iterate over all of the
            // signature fields.
            final SignatureManager sigService = SignatureManager.newInstance(pdfDoc);
            if (sigService.hasUnsignedSignatureFields()) {
                final Iterator<SignatureFieldInterface> iter = sigService.getDocSignatureFieldIterator();
                while (iter.hasNext()) {
                    final SignatureFieldInterface sigField = iter.next();
                    signField(sigService, sigField, outputUrl);
                }
            }
        } finally {
            try {
                if (pdfDoc != null) {
                    pdfDoc.close();
                }
            } catch (final PDFException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    private static void signField(final SignatureManager sigMgr,
                                  final SignatureFieldInterface sigField, final URL outputUrl)
                    throws Exception {

        final String qualifiedName = "Fully Qualified Name: " + sigField.getQualifiedName();
        LOGGER.info(qualifiedName);

        ByteWriter byteWriter = null;
        try {
            final PrivateKey privateKey = (PrivateKey) hsmManager.getKey(password, PRIVATE_KEY_LABEL);
            final X509Certificate[] certChain = (X509Certificate[]) hsmManager.getCertificateChain(CERTIFICATE_LABEL);

            // Create credentials
            final CredentialFactory credentialFactory = CredentialFactory.newInstance();
            final String provider = hsmManager.getProviderName();
            final PrivateKeyHolder pkh = PrivateKeyHolderFactory.newInstance().createPrivateKey(privateKey, provider);
            final Credentials credentials = credentialFactory.createCredentials(pkh, certChain[0], certChain);

            // Must be permitted to sign doc and field must be visible.
            if (sigField.isSigningPermitted()) {
                if (sigField.isVisible()) {
                    // Create output file to hold the signed PDF data.
                    byteWriter = IoUtils.newByteWriter(outputUrl);

                    final SignatureOptions signatureOptions = SignatureOptions.newInstance();
                    final UserInfo userInfo = UserInfo.newInstance();

                    // This name will show up in the signature as "Digitally signed by <name>".
                    // If no name is specified the signature will say it was signed by whatever name is
                    // on the credentials used to sign the document.
                    userInfo.setName("John Doe");
                    signatureOptions.setUserInfo(userInfo);

                    // Set the crypto context mode, digest/hash method, and signature/encryption algorithm
                    final CryptoContext context = new CryptoContext(CryptoMode.NON_FIPS_MODE, DIGESTER_ALG, "RSA");

                    // Sign the document.
                    sigMgr.sign(sigField, signatureOptions, credentials, byteWriter, new JCEProvider(context));
                } else {
                    throw new PDFIOException("Signature field is not visible");
                }
            }
        } finally {
            if (byteWriter != null) {
                byteWriter.close();
            }
        }
    }
}
