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

package org.kie.workbench.common.stunner.forms.client.session.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GenerateDiagramFormsSessionCommandTest
        extends AbstractFormsSessionCommandTest {

    private GenerateDiagramFormsSessionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        tested = new GenerateDiagramFormsSessionCommand(formGenerationManager);
        tested.bind(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerate() {
        final ClientSessionCommand.Callback callback = mock(ClientSessionCommand.Callback.class);
        tested.execute(callback);
        verify(formGenerationService, times(1)).generateAllForms(eq(diagram));
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onError(anyObject());
    }

    @Override
    protected AbstractClientSessionCommand getCommand() {
        return tested;
    }
}
