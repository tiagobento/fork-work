/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class EventCancelActivityViewHandlerTest extends EventViewHandlerTestBase {

    private EventCancelActivityViewHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        when(child1.getPrimitiveId()).thenReturn(EventCancelActivityViewHandler.ID_INTERMEDIATE);
        tested = new EventCancelActivityViewHandler();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleTimerIsNotCancel() {
        final IntermediateTimerEvent bean =
                new IntermediateTimerEvent();
        bean.getExecutionSet().getCancelActivity().setValue(false);
        tested.handle(bean, view);
        verify(prim1).setFillAlpha(eq(0d));
        verify(prim1).setStrokeAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleTimerIsCancel() {
        final IntermediateTimerEvent bean =
                new IntermediateTimerEvent();

        bean.getExecutionSet().getCancelActivity().setValue(true);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(1d));
        verify(prim1).setStrokeAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleSignalIsNotCancel() {
        final IntermediateSignalEventCatching bean =
                new IntermediateSignalEventCatching();

        bean.getExecutionSet().getCancelActivity().setValue(false);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(0d));
        verify(prim1).setStrokeAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleSignalIsCancel() {
        final IntermediateSignalEventCatching bean =
                new IntermediateSignalEventCatching();

        bean.getExecutionSet().getCancelActivity().setValue(true);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(1d));
        verify(prim1).setStrokeAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleMessageIsNotCancel() {
        final IntermediateMessageEventCatching bean =
                new IntermediateMessageEventCatching();

        bean.getExecutionSet().getCancelActivity().setValue(false);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(0d));
        verify(prim1).setStrokeAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleMessageIsCancel() {
        final IntermediateMessageEventCatching bean =
                new IntermediateMessageEventCatching();

        bean.getExecutionSet().getCancelActivity().setValue(true);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(1d));
        verify(prim1).setStrokeAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleConditionalIsNotCancel() {
        final IntermediateConditionalEvent bean =
                new IntermediateConditionalEvent();

        bean.getExecutionSet().getCancelActivity().setValue(false);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(0d));
        verify(prim1).setStrokeAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleConditionalIsCancel() {
        final IntermediateConditionalEvent bean =
                new IntermediateConditionalEvent();

        bean.getExecutionSet().getCancelActivity().setValue(true);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(1d));
        verify(prim1).setStrokeAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleEscalationIsNotCancel() {
        final IntermediateEscalationEvent bean =
                new IntermediateEscalationEvent();

        bean.getExecutionSet().getCancelActivity().setValue(false);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(0d));
        verify(prim1).setStrokeAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleEscalationIsCancel() {
        final IntermediateEscalationEvent bean =
                new IntermediateEscalationEvent();

        bean.getExecutionSet().getCancelActivity().setValue(true);
        tested.handle(bean, view);

        verify(prim1).setFillAlpha(eq(1d));
        verify(prim1).setStrokeAlpha(eq(0d));
    }
}
