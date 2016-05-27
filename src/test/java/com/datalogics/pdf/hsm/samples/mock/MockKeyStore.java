/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

/**
 * Mock key store for testing.
 */
public class MockKeyStore extends KeyStoreSpi {

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetKey(java.lang.String, char[])
     */
    @Override
    public Key engineGetKey(final String alias, final char[] password) throws NoSuchAlgorithmException,
                    UnrecoverableKeyException {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificateChain(java.lang.String)
     */
    @Override
    public Certificate[] engineGetCertificateChain(final String alias) {
        return new Certificate[0];
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificate(java.lang.String)
     */
    @Override
    public Certificate engineGetCertificate(final String alias) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCreationDate(java.lang.String)
     */
    @Override
    public Date engineGetCreationDate(final String alias) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String, java.security.Key, char[],
     * java.security.cert.Certificate[])
     */
    @Override
    public void engineSetKeyEntry(final String alias, final Key key, final char[] password, final Certificate[] chain)
                    throws KeyStoreException {

    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String, byte[], java.security.cert.Certificate[])
     */
    @Override
    public void engineSetKeyEntry(final String alias, final byte[] key, final Certificate[] chain)
                    throws KeyStoreException {}

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSetCertificateEntry(java.lang.String, java.security.cert.Certificate)
     */
    @Override
    public void engineSetCertificateEntry(final String alias, final Certificate cert) throws KeyStoreException {}

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineDeleteEntry(java.lang.String)
     */
    @Override
    public void engineDeleteEntry(final String alias) throws KeyStoreException {}

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineAliases()
     */
    @Override
    public Enumeration<String> engineAliases() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineContainsAlias(java.lang.String)
     */
    @Override
    public boolean engineContainsAlias(final String alias) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineSize()
     */
    @Override
    public int engineSize() {
        return 0;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineIsKeyEntry(java.lang.String)
     */
    @Override
    public boolean engineIsKeyEntry(final String alias) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineIsCertificateEntry(java.lang.String)
     */
    @Override
    public boolean engineIsCertificateEntry(final String alias) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetCertificateAlias(java.security.cert.Certificate)
     */
    @Override
    public String engineGetCertificateAlias(final Certificate cert) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineStore(java.io.OutputStream, char[])
     */
    @Override
    public void engineStore(final OutputStream stream, final char[] password)
                    throws IOException, NoSuchAlgorithmException, CertificateException {}

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineLoad(java.io.InputStream, char[])
     */
    @Override
    public void engineLoad(final InputStream stream, final char[] password)
                    throws IOException, NoSuchAlgorithmException, CertificateException {}
}
