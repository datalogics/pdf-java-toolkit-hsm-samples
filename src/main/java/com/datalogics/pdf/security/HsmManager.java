/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * The basic interface for logging into a HSM machine.
 */
public interface HsmManager {

    /**
     * Performs a login operation to the HSM device.
     *
     * @param parms = Contains the parameters needed to login to the device
     * @return a boolean indicating if the login was successful
     * @throws IllegalArgumentException if an argument was invalid
     */
    boolean hsmLogin(final HsmLoginParameters parms);

    /**
     * Logs out of the default session with the HSM device.
     *
     * <p>
     * This method should be called only if you have called {@link hsmLogin} to establish a login session.
     */
    void hsmLogout();

    /**
     * Determines if logged in to the HSM Device.
     *
     * @return boolean
     */
    boolean isLoggedIn();

    /**
     * Get the Private Key object for the HSM device.
     *
     * @param password - the password for recovering the key
     * @param keyLabel - the given alias associated with the key
     * @return A private key
     */
    PrivateKey getPrivateKey(final String password, final String keyLabel);

    /**
     * Get an array of x509 Certificates for the HSM device.
     *
     * @param certLabel - the given alias associated with the certificate
     * @return X509Certificate[]
     */
    X509Certificate[] getCertificateChain(final String certLabel);

    /**
     * Get the HSM provider name .
     *
     * @return String (The Provider name)
     */
    String getProviderName();

}
