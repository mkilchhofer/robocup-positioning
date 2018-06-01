/*
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package info.kilchhofer.bfh.lidar.hardwareservice.test.service;

import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.IResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Simple example of how to embed the broker in another project
 * */
public final class EmbeddedBrokerLauncher {
    private static final Logger LOGGER = LogManager.getLogger(EmbeddedBrokerLauncher.class);

    public EmbeddedBrokerLauncher() throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

        LOGGER.info("Host: '{}', Port: '{}', allow_anonymous: {}",
                classPathConfig.getProperty("host"),
                classPathConfig.getProperty("port"));

        final Server mqttBroker = new Server();
        mqttBroker.startServer(classPathConfig);

        LOGGER.info("Broker started press [CTRL+C] to stop");
        //Bind  a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Stopping broker");
            mqttBroker.stopServer();
            LOGGER.info("Broker stopped");
        }));

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new EmbeddedBrokerLauncher();
    }
}
