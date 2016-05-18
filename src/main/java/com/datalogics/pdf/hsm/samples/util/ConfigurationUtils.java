/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

/**
 * Helper class to provide Apache Configuration objects to clients.
 */
public final class ConfigurationUtils {
    /**
     * This is a utility class, and won't be instantiated.
     */
    private ConfigurationUtils() {}

    /**
     * Get a configuration object.
     *
     * @param configurationFile name of configuration file to load
     * @return a Configuration object
     * @throws ConfigurationException if configuration file could not be found
     */
    public static Configuration getConfiguration(final String configurationFile) throws ConfigurationException {
        final Configurations configs = new Configurations();
        return configs.properties(new File(configurationFile));
    }
}
