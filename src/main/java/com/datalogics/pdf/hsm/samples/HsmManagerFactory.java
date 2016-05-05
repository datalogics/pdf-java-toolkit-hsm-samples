/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples;

/**
 * Used to get instances of a HsmManager object.
 */
public final class HsmManagerFactory {

    public static final int LUNA_SA_HSM = 1;

    /**
     * Factory class should not be instantiated.
     */
    private HsmManagerFactory() {}

    /**
     * Gets a instance of a HsmManager for the needed type of HSM device.
     *
     * @param type - Type of HSM device you are connecting to
     * @return HsmManager
     */

    public static HsmManager getInstance(final int type) {
        if (type == LUNA_SA_HSM) {
            return new LunaHsmManager();
        }
        return null;
    }
}
