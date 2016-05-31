/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import static org.junit.Assert.assertEquals;

import com.datalogics.pdf.hsm.samples.mock.MockProvider;
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

import java.security.Security;

/**
 * Unit tests for the LunaHsmManager.
 */
public class LunaHsmManagerTest {
    public static final String GOOD_PASSWORD = "good_password";
    public static final String BAD_PASSWORD = "bad_password";

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
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters("token", GOOD_PASSWORD));

        assertEquals("LunaHsmManager login should be successful", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void unsuccessfulLoginThrowsException() {
        // Expect a IllegalArgumentException to be thrown
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Error while logging into the Luna HSM");

        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters("token", BAD_PASSWORD));

        assertEquals("LunaHsmManager login should be unsuccessful", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void unsuccessfulLoginRemainsInReadyState() {
        try {
            lunaHsmManager.hsmLogin(new LunaHsmLoginParameters("token", BAD_PASSWORD));
        } catch (final IllegalArgumentException e) {
            // Expected exception
        }

        assertEquals("LunaHsmManager login should be unsuccessful", ConnectionState.READY,
                     lunaHsmManager.getConnectionState());
    }

    @Test
    public void connectionStateIsCorrectlyObserved() {
        // Start in READY state
        assertEquals("LunaHsmManager should start in READY state", ConnectionState.READY,
                     lunaHsmManager.getConnectionState());

        // In CONNECTED state after login
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters("token", GOOD_PASSWORD));
        assertEquals("LunaHsmManager should be in CONNECTED state after login", ConnectionState.CONNECTED,
                     lunaHsmManager.getConnectionState());

        // In DISCONNECTED state after logout
        lunaHsmManager.hsmLogout();
        assertEquals("LunaHsmManager should be in DISCONNECTED state after logout", ConnectionState.DISCONNECTED,
                     lunaHsmManager.getConnectionState());
    }


    @Test
    public void providerNameIsCorrect() {
        lunaHsmManager.hsmLogin(new LunaHsmLoginParameters("token", GOOD_PASSWORD));
        assertEquals("LunaHsmManager has unexpected provider name", LunaHsmManager.PROVIDER_NAME,
                     lunaHsmManager.getProviderName());
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
            loggedIn = true;
            return 0;
        }

        @Mock
        boolean login(final String slot, final String password) {
            // We will only accept GOOD_PASSWORD
            if (!password.contentEquals(GOOD_PASSWORD)) {
                throw new LunaException();
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
    }

    /*
     * Fake LunaProvider
     */
    public static final class MockLunaProvider extends MockUp<LunaProvider> {
        /**
         * Install a MockProvider to shadow the LunaProvider.
         */
        @Mock
        // CHECKSTYLE IGNORE MethodName FOR NEXT 1 LINE
        public void $init() {
            if (Security.getProvider(MockProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new MockProvider());
            }
        }
    }
}
