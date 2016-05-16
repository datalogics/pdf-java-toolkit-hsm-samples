/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;


/**
 * Used to get instances of a HsmManager object.
 */
public final class HsmManagerFactory {

    /**
     * Luna SA Hsm Type.
     */
    public static final String LUNA_HSM_TYPE = "luna";

    /**
     * Factory class should not be instantiated.
     */
    private HsmManagerFactory() {}

    /**
     * Gets a new instance of a HsmManager for the needed type of HSM device.
     *
     * @param hsmType - Type of HSM device you are connecting to
     * @return HsmManager
     */

    public static HsmManager newInstance(final String hsmType) {
        if (hsmType.equals(LUNA_HSM_TYPE)) {
            return new LunaHsmManager();
        }
        throw new IllegalArgumentException("Must use a valid HsmType");
    }
}
