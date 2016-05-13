/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import com.adobe.pdfjt.core.credentials.Credentials;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;

import java.io.IOException;

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
    boolean hsmLogin(final HsmLoginParms parms);

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
     * @throws IOException an I/O operation failed or was interrupted
     * @throws SecurityException thrown by the security manager to indicate a security violation
     * @throws PDFInvalidParameterException one or more of the parameters passed to a method is invalid
     */
    Credentials getCredentials(final String password, final String keyLabel, final String certLabel)
                    throws SecurityException, IOException, PDFInvalidParameterException;
}
