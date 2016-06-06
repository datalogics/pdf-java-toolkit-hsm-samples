/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for HsmLoginParameters class.
 */
public class HsmLoginParametersTest {
    public static final String PASSWORD = "password";

    @Test
    public void passwordIsRetreiveable() {
        final HsmLoginParameters parameters = new HsmLoginParameters(PASSWORD);
        assertEquals("Password does not match", PASSWORD, parameters.getPassword());
    }
}
