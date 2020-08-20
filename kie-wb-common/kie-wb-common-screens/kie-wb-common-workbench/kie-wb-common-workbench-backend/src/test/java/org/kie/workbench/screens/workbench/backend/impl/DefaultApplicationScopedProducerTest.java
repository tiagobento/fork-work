/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.screens.workbench.backend.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultApplicationScopedProducerTest {

    private String spWatcherAutoStart = null;
    private String spDeploymentLocation = null;

    @Before
    public void storeSystemProperties() {
        spWatcherAutoStart = System.getProperty( "org.uberfire.watcher.autostart" );
        spDeploymentLocation = System.getProperty( "org.kie.deployment.desc.location" );
    }

    @After
    public void restoreSystemProperties() {
        if ( spWatcherAutoStart != null ) {
            System.setProperty( "org.uberfire.watcher.autostart",
                                spWatcherAutoStart );
        }
        if ( spDeploymentLocation != null ) {
            System.setProperty( "org.kie.deployment.desc.location",
                                spDeploymentLocation );
        }
    }

    @Test
    public void testInitProperties() {
        DefaultApplicationScopedProducer asp = new DefaultApplicationScopedProducer();
        String p1 = System.getProperty("org.uberfire.watcher.autostart");
        String p2 = System.getProperty("org.kie.deployment.desc.location");
        assertTrue(spWatcherAutoStart != null || p1.equals("false"));
        assertTrue(spDeploymentLocation != null || p2.equals("classpath:META-INF/kie-wb-deployment-descriptor.xml"));
    }
}
