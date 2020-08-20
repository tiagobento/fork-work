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

import java.util.function.Consumer;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractFormsSessionCommandTest {

    @Mock
    protected EditorSession session;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected Diagram diagram;

    @Mock
    protected ClientFormGenerationManager formGenerationManager;

    @Mock
    protected FormGenerationService formGenerationService;

    @SuppressWarnings("unchecked")
    public void init() {
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        doAnswer(invocationOnMock -> {
            final Consumer<FormGenerationService> consumer =
                    (Consumer<FormGenerationService>) invocationOnMock.getArguments()[0];
            consumer.accept(formGenerationService);
            return null;
        }).when(formGenerationManager).call(any(Consumer.class));
    }

    @Test
    public void testAcceptsSession() {
        assertTrue(getCommand().accepts(mock(EditorSession.class)));
        assertFalse(getCommand().accepts(mock(ViewerSession.class)));
    }

    protected abstract AbstractClientSessionCommand getCommand();
}
