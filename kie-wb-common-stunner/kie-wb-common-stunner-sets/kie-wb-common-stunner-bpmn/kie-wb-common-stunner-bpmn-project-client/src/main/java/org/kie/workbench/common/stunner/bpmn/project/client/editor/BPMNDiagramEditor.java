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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import java.util.Collection;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.project.client.docks.StunnerDocksHandler;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = BPMNDiagramEditor.EDITOR_ID, supportedTypes = {BPMNDiagramResourceType.class})
public class BPMNDiagramEditor extends AbstractProjectDiagramEditor<BPMNDiagramResourceType> {

    public static final String EDITOR_ID = "BPMNDiagramEditor";

    private final UberfireDocks uberfireDocks;
    private final StunnerDocksHandler stunnerDocksHandler;

    private Consumer<Boolean> saveCallback;
    private boolean isMigrating = false;
    private boolean isPropertiesOpenedBeforeMaximize = false;
    private boolean isExplorerOpenedBeforeMaximize = false;

    @Inject
    public BPMNDiagramEditor(final View view,
                             final TextEditorView xmlEditorView,
                             final ManagedInstance<SessionEditorPresenter<EditorSession>> editorPresenter,
                             final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerPresenter,
                             final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                             final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                             final Event<NotificationEvent> notificationEvent,
                             final ErrorPopupPresenter errorPopupPresenter,
                             final DiagramClientErrorHandler diagramClientErrorHandler,
                             final @BPMN DocumentationView documentationView,
                             final BPMNDiagramResourceType resourceType,
                             final BPMNProjectEditorMenuSessionItems menuSessionItems,
                             final ProjectMessagesListener projectMessagesListener,
                             final ClientTranslationService translationService,
                             final ClientProjectDiagramService projectDiagramServices,
                             final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                             final UberfireDocks uberfireDocks,
                             final StunnerDocksHandler stunnerDocksHandler) {
        super(view,
              xmlEditorView,
              editorPresenter,
              viewerPresenter,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              notificationEvent,
              errorPopupPresenter,
              diagramClientErrorHandler,
              documentationView,
              resourceType,
              menuSessionItems,
              projectMessagesListener,
              translationService,
              projectDiagramServices,
              projectDiagramResourceServiceCaller);
        this.uberfireDocks = uberfireDocks;
        this.stunnerDocksHandler = stunnerDocksHandler;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path,
                        place);
    }

    @Override
    public String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @OnOpen
    public void onOpen() {
        openPropertiesDocks();
        super.doOpen();
    }

    private void performDockOperation(final String id, final Consumer<? super UberfireDock> action) {
        String currentPerspectiveIdentifier = perspectiveManager.getCurrentPerspective().getIdentifier();
        Collection<UberfireDock> stunnerDocks = stunnerDocksHandler.provideDocks(currentPerspectiveIdentifier);

        stunnerDocks.stream()
                .filter(dock -> dock.getPlaceRequest().getIdentifier().compareTo(id) == 0)
                .forEach(action);
    }

    public void openPropertiesDocks() {
        performDockOperation(DiagramEditorPropertiesScreen.SCREEN_ID, uberfireDocks::open);
    }

    public void closePropertiesDocks() {
        performDockOperation(DiagramEditorPropertiesScreen.SCREEN_ID, uberfireDocks::close);
    }

    public void openExplorerDocks() {
        performDockOperation(DiagramEditorExplorerScreen.SCREEN_ID, uberfireDocks::open);
    }

    public void closeExplorerDocks() {
        performDockOperation(DiagramEditorExplorerScreen.SCREEN_ID, uberfireDocks::close);
    }

    public void onScreenMaximizedEvent(@Observes ScreenMaximizedEvent event) {
        isPropertiesOpenedBeforeMaximize = false;
        isExplorerOpenedBeforeMaximize = false;
    }

    public void onScreenPreMaximizedStateEvent(final @Observes ScreenPreMaximizedStateEvent event) {
        // If Event Fired, it means the properties panel is active, hence it was open before maximized
        isPropertiesOpenedBeforeMaximize = !event.isExplorerScreen();
        isExplorerOpenedBeforeMaximize = event.isExplorerScreen();
    }

    public void onScreenMinimizedEvent(@Observes ScreenMinimizedEvent event) {
        if (isPropertiesOpenedBeforeMaximize) {
            openPropertiesDocks();
        } else if (isExplorerOpenedBeforeMaximize) {
            openExplorerDocks();
        }
    }

    @OnClose
    @Override
    public void onClose() {
        super.doClose();
    }

    @OnFocus
    public void onFocus() {
        super.doFocus();
    }

    @OnLostFocus
    public void onLostFocus() {
        super.doLostFocus();
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
    }

    @Override
    protected void onSaveSuccess() {
        super.onSaveSuccess();
        if (isMigrating) {
            isMigrating = false;
            saveCallback.accept(true);
        }
    }

    @Override
    public void onSaveError(ClientRuntimeError error) {
        super.onSaveError(error);
        if (isMigrating) {
            isMigrating = false;
            saveCallback.accept(false);
        }
    }
}
