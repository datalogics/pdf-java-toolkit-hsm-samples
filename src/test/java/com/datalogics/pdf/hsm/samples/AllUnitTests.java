/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.hsm.samples;

import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;

/**
 * Run all the unit tests.
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses({ "**/*Test.class", "!**/*IntegrationTest.class" })
public class AllUnitTests {

}
