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

package org.kie.workbench.common.dmn.client.docks.navigator.factories;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ROOT;

@Dependent
public class DecisionNavigatorItemFactory {

    private DecisionNavigatorBaseItemFactory baseItemFactory;

    @Inject
    public DecisionNavigatorItemFactory(final DecisionNavigatorBaseItemFactory baseItemFactory) {
        this.baseItemFactory = baseItemFactory;
    }

    public DecisionNavigatorItem makeRoot(final Node<View, Edge> node) {
        return baseItemFactory.makeItem(node, ROOT);
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node) {
        final String nodeClassName = Optional.ofNullable(DefinitionUtils.getElementDefinition(node))
                .map(elementDefinition -> elementDefinition.getClass().getSimpleName())
                .orElse(Node.class.getSimpleName());

        return baseItemFactory.makeItem(node, Type.ofExpressionNodeClassName(nodeClassName));
    }
}
