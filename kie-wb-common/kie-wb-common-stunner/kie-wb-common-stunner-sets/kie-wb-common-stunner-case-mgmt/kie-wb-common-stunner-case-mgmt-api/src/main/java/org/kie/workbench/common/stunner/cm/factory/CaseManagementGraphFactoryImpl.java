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
package org.kie.workbench.common.stunner.cm.factory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractGraphFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

/**
 * The custom factory for Case Management graphs.
 */
@Dependent
public class CaseManagementGraphFactoryImpl extends AbstractGraphFactory implements CaseManagementGraphFactory {

    public static String START_EVENT_ID = getDefinitionId(StartNoneEvent.class);
    public static String SUBPROCESS_ID = getDefinitionId(AdHocSubprocess.class);
    public static String END_EVENT_ID = getDefinitionId(EndNoneEvent.class);
    public static String SEQ_FLOW_ID = getDefinitionId(SequenceFlow.class);
    private static final double CONNECTION_LOCATION_X = 475d;
    private static final double CONNECTION_LOCATION_Y = 475d;

    private final DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final GraphIndexBuilder<?> indexBuilder;
    private final GraphCommandManager graphCommandManager;
    private final GraphCommandFactory graphCommandFactory;
    private final FactoryManager factoryManager;
    private final BPMNGraphFactory bpmnGraphFactory;

    private Class<? extends BPMNDiagram> diagramType;

    protected CaseManagementGraphFactoryImpl() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public CaseManagementGraphFactoryImpl(final DefinitionManager definitionManager,
                                          final FactoryManager factoryManager,
                                          final RuleManager ruleManager,
                                          final GraphCommandManager graphCommandManager,
                                          final GraphCommandFactory graphCommandFactory,
                                          final GraphIndexBuilder<?> indexBuilder,
                                          final BPMNGraphFactoryImpl graphFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.ruleManager = ruleManager;
        this.graphCommandManager = graphCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.indexBuilder = indexBuilder;
        this.diagramType = CaseManagementDiagram.class;
        this.bpmnGraphFactory = graphFactory;
    }

    @PostConstruct
    public void init() {
        bpmnGraphFactory.setDiagramType(CaseManagementDiagram.class);
    }

    public void setDiagramType(final Class<? extends BPMNDiagram> diagramType) {
        this.diagramType = diagramType;
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return CaseManagementGraphFactory.class;
    }

    @Override
    public Graph<DefinitionSet, Node> build(final String uuid, final String definitionSetId) {

        final Graph<DefinitionSet, Node> graph = super.build(uuid, definitionSetId);
        final List<Command> commands = buildInitialisationCommands();
        final CompositeCommand.Builder commandBuilder = new CompositeCommand.Builder();

        commands.forEach(commandBuilder::addCommand);
        graphCommandManager.execute(createGraphContext(graph), commandBuilder.build());

        return graph;
    }

    @Override
    public boolean accepts(final String source) {
        return true;
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @SuppressWarnings("unchecked")
    protected List<Command> buildInitialisationCommands() {
        final List<Command> commands = new ArrayList<>();
        final Node<Definition<CaseManagementDiagram>, Edge> diagramNode =
                (Node<Definition<CaseManagementDiagram>, Edge>) factoryManager.newElement(UUID.uuid(), getDefinitionId(diagramType));
        commands.add(graphCommandFactory.addNode(diagramNode));
        return commands;
    }

    protected GraphCommandExecutionContext createGraphContext(final Graph graph) {
        return new DirectGraphCommandExecutionContext(definitionManager, factoryManager, indexBuilder.build(graph));
    }
}
