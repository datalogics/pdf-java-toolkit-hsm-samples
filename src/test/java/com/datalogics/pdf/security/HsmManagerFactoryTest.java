/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaProvider;

/**
 * Unit tests for the HsmManagerFactory.
 */
public class HsmManagerFactoryTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void throwExceptionForUnsupportedDevice() {
        // Expect an IllegalArgumentException to be thrown
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Must use a valid HsmType");

        HsmManagerFactory.newInstance("unsupported device");
    }

    @Test
    public void obtainLunaHsmDevice() {

        // Create fake LunaSlotManager and LunaProvider
        new MockLunaSlotManager();
        new MockLunaProvider();

        final HsmManager hsmManager = HsmManagerFactory.newInstance(HsmManagerFactory.LUNA_HSM_TYPE);
        assertThat(hsmManager, instanceOf(LunaHsmManager.class));
    }

    /*
     * Fake LunaSlotManager
     */
    public static final class MockLunaSlotManager extends MockUp<LunaSlotManager> {
        @Mock
        static LunaSlotManager getInstance() {
            return null;
        }
    }

    /*
     * Fake LunaProvider
     */
    public static final class MockLunaProvider extends MockUp<LunaProvider> {
        @Mock
        // CHECKSTYLE IGNORE MethodName FOR NEXT 1 LINE
        public void $init() {}
    }
}
