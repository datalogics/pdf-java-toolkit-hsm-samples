/*
 * Copyright 2016 Datalogics Inc.
 */

package com.datalogics.pdf.hsm.samples.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.BasePathLocationStrategy;
import org.apache.commons.configuration2.io.CombinedLocationStrategy;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.HomeDirectoryLocationStrategy;
import org.apache.commons.configuration2.io.ProvidedURLLocationStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to provide Apache Configuration objects to clients.
 */
public final class SampleConfigurationUtils {
    /**
     * This is a utility class, and won't be instantiated.
     */
    private SampleConfigurationUtils() {}

    /**
     * Get a configuration object.
     *
     * <p>
     * You can specify a path to the configuration file; by default, it will search in the current working directory and
     * the user's home directory, in that order.
     *
     * @param configurationFile name of configuration file to load
     * @return a Configuration object
     * @throws IOException if configuration file could not be loaded
     */
    public static Configuration getConfiguration(final String configurationFile) throws IOException {
        try {
            final Parameters parameters = new Parameters();
            final FileBasedConfigurationBuilder<FileBasedConfiguration> builder = newPropertiesBuilder();
            builder.configure(parameters.fileBased()
                                        .setLocationStrategy(buildDefaultLocationStrategy())
                                        .setFileName(configurationFile));
            return builder.getConfiguration();
        } catch (final ConfigurationException e) {
            throw new IOException("Could not load configuration file '" + configurationFile + "'", e);
        }
    }

    /**
     * Build a location strategy for finding a configuration file.
     *
     * <p>
     * This strategy searches the following locations in order:
     * <ul>
     * <li>A provided URL</li>
     * <li>The current working directory</li>
     * <li>The user's home directory</li>
     * </ul>
     *
     * @return a strategy for finding a configuration file
     */
    private static FileLocationStrategy buildDefaultLocationStrategy() {
        final List<FileLocationStrategy> strategies = Arrays.asList(new ProvidedURLLocationStrategy(),
                                                                    new BasePathLocationStrategy(),
                                                                    new HomeDirectoryLocationStrategy());
        return new CombinedLocationStrategy(strategies);
    }

    private static FileBasedConfigurationBuilder<FileBasedConfiguration> newPropertiesBuilder() {
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
    }
}
