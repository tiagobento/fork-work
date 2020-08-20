/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.MagnetDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresHandlerFactoryImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.wires.decorator.StunnerMagnetDecorator;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresManagerFactoryImplTest {

    private WiresManagerFactoryImpl wiresManagerFactory;

    @Mock
    private StunnerWiresControlFactory wiresControlFactory;

    @Mock
    private WiresHandlerFactoryImpl wiresHandlerFactory;

    private Layer layer = new Layer();

    @Before
    public void setUp() throws Exception {
        wiresManagerFactory = new WiresManagerFactoryImpl(wiresControlFactory, wiresHandlerFactory);
    }

    @Test
    public void newWiresManager() {
        WiresManager wiresManager = wiresManagerFactory.newWiresManager(layer);
        assertEquals(wiresManager.getControlFactory(), wiresControlFactory);
        assertEquals(wiresManager.getWiresHandlerFactory(), wiresHandlerFactory);
        MagnetDecorator magnetDecorator = (MagnetDecorator) Whitebox.getInternalState(wiresManager.getMagnetManager(),
                                                                                      "m_magnetDecorator");
        assertTrue(magnetDecorator instanceof StunnerMagnetDecorator);
    }
}