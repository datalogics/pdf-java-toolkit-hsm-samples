/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

import com.datalogics.pdf.security.HsmManager;

/**
 * Used to get instances of a HsmManager object.
 */
public final class HsmManagerFactory {

    /**
     * Factory class should not be instantiated.
     */
    private HsmManagerFactory() {}

    /**
     * Gets a new instance of a HsmManager for the needed type of HSM device.
     *
     * @param hsm - Type of HSM device you are connecting to
     * @return HsmManager
     */

    public static HsmManager newInstance(final HsmType hsm) {
        if (hsm.equals(HsmType.LUNA_SA_HSM)) {
            return new LunaHsmManager();
        }
        throw new IllegalArgumentException("Must use a valid HsmType");
    }

    public enum HsmType {
        LUNA_SA_HSM
    }
}
