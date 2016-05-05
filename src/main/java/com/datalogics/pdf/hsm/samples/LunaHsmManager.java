/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.adobe.pdfjt.core.credentials.CredentialFactory;
import com.adobe.pdfjt.core.credentials.Credentials;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolder;
import com.adobe.pdfjt.core.credentials.PrivateKeyHolderFactory;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;

import com.safenetinc.luna.LunaSlotManager;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class allows for connecting to a Luna SA Hsm Device.
 */
public final class LunaHsmManager implements HsmManager {
    private static final Logger LOGGER = Logger.getLogger(LunaHsmManager.class.getName());

    private LunaSlotManager slotManager;


    /**
     * Default no-arg constructor.
     */
    protected LunaHsmManager() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.hsm.samples.HsmManager#hsmLogin(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hsmLogin(final String tokenLabel, final String password) {
        // Initialize the SlotManager class
        slotManager = LunaSlotManager.getInstance();

        // Login to the HSM
        /*
         * This method unlocks the token for use. There are multiple methods available within the LunaSlotManager class
         * to login to the HSM:
         *
         * Login to the first available partition: login(String password)
         *
         * Login to the partition at the specified slot: login(int slot, String password)
         *
         * Login to the partition with the specified label: login(String TokenLabel, String password)
         *
         * Login to the partition with the specified user role: login(int slot, UserRole userType, String password)
         *
         * The password argument is the challenge password for the Luna SA partition or HSM. (Applications generally ask
         * for password information interactively from the user.)
         */
        if (tokenLabel == null) {
            slotManager.login(password);
        } else {
            slotManager.login(tokenLabel, password);
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
    public Credentials getCredentials(final String password, final String keyLabel, final String certLabel) {

        try {
            // Obtain the Luna Keystore - Access the LunaSA via PKCS11 through
            // the Luna Provider
            final KeyStore lunaKeyStore = KeyStore.getInstance("Luna");
            lunaKeyStore.load(null, null); // Can be null-null after login

            // List the LunaSA contents
            LOGGER.info("Luna Keystore contains");
            final Enumeration<String> aliases = lunaKeyStore.aliases();
            while (aliases.hasMoreElements()) {
                final String keyStoreObj = aliases.nextElement();
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("\t-" + keyStoreObj);
                }
            }

            // Retrieve the PrivateKey and Certificate by labels
            final PrivateKey privateKey = (PrivateKey) lunaKeyStore.getKey(keyLabel, password.toCharArray());
            final X509Certificate cert = (X509Certificate) lunaKeyStore.getCertificate(certLabel);
            final X509Certificate[] certChain = new X509Certificate[1];
            certChain[0] = cert;

            // Create credentials
            final CredentialFactory credentialFactory = CredentialFactory.newInstance();
            final PrivateKeyHolder pkh = PrivateKeyHolderFactory.newInstance().createPrivateKey(privateKey,
                                                                                                "LunaProvider");
            return credentialFactory.createCredentials(pkh, certChain[0], certChain);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                 | IOException | UnrecoverableKeyException
                 | PDFInvalidParameterException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Exception while obtaining LunaSA Credentials: " + e.getMessage());
            }
        }
        return null;
    }
}
