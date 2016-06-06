/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * Unit test for LunaHsmLoginParameters class.
 */
public class LunaHsmLoginParametersTest {
    public static final String PASSWORD = "password";
    public static final String TOKEN_LABEL = "token_label";

    @Test
    public void tokenLabelIsNullForSingleArgConstructor() {
        final LunaHsmLoginParameters parameters = new LunaHsmLoginParameters(PASSWORD);
        assertNull("Token label should be null", parameters.getTokenLabel());
        assertEquals("Password does not match", PASSWORD, parameters.getPassword());
    }

    @Test
    public void tokenLabelIsRetreiveable() {
        final LunaHsmLoginParameters parameters = new LunaHsmLoginParameters(TOKEN_LABEL, PASSWORD);
        assertEquals("Token label does not match", TOKEN_LABEL, parameters.getTokenLabel());
        assertEquals("Password does not match", PASSWORD, parameters.getPassword());
    }
}
