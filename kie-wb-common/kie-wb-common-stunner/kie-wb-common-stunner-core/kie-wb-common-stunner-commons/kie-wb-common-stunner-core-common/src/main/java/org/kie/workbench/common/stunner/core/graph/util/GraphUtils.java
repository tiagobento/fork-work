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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GraphUtils {

    public static Object getProperty(final DefinitionManager definitionManager,
                                     final Element<? extends Definition> element,
                                     final String id) {

        return Optional.ofNullable(element)
                .map(Element::getContent)
                .map(Definition::getDefinition)
                .map(def -> Exceptions.<Set>swallow(() -> definitionManager.adapters().forDefinition().getProperties(def),
                                                    Collections.emptySet()))
                .map(properties -> Exceptions.swallow(() -> getProperty(definitionManager, properties, id), null))
                .orElseGet(
                        //getting by field if not found by the id (class name)
                        () -> Optional.ofNullable(element)
                                .map(Element::getContent)
                                .map(Definition::getDefinition)
                                .map(def -> getPropertyByField(definitionManager, def, id))
                                .orElse(null)
                );
    }

    public static Object getPropertyByField(final DefinitionManager definitionManager,
                                            final Object bean,
                                            final String field) {
        return getPropertyByField(currentField -> definitionManager.adapters().forDefinition().getProperty(bean, currentField),
                                  (ps, currentField) -> definitionManager.adapters().forPropertySet().getProperty(ps, currentField),
                                  field);
    }

    private static Object getPropertyByField(final Function<String, Optional<?>> propertyProvider,
                                             final BiFunction<Object, String, Optional<?>> childPropertiesProvider,
                                             final String field) {
        final int index = field.indexOf('.');
        final String firstField = index > -1 ? field.substring(0, index) : field;
        final Optional<?> optionalBean = propertyProvider.apply(firstField);
        final Object bean = optionalBean.orElse(null);
        return (index > 0 && null != bean)
                ? getPropertyByField(currentField -> childPropertiesProvider.apply(bean, currentField),
                                     childPropertiesProvider,
                                     field.substring(index + 1))
                : bean;
    }

    public static Object getProperty(final DefinitionManager definitionManager,
                                     final Set properties,
                                     final String id) {
        if (null != id && null != properties) {
            for (final Object property : properties) {
                final String pId = definitionManager.adapters().forProperty().getId(property);
                if (pId.equals(id)) {
                    return property;
                }
            }
        }
        return null;
    }

    public static int countEdges(final DefinitionManager definitionManager,
                                 final String edgeId,
                                 final Collection<? extends Edge> edges) {
        if (null == edges) {
            return 0;
        }

        return (int) edges.stream()
                .map(edge -> getElementDefinitionId(definitionManager, edge))
                .filter(edgeId::equals)
                .count();
    }

    /**
     * Does not returns labels not being used on the graph,
     * even if included in the <code>filter</code>.
     */
    public static Map<String, Integer> getLabelsCount(final Graph<?, ? extends Node> target,
                                                      final Set<String> roleFilter) {
        return getLabelsCount(target,
                              e -> true,
                              roleFilter);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> getLabelsCount(final Graph<?, ? extends Node> target,
                                                      final Predicate<Element<?>> elementFilter,
                                                      final Set<String> roleFilter) {

        final Map<String, Integer> labelsCount = new HashMap<>();
        final Iterable<Node> nodes = (Iterable<Node>) target.nodes();
        StreamSupport.stream(nodes.spliterator(), false)
                .filter(elementFilter::test)
                .forEach(node -> computeLabelsCount(node, labelsCount, roleFilter));
        return labelsCount;
    }

    @SuppressWarnings("unchecked")
    public static void computeLabelsCount(final Node node,
                                          final Map<String, Integer> labelsCount,
                                          final Set<String> roleFilter) {
        final Set<String> labels = getLabels(node);
        labels
                .stream()
                .filter(role -> null == roleFilter || roleFilter.contains(role))
                .forEach(role -> {
                    final Integer i = labelsCount.get(role);
                    labelsCount.put(role,
                                    null != i ? i + 1 : 1);
                });
    }

    public enum CardinalityCountState {
        EMPTY,
        SINGLE_NODE,
        MULTIPLE_NODES
    }

    public static CardinalityCountState computeCardinalityState(final Diagram diagram) {
        final String rootUUID = diagram.getMetadata().getCanvasRootUUID();
        final Graph graph = diagram.getGraph();
        final Iterator nodes = graph.nodes().iterator();
        final Node firstElement = nodes.hasNext() ? ((Element) nodes.next()).asNode() : null;
        final Node secondElement = null != firstElement && nodes.hasNext() ? ((Element) nodes.next()).asNode() : null;
        final Node thirdElement = null != secondElement && nodes.hasNext() ? ((Element) nodes.next()).asNode() : null;
        if (null != thirdElement) {
            return CardinalityCountState.MULTIPLE_NODES;
        }
        if (null != secondElement && secondElement.getUUID().equals(rootUUID)) {
            return CardinalityCountState.SINGLE_NODE;
        }
        if (null != firstElement && firstElement.getUUID().equals(rootUUID)) {
            return null != secondElement ? CardinalityCountState.SINGLE_NODE : CardinalityCountState.EMPTY;
        }
        if (null != firstElement && null != secondElement) {
            return CardinalityCountState.MULTIPLE_NODES;
        }
        return null != firstElement ? CardinalityCountState.SINGLE_NODE : CardinalityCountState.EMPTY;
    }

    public static Set<String> getLabels(final Element<? extends Definition<?>> element) {
        return element != null && null != element.getLabels() ? element.getLabels() : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public static Element<?> getParent(final Node<?, ? extends Edge> element) {
        return Optional.ofNullable(element)
                .map(Node::getInEdges)
                .orElse(Collections.emptyList())
                .stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .map(Edge::getSourceNode)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static Optional<Element<? extends Definition>> getParentByDefinitionId(final DefinitionManager definitionManager,
                                                                                  final Function<Node, Element> parentSupplier,
                                                                                  final Node<?, ? extends Edge> candidate,
                                                                                  final Predicate<String> parentDefIdFilter) {
        checkNotNull("candidate",
                     candidate);
        Element<?> p = parentSupplier.apply(candidate);
        while (null != p && null != p.asNode() && p.getContent() instanceof Definition) {
            final String cID = getElementDefinitionId(definitionManager, p);
            if (parentDefIdFilter.test(cID)) {
                return Optional.of((Element<? extends Definition>) p);
            }
            p = parentSupplier.apply(p.asNode());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Optional<Element<? extends Definition>> getParentByDefinitionId(final DefinitionManager definitionManager,
                                                                                  final Function<Node, Element> parentSupplier,
                                                                                  final Node<?, ? extends Edge> candidate,
                                                                                  final String parentDefId) {
        checkNotNull("candidate",
                     candidate);
        checkNotNull("parentDefId",
                     parentDefId);
        return getParentByDefinitionId(definitionManager,
                                       parentSupplier,
                                       candidate,
                                       parentDefId::equals);
    }

    public static Point2D getPosition(final View element) {
        final Bound ul = element.getBounds().getUpperLeft();
        final double x = ul.getX();
        final double y = ul.getY();
        return new Point2D(x,
                           y);
    }

    public static Point2D getComputedPosition(final Node<?, ? extends Edge> element) {
        double x = 0;
        double y = 0;
        Element<?> parent = element;
        while (null != parent
                && null != parent.asNode()
                && parent.getContent() instanceof View) {
            final Point2D position = getPosition((View) parent.getContent());
            x += position.getX();
            y += position.getY();
            parent = getParent((Node<?, ? extends Edge>) parent);
        }
        return new Point2D(x,
                           y);
    }

    public static double[] getNodeSize(final View element) {
        return getSize(element.getBounds());
    }

    private static double[] getSize(Bounds bounds) {
        final Bound ul = bounds.getUpperLeft();
        final Bound lr = bounds.getLowerRight();
        final double w = lr.getX() - ul.getX();
        final double h = lr.getY() - ul.getY();
        return new double[]{Math.abs(w), Math.abs(h)};
    }

    public static boolean isRootNode(Element<? extends View<?>> element, final Graph<DefinitionSet, Node> graph) {
        if (element instanceof Node) {
            Node node = (Node) element;

            final Element<?> parent = GraphUtils.getParent(node);
            return (parent == null);
        }

        return false;
    }

    public static boolean checkBoundsExceeded(final Bounds parentBounds,
                                              final Bounds bounds) {
        if (null == parentBounds) {
            return true;
        }
        if (parentBounds.hasUpperLeft()) {
            if ((bounds.getUpperLeft().getX() < parentBounds.getUpperLeft().getX())
                    || (bounds.getUpperLeft().getY() < parentBounds.getUpperLeft().getY())) {
                return false;
            }
        }
        if (parentBounds.hasLowerRight()) {
            if ((bounds.getLowerRight().getX() > parentBounds.getLowerRight().getX())
                    || (bounds.getLowerRight().getY() > parentBounds.getLowerRight().getY())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first node in the graph structure for the given type.
     * @param graph The graph structure.
     * @param type The Definition type..
     */
    @SuppressWarnings("unchecked")
    public static <C> Node<Definition<C>, ?> getFirstNode(final Graph<?, Node> graph,
                                                          final Class<?> type) {
        if (null != graph) {
            for (final Node node : graph.nodes()) {
                final Object content = node.getContent();
                try {
                    final Definition definitionContent = (Definition) content;
                    if (instanceOf(definitionContent.getDefinition(),
                                   type)) {
                        return node;
                    }
                } catch (final ClassCastException e) {
                    // Node content does not contains a definition.
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static boolean hasChildren(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getOutEdges()) ?
                element.getOutEdges().stream()
                        .anyMatch(edge -> (edge.getContent() instanceof Child)) :
                false;
    }

    public static boolean hasDockedNodes(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getOutEdges()) ?
                element.getOutEdges()
                        .stream()
                        .anyMatch(edge -> (edge.getContent() instanceof Dock)) :
                false;
    }

    public static boolean hasConnections(Node<? extends Definition<?>, ? extends Edge> node) {
        return Stream.concat(node.getInEdges().stream(), node.getOutEdges().stream())
                .anyMatch(hasConnectionFilter());
    }

    public static boolean hasTargetConnections(Node<? extends Definition<?>, ? extends Edge> node) {
        return node.getInEdges().stream().anyMatch(hasConnectionFilter());
    }

    public static Predicate<Element> isContentSomeDefinition() {
        return e -> e.getContent() instanceof Definition;
    }

    private static Predicate<Edge> hasConnectionFilter() {
        return edge -> edge.getContent() instanceof ViewConnector;
    }

    public static List<Node> getDockedNodes(final Node<?, ? extends Edge> element) {
        return getNodesFromOutEdges(element, edge -> (edge.getContent() instanceof Dock));
    }

    public static List<Node> getChildNodes(final Node<?, ? extends Edge> element) {
        return getNodesFromOutEdges(element, edge -> (edge.getContent() instanceof Child));
    }

    private static List<Node> getNodesFromOutEdges(final Node<?, ? extends Edge> element, Predicate<Edge> filter) {
        Objects.requireNonNull(element.getOutEdges());
        return element.getOutEdges()
                .stream()
                .filter(filter::test)
                .map(Edge::getTargetNode)
                .collect(Collectors.toList());
    }

    public static List<Edge<? extends ViewConnector<?>, Node>> getSourceConnections(
            final Node<?, ? extends Edge> element) {
        Objects.requireNonNull(element.getOutEdges());
        return getConnections(element.getOutEdges());
    }

    public static List<Edge<? extends ViewConnector<?>, Node>> getTargetConnections(
            final Node<?, ? extends Edge> element) {
        Objects.requireNonNull(element.getInEdges());
        return getConnections(element.getInEdges());
    }

    private static List<Edge<? extends ViewConnector<?>, Node>> getConnections(List<? extends Edge> edges) {
        return edges.stream()
                .filter(edge -> (edge.getContent() instanceof ViewConnector))
                .map(edge -> (Edge<? extends ViewConnector<?>, Node>) edge)
                .collect(Collectors.toList());
    }

    public static boolean isDockedNode(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getInEdges()) ?
                element.getInEdges()
                        .stream()
                        .anyMatch(edge -> edge.getContent() instanceof Dock) :
                false;
    }

    public static Optional<Node> getDockParent(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getInEdges()) ?
                element.getInEdges()
                        .stream()
                        .filter(edge -> edge.getContent() instanceof Dock)
                        .map(Edge::getSourceNode)
                        .findFirst() :
                Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Long countChildren(final Node<?, ? extends Edge> element) {
        return element.getOutEdges().stream()
                .filter(edge -> (edge.getContent() instanceof Child)).count();
    }

    private static String getElementDefinitionId(final DefinitionManager definitionManager,
                                                 final Element<?> element) {
        String targetId = null;
        if (element.getContent() instanceof Definition) {
            final Object definition = ((Definition) element.getContent()).getDefinition();
            targetId = definitionManager.adapters().forDefinition().getId(definition).value();
        } else if (element.getContent() instanceof DefinitionSet) {
            targetId = ((DefinitionSet) element.getContent()).getDefinition();
        }
        return targetId;
    }

    private static boolean instanceOf(final Object item,
                                      final Class<?> clazz) {
        return null != item && item.getClass().equals(clazz);
    }

    public static class ParentPredicate {

        private final Function<Node, Element> parentSupplier;

        public ParentPredicate(final Function<Node, Element> parentSupplier) {
            this.parentSupplier = parentSupplier;
        }

        public BiPredicate<Node<?, ? extends Edge>, Element<?>> isChildOf() {
            return (candidate, parent) -> {
                if (null != candidate) {
                    Element<?> p = parentSupplier.apply(candidate);
                    while (null != p) {
                        if (Objects.equals(p, parent)) {
                            return true;
                        }
                        if (p instanceof Node) {
                            p = parentSupplier.apply((Node) p);
                        }
                    }
                }
                return false;
            };
        }
    }

    public static OptionalInt getChildIndex(final Element element,
                                            final String uuid) {
        if (!(element instanceof Node)) {
            return OptionalInt.empty();
        }

        final Node<?, ?> node = (Node<?, ? extends Edge>) element;
        return Objects.nonNull(node.getOutEdges()) ?
                IntStream.range(0, node.getOutEdges().size())
                        .filter(i -> uuid.equals(node.getOutEdges().get(i).getTargetNode().getUUID())).findFirst() :
                OptionalInt.empty();
    }
}
