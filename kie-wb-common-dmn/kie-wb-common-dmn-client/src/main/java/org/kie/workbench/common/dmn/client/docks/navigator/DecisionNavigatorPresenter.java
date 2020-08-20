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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.appformer.client.context.Channel.DEFAULT;
import static org.appformer.client.context.Channel.VSCODE;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;

@ApplicationScoped
@WorkbenchScreen(identifier = DecisionNavigatorPresenter.IDENTIFIER)
public class DecisionNavigatorPresenter {

    public static final String IDENTIFIER = "org.kie.dmn.decision.navigator";

    private View view;

    private DecisionNavigatorTreePresenter treePresenter;

    private DecisionComponents decisionComponents;

    private DecisionNavigatorObserver decisionNavigatorObserver;

    private DecisionNavigatorChildrenTraverse navigatorChildrenTraverse;

    private DecisionNavigatorItemFactory itemFactory;

    private TranslationService translationService;

    private CanvasHandler handler;

    private EditorContextProvider context;

    protected DecisionNavigatorPresenter() {
        //CDI proxy
    }

    @Inject
    public DecisionNavigatorPresenter(final View view,
                                      final DecisionNavigatorTreePresenter treePresenter,
                                      final DecisionComponents decisionComponents,
                                      final DecisionNavigatorObserver decisionNavigatorObserver,
                                      final DecisionNavigatorChildrenTraverse navigatorChildrenTraverse,
                                      final DecisionNavigatorItemFactory itemFactory,
                                      final TranslationService translationService,
                                      final EditorContextProvider context) {
        this.view = view;
        this.treePresenter = treePresenter;
        this.decisionComponents = decisionComponents;
        this.decisionNavigatorObserver = decisionNavigatorObserver;
        this.navigatorChildrenTraverse = navigatorChildrenTraverse;
        this.itemFactory = itemFactory;
        this.translationService = translationService;
        this.context = context;
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(DecisionNavigatorPresenter_DecisionNavigator);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @PostConstruct
    void setup() {
        initialize();
        setupView();
    }

    public void onRefreshDecisionComponents(final @Observes RefreshDecisionComponents events) {
        refreshComponentsView();
    }

    public DecisionNavigatorTreePresenter getTreePresenter() {
        return treePresenter;
    }

    public Diagram getDiagram() {
        return handler.getDiagram();
    }

    public CanvasHandler getHandler() {
        return handler;
    }

    public void setHandler(final CanvasHandler handler) {
        this.handler = handler;

        refreshTreeView();
        refreshComponentsView();
    }

    public void addOrUpdateElement(final Element<?> element) {

        if (!isNode(element)) {
            return;
        }

        treePresenter.addOrUpdateItem(makeItem(element));
    }

    public void updateElement(final Element<?> element) {

        if (!isNode(element)) {
            return;
        }

        treePresenter.updateItem(makeItem(element));
    }

    public void removeElement(final Element<?> element) {

        if (!isNode(element)) {
            return;
        }

        treePresenter.remove(makeItem(element));
    }

    public void removeAllElements() {
        treePresenter.removeAllItems();
        decisionComponents.removeAllItems();
    }

    void initialize() {
        view.init(this);
        decisionNavigatorObserver.init(this);
    }

    void setupView() {
        view.setupMainTree(treePresenter.getView());
        final Channel channel = context.getChannel();
        if (Objects.equals(channel, VSCODE) || Objects.equals(channel, DEFAULT)) {
            view.showDecisionComponentsContainer();
            view.setupDecisionComponents(decisionComponents.getView());
        } else {
            view.hideDecisionComponentsContainer();
        }
    }

    public void refreshTreeView() {
        treePresenter.setupItems(getItems());
    }

    public void refreshComponentsView() {
        getOptionalHandler().ifPresent(handler -> decisionComponents.refresh(handler.getDiagram()));
    }

    List<DecisionNavigatorItem> getItems() {
        return getGraph().map(navigatorChildrenTraverse::getItems).orElse(emptyList());
    }

    public Optional<Graph> getGraph() {
        return getOptionalHandler()
                .map((Function<CanvasHandler, Diagram>) CanvasHandler::getDiagram)
                .map(Diagram::getGraph);
    }

    Optional<CanvasHandler> getOptionalHandler() {
        return ofNullable(this.handler);
    }

    DecisionNavigatorItem makeItem(final Element<?> element) {

        final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node =
                (Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>) element.asNode();

        return itemFactory.makeItem(node);
    }

    private boolean isNode(final Element<?> element) {
        return element instanceof Node;
    }

    public void clearSelections() {
        getTreePresenter().deselectItem();
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter>,
                                  IsElement {

        void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent);

        void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent);

        void showDecisionComponentsContainer();

        void hideDecisionComponentsContainer();
    }
}
