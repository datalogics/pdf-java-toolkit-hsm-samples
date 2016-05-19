/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import java.security.Key;
import java.security.cert.Certificate;


/**
 * The basic interface for logging into a HSM machine.
 */
public interface HsmManager {

    /**
     * Performs a login operation to the HSM device.
     *
     * @param parms the parameters needed to login to the device
     * @return a boolean indicating if the login was successful
     * @throws IllegalArgumentException if an argument was invalid
     */
    boolean hsmLogin(final HsmLoginParameters parms);

    /**
     * Logs out of the default session with the HSM device.
     *
     * <p>
     * This method should be called only if you have called {@link HsmManager#hsmLogin(HsmLoginParameters)} to establish
     * a login session.
     */
    void hsmLogout();

    /**
     * Get the connection state of the HSM Manager.
     *
     * @return ConnectionState
     */
    ConnectionState getConnectionState();

    /**
     * Get the Key object for the HSM device.
     *
     * @param password the password for recovering the key
     * @param keyLabel the given alias associated with the key
     * @return key
     */
    Key getKey(final String password, final String keyLabel);

    /**
     * Get an array of Certificates for the HSM device.
     *
     * @param certLabel the given alias associated with the certificate
     * @return Certificate[]
     */
    Certificate[] getCertificateChain(final String certLabel);

    /**
     * Get the provider name installed by this HSM.
     *
     * @return the provider name
     */
    String getProviderName();

    /**
     * Represents the Connection state with the HSM device.
     */
    enum ConnectionState {
        READY, CONNECTED, DISCONNECTED
    }
}
