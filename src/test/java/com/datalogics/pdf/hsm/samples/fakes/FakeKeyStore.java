/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.fakes;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

/**
 * Mock keyPair store for testing.
 */
public class FakeKeyStore extends KeyStoreSpi {
    private KeyPair keyPair;
    private X509Certificate certificate;

    /**
     * Constructor for FakeKeyStore.
     *
     * @throws NoSuchProviderException if provider is not found
     * @throws NoSuchAlgorithmException if algorithm is not found
     * @throws CertificateException if certificate could not be created
     * @throws OperatorCreationException if certificate could not be created
     */
    public FakeKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException,
                    CertificateException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(1024, random);
        keyPair = generator.generateKeyPair();
        certificate = generateX509Certificate();
    }

    /* (non-Javadoc)
     * @see java.security.KeyStoreSpi#engineGetKey(java.lang.String, char[])
     */
    @Override
    public Key engineGetKey(final String alias, final char[] password) {
        return keyPair.getPrivate();
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
        return certificate;
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
                    throws KeyStoreException {}

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

    private X509Certificate generateX509Certificate() throws OperatorCreationException, CertificateException {
        final X500Name issuerName = new X500Name("CN=www.datalogics.com");
        final X500Name subjectName = issuerName;
        final BigInteger serial = BigInteger.valueOf(3);
        final Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
        final Date notAfter = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30);
        final X509v3CertificateBuilder x509builder = new JcaX509v3CertificateBuilder(issuerName, serial, notBefore,
                                                                                 notAfter, subjectName,
                                                                                 keyPair.getPublic());

        final JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA1WithRSA");
        signerBuilder.setProvider("BC");
        final X509CertificateHolder certHolder = x509builder.build(signerBuilder.build(keyPair.getPrivate()));

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
    }
}
