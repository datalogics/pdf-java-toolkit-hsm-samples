/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.security;

/**
 * This class represents login parameters that are used to login into a LunaSa HSM device.
 */
public class LunaHsmLoginParms extends HsmLoginParms {

    private final String tokenLabel;

    public LunaHsmLoginParms(final String pswd) {
        super(pswd);
        this.tokenLabel = null;
    }

    public LunaHsmLoginParms(final String tokenLbl, final String pswd) {
        super(pswd);
        this.tokenLabel = tokenLbl;
    }

    public String getTokenLabel() {
        return tokenLabel;
    }
}
