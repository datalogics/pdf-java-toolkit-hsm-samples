/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

/**
 * This class represents login parameters that are used to login into a LunaSa HSM device.
 */
public class LunaHsmLoginParameters extends HsmLoginParameters {

    private final String tokenLabel;

    /**
     * Instantiate the login parameters for logging in to a Luna SA device using the first available HSM partition slot
     * and the given password.
     *
     * @param password the password to use for the login
     */
    public LunaHsmLoginParameters(final String password) {
        super(password);
        this.tokenLabel = null;
    }

    /**
     * Instantiate the login parameters for logging in to a Luna SA HSM device using the given tokenLabel and password.
     *
     * @param tokenLabel the label of the token to which to login
     * @param password the password to use for the login
     * 
     */
    public LunaHsmLoginParameters(final String tokenLabel, final String password) {
        super(password);
        this.tokenLabel = tokenLabel;
    }

    /**
     * Get the HSM tokenLabel which is synonymous with partition name.
     *
     * @return tokenLabel
     */
    public String getTokenLabel() {
        return tokenLabel;
    }
}
