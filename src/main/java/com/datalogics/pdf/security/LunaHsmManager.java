/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

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
 *
 * <p>
 * Note: Once an instance of LunaHsmManager goes into a
 * {@link com.datalogics.pdf.security.HsmManager.ConnectionState#DISCONNECTED} state during logout from the LunaSA you
 * will need to create a new instance of this class if you need to login to the LunaSA device again.
 */
public final class LunaHsmManager implements HsmManager {

    private LunaSlotManager slotManager;
    private KeyStore lunaKeyStore;
    private ConnectionState state;

    public static final String KEYSTORE_TYPE = "Luna";
    public static final String PROVIDER_NAME = "LunaProvider";

    /**
     * Default no-arg constructor.
     *
     */
    protected LunaHsmManager() {
        super();
        slotManager = LunaSlotManager.getInstance();
        initializeProvider();
        state = ConnectionState.READY;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.hsm.samples.HsmManager#hsmLogin(java.lang.String, java.lang.String)
     */
    @Override
    public void hsmLogin(final HsmLoginParameters parms) {
        if (!(parms instanceof LunaHsmLoginParameters)) {
            throw new IllegalArgumentException("Must pass a instanceof LunaHsmLoginParms "
                                               + "to hsmLogin for LunaHsmManager");
        }

        checkLoginAbility(parms);

        final String tokenLabel = ((LunaHsmLoginParameters) parms).getTokenLabel();
        final String password = parms.getPassword();
        try {
            if (tokenLabel == null) {
                slotManager.login(password);
            } else {
                slotManager.login(tokenLabel, password);
            }
        } catch (LunaException | LunaCryptokiException e) {
            throw new IllegalArgumentException("Error while logging into the Luna HSM" + e);
        }

        if (slotManager.isLoggedIn()) {
            state = ConnectionState.CONNECTED;
        }
        loadKeyStore();
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
         */
        if (state.equals(ConnectionState.CONNECTED)) {
            slotManager.logout();
            state = ConnectionState.DISCONNECTED;
            cleanUpResources();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.security.HsmManager#getConnectionState()
     */
    @Override
    public ConnectionState getConnectionState() {
        return state;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.security.HsmManager#getCertificateChain(java.lang.String)
     */
    @Override
    public X509Certificate[] getCertificateChain(final String certLabel) {
        if (!state.equals(ConnectionState.CONNECTED)) {
            throw new SecurityException("Call the hsmLogin method to login to HSM device first.");
        }

        X509Certificate cert;
        try {
            cert = (X509Certificate) lunaKeyStore.getCertificate(certLabel);
        } catch (final KeyStoreException e) {
            throw new SecurityException("Exception while obtaining certificate chain for LunaSA: ", e);
        }
        final X509Certificate[] certChain = new X509Certificate[1];
        certChain[0] = cert;

        return certChain;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.security.HsmManager#getKey(java.lang.String, java.lang.String)
     */
    @Override
    public PrivateKey getKey(final String password, final String keyLabel) {
        if (!state.equals(ConnectionState.CONNECTED)) {
            throw new SecurityException("Call the hsmLogin method to login to HSM device first.");
        }
        try {
            return (PrivateKey) lunaKeyStore.getKey(keyLabel, password.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new SecurityException("Exception while obtaining Private Key for LunaSa: ", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datalogics.pdf.security.HsmManager#getProviderName()
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    private void checkLoginAbility(final HsmLoginParameters parms) {
        if (!state.equals(ConnectionState.READY)) {
            throw new IllegalStateException("HsmManager not in a ready to login state, "
                                            + "create a new HsmManager instance");
        }

        final String password = parms.getPassword();
        // Check for non-null, non zero length password
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        } else if (password.length() <= 0) {
            throw new IllegalArgumentException("Password must not be zero length");
        }
    }

    private void cleanUpResources() {
        slotManager = null;
        lunaKeyStore = null;
    }

    private void initializeProvider() {
        // Add the Luna Security Provider if it is not already in the list of
        // Java Security Providers
        if (Security.getProvider(PROVIDER_NAME) == null) {
            Security.addProvider(new LunaProvider());
        }
    }

    private void loadKeyStore() {
        if (!state.equals(ConnectionState.CONNECTED)) {
            throw new SecurityException("Call the hsmLogin method to login to HSM device first.");
        }
        try {
            // Obtain the Luna Keystore - Access the LunaSA via PKCS11 through
            // the Luna Provider
            lunaKeyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            lunaKeyStore.load(null, null); // Can be null-null after login
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
            throw new SecurityException("Exception while loading LunaSA KeyStore: ", e);
        }
    }
}
