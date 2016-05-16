/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

/**
 * This class represents login parameters that are used to login into any HSM device.
 */
public class HsmLoginParms {

    private final String password;

    /**
     * Instantiate the login parameters for logging in to any Hsm device using the given password.
     *
     * @param pswd - The password to use for the login
     */
    public HsmLoginParms(final String pswd) {
        this.password = pswd;
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
