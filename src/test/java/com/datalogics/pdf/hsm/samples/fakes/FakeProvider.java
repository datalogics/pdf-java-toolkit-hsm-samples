/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.fakes;

import java.security.Provider;

/**
 * Mock security provider for testing.
 */
public class FakeProvider extends Provider {

    public static final String PROVIDER_NAME = "FakeProvider";

    /**
     * Create a FakeProvider.
     */
    public FakeProvider() {
        this(PROVIDER_NAME, 1.0, "Mock provider for testing.");
    }

    /**
     * Constructor for FakeProvider.
     *
     * @param name provider name
     * @param version provider version
     * @param info provider information
     */
    protected FakeProvider(final String name, final double version, final String info) {
        super(name, version, info);

        // Provide a FakeSigner
        put("Signature.SHA256withRSA", FakeSigner.class.getName());

        // Provide a FakeKeyStore (mocks the LunaKeyStore)
        put("KeyStore.Luna", FakeKeyStore.class.getName());
    }

    /**
     * Serial number.
     */
    private static final long serialVersionUID = 1L;

}
