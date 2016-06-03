/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import static org.junit.Assert.assertEquals;

import com.datalogics.pdf.hsm.samples.fakes.FakeKeyStore;
import com.datalogics.pdf.hsm.samples.fakes.FakeProvider;
import com.datalogics.pdf.security.HsmManager.ConnectionState;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.safenetinc.luna.LunaException;
import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaProvider;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * Unit tests for the LunaHsmManager.
 */
public class LunaHsmManagerTest {
    public static final String GOOD_PASSWORD = "good_password";
    public static final String BAD_PASSWORD = "bad_password";
    public static final String GOOD_SLOT_NAME = "good_slot_name";
    public static final String BAD_SLOT_NAME = "bad_slot_name";
    public static final String KEY_LABEL = "key_label";
    public static final String CERTIFICATE_LABEL = "certificate_label";

    private LunaHsmManager lunaHsmManager;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Install mock Luna classes.
     */
    @Before
    public void setUp() {
        new MockLunaSlotManager();
        new MockLunaProvider();

        lunaHsmManager = (LunaHsmManager) HsmManagerFactory.newInstance(HsmManagerFactory.LUNA_HSM_TYPE);
    }

    @Test
    public void successfulLogin() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_PASSWORD));

        assertEquals("LunaHsmManager login should be successful", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void successfulLoginWithSlotName() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_SLOT_NAME, GOOD_PASSWORD));

        assertEquals("LunaHsmManager login with good slot name should be successful", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void unsuccessfulLoginThrowsException() {
        // Expect a IllegalArgumentException to be thrown
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Error while logging into the Luna HSM");

        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(BAD_PASSWORD));
    }

    @Test
    public void unsuccessfulLoginRemainsInReadyState() {
        try {
            lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(BAD_PASSWORD));
        } catch (final IllegalArgumentException e) {
            // Expected exception
        }

        assertEquals("LunaHsmManager login should be in ready state", ConnectionState.READY,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void loginWithBadSlotNameThrowsException() {
        // Expect a IllegalArgumentException to be thrown
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Token label must refer to available slot");

        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(BAD_SLOT_NAME, GOOD_PASSWORD));
    }

    @Test
    public void loginWithBadSlotNameRemainsInReadyState() {
        try {
            lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(BAD_SLOT_NAME, GOOD_PASSWORD));
        } catch (final IllegalArgumentException e) {
            // Expected exception
        }

        assertEquals("LunaHsmManager login should be in ready state", ConnectionState.READY,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void connectionStateIsCorrectlyObserved() {
        // Start in READY state
        assertEquals("LunaHsmManager should start in READY state", ConnectionState.READY,
                     lunaHsmManager.getConnectionState());

        // In CONNECTED state after login
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_PASSWORD));
        assertEquals("LunaHsmManager should be in CONNECTED state after login", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());

        // In DISCONNECTED state after logout
        lunaHsmManager.hsmLogout();
        assertEquals("LunaHsmManager should be in DISCONNECTED state after logout", ConnectionState.DISCONNECTED,
                     lunaHsmManager.getConnectionState());
    }


    @Test
    public void providerNameIsCorrect() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_PASSWORD));
        assertEquals("LunaHsmManager has unexpected provider name", LunaHsmManager.PROVIDER_NAME,
                     lunaHsmManager.getProviderName());
    }

    @Test
    public void canRetrieveKey() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_PASSWORD));
        final PrivateKey key = lunaHsmManager.getKey(GOOD_PASSWORD, KEY_LABEL);
        assertEquals("Key should have correct algorithm", "RSA", key.getAlgorithm());
        assertEquals("Key should have correct format", "PKCS#8", key.getFormat());
    }

    @Test
    public void canRetrieveCertificateChain() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters(GOOD_PASSWORD));
        final X509Certificate[] certificates = lunaHsmManager.getCertificateChain(CERTIFICATE_LABEL);
        for (final X509Certificate certificate : certificates) {
            assertEquals("Certificate is wrong type", "X.509", certificate.getType());
            assertEquals("Issuer name is correct", FakeKeyStore.ISSUER_NAME, certificate.getIssuerDN().getName());
        }
    }

    /*
     * Fake LunaSlotManager
     */
    public static final class MockLunaSlotManager extends MockUp<LunaSlotManager> {
        private boolean loggedIn = false;

        @Mock
        // CHECKSTYLE IGNORE MethodName FOR NEXT 1 LINE
        public void $init() {}

        @Mock
        int login(final String password) {
            // We will only accept GOOD_PASSWORD
            if (!password.contentEquals(GOOD_PASSWORD)) {
                throw new LunaException("Bad password");
            }
            loggedIn = true;
            return 0;
        }

        @Mock
        boolean login(final String slot, final String password) {
            // We will only accept GOOD_SLOT_NAME
            if (!slot.contentEquals(GOOD_SLOT_NAME)) {
                loggedIn = false;
                return loggedIn;
            }
            // We will only accept GOOD_PASSWORD
            if (!password.contentEquals(GOOD_PASSWORD)) {
                throw new LunaException("Bad password");
            }
            loggedIn = true;
            return loggedIn;
        }

        @Mock
        void logout() {
            loggedIn = false;
        }

        @Mock
        boolean isLoggedIn() {
            return loggedIn;
        }

        @Mock
        int findSlotFromLabel(final String tokenLabel) {
            if (tokenLabel.contentEquals(GOOD_SLOT_NAME)) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /*
     * Fake LunaProvider
     */
    public static final class MockLunaProvider extends MockUp<LunaProvider> {
        /**
         * Install a FakeProvider to shadow the LunaProvider.
         */
        @Mock
        // CHECKSTYLE IGNORE MethodName FOR NEXT 1 LINE
        public void $init() {
            if (Security.getProvider(FakeProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new FakeProvider());
            }
        }
    }
}
