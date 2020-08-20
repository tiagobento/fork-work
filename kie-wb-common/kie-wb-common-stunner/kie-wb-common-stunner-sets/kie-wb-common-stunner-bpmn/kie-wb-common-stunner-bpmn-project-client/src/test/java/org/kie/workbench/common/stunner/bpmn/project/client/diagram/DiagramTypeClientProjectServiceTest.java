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

package org.kie.workbench.common.stunner.bpmn.project.client.diagram;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiagramTypeClientProjectServiceTest {

    private static final String ID = "ID";
    private DiagramTypeClientProjectService tested;

    @Mock
    private BPMNDiagramService bpmnDiagramService;

    @Mock
    private Caller<BPMNDiagramService> bpmnDiagramServiceCaller;

    @Mock
    private Metadata metadata;

    @Mock
    private SessionDestroyedEvent sessionDestroyedEvent;

    @Mock
    private Path path;

    @Before
    public void setUp() throws Exception {
        when(sessionDestroyedEvent.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ID);
        when(metadata.getRoot()).thenReturn(path);
        when(bpmnDiagramService.getProjectType(path)).thenReturn(ProjectType.CASE);

        bpmnDiagramServiceCaller = new CallerMock<>(bpmnDiagramService);
        tested = new DiagramTypeClientProjectService(bpmnDiagramServiceCaller);
    }

    @Test
    public void loadDiagramType() {
        tested.loadDiagramType(metadata);
        final ProjectType projectType = tested.getProjectType(metadata);
        assertThat(projectType).isEqualTo(ProjectType.CASE);
    }

    @Test
    public void getNull() {
        ProjectType projectType = tested.getProjectType(metadata);
        assertThat(projectType).isEqualTo(ProjectType.BPMN);
    }

    @Test
    public void onSessionClosed() {
        tested.loadDiagramType(metadata);
        ProjectType projectType = tested.getProjectType(metadata);
        assertThat(projectType).isEqualTo(ProjectType.CASE);
        tested.onSessionClosed(sessionDestroyedEvent);
        projectType = tested.getProjectType(metadata);
        assertThat(projectType).isEqualTo(ProjectType.BPMN);
    }
}