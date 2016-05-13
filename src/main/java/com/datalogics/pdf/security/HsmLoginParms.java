/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

/**
 * This class represents login parameters that are used to login into any HSM device.
 */
public class HsmLoginParms {

    private final String password;

    public HsmLoginParms(final String pswd) {
        this.password = pswd;
    }

    public String getPassword() {
        return password;
    };
}
