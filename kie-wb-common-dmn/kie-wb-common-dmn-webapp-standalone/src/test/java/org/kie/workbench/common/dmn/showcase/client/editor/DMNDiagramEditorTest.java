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

package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.dmn.webapp.common.client.docks.preview.PreviewDiagramDock;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor.DATA_TYPES_PAGE_INDEX;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramEditorTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionEditorPresenter<EditorSession> presenter;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    private DiagramEditorPropertiesDock diagramPropertiesDock;

    @Mock
    private PreviewDiagramDock diagramPreviewAndExplorerDock;

    @Mock
    private LayoutHelper layoutHelper;

    @Mock
    private OpenDiagramLayoutExecutor layoutExecutor;

    @Mock
    private DataTypesPage dataTypesPage;

    @Mock
    private IncludedModelsPage includedModelsPage;

    @Mock
    private IncludedModelsPageStateProviderImpl importsPageProvider;

    @Mock
    private DocumentationView<Diagram> documentationView;

    @Mock
    private ScreenPanelView screenPanelView;

    @Mock
    private KieEditorWrapperView kieView;

    @Mock
    private ExpressionEditorView.Presenter expressionEditor;

    @Mock
    private DMNEditorSession session;

    @Mock
    private DMNEditorSearchIndex editorSearchIndex;

    @Mock
    private SearchBarComponent<DMNSearchableElement> searchBarComponent;

    @Mock
    private SearchBarComponent.View searchBarComponentView;

    @Mock
    private HTMLElement searchBarComponentViewElement;

    @Mock
    private MonacoFEELInitializer feelInitializer;

    @Mock
    private ElementWrapperWidget searchBarComponentWidget;
    private DMNDiagramEditor editor;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(searchBarComponent.getView()).thenReturn(searchBarComponentView);
        when(searchBarComponentView.getElement()).thenReturn(searchBarComponentViewElement);
        doReturn(presenter).when(presenter).withToolbar(anyBoolean());
        doReturn(presenter).when(presenter).withPalette(anyBoolean());
        doReturn(presenter).when(presenter).displayNotifications(any());
        doReturn(session).when(presenter).getInstance();
        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(expressionEditor).when(session).getExpressionEditor();
        doAnswer((invocation) -> {
            Diagram diagram = (Diagram) invocation.getArguments()[0];
            SessionPresenter.SessionPresenterCallback callback = (SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1];
            callback.onOpen(diagram);
            callback.afterCanvasInitialized();
            callback.afterSessionOpened();
            callback.onSuccess();
            return null;
        }).when(presenter).open(any(Diagram.class),
                                any(SessionPresenter.SessionPresenterCallback.class));

        editor = spy(new DMNDiagramEditor(sessionManager,
                                          null,
                                          presenter,
                                          refreshFormPropertiesEvent,
                                          null,
                                          null,
                                          decisionNavigatorDock,
                                          diagramPropertiesDock,
                                          diagramPreviewAndExplorerDock,
                                          layoutHelper,
                                          layoutExecutor,
                                          dataTypesPage,
                                          includedModelsPage,
                                          importsPageProvider,
                                          documentationView,
                                          editorSearchIndex,
                                          searchBarComponent,
                                          null,
                                          null,
                                          null,
                                          null,
                                          screenPanelView,
                                          null,
                                          kieView,
                                          feelInitializer));

        doReturn(searchBarComponentWidget).when(editor).getWidget(searchBarComponentViewElement);
    }

    @Test
    public void testInit() {

        final Widget screenPanelWidget = mock(Widget.class);
        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);
        final DocumentationPage documentationPage = mock(DocumentationPage.class);
        final Supplier<Boolean> isDataTypesTabActiveSupplier = () -> true;
        final Supplier<Integer> hashcodeSupplier = () -> 123;

        doReturn(hashcodeSupplier).when(editor).getHashcodeSupplier();
        doReturn(isDataTypesTabActiveSupplier).when(editor).getIsDataTypesTabActiveSupplier();
        doReturn(documentationPage).when(editor).getDocumentationPage();
        when(kieView.getMultiPage()).thenReturn(multiPageEditor);
        when(screenPanelView.asWidget()).thenReturn(screenPanelWidget);

        editor.init();

        verify(decisionNavigatorDock).init(AuthoringPerspective.PERSPECTIVE_ID);
        verify(diagramPreviewAndExplorerDock).init(AuthoringPerspective.PERSPECTIVE_ID);
        verify(diagramPropertiesDock).init(AuthoringPerspective.PERSPECTIVE_ID);
        verify(kieView).setPresenter(editor);
        verify(kieView).clear();
        verify(kieView).addMainEditorPage(screenPanelWidget);
        verify(multiPageEditor).addPage(dataTypesPage);
        verify(multiPageEditor).addPage(includedModelsPage);
        verify(multiPageEditor).addPage(documentationPage);
        verify(editorSearchIndex).setIsDataTypesTabActiveSupplier(isDataTypesTabActiveSupplier);
        verify(editorSearchIndex).setCurrentAssetHashcodeSupplier(hashcodeSupplier);

        verify(editor).setupSearchComponent();
    }

    @Test
    public void testGetHashcodeSupplier() {

        final Diagram diagram = new DiagramImpl("something", null);
        final Integer expectedHashcode = diagram.hashCode();

        doReturn(diagram).when(editor).getDiagram();

        final Integer actualHashcode = editor.getHashcodeSupplier().get();

        assertEquals(expectedHashcode, actualHashcode);
    }

    @Test
    public void testGetIsDataTypesTabActiveSupplierWhenDataTypesTabIsActive() {

        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);
        when(multiPageEditor.selectedPage()).thenReturn(DATA_TYPES_PAGE_INDEX);

        assertTrue(editor.getIsDataTypesTabActiveSupplier().get());
    }

    @Test
    public void testGetIsDataTypesTabActiveSupplierWhenDataTypesTabIsNotActive() {
        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);
        when(multiPageEditor.selectedPage()).thenReturn(DATA_TYPES_PAGE_INDEX + 1);

        assertFalse(editor.getIsDataTypesTabActiveSupplier().get());
    }

    @Test
    public void testSetupSearchComponent() {

        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);

        editor.setupSearchComponent();

        verify(searchBarComponent).init(editorSearchIndex);
        verify(multiPageEditor).addTabBarWidget(searchBarComponentWidget);
    }

    @Test
    public void testOpenDiagram() {

        final Diagram diagram = mock(Diagram.class);
        final Command callback = mock(Command.class);
        final Metadata metadata = mock(Metadata.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(importsPageProvider.withDiagram(diagram)).thenReturn(importsPageProvider);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(diagram.getMetadata()).thenReturn(metadata);

        editor.open(diagram, callback);

        verify(feelInitializer).initializeFEELEditor();
        verify(layoutHelper).applyLayout(diagram, layoutExecutor);

        final InOrder inOrder = inOrder(decisionNavigatorDock, diagramPreviewAndExplorerDock, diagramPropertiesDock);
        inOrder.verify(decisionNavigatorDock).setupCanvasHandler(canvasHandler);
        inOrder.verify(decisionNavigatorDock).open();
        inOrder.verify(diagramPropertiesDock).open();
        inOrder.verify(diagramPreviewAndExplorerDock).open();

        verify(dataTypesPage).reload();
        verify(dataTypesPage).enableShortcuts();
        verify(includedModelsPage).setup(importsPageProvider);
    }

    @Test
    public void testOnClose() {

        doNothing().when(editor).destroyDock();
        doNothing().when(editor).destroySession();

        editor.onClose();

        verify(editor).destroyDock();
        verify(editor).destroySession();
        verify(dataTypesPage).disableShortcuts();
    }

    @Test
    public void testOpenDock() {

        final EditorSession session = mock(EditorSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        editor.openDock();

        verify(decisionNavigatorDock).open();
    }

    @Test
    public void testDestroyDock() {

        editor.destroyDock();

        verify(decisionNavigatorDock).close();
        verify(decisionNavigatorDock).resetContent();
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {

        final MultiPageEditor multiPageEditor = mock(MultiPageEditor.class);

        when(kieView.getMultiPage()).thenReturn(multiPageEditor);

        editor.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(multiPageEditor).selectPage(DMNDiagramEditor.DATA_TYPES_PAGE_INDEX);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnDataTypeEditModeToggleWhenEditModeIsEnabled() {

        final DataTypeEditModeToggleEvent event = mock(DataTypeEditModeToggleEvent.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Menus menus = mock(Menus.class);
        final List<MenuItem> items = singletonList(menuItem);

        when(menus.getItems()).thenReturn(items);
        when(event.isEditModeEnabled()).thenReturn(true);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(menus);
            return null;
        }).when(editor).getMenu(any());

        editor.onDataTypeEditModeToggle(event);

        verify(menuItem).setEnabled(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnDataTypeEditModeToggleWhenEditModeIsNotEnabled() {

        final DataTypeEditModeToggleEvent event = mock(DataTypeEditModeToggleEvent.class);
        final MenuItem menuItem = mock(MenuItem.class);
        final Menus menus = mock(Menus.class);
        final List<MenuItem> items = singletonList(menuItem);

        when(menus.getItems()).thenReturn(items);
        when(event.isEditModeEnabled()).thenReturn(false);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(menus);
            return null;
        }).when(editor).getMenu(any());

        editor.onDataTypeEditModeToggle(event);

        verify(menuItem).setEnabled(true);
    }

    @Test
    public void testGetOnStartupDiagramEditorCallback() {

        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        final String title = "title";

        doNothing().when(editor).updateTitle(anyString());
        doReturn(diagram).when(editor).getDiagram();
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(title);

        editor.getOnStartupDiagramEditorCallback().execute();

        verify(editor).updateTitle(title);
        verify(documentationView).initialize(diagram);
    }

    @Test
    public void testGetDocumentationPage() {

        final DocumentationPage documentationPage = editor.getDocumentationPage();

        assertEquals("Documentation", documentationPage.getLabel());
        assertEquals(documentationView, documentationPage.getDocumentationView());
    }

    @Test
    public void testOnEditExpressionEvent() {
        editor.onEditExpressionEvent(mock(EditExpressionEvent.class));
        verify(searchBarComponent).disableSearch();
    }

    @Test
    public void testOnMultiPageEditorSelectedPageEvent() {
        editor.onMultiPageEditorSelectedPageEvent(mock(MultiPageEditorSelectedPageEvent.class));
        verify(searchBarComponent).disableSearch();
    }

    @Test
    public void testOnRefreshFormPropertiesEvent() {
        editor.onRefreshFormPropertiesEvent(mock(RefreshFormPropertiesEvent.class));
        verify(searchBarComponent).disableSearch();
    }
}
