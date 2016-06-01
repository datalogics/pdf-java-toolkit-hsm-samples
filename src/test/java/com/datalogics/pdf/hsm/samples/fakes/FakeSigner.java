/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.fakes;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;

/**
 * Mock signer for testing.
 */
public class FakeSigner extends SignatureSpi {
    Signature signature;

    public FakeSigner() throws NoSuchAlgorithmException {
        signature = Signature.getInstance("SHA256withRSA");
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitVerify(java.security.PublicKey)
     */
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {}

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitSign(java.security.PrivateKey)
     */
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        signature.initSign(privateKey);
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte)
     */
    @Override
    protected void engineUpdate(final byte data) throws SignatureException {}

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte[], int, int)
     */
    @Override
    protected void engineUpdate(final byte[] data, final int off, final int len) throws SignatureException {
        signature.update(data, off, len);
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSign()
     */
    @Override
    protected byte[] engineSign() throws SignatureException {
        return signature.sign();
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineVerify(byte[])
     */
    @Override
    protected boolean engineVerify(final byte[] sigBytes) throws SignatureException {
        return false;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSetParameter(java.lang.String, java.lang.Object)
     */
    @Override
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {}

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineGetParameter(java.lang.String)
     */
    @Override
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        return null;
    }
}
