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

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@Dependent
@CaseManagementEditor
public class CaseManagementCanvasHandler<D extends Diagram, C extends WiresCanvas> extends CanvasHandlerImpl<D, C> {

    @Inject
    public CaseManagementCanvasHandler(final ClientDefinitionManager clientDefinitionManager,
                                       final RuleManager ruleManager,
                                       final GraphUtils graphUtils,
                                       final GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder,
                                       final ShapeManager shapeManager,
                                       final TextPropertyProviderFactory textPropertyProviderFactory,
                                       final Event<CanvasElementAddedEvent> canvasElementAddedEvent,
                                       final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent,
                                       final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                                       final Event<CanvasElementsClearEvent> canvasElementsClearEvent,
                                       final @CaseManagementEditor CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        super(clientDefinitionManager,
              canvasCommandFactory,
              ruleManager,
              graphUtils,
              indexBuilder,
              shapeManager,
              textPropertyProviderFactory,
              canvasElementAddedEvent,
              canvasElementRemovedEvent,
              canvasElementUpdatedEvent,
              canvasElementsClearEvent);
    }

    @Override
    public boolean isCanvasRoot(final Element parent) {
        return false;
    }

    @Override
    protected boolean isCanvasRoot(final String pUUID) {
        return false;
    }

    @Override
    public void register(final Shape shape,
                         final Element<View<?>> candidate,
                         final boolean fireEvents) {
        if (!isRenderable(shape)) {
            return;
        }
        super.register(shape,
                       candidate,
                       fireEvents);

        ((CaseManagementShapeView) shape.getShapeView())
                .setCanvas((CaseManagementCanvas) this.getCanvas());
    }

    @Override
    public void deregister(final Shape shape,
                           final Element element,
                           final boolean fireEvents) {
        if (!isRenderable(shape)) {
            return;
        }
        super.deregister(shape,
                         element,
                         fireEvents);
    }

    @Override
    public void addShape(final Shape shape) {
        if (!isRenderable(shape)) {
            return;
        }
        super.addShape(shape);
    }

    @Override
    public void addChild(final Element parent,
                         final Element child) {
        final Shape parentShape = getCanvas().getShape(parent.getUUID());
        final Shape childShape = getCanvas().getShape(child.getUUID());
        if (!isRenderable(parentShape,
                          childShape)) {
            return;
        }
        super.addChild(parent,
                       child);
    }

    @SuppressWarnings("unchecked")
    public void addChild(final Element parent,
                         final Element child,
                         final int index) {
        final Shape parentShape = getCanvas().getShape(parent.getUUID());
        final Shape childShape = getCanvas().getShape(child.getUUID());
        if (!isRenderable(parentShape,
                          childShape)) {
            return;
        }

        final CaseManagementCanvas cmCanvas = (CaseManagementCanvas) getCanvas();
        cmCanvas.addChild(parentShape,
                          childShape,
                          index);
    }

    @Override
    public void removeShape(final Shape shape) {
        if (!isRenderable(shape)) {
            return;
        }
        super.removeShape(shape);
    }

    @Override
    public void removeChild(final Element parent,
                            final Element child) {
        final Shape parentShape = getCanvas().getShape(parent.getUUID());
        final Shape childShape = getCanvas().getShape(child.getUUID());
        if (!isRenderable(parentShape,
                          childShape)) {
            return;
        }
        super.removeChild(parent,
                          child);
    }

    @Override
    public void applyElementMutation(final Shape shape,
                                     final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        if (!isRenderable(shape)) {
            return;
        }

        if (shape instanceof CaseManagementShape) {
            CaseManagementShape caseManagementShape = (CaseManagementShape) shape;

            if (applyProperties) {
                applyElementTitle(caseManagementShape,
                                  candidate);

                applyProperties(caseManagementShape,
                                candidate);
            }

            caseManagementShape.getShapeView().refresh();
        }

        this.applyElementMutation(shape,
                                  candidate);
    }

    private void applyElementTitle(final CaseManagementShape shape,
                                   final Element candidate) {
        final TextPropertyProvider textPropertyProvider = this.getTextPropertyProviderFactory().getProvider(candidate);
        final String name = textPropertyProvider.getText(candidate);
        ((CaseManagementShapeView) shape.getShapeView()).setLabel(name);
    }

    @SuppressWarnings("unchecked")
    private void applyProperties(final CaseManagementShape shape,
                                 final Element candidate) {
        shape.getShapeDef()
                .viewHandler()
                .accept(((Element<? extends View>) candidate).getContent().getDefinition(),
                        shape.getShapeView());
    }

    @Override
    public void applyElementMutation(final Element candidate,
                                     final boolean applyPosition,
                                     final boolean applyProperties,
                                     final MutationContext mutationContext) {
        final Shape candidateShape = getCanvas().getShape(candidate.getUUID());
        if (!isRenderable(candidateShape)) {
            return;
        }
        super.applyElementMutation(candidate,
                                   applyPosition,
                                   applyProperties,
                                   mutationContext);
    }

    boolean isRenderable(final Shape... shapes) {
        for (Shape shape : shapes) {
            if (shape == null) {
                return false;
            }
        }
        return true;
    }

    public Optional<Element> getDiagramNode() {
        return this.getCanvas().getShapes().stream().map(Shape::getUUID).map(this::getElement)
                .filter(Optional::isPresent).map(Optional::get)
                .filter(n -> ((View) n.getContent()).getDefinition() instanceof CaseManagementDiagram)
                .findAny();
    }

    @Override
    public Optional<Element> getElementAt(double x, double y) {
        if (Double.compare(x, 0d) <= 0 || Double.compare(y, 0d) <= 0) {
            return Optional.empty();
        }

        final Point2D transform = this.getAbstractCanvas().getTransform().transform(x, y);
        final Optional<Element> parent = super.getElementAt(transform.getX(), transform.getY());

        // Return the root diagram if there is no element at the given location
        return parent.isPresent() ? parent : this.getDiagramNode();
    }
}
