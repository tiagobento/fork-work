/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.workbench.client.test;

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.docks.AuthoringWorkbenchDocks;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TestReportingDocksHandlerTest {

    @Mock
    private AuthoringWorkbenchDocks authoringWorkbenchDocks;

    @InjectMocks
    private TestReportingDocksHandler testReportingDocksHandler;

    @Test
    public void amountOfItems() {
        assertEquals(1, testReportingDocksHandler.provideDocks("id").size());
    }

    @Test
    public void expandTestResultsDock() {
        final Collection<UberfireDock> docks = testReportingDocksHandler.provideDocks("id");
        final UberfireDock dock = docks.iterator().next();

        testReportingDocksHandler.expandTestResultsDock();

        verify(authoringWorkbenchDocks).expandAuthoringDock(dock);
    }
}