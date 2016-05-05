/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.adobe.pdfjt.core.credentials.Credentials;

/**
 * The basic interface for logging into a HSM machine.
 */
public interface HsmManager {

    /**
     * Performs a login operation to the HSM device. The login operation occurs on the given tokenLabel or the first
     * available slot if tokenLabel is null. NOTE: tokenLabel is synonymous with partition name.
     *
     * @param tokenLabel- The label of the token to which to login
     * @param password - The password to use for the login
     * @return a boolean indicating if the login was successful
     */
    boolean hsmLogin(final String tokenLabel, final String password);

    /**
     * Logs out of the default session with the HSM device.
     *
     * <p>
     * This method should be called only if you have called {@link hsmLogin} to establish a login session.
     */
    void hsmLogout();

    /**
     * Get the Credentials object for the HSM device.
     *
     * @param password - the password for recovering the key
     * @param keyLabel - the given alias associated with the key
     * @param certLabel - the given alias associated with the certificate
     * @return a Credentials object used to sign documents
     */
    Credentials getCredentials(final String password, final String keyLabel, final String certLabel);
}
