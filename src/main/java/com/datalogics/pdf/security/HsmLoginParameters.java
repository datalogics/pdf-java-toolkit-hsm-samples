/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

/**
 * This class represents login parameters that are used to login into any HSM device.
 */
public class HsmLoginParameters {

    private final String password;

    /**
     * Instantiate the login parameters for logging in to any HSM device using the given password.
     *
     * @param password the password to use for the login
     * 
     */
    public HsmLoginParameters(final String password) {
        this.password = password;
    }

    /**
     * Get the password needed to login into a Hsm device.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
