/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.verifier.reporting.client.analysis;

import com.google.gwt.webworker.client.Worker;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.verifier.api.client.api.Initialize;
import org.kie.workbench.common.services.verifier.api.client.api.RequestStatus;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class VerifierWebWorkerConnectionImplTest {

    private VerifierWebWorkerConnection verifierWebWorkerConnection;

    @Mock
    private Initialize initialize;

    @Mock
    private Poster poster;

    @Mock
    private Receiver receiver;

    @Mock
    private Worker worker;

    @Before
    public void setUp() {
        verifierWebWorkerConnection = spy(new VerifierWebWorkerConnectionImpl(initialize,
                                                                              "verifier/dtableVerifier/dtableVerifier.nocache.js",
                                                                              poster,
                                                                              receiver) {
            @Override
            protected Worker createWorker() {
                return worker;
            }
        });
    }

    @Test
    public void firstActivationStartWebWorker() throws
            Exception {
        verifierWebWorkerConnection.activate();

        verify(receiver).activate();
        verify(receiver).setUp(any());

        verify(poster).setUp(any());
        verify(poster).post(any(Initialize.class));
    }

    @Test
    public void secondActivationDoesNotStartWebWorker() throws
            Exception {
        verifierWebWorkerConnection.activate();

        reset(receiver,
              poster);

        verifierWebWorkerConnection.activate();

        verify(receiver).activate();
        verify(receiver,
               never()).setUp(any());

        verify(poster,
               never()).setUp(any());
        verify(poster).post(any(RequestStatus.class));
    }

    @Test
    public void terminateCanBeCalledEvenIfWorkerHasNotBeenActivated() throws
            Exception {

        verifierWebWorkerConnection.activate();

        verifierWebWorkerConnection.terminate();

        verify(worker).terminate();
    }
}