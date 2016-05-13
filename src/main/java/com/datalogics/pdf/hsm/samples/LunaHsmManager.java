/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.adobe.pdfjt.core.credentials.CredentialFactory;
import com.adobe.pdfjt.core.credentials.Credentials;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolder;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolderFactory;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;

import com.datalogics.pdf.security.HsmManager;

import com.safenetinc.luna.LunaCryptokiException;
import com.safenetinc.luna.LunaException;
import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaProvider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * This class allows for connecting to a Luna SA Hsm Device.
 */
public final class LunaHsmManager implements HsmManager {

    private final LunaSlotManager slotManager;

    private static final String KEYSTORE_TYPE = "Luna";
    private static final String PROVIDER_NAME = "LunaProvider";

    /**
     * Default no-arg constructor.
     */
    protected LunaHsmManager() {
        super();
        slotManager = LunaSlotManager.getInstance();
        initializeProvider();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.hsm.samples.HsmManager#hsmLogin(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hsmLogin(final String tokenLabel, final String password) {

        // Check for non-null, non zero length password
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        } else if (password.length() <= 0) {
            throw new IllegalArgumentException("Password must not be zero length");
        }

        try {
            if (tokenLabel == null) {
                slotManager.login(password);
            } else {
                slotManager.login(tokenLabel, password);
            }
        } catch (LunaException | LunaCryptokiException e) {
            throw new IllegalArgumentException("Error while logging into the Luna HSM" + e);
        }

        return slotManager.isLoggedIn();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.hsm.samples.HsmManager#hsmLogout()
     */
    @Override
    public void hsmLogout() {
        /*
         * When you are done using the Luna HSM, it is customary to log out of the HSM to prevent unauthorized access at
         * a later point in your application.
         *
         * Only use the LunaSlotManager.logout() method if you used one of the LunaSlotManager.login() methods for
         * opening access to the HSM. If you use an external login method, you will need to use an external logout
         * method.
         */
        slotManager.logout();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.hsm.samples.HsmManager#getCredentials(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public Credentials getCredentials(final String password, final String keyLabel, final String certLabel)
                    throws SecurityException, IOException, PDFInvalidParameterException {

        try {
            // Obtain the Luna Keystore - Access the LunaSA via PKCS11 through
            // the Luna Provider
            final KeyStore lunaKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            lunaKeyStore.load(null, null); // Can be null-null after login

            // Retrieve the PrivateKey and Certificate by labels
            final PrivateKey privateKey = (PrivateKey) lunaKeyStore.getKey(keyLabel, password.toCharArray());
            final X509Certificate cert = (X509Certificate) lunaKeyStore.getCertificate(certLabel);
            final X509Certificate[] certChain = new X509Certificate[1];
            certChain[0] = cert;

            // Create credentials
            final CredentialFactory credentialFactory = CredentialFactory.newInstance();
            final PrivateKeyHolder pkh = PrivateKeyHolderFactory.newInstance().createPrivateKey(privateKey,
                                                                                                PROVIDER_NAME);
            return credentialFactory.createCredentials(pkh, certChain[0], certChain);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                 | UnrecoverableKeyException e) {
            throw new SecurityException("Exception while obtaining LunaSA Credentials: ", e);
        } catch (final IOException e) {
            throw new IOException("Exception while obtaining LunaSA Credentials: ", e);
        } catch (final PDFInvalidParameterException e) {
            throw new PDFInvalidParameterException("Exception while obtaining LunaSA Credentials: ", e);
        }
    }

    private void initializeProvider() {
        // Add the Luna Security Provider if it is not already in the list of
        // Java Security Providers
        if (Security.getProvider("PROVIDER_NAME") == null) {
            Security.addProvider(new LunaProvider());
        }
    }
}
