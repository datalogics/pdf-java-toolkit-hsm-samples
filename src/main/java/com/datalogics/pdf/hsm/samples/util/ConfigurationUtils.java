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

import java.util.ArrayList;
import java.util.List;

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
        final Parameters parameters = new Parameters();
        final FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                                                                                                                                                                      .configure(parameters.fileBased()
                                                                                                                                                                                           .setLocationStrategy(buildDefaultLocationStrategy())
                                                                                                                                                                                           .setFileName(configurationFile));
        return builder.getConfiguration();
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
        final List<FileLocationStrategy> strategies = new ArrayList<FileLocationStrategy>();
        strategies.add(new ProvidedURLLocationStrategy());
        strategies.add(new BasePathLocationStrategy());
        strategies.add(new HomeDirectoryLocationStrategy());
        return new CombinedLocationStrategy(strategies);
    }
}
