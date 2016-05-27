/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.mock;

import java.security.Provider;

/**
 * Mock security provider for testing.
 */
public class MockProvider extends Provider {

    public static final String PROVIDER_NAME = "MockProvider";

    /**
     * Create a MockProvider.
     */
    public MockProvider() {
        this(PROVIDER_NAME, 1.0, "Mock provider for testing.");
    }

    /**
     * Constructor for MockProvider.
     *
     * @param name provider name
     * @param version provider version
     * @param info provider information
     */
    protected MockProvider(final String name, final double version, final String info) {
        super(name, version, info);

        // Provide a MockSigner
        put("Signature.SHA256withRSA", MockSigner.class.getName());

        // Provide a MockKeyStore (mocks the LunaKeyStore)
        put("KeyStore.Luna", MockKeyStore.class.getName());
    }

    /**
     * Serial number.
     */
    private static final long serialVersionUID = 1L;

}
