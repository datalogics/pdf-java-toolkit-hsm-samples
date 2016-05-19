/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.util;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration2.Configuration;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Unit tests for the SampleConfigurationUtils.
 */
public class SampleConfigurationUtilsTest {
    public static final String TEST_PROPERTIES_FILE = "test.properties";
    public static final String PROPERTY_1 = "property1";
    public static final String PROPERTY_1_VALUE = "alpha";

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @AfterClass
    public static void cleanUp() throws IOException {
        removePropertiesFileFromAllDirectories();
    }

    /*
     * Helper methods
     */
    private static Path getCurrentWorkingDirectoryPath() {
        final Path currentDir = Paths.get(System.getProperty("user.dir"));
        return currentDir.resolve(TEST_PROPERTIES_FILE);
    }

    private static Path getUserHomeDirectoryPath() {
        final Path homeDir = Paths.get(System.getProperty("user.home"));
        return homeDir.resolve(TEST_PROPERTIES_FILE);
    }

    private static void removePropertiesFileFromAllDirectories() throws IOException {
        Files.deleteIfExists(getCurrentWorkingDirectoryPath());
        Files.deleteIfExists(getUserHomeDirectoryPath());
    }

    private void copyPropertiesFileToCurrentWorkingDirectory() throws IOException {
        removePropertiesFileFromAllDirectories();

        final InputStream properties = this.getClass().getResourceAsStream(TEST_PROPERTIES_FILE);
        Files.copy(properties, getCurrentWorkingDirectoryPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void copyPropertiesFileToUsersHomeDirectory() throws IOException {
        removePropertiesFileFromAllDirectories();

        final InputStream properties = this.getClass().getResourceAsStream(TEST_PROPERTIES_FILE);
        Files.copy(properties, getUserHomeDirectoryPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /*
     * Unit tests
     */
    @Test
    public void findPropertiesFileInCurrentWorkingDirectory() throws IOException {
        copyPropertiesFileToCurrentWorkingDirectory();
        final Configuration testConfiguration = SampleConfigurationUtils.getConfiguration(TEST_PROPERTIES_FILE);
        assertEquals(PROPERTY_1 + " value was not correct", PROPERTY_1_VALUE, testConfiguration.getString(PROPERTY_1));
    }

    @Test
    public void findPropertiesFileInUsersHomeDirectory() throws IOException {
        copyPropertiesFileToUsersHomeDirectory();
        final Configuration testConfiguration = SampleConfigurationUtils.getConfiguration(TEST_PROPERTIES_FILE);
        assertEquals(PROPERTY_1 + " value was not correct", PROPERTY_1_VALUE, testConfiguration.getString(PROPERTY_1));
    }

    @Test
    public void throwExceptionWhenPropertiesFileIsNotFound() throws IOException {
        removePropertiesFileFromAllDirectories();
        expected.expect(IOException.class);
        SampleConfigurationUtils.getConfiguration(TEST_PROPERTIES_FILE);
    }
}
