/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.datalogics.pdf.security.HsmLoginParameters;
import com.datalogics.pdf.security.HsmManager;

import java.security.Key;
import java.security.cert.Certificate;

/**
 * Implement an HsmManager.
 *
 * <p>
 * This implementation provides dummy implementations of the required APIs; tests should extend and override required
 * APIs as necessary.
 */
abstract class AbstractHsmManager implements HsmManager {

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#hsmLogin(com.datalogics.pdf.security.HsmLoginParameters)
     */
    @Override
    public void hsmLogin(final HsmLoginParameters parms) {}

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#hsmLogout()
     */
    @Override
    public void hsmLogout() {}

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#getConnectionState()
     */
    @Override
    public ConnectionState getConnectionState() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#getKey(java.lang.String, java.lang.String)
     */
    @Override
    public Key getKey(final String password, final String keyLabel) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#getCertificateChain(java.lang.String)
     */
    @Override
    public Certificate[] getCertificateChain(final String certLabel) {
        return new Certificate[0];
    }

    /* (non-Javadoc)
     * @see com.datalogics.pdf.security.HsmManager#getProviderName()
     */
    @Override
    public String getProviderName() {
        return null;
    }

}
