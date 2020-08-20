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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.nodes;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class GatewayNodeTest<T extends BaseGateway> extends BPMNDiagramMarshallerBaseTest {

    protected final int DIAGRAM_NODE_SIZE = 10;

    public GatewayNodeTest() {
        super.init();
    }

    @Test
    public void testMarshallTopLevelGatewayFilledProperties() throws Exception {
        checkNodeMarshalling(getGatewayNodeType(), getFilledTopLevelGatewayId());
    }

    @Test
    public void testMarshallTopLevelEmptyGatewayProperties() throws Exception {
        checkNodeMarshalling(getGatewayNodeType(), getEmptyTopLevelGatewayId());
    }

    @Test
    public void testMarshallSubprocessLevelGatewayFilledProperties() throws Exception {
        checkNodeMarshalling(getGatewayNodeType(), getFilledSubprocessLevelGatewayId());
    }

    @Test
    public void testMarshallSubprocessLevelGatewayEmptyProperties() throws Exception {
        checkNodeMarshalling(getGatewayNodeType(), getEmptySubprocessLevelGatewayId());
    }

    abstract Class<T> getGatewayNodeType();

    abstract String getGatewayNodeFilePath();

    abstract String getFilledTopLevelGatewayId();

    abstract String getEmptyTopLevelGatewayId();

    abstract String getFilledSubprocessLevelGatewayId();

    abstract String getEmptySubprocessLevelGatewayId();

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId, Class<T> startType) {
        T nodeBeforeMarshalling = getGatewayNodeById(before, nodeId, startType);
        T nodeAfterMarshalling = getGatewayNodeById(after, nodeId, startType);
        assertThat(nodeBeforeMarshalling).isEqualTo(nodeAfterMarshalling);
    }

    @SuppressWarnings("unchecked")
    T getGatewayNodeById(Diagram<Graph, Metadata> diagram, String id, Class<T> type) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        return type.cast(node.getContent().getDefinition());
    }

    @SuppressWarnings("unchecked")
    void checkNodeMarshalling(Class gatewayNodeType, String nodeID) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getGatewayNodeFilePath());
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, gatewayNodeType);
    }

    void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertThat(generalSet).isNotNull();
        assertThat(generalSet.getName()).isNotNull();
        assertThat(generalSet.getDocumentation()).isNotNull();
        assertThat(generalSet.getName().getValue()).isEqualTo(nodeName);
        assertThat(generalSet.getDocumentation().getValue()).isEqualTo(documentation);
    }

    void assertGatewayExecutionSet(GatewayExecutionSet gatewayExecutionSet, String value) {
        assertThat(gatewayExecutionSet).isNotNull();
        DefaultRoute defaultRoute = gatewayExecutionSet.getDefaultRoute();
        assertThat(defaultRoute).isNotNull();
        assertThat(defaultRoute.getValue()).isEqualTo(value);
    }
}
